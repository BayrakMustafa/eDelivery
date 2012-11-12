package eu.spocseu.edeliverygw.messages;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Normalized;
import eu.spocseu.edeliverygw.messageparts.Original;
import eu.spocseu.edeliverygw.messageparts.Originators;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;

public class IntegrationTestCommon
{

	private static Logger LOG = LoggerFactory
			.getLogger(IntegrationTestCommon.class.getName());

	public static DispatchMessage createDispatchMessage() throws IOException,
			GeneralSecurityException, DatatypeConfigurationException,
			JAXBException, SpocsConfigurationException
	{
		// These values will be set on the from/electronicAddress. Some of these
		// values will be put into the SAML token.
		Originators ori = new Originators("rl@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");

		// Originators ori = new Originators(
		// IntegrationTestSimulationTest.RECIPIENT, "Lindemann, Ralf");

		// The destination address will be used to look into the TSL and to
		// address the message to the next Spocs gateway with the configured
		// credentials.
		Destinations dest = new Destinations("wo@localhost-spocs-edelivery.eu",
			"Wilko Oley");

		// Destinations dest = new Destinations(
		// IntegrationTestSimulationTest.RECIPIENT, "Wilko Oley");
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		// Some detailed information about the message
		DeliveryConstraints deliveryConstraints = new DeliveryConstraints(
			new Date());
		DispatchMessage dispatchMessage = new DispatchMessage(
			deliveryConstraints, ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);
		// All the created objects can be configured in more details by getting
		// the underlying JAXB objects with the related getXSDObject
		// As an example a message timeout value will be changed.
		deliveryConstraints.getXSDObject().setObsoleteAfter(
			SpocsFragments.createXMLGregorianCalendar(3));
		dispatchMessage.setContent(createNormalized(), createOriginal());
		return dispatchMessage;
	}

	public static Normalized createNormalized()
	{
		LOG.debug("Create standart normalized.");
		Normalized normalized = new Normalized();

		normalized.setInformational("mySubject", "Test doument");
		normalized
				.setText(
					"Here now the text with the hole content this could be xml of course",
					Normalized.TEXT_FORMATS.TEXT);
		return normalized;
	}

	
	public static Original createOriginal() throws IOException
	{
		LOG.debug("Create standart Original.");

		String text = "TEST MESSAGE!";
		return new Original(text.getBytes(),"ASCII");
	}
}
