package eu.spocseu.gw.de.egvp;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import eu.eu_spocs.uri.edelivery.v1_.ObjectFactory;
import eu.spocseu.common.JaxbContextHolder;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.EvidenceHolder;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Normalized;
import eu.spocseu.edeliverygw.messageparts.Originators;
import eu.spocseu.edeliverygw.messages.DispatchMessage;

/**
 * Sample client for dispatch message
 * 
 * @author R. Lindemann
 * 
 */
public class SendDispatch {
	private static Logger LOG = Logger.getLogger(SendDispatch.class.getName());

	private static void configure() {
		Properties spocsProperties = new Properties();
		spocsProperties.put("gatewayAddress",
				"https://localhost:8444/MDTemplate/spocs");
		spocsProperties.put("gatewayName", "FHB_EGVP_Gateway");
		spocsProperties.put("signatureTrustStore",
				"test_osci-manager_signature.p12");
		spocsProperties.put("signatureTrustPassword", "123456");
		Configuration.setSpocsProperties(spocsProperties);
	}

	public static void main(String[] args) throws Exception {
		configure();
		Originators ori = new Originators(Configuration.getConfiguration(),
				"rl@bos-bremen.de", "Lindemann", "Ralf");
		Destinations dest = new Destinations("ja@bos-bremen.de",
				"Jörg Apitzsch");

		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
				new Date()), ori, dest, new MsgIdentification("initMsgId"));
		ByteArrayOutputStream sendOut = new ByteArrayOutputStream();
		JaxbContextHolder
				.getSpocsJaxBContext()
				.createMarshaller()
				.marshal(
						new ObjectFactory().createREMDispatch(message
								.getXSDObject()), sendOut);
		LOG.severe("Request message." + sendOut);
		EvidenceHolder response = message.sendDispatchMessage();
		if (response.getRelayToREMMDAcceptanceRejection() == null
				&& response.getDeliveryNonDeliveryToRecipient() == null)
			throw new IllegalStateException("Wrong evidences receipt.");
		ByteArrayOutputStream responseOut = new ByteArrayOutputStream();
		JaxbContextHolder
				.getSpocsJaxBContext()
				.createMarshaller()
				.marshal(
						new ObjectFactory().createREMMDEvidenceList(response
								.getXSDObject()), responseOut);
		LOG.severe("Response message." + responseOut);
	}

	private static Normalized createNormalized() {
		Normalized normalized = new Normalized();
		normalized.addKeywords(value, schema, meaning)
		normalized.setInformational("mySubject",
				"Here now the spocs content for testing.");
		normalized
				.setText(
						"Here now the text with the hole content this could be xml of course",
						Normalized.TEXT_FOMATS.TEXT);
		return normalized;
	}
}