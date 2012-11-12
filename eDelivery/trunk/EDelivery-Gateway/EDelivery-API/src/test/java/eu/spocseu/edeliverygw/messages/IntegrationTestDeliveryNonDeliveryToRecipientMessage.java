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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;

public class IntegrationTestDeliveryNonDeliveryToRecipientMessage
{

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void sendDeliveryToRecipient() throws Exception
	{
		DispatchMessage dispatchMessage = IntegrationTestCommon
				.createDispatchMessage();
		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), dispatchMessage, true);
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		DeliveryNonDeliveryToRecipientMessage message = new DeliveryNonDeliveryToRecipientMessage(
			new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), subEvidence), samlProperties);
		message.sendEvidenceMessage();
	}

	@Test
	public void sendNonDeliveryToRecipient() throws Exception
	{
		DispatchMessage dispatchMessage = IntegrationTestCommon
				.createDispatchMessage();
		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), dispatchMessage, false);
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Ralf");
		samlProperties.setSurname("Lindemann");
		DeliveryNonDeliveryToRecipientMessage message = new DeliveryNonDeliveryToRecipientMessage(
			new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), subEvidence, false),
			samlProperties);
		message.sendEvidenceMessage();

	}

}
