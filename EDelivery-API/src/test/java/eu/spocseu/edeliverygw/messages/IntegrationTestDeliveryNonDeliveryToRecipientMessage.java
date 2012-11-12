package eu.spocseu.edeliverygw.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;

public class IntegrationTestDeliveryNonDeliveryToRecipientMessage
{

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void sendDeliveryToRecipient() throws Exception
	{
		DispatchMessage dispatchMessage = IntegrationTestCommon
				.createDispatchMessage();
		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), dispatchMessage, true);
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		DeliveryNonDeliveryToRecipientMessage message = new DeliveryNonDeliveryToRecipientMessage(
			new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), subEvidence), samlProperties);
		message.sendEvidenceMessage();
	}

	@Test
	public void sendNonDeliveryToRecipient() throws Exception
	{
		DispatchMessage dispatchMessage = IntegrationTestCommon
				.createDispatchMessage();
		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), dispatchMessage, false);
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		DeliveryNonDeliveryToRecipientMessage message = new DeliveryNonDeliveryToRecipientMessage(
			new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), subEvidence, false),
			samlProperties);
		message.sendEvidenceMessage();

	}

}
