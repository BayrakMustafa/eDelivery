package eu.spocseu.edeliverygw.messages;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.exception.NonDeliveryException;
import eu.spocseu.edeliverygw.evidences.exception.RelayREMMDRejectionException;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Normalized;
import eu.spocseu.edeliverygw.messageparts.Original;
import eu.spocseu.edeliverygw.messageparts.Originators;

public class IntegrationTestSimulation
{

	static String RECIPIENT = "rl@SPOCS-EDELIVERYTEST.EU";
	static String SENDER = "wo@SPOCS-EDELIVERYTEST.EU";

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	public DispatchMessageResponse sendDispatchMessage(String simulation)
			throws Exception
	{
		Date initialSend = new Date();
		DeliveryConstraints deliveryConstraints = new DeliveryConstraints(
			initialSend);

		Originators originator = new Originators(RECIPIENT);
		Destinations destinations = new Destinations(RECIPIENT);
		MsgIdentification msgIdentification = new MsgIdentification("MSG_ID");
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setSurname("Oley");

		DispatchMessage dispatch = new DispatchMessage(deliveryConstraints,
			originator, destinations, msgIdentification, samlProperties);

		Normalized normalized = new Normalized();
		normalized.setInformational("Test", "");
		normalized.addKeywords(simulation, "SpocsTesting", "SimulateEvidences");

		Original original = new Original("test".getBytes(), "txt");
		dispatch.setContent(normalized, original);

		return dispatch.sendDispatchMessage();
	}

	@Test
	public void DeliveryNonDeliverySimulation() throws Exception
	{
		DispatchMessageResponse reponse = null;
		try {
			reponse = sendDispatchMessage("DeliveryNonDeliveryToRecipient");
			assertNotNull(reponse.getDeliveryNonDeliveryToRecipientObj());
		} catch (NonDeliveryException ex) {
			// this is what we expect!
		}
	}

	@Test
	public void RelayREMMDRejectionSimulation() throws Exception
	{
		DispatchMessageResponse reponse = null;
		try {
			reponse = sendDispatchMessage("RelayREMMDRejection");
			assertNotNull(reponse.getRelayToREMMDAcceptanceRejectionObj());
		} catch (RelayREMMDRejectionException ex) {
			// this is what we expect!
		}
	}

}
