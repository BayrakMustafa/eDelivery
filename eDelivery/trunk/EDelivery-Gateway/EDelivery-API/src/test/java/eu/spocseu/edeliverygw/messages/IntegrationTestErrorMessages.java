package eu.spocseu.edeliverygw.messages;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.util.Date;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.common.CryptoTools;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.exception.RelayREMMDRejectionException;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Originators;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;

public class IntegrationTestErrorMessages
{
	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void testWrongSignature() throws Exception
	{
		Configuration config = Configuration.getConfiguration();
		KeyStore sigkey = CryptoTools.loadPKCS12(getClass()
				.getResourceAsStream("/test_alice_signature.p12"), "123456"
				.toCharArray());
		config.setSignatureTrustStore(sigkey, "123456".toCharArray());
		Originators ori = new Originators("rl@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");
		Destinations dest = new Destinations("ja@localhost-spocs-edelivery.eu",
			"Joerg, Apitzsch");
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);
		message.setContent(IntegrationTestCommon.createNormalized(), null);
		try {
			message.sendDispatchMessage();
		} catch (SOAPFaultException ex) {
			if (!ex.getMessage().equals("Error validate the signature.")) {
				throw ex;
			}
		}
	}

	@Test
	public void testWrongOriginator() throws Exception
	{
		DispatchMessage message = IntegrationTestCommon.createDispatchMessage();
		message.getXSDObject()
				.getMsgMetaData()
				.getOriginators()
				.getFrom()
				.getAttributedElectronicAddressOrElectronicAddress().add(
					SpocsFragments.createElectoricAddress("rl@wrongFrom.de"));
		try {
			message.sendDispatchMessage();

		} catch (RelayREMMDRejectionException ex) {
			assertEquals("Wrong event code",
				REMErrorEvent.UNKNOWN_ORIGINATOR_ADDRESS.getEvidence()
						.getFaultEventCode(), ex.getEvidence().getXSDObject()
						.getEventCode());
			assertEquals("Wrong reason code.",
				REMErrorEvent.UNKNOWN_ORIGINATOR_ADDRESS.getEventCode(), ex
						.getEvidence().getXSDObject().getEventReasons()
						.getEventReason().get(0).getCode());
			assertEquals(
				"Wrong details",
				"eu.spocseu.tsl.internal.exception.UnknownDomainException: Can't find entry in the TSL for domain: wrongFrom.de",
				ex.getEvidence().getXSDObject().getEventReasons()
						.getEventReason().get(0).getDetails());
		}

	}

	@Test
	public void testSoapFault() throws Exception
	{
		DispatchMessage message = IntegrationTestCommon.createDispatchMessage();
		// only to create a error
		message.getXSDObject()
				.getMsgMetaData()
				.getDestinations()
				.getRecipient()
				.getAttributedElectronicAddressOrElectronicAddress().add(
					SpocsFragments
							.createElectoricAddress("rl@def.localhost-spocs-edelivery.eu"));
		try {
			message.sendDispatchMessage();

		} catch (RelayREMMDRejectionException ex) {
			assertEquals("Wrong event code",
				REMErrorEvent.UNKNOWN_ORIGINATOR_ADDRESS.getEvidence()
						.getFaultEventCode(), ex.getEvidence().getXSDObject()
						.getEventCode());
			assertEquals("Wrong reason code.",
				REMErrorEvent.UNKNOWN_ORIGINATOR_ADDRESS.getEventCode(), ex
						.getEvidence().getXSDObject().getEventReasons()
						.getEventReason().get(0).getCode());
			assertEquals(
				"Wrong details",
				"eu.spocseu.tsl.internal.exception.UnknownDomainException: Can't find entry in the TSL for domain: wrongFrom.de",
				ex.getEvidence().getXSDObject().getEventReasons()
						.getEventReason().get(0).getDetails());
		}

	}
}
