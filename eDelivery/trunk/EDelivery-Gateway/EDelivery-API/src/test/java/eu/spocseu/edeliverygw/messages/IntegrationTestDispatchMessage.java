/*******************************************************************************
 * Copyright (c) 2012 EU LSP SPOCS http://www.eu-spocs.eu/.
 * 
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *  
 *  http://ec.europa.eu/idabc/en/document/7774.html
 *  
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 ******************************************************************************/
package eu.spocseu.edeliverygw.messages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import eu.spocseu.common.SpocsConstants;
import eu.spocseu.edeliverygw.XMLSignatureHelper;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Originators;

public class IntegrationTestDispatchMessage
{
	private static Logger LOG = LoggerFactory
			.getLogger(IntegrationTestDispatchMessage.class.getName());

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
		// Configuration.setInputDataXML(IntegrationTestDispatchMessage.class
		// .getResourceAsStream("/src/test/resources/spocsConfig.xml"));
	}

	@Test
	public void testSerializeAndSend() throws Exception
	{
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
		message.setContent(IntegrationTestCommon.createNormalized(),
			IntegrationTestCommon.createOriginal());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(message);
		oos.close();

		byte[] messageByte = out.toByteArray();
		ByteArrayInputStream is = new ByteArrayInputStream(messageByte);
		LOG.error("Dispatch message: " + out);
		ObjectInputStream ois = new ObjectInputStream(is);
		DispatchMessage readMsg = (DispatchMessage) ois.readObject();
		out = new ByteArrayOutputStream();
		readMsg.serialize(out);
		LOG.error("Dispatch message: " + out);
		readMsg.sendDispatchMessage();
	}

	@Test
	public void testSimpleSend() throws Exception
	{
		Originators ori = new Originators("rl@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");
		Destinations dest = new Destinations("ja@localhost-spocs-edelivery.eu",
			"Joerg, Apitzsch");

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		samlProperties.setRole(SpocsConstants.ActorRole.CITIZEN);
		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);
		message.setContent(IntegrationTestCommon.createNormalized(),
			IntegrationTestCommon.createOriginal());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		message.serialize(out);

		System.err.println(out.toString());

		message.sendDispatchMessage();
		ByteArrayOutputStream out2 = new ByteArrayOutputStream();
		message.serialize(out2);

		System.err.println(out2.toString());

	}

	@Test
	public void testResponse() throws Exception
	{
		DispatchMessage message = IntegrationTestCommon.createDispatchMessage();
		DispatchMessageResponse rsp = message.sendDispatchMessage();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		rsp.serialize(out);

		// DocumentBuilderFactory factory =
		// DocumentBuilderFactory.newInstance();
		// DocumentBuilder builder = factory.newDocumentBuilder();
		// ByteArrayInputStream bais = new
		// ByteArrayInputStream(out.toByteArray());
		Document doc = rsp.document;

		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = transFactory.newTransformer();

		StringWriter buffer = new StringWriter();

		Node signedEvidenceMessage = doc.getElementsByTagName(
			"ns7:REMMDEvidenceList").item(0);
		transformer.transform(new DOMSource(signedEvidenceMessage),
			new StreamResult(buffer));

		String str = buffer.toString();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder;
		Document evidenceDocument;

		builder = dbf.newDocumentBuilder();
		evidenceDocument = builder
				.parse(new InputSource(new StringReader(str)));

		// FIXME
		XMLSignatureHelper.validateSignature(evidenceDocument);

		System.err.println(out.toString());
		assertNotNull("RelayToREMMDAcceptanceRejection",
			rsp.getRelayToREMMDAcceptanceRejection());
		assertNull("DeliveryNonDeliveryToRecipient",
			rsp.getDeliveryNonDeliveryToRecipient());
		AttributedElectronicAddressType sendElReq = GeneralMessage
				.getAttributedElectronicAdress(message
						.getSubmissionAcceptanceRejection().getXSDObject()
						.getSenderDetails());
		AttributedElectronicAddressType sendElRsp = GeneralMessage
				.getAttributedElectronicAdress(rsp
						.getRelayToREMMDAcceptanceRejection()
						.getSenderDetails());

		assertNotNull("Wrong sender details request", sendElReq);
		assertNotNull("Wrong sender details request", sendElRsp);
	}

	@Test
	public void testGetSAMLAttributes() throws Exception
	{
		Originators ori = new Originators("rl@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");
		Destinations dest = new Destinations("ja@localhost-spocs-edelivery.eu",
			"Joerg, Apitzsch");

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		samlProperties.setAuthenticationMethod("none");
		samlProperties.setDateOfBirth(new Date());
		samlProperties.setAuthenticationTime(new Date());

		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);

		message.setContent(IntegrationTestCommon.createNormalized(),
			IntegrationTestCommon.createOriginal());
		message.sendDispatchMessage();

		Map<String, String> samlAttributes = message.getSamlAttributes();
		Set<String> keys = message.getSamlAttributes().keySet();

		for (String key : keys) {
			LOG.info(key + " | " + samlAttributes.get(key));
		}
		Assert.assertEquals(samlProperties.getCitizenQAAlevel(), Integer
				.parseInt(samlAttributes
						.get("http://www.stork.gov.eu/1.0/citizenQAAlevel")));
		Assert.assertEquals(samlProperties.getSurname(),
			samlAttributes.get("http://www.stork.gov.eu/1.0/surname"));
		Assert.assertEquals(samlProperties.getGivenName(),
			samlAttributes.get("http://www.stork.gov.eu/1.0/givenName"));

	}

	@Test
	public void testGetSamlProperties() throws Exception
	{
		Originators ori = new Originators("rl@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");
		Destinations dest = new Destinations("ja@localhost-spocs-edelivery.eu",
			"Joerg, Apitzsch");

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		samlProperties.setAuthenticationMethod("none");
		samlProperties.setDateOfBirth(new Date());
		samlProperties.setAuthenticationTime(new Date());
		samlProperties.seteIdentifier("TEST");

		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);

		message.setContent(IntegrationTestCommon.createNormalized(),
			IntegrationTestCommon.createOriginal());
		message.sendDispatchMessage();

		SAMLProperties samlProps = message.getSamlProperties();

		// Assert.assertEquals(samlProperties.getAuthenticationMethod(),samlProps.getAuthenticationMethod());
		Assert.assertEquals(samlProperties.getCitizenQAAlevel(),
			samlProps.getCitizenQAAlevel());
		Assert.assertEquals(samlProperties.getSurname(), samlProps.getSurname());
		Assert.assertEquals(samlProperties.getGivenName(),
			samlProps.getGivenName());

		LOG.info(samlProperties.getDateOfBirth().toString());
		LOG.info(samlProps.getDateOfBirth().toString());

		Assert.assertTrue(samlProperties.getDateOfBirth().equals(
			samlProps.getDateOfBirth()));
		Assert.assertTrue(samlProperties.geteIdentifier().equals(
			samlProps.geteIdentifier()));

		}
}
