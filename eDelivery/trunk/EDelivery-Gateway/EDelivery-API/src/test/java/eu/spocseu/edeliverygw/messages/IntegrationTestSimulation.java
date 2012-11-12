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
