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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.etsi.uri._02640.soapbinding.v1_.MsgMetaData;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Originators;

public class TestDispatchMessage
{

	private static String ORIGINATOR = "wo@bos-bremen.de";
	private static String DESTINATION = "rl@bos-bremen.de";

	private static String MSG_ID = "123";

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDispatchMessage() throws Exception
	{
		Date initialSend = new Date(2010, 11, 25);
		DeliveryConstraints deliveryConstraints = new DeliveryConstraints(
			initialSend);

		Originators originators = new Originators("wo@bos-bremen.de",
			"Oley, Wilko");

		Destinations destinations = new Destinations("rl@bos-bremen.de");
		MsgIdentification msgIdentification = new MsgIdentification(MSG_ID);
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		DispatchMessage dispatchMessage = new DispatchMessage(
			deliveryConstraints, originators, destinations, msgIdentification,
			samlProperties);

		MsgMetaData metaData = dispatchMessage.getXSDObject().getMsgMetaData();

		Date date = metaData.getDeliveryConstraints().getOrigin()
				.toGregorianCalendar().getTime();
		assertTrue(date.equals(initialSend));

		String originator = GeneralMessage.getAttributedElectronicAdress(
			metaData.getOriginators().getFrom()).getValue();
		assertTrue(ORIGINATOR.equals(originator));

		String destination = GeneralMessage.getAttributedElectronicAdress(
			metaData.getDestinations().getRecipient()).getValue();
		assertTrue(DESTINATION.equals(destination));
	}

	@Test
	public void serializeObjectStream() throws Exception
	{
		Originators ori = new Originators("rl@bos-bremen.de", "Lindemann, Ralf");
		Destinations dest = new Destinations("ja@test.com", "Joerg, Apitzsch");
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(message);
		oos.close();

		byte[] messageByte = out.toByteArray();
		InputStream is = new ByteArrayInputStream(messageByte);
		ObjectInputStream ois = new ObjectInputStream(is);
		DispatchMessage readMsg = (DispatchMessage) ois.readObject();

		assertEquals(GeneralMessage.getAttributedElectronicAdress(readMsg.getXSDObject().getMsgMetaData().getOriginators()
				.getFrom()).getValue(),
			"rl@bos-bremen.de");

		assertEquals(GeneralMessage.getAttributedElectronicAdress(readMsg.getXSDObject().getMsgMetaData().getDestinations()
				.getRecipient()).getValue(),
			"ja@test.com");

		assertEquals(readMsg.getXSDObject().getMsgMetaData()
				.getMsgIdentification().getMessageID(), "initMsgId");
	}

	@Test
	public void serialize() throws Exception
	{
		Originators ori = new Originators("rl@bos-bremen.de", "Lindemann, Ralf");
		Destinations dest = new Destinations("ja@test.com", "Joerg, Apitzsch");
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		message.serialize(out);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		DispatchMessage readMsg = new DispatchMessage(in);

		assertEquals(GeneralMessage.getAttributedElectronicAdress(readMsg.getXSDObject().getMsgMetaData().getOriginators()
				.getFrom()).getValue(),
			"rl@bos-bremen.de");

		assertEquals(GeneralMessage.getAttributedElectronicAdress(readMsg.getXSDObject().getMsgMetaData().getDestinations()
				.getRecipient()).getValue(),
			"ja@test.com");

		assertEquals(readMsg.getXSDObject().getMsgMetaData()
				.getMsgIdentification().getMessageID(), "initMsgId");
	}

}
