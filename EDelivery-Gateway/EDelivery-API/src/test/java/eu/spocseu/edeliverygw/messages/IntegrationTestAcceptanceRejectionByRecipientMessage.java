package eu.spocseu.edeliverygw.messages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.AcceptanceRejectionByRecipient;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;

public class IntegrationTestAcceptanceRejectionByRecipientMessage
{

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void sendAcceptanceByRecipientMessage() throws Exception
	{
		DispatchMessage simulatedMessage = IntegrationTestCommon
				.createDispatchMessage();
		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), simulatedMessage, true);
		DeliveryNonDeliveryToRecipient delNon = new DeliveryNonDeliveryToRecipient(
			Configuration.getConfiguration(), subEvidence);

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		AcceptanceRejectionByRecipientMessage evidence = new AcceptanceRejectionByRecipientMessage(
			new AcceptanceRejectionByRecipient(
				Configuration.getConfiguration(), delNon), samlProperties);
		assertTrue(evidence.isSuccessfull());
		evidence.sendEvidenceMessage();
	}

	@Test
	public void sendAcceptanceByRecipientMessageFalse() throws Exception
	{
		DispatchMessage simulatedMessage = IntegrationTestCommon
				.createDispatchMessage();
		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), simulatedMessage, true);
		DeliveryNonDeliveryToRecipient delNon = new DeliveryNonDeliveryToRecipient(
			Configuration.getConfiguration(), subEvidence, true);

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Wilko");
		samlProperties.setSurname("Oley");
		AcceptanceRejectionByRecipientMessage emsg = new AcceptanceRejectionByRecipientMessage(
			new AcceptanceRejectionByRecipient(
				Configuration.getConfiguration(), delNon, false),
			samlProperties);
		assertFalse(emsg.isSuccessfull());
		emsg.sendEvidenceMessage();
	}

}
