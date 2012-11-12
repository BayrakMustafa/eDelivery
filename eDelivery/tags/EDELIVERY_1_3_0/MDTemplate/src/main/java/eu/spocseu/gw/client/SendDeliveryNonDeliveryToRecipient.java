package eu.spocseu.gw.client;

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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.messageparts.Originators;
import eu.spocseu.edeliverygw.messages.DeliveryNonDeliveryToRecipientMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessage;

/**
 * The DeliveryNonDeliveryToRecipient can be set as a new SOAP message in
 * asynchronous cases. The API needs as an input the previous
 * SubmissionAcceptanceRejection so that the next evidence in progress (the
 * DeliveryNonDeliveryToRecipient) can be built on that information. This sample
 * simulates the previous messages and sends the DeliveryNonDeliveryToRecipient
 * evidence message as a new SOAP message to the foreign gateway.
 * 
 * @author R.Lindemann
 * 
 * @version SPOCS_WP3_CURRENT_RELEASE_LABEL
 * @since SPOCS_WP3_INITIAL_RELEASE_LABEL
 * @see <a href="SPOCS_WP3_SPECIFICATION_URL">SPOCS_WP3_SPECIFICATION_LABEL</a>
 */

public class SendDeliveryNonDeliveryToRecipient
{
	private static Logger LOG = LoggerFactory
			.getLogger(SendDeliveryNonDeliveryToRecipient.class);

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
	 * This method only simulates the SubmissionAcceptanceRejection that must be
	 * stored by the gateway implementation with the previous sent messages. In
	 * real life the information from the SubmissionAcceptanceRejection does not
	 * need to be created.
	 * 
	 * @return Created SubmissionAcceptanceRejection evidence.
	 */
	private static SubmissionAcceptanceRejection createSubmissionAcceptanceRejection()
			throws JAXBException
	{
		try {
			DispatchMessage message = SendDispatch.createDispatchMessage();
			return new SubmissionAcceptanceRejection(
				Configuration.getConfiguration(), message, true);
		} catch (Exception ex) {
			LOG.warn(
				"Unexpected error simulate the DeliveryNonDeliveryToRecipient",
				ex);
			// usually these exceptions will not be thrown because the
			// SubmissionAcceptanceRejection creation happens much earlier.
			// So we throw the one and only expected exception
			throw new JAXBException(ex);

		}
	}

	/**
	 * Start sending the evidence.
	 */
	public static void main(String[] args) throws Exception
	{
		SendDeliveryNonDeliveryToRecipient sendHandle = new SendDeliveryNonDeliveryToRecipient();
		sendHandle.sendSendRetrievalNonRetrieval();

	}

	/**
	 * This is the processing method that assembles the steps of creating and
	 * sending the evidence message.
	 * 
	 * @throws IOException
	 * 
	 * @throws SpocsConfigurationException
	 * @throws JAXBException
	 * @throws EvidenceException
	 * @throws SpocsSystemInstallationException
	 * @throws SpocsWrongInputDataException
	 */
	public void sendSendRetrievalNonRetrieval() throws IOException,
			SpocsConfigurationException, JAXBException,
			SpocsWrongInputDataException, SpocsSystemInstallationException,
			EvidenceException
	{
		// first configure the API
		configure();
		// These values will be set on the from/electronicAddress. Some of these
		// values will be put into the SAML token.
		Originators ori = new Originators("rl@localhost-spocs-edelivery.eu", "Lindemann, Ralf");
		// Simulate the previous evidence.
		SubmissionAcceptanceRejection subEvidence = createSubmissionAcceptanceRejection();
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		// Then create the new evidence object based on the previous evidence

		DeliveryNonDeliveryToRecipientMessage message = new DeliveryNonDeliveryToRecipientMessage(
			new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), subEvidence), samlProperties);
		// Now send the message to the remote gateway. No response will be
		// expected.
		message.sendEvidenceMessage();
	}
}
