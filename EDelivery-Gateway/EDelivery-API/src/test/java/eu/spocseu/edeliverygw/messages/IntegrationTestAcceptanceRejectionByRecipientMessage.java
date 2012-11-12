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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.AcceptanceRejectionByRecipient;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;

public class IntegrationTestAcceptanceRejectionByRecipientMessage
{

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void sendAcceptanceByRecipientMessage() throws Exception
	{
		DispatchMessage simulatedMessage = IntegrationTestCommon
				.createDispatchMessage();
		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), simulatedMessage, true);
		DeliveryNonDeliveryToRecipient delNon = new DeliveryNonDeliveryToRecipient(
			Configuration.getConfiguration(), subEvidence);

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		AcceptanceRejectionByRecipientMessage evidence = new AcceptanceRejectionByRecipientMessage(
			new AcceptanceRejectionByRecipient(
				Configuration.getConfiguration(), delNon), samlProperties);
		assertTrue(evidence.isSuccessfull());
		evidence.sendEvidenceMessage();
	}

	@Test
	public void sendAcceptanceByRecipientMessageFalse() throws Exception
	{
		DispatchMessage simulatedMessage = IntegrationTestCommon
				.createDispatchMessage();
		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), simulatedMessage, true);
		DeliveryNonDeliveryToRecipient delNon = new DeliveryNonDeliveryToRecipient(
			Configuration.getConfiguration(), subEvidence, true);

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Wilko");
		samlProperties.setSurname("Oley");
		AcceptanceRejectionByRecipientMessage emsg = new AcceptanceRejectionByRecipientMessage(
			new AcceptanceRejectionByRecipient(
				Configuration.getConfiguration(), delNon, false),
			samlProperties);
		assertFalse(emsg.isSuccessfull());
		emsg.sendEvidenceMessage();
	}

}
