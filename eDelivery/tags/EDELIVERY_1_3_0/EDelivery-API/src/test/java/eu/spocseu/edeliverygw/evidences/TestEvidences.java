package eu.spocseu.edeliverygw.evidences;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.NamesPostalAddressListType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Originators;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.EvidenceMessage;
import eu.spocseu.edeliverygw.messages.GeneralMessage;

public class TestEvidences
{
	private static Logger LOG = LoggerFactory.getLogger(TestEvidences.class);

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void testImportDeliveryNonDeliveryToRecipient() throws Exception
	{
		Originators ori = new Originators("rl@bos-bremen.de", "Lindemann, Ralf");
		Destinations dest = new Destinations("wo@localhost-spocs-edelivery.eu",
			"Joerg Apitzsch");

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");

		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);

		SubmissionAcceptanceRejection sub = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), message, true);
		DeliveryNonDeliveryToRecipient evidence = new DeliveryNonDeliveryToRecipient(
			Configuration.getConfiguration(), sub);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		evidence.serialize(out);
		LOG.warn("Evidence: " + out);
		DeliveryNonDeliveryToRecipient delNonDel = new DeliveryNonDeliveryToRecipient(
			Configuration.getConfiguration(), new ByteArrayInputStream(
				out.toByteArray()));

	}

	@Test
	public void testIssuerDetails() throws SpocsConfigurationException
	{
		Configuration config = Configuration.getConfiguration();

		SubmissionAcceptanceRejection sub = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration());
		org.etsi.uri._02640.v2_.EntityDetailsType evidenceIssuerDetails = sub.getXSDObject()
				.getEvidenceIssuerDetails();

		AttributedElectronicAddressType electronicAddress = GeneralMessage.getAttributedElectronicAdress(evidenceIssuerDetails);
		NamesPostalAddressListType postalAdress =evidenceIssuerDetails.getNamesPostalAddresses();
		
		assertEquals(electronicAddress.getValue(), config
				.geteDeliveryDetails().getGatewayAddress());

		assertEquals(config.geteDeliveryDetails().getStreetAdress(),
			postalAdress.getNamePostalAddress().get(0).getPostalAddress()
					.getStreetAddress().get(0));

		assertEquals(config.geteDeliveryDetails().getLocality(), postalAdress
				.getNamePostalAddress().get(0).getPostalAddress().getLocality());

		assertEquals(config.geteDeliveryDetails().getPostalCode(), postalAdress
				.getNamePostalAddress().get(0).getPostalAddress()
				.getPostalCode());

		assertEquals(config.geteDeliveryDetails().getGatewayName(),
			postalAdress.getNamePostalAddress().get(0).getEntityName()
					.getName().get(0));

	}
	

}
