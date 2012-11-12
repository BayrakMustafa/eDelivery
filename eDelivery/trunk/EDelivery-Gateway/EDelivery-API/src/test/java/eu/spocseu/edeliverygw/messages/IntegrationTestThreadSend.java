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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Originators;

public class IntegrationTestThreadSend
{
	private static Logger LOG = LoggerFactory
			.getLogger(IntegrationTestDispatchMessage.class.getName());

	final static String RECIPIENT = "ja@localhost-spocs-edelivery.eu";
	
	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}
	
	@Test
	public void testThreadedSend() throws Exception
	{
		LOG.info("starting sending");
		Originators ori = new Originators("threadTest@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");
		Destinations dest = new Destinations("psc@localhost-spocs-edelivery.eu", "Thread Test");
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		DispatchMessage message = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori, dest, new MsgIdentification("initMsgId"),
			samlProperties);
		message.setContent(IntegrationTestCommon.createNormalized(), IntegrationTestCommon.createOriginal());



		LOG.info("starting sending");
		Originators ori2 = new Originators("rl@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");
		Destinations dest2 = new Destinations("test@localhost-spocs-edelivery.eu", "Thread Test");
		SAMLProperties samlProperties2 = new SAMLProperties();
		samlProperties2.setCitizenQAAlevel(1);
		samlProperties2.setGivenName("Ralf");
		samlProperties2.setSurname("Lindemann");
		DispatchMessage message2 = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori2, dest2, new MsgIdentification("initMsgId"),
			samlProperties2);
		message2.setContent(IntegrationTestCommon.createNormalized(), IntegrationTestCommon.createOriginal());
		
		LOG.info("starting sending");
		Originators ori3 = new Originators("rl@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");
		Destinations dest3 = new Destinations("test@localhost-spocs-edelivery.eu", "Thread Test");
		SAMLProperties samlProperties3 = new SAMLProperties();
		samlProperties3.setCitizenQAAlevel(1);
		samlProperties3.setGivenName("Ralf");
		samlProperties3.setSurname("Lindemann");
		DispatchMessage message3 = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori3, dest3, new MsgIdentification("initMsgId"),
			samlProperties3);
		message3.setContent(IntegrationTestCommon.createNormalized(), IntegrationTestCommon.createOriginal());
		
		
		LOG.info("starting sending");
		Originators ori4 = new Originators("rl@localhost-spocs-edelivery.eu",
			"Lindemann, Ralf");
		Destinations dest4 = new Destinations("test@localhost-spocs-edelivery.eu", "Thread Test");
		SAMLProperties samlProperties4 = new SAMLProperties();
		samlProperties2.setCitizenQAAlevel(1);
		samlProperties2.setGivenName("Ralf");
		samlProperties2.setSurname("Lindemann");
		DispatchMessage message4 = new DispatchMessage(new DeliveryConstraints(
			new Date()), ori4, dest4, new MsgIdentification("initMsgId"),
			samlProperties4);
		message4.setContent(IntegrationTestCommon.createNormalized(), IntegrationTestCommon.createOriginal());

		sendThread s1= new sendThread(message);
		Thread t1 = new Thread(s1);
		t1.start();
		Thread.sleep(2500);
		sendThread s2 = new sendThread(message2);
		Thread t2 = new Thread(s2);
		t2.start();
		
		sendThread s3= new sendThread(message3);
		Thread t3 = new Thread(s3);
		t3.start();
		Thread.sleep(2500);
		sendThread s4= new sendThread(message4);
		Thread t4 = new Thread(s4);
		t4.start();

		try {
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			if(s1.error){
				LOG.error("Error Thread 1",s1.ex);
				fail(s1.ex.getMessage());
			}
			if(s2.error){
				LOG.error("Error Thread 2",s2.ex);
				fail(s2.ex.getMessage());
			}
			if(s3.error){
				LOG.error("Error Thread 3",s3.ex);
				fail(s3.ex.getMessage());
			}
			if(s4.error){
				LOG.error("Error Thread 4:",s4.ex);
				fail(s4.ex.getMessage());
			}
		} catch (InterruptedException ex) {
			LOG.error("", ex);
		}

	}

	class sendThread implements Runnable
	{
		DispatchMessage message;
		public boolean error = false;
		public Exception ex; 
		public sendThread(DispatchMessage _message)
		{
			message = _message;
		}

		@Override
		public void run()
		{
			try {
				message.sendDispatchMessage();
				LOG.info("finished sending");

			} catch (Exception e) {
				error = true;
				ex = e;
				LOG.error("Error ", e);
			}

		}
	}
}
