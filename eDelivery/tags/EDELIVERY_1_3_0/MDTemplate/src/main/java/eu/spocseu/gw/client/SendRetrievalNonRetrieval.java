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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.RetrievalNonRetrievalByRecipientMessage;

/**
 * The RetrievalNonRetrievalByRecipient evidence will always send asynchronous
 * as new SOAP message. The API needs as an input the previous
 * DeliveryNonDeliveryToRecipient so that the next evidence in progress (the
 * RetrievalNonRetrievalByRecipient) can be built on that information. This
 * sample simulates the previous messages and sends the
 * RetrievalNonRetrievalByRecipient evidence message as a new SOAP message to
 * the foreign gateway.
 * 
 * @author R.Lindemann
 * 
 * @version SPOCS_WP3_CURRENT_RELEASE_LABEL
 * @since SPOCS_WP3_INITIAL_RELEASE_LABEL
 * @see <a href="SPOCS_WP3_SPECIFICATION_URL">SPOCS_WP3_SPECIFICATION_LABEL</a>
 */

public class SendRetrievalNonRetrieval
{
	private static Logger LOG = LoggerFactory
			.getLogger(SendRetrievalNonRetrieval.class.getName());

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
	 * This method only simulate the DeliveryNonDeliveryToRecipient that must be
	 * stored by the gateway implementation with the previous sent messages. In
	 * real life the information from the DeliveryNonDeliveryToRecipient does
	 * not need not be created.
	 * 
	 * @return The serialized DeliveryNonDeliveryToRecipient evidence.
	 */
	private static ByteArrayInputStream simulateDeliveryNonDeliveryToRecipient()
			throws JAXBException
	{
		try {
			DispatchMessage message = SendDispatch.createDispatchMessage();
			SubmissionAcceptanceRejection sub = new SubmissionAcceptanceRejection(
				Configuration.getConfiguration(), message, true);
			ByteArrayOutputStream out1 = new ByteArrayOutputStream();
			sub.serialize(out1);
			LOG.error("Sub:" + out1);
			DeliveryNonDeliveryToRecipient evidence = new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), sub);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			evidence.serialize(out);
			LOG.error("" + out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (Exception ex) {
			LOG.warn(
				"Unexpected error at simulating the DeliveryNonDeliveryToRecipient",
				ex);
			// usually these exceptions will not be thrown because the
			// DeliveryNonDeliveryToRecipient creation happens much earlier.
			// So we throw the one and only expected exception
			throw new JAXBException(ex);

		}
	}

	/**
	 * Start sending the evidence.
	 */
	public static void main(String[] args) throws Exception
	{
		SendRetrievalNonRetrieval sendHandle = new SendRetrievalNonRetrieval();
		sendHandle.sendSendRetrievalNonRetrieval();

	}

	/**
	 * This is the processing method that assembles the steps of creating and
	 * sending the evidence message.
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 * @throws SpocsConfigurationException
	 * @throws SpocsWrongInputDataException
	 * @throws SpocsSystemInstallationException
	 * @throws EvidenceException
	 */
	public void sendSendRetrievalNonRetrieval() throws IOException,
			JAXBException, SpocsWrongInputDataException,
			SpocsConfigurationException, EvidenceException,
			SpocsSystemInstallationException
	{
		// first configure the API
		configure();
		// These values will be set on the from/electronicAddress. Some of these
		// values will be put into the SAML token.
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		// Simulate the previous evidence.
		ByteArrayInputStream previousStream = simulateDeliveryNonDeliveryToRecipient();
		// Then create the new evidence object based on the previous evidence
		// stream
		RetrievalNonRetrievalByRecipientMessage message = new RetrievalNonRetrievalByRecipientMessage(
			previousStream, Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT,
			samlProperties);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		message.serialize(out);
		// Now send the message to the remote gateway. No response will be
		// expected.
		message.sendEvidenceMessage();
	}
}
