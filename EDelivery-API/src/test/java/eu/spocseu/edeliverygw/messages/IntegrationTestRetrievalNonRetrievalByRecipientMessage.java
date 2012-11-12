package eu.spocseu.edeliverygw.messages;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.RetrievalNonRetrievalByRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;

public class IntegrationTestRetrievalNonRetrievalByRecipientMessage
{

	private static Logger LOG = LoggerFactory
			.getLogger(IntegrationTestRetrievalNonRetrievalByRecipientMessage.class);

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void sendSendRetrieval()
	{

		try {
			DispatchMessage simulatedMessage = IntegrationTestCommon
					.createDispatchMessage();
			SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
				Configuration.getConfiguration(), simulatedMessage, true);
			SAMLProperties samlProperties = new SAMLProperties();
			samlProperties.setCitizenQAAlevel(1);
			samlProperties.setGivenName("Ralf");
			samlProperties.setSurname("Lindemann");
			DeliveryNonDeliveryToRecipient delNon = new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), subEvidence);
			RetrievalNonRetrievalByRecipientMessage message = new RetrievalNonRetrievalByRecipientMessage(
				new RetrievalNonRetrievalByRecipient(
					Configuration.getConfiguration(), subEvidence),
				samlProperties);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			message.serialize(out);
			LOG.debug("Send Message: " + out.toString());
			message.sendEvidenceMessage();
		} catch (EvidenceException ex) {
			LOG.warn("ErrorEvent"+ex.getErrorEvent());
			LOG.warn("Unexpected error", ex);
			fail("Unexpected error" + ex.getMessage());
		} catch (Exception ex) {
			LOG.warn("Unexpected error", ex);
			fail("Unexpected error" + ex.getMessage());
		}

	}

	@Test
	public void sendNonRetrieval()
	{
		try {
			DispatchMessage simulatedMessage = IntegrationTestCommon
					.createDispatchMessage();
			SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
				Configuration.getConfiguration(), simulatedMessage, false);
			DeliveryNonDeliveryToRecipient delNon = new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), subEvidence);
			SAMLProperties samlProperties = new SAMLProperties();
			samlProperties.setCitizenQAAlevel(1);
			samlProperties.setGivenName("Ralf");
			samlProperties.setSurname("Lindemann");
			RetrievalNonRetrievalByRecipientMessage message = new RetrievalNonRetrievalByRecipientMessage(
				new RetrievalNonRetrievalByRecipient(
					Configuration.getConfiguration(), delNon), samlProperties);
			message.sendEvidenceMessage();

		} catch (Exception ex) {
			LOG.warn("Unexpected error", ex);
			fail("Unexpected error" + ex.getMessage());
		}
	}

	// the austrian way
	@Test
	public void sendRetrievalInitWithSubmissiion()
	{
		try {
			DispatchMessage simulatedMessage = IntegrationTestCommon
					.createDispatchMessage();
			SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
				Configuration.getConfiguration(), simulatedMessage, true);
			SAMLProperties samlProperties = new SAMLProperties();
			samlProperties.setCitizenQAAlevel(1);
			samlProperties.setGivenName("Ralf");
			samlProperties.setSurname("Lindemann");
			RetrievalNonRetrievalByRecipientMessage message = new RetrievalNonRetrievalByRecipientMessage(
				new RetrievalNonRetrievalByRecipient(
					Configuration.getConfiguration(), subEvidence),
				samlProperties);

			message.sendEvidenceMessage();

		} catch (Exception ex) {
			LOG.warn("Unexpected error", ex);
			fail("Unexpected error" + ex.getMessage());

		}
	}
}
