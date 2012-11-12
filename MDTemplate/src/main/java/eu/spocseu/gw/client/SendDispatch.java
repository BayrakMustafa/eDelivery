/* ---------------------------------------------------------------------------
             COMPETITIVENESS AND INNOVATION FRAMEWORK PROGRAMME
                   ICT Policy Support Programme (ICT PSP)
           Preparing the implementation of the Services Directive
                   ICT PSP call identifier: ICT PSP-2008-2
             ICT PSP main Theme identifier: CIP-ICT-PSP.2008.1.1
                           Project acronym: SPOCS
   Project full title: Simple Procedures Online for Cross-border Services
                         Grant agreement no.: 238935
                               www.eu-spocs.eu
------------------------------------------------------------------------------
    WP3 Interoperable delivery, eSafe, secure and interoperable exchanges
                       and acknowledgement of receipt
------------------------------------------------------------------------------
        Open module implementing the eSafe document exchange protocol
------------------------------------------------------------------------------

$URL: $HeadURL$ $
$Date: $Date$ $
$Revision: $Revision$ $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */
package eu.spocseu.gw.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.LogHelper;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.RelayREMMDAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Normalized;
import eu.spocseu.edeliverygw.messageparts.Original;
import eu.spocseu.edeliverygw.messageparts.Originators;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessageResponse;

/**
 * The <code>SendDispatch</code> class represents a sample implementation of the
 * Client side of a MD Implementation. <br />
 * 
 * We will load a configuration from a configuration File, send a Dispatch and
 * check the Response.
 * 
 * @author R.Lindemann
 * 
 * @version SPOCS_WP3_CURRENT_RELEASE_LABEL
 * @since SPOCS_WP3_INITIAL_RELEASE_LABEL
 * @see <a href="SPOCS_WP3_SPECIFICATION_URL">SPOCS_WP3_SPECIFICATION_LABEL</a>
 */

public class SendDispatch
{
	private static Logger LOG = LoggerFactory.getLogger(SendDispatch.class
			.getName());

	public static void main(String[] args)
	{
		try {
			SendDispatch handle = new SendDispatch();
			handle.configure();
			handle.sendDispatch();
			LOG.info("TEST DONE!");

		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}

	/**
	 * This Method just loads the Configuration from the file
	 * ClientSpocsGW.properties. You can also set the configuration dynamically
	 * in the code.
	 * 
	 * @since SPOCS_WP3_INITIAL_LABEL
	 */

	public static void configure() throws IOException
	{
		InputStream in = SendDispatch.class
				.getResourceAsStream("/spocsConfig.xml");
		// Some properties out of the gateway configuration will be used to
		// fulfill the SAML-Token or the dispatch message
		Configuration.setInputDataXML(in);
	}

	/**
	 * 
	 * Create the DispatchMessage and fill the required values into the object.
	 * We will use the constructors provided by the EDelivery-API from
	 * <code>eu.spocseu.edeliverygw.messageparts</code>. They will help us to
	 * set all the mandatory fields.
	 * 
	 * @return the created {@link DispatchMessage} Object
	 * @throws SpocsConfigurationException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws DatatypeConfigurationException
	 * @throws JAXBException
	 * @throws SpocsSystemInstallationException
	 * @throws EvidenceException
	 * 
	 * @since SPOCS_WP3_INITIAL_LABEL
	 */
	public static DispatchMessage createDispatchMessage()
			throws SpocsConfigurationException, DatatypeConfigurationException,
			JAXBException, EvidenceException, SpocsSystemInstallationException,
			IOException
	{
		// These values will be set on the from/electronicAddress. Some of these
		// values will be put into the SAML token.
		Originators ori = new Originators("wo@localhost-spocs-edelivery.eu",
			"Wilko, Oley");
		// The destination address will be used to look into the TSL and to
		// address the message to the next Spocs gateway with the configured
		// credentials.
		Destinations dest = new Destinations("wo@localhost-spocs-edelivery.eu",
			"Wilko Oley");
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");

		// Some detailed information about the message
		DeliveryConstraints deliveryConstraints = new DeliveryConstraints(
			new Date());
		DispatchMessage dispatchMessage = new DispatchMessage(
			deliveryConstraints, ori, dest, new MsgIdentification("initMsgId",
				new String[] {}), samlProperties);
		// All the created objects can be configured in more details by getting
		// the underlying JAXB objects with the related getXSDObject
		// As an example a message timeout value will be changed.
		deliveryConstraints.getXSDObject().setObsoleteAfter(
			SpocsFragments.createXMLGregorianCalendar(3));

		String message = "Here now the text with the whole content, this could be xml of course";

		Normalized normalized = new Normalized();
		normalized.setInformational("mySubject",
			"Here now the spocs content for testing.");
		normalized.setText(message, Normalized.TEXT_FORMATS.TEXT);

		Original original = new Original(message.getBytes(), "text/plain");
		dispatchMessage.setContent(normalized, original);
		return dispatchMessage;
	}

	/**
	 * This Method will check the Response we will get when sending our
	 * Dispatch. The allowed responses are {@link RelayREMMDAcceptanceRejection}
	 * and {@link DeliveryNonDeliveryToRecipient}.
	 * 
	 * @throws JAXBException
	 * @throws SpocsSystemInstallationException
	 * @throws SpocsConfigurationException
	 * 
	 * @since SPOCS_WP3_INITIAL_LABEL
	 */
	private void checkResponse(DispatchMessageResponse response)
			throws JAXBException, SpocsConfigurationException,
			SpocsSystemInstallationException
	{
		// In any case there must be a SubmissionAcceptanceRejections

		// if (response.getSubmissionAcceptanceRejection() == null)
		// throw new IllegalStateException(
		// "we expect a SubmissionAcceptanceRejection.");
		// We expect a RelayToREMMDAcceptanceRejection or a
		// DeliveryNonDeliveryToRecipient evidence object as response from the
		// remote gateway
		if (response.getRelayToREMMDAcceptanceRejection() == null
				&& response.getDeliveryNonDeliveryToRecipient() == null)
			throw new IllegalStateException("Wrong evidences receipt.");
		// The evidence object can be stored or can be used for further
		// workflow.
		ByteArrayOutputStream responseOut = new ByteArrayOutputStream();
		response.serialize(responseOut);
		LOG.debug("Response message." + responseOut);
	}

	/**
	 * This Method will check the Response. We will call the actual
	 * sendDispatchMessage on our created Message and check the response we get.
	 * 
	 * @throws DatatypeConfigurationException
	 * @throws SpocsConfigurationException
	 * @throws JAXBException
	 * @throws SpocsSystemInstallationException
	 * @throws IOException
	 * 
	 * @since SPOCS_WP3_INITIAL_LABEL
	 */

	public void sendDispatch() throws SpocsConfigurationException,
			DatatypeConfigurationException, JAXBException,
			SpocsSystemInstallationException, IOException
	{

		try {
			// The following line sends the message to the remote Spocs gateway
			// with the given content.
			DispatchMessage dm = createDispatchMessage();
			DispatchMessageResponse response = dm.sendDispatchMessage();
			// now check the response evidence
			checkResponse(response);
		} catch (EvidenceException ex) {

			// If the response contains a fault evidence like
			// RelayToREMMDRejection or NonDeliveryToRecipient the fault
			// evidence will be thrown as exception and must be handled here

			LOG.error("Catch spocs evidence exception of type: "
					+ ex.getEvidence().getEvidenceType(), ex);
		}

	}

	/**
	 * This is just a helper Method to create our message content. In this case
	 * it will be a Normalized Object which provides some features in its
	 * Informational part.
	 * 
	 * @see Normalized
	 * 
	 * @since SPOCS_WP3_INITIAL_LABEL
	 */
	public static Normalized createNormalized()
	{
		Normalized normalized = new Normalized();
		normalized.setInformational("mySubject",
			"Here now the spocs content for testing.");
		normalized
				.setText(
					"Here now the text with the whole content, this could be xml of course",
					Normalized.TEXT_FORMATS.TEXT);
		return normalized;
	}
}