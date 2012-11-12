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
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;

public class TestSubmissionARError
{
	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}
	
	@Test	
	public void testSubmissionAcceptranceRehjectionError() throws SpocsConfigurationException, IOException, GeneralSecurityException, DatatypeConfigurationException, JAXBException {
		GeneralMessage message = new GeneralMessage() {
			
			@Override
			public void serialize(OutputStream out)
			{				
			}
		};
		SubmissionAcceptanceRejection subError = message.new SubmissionAcceptanceRejectionError(Configuration.getConfiguration(), new EvidenceMessage());
		if(!subError.getXSDObject().getEventCode().equals((Evidences.SUBMISSION_ACCEPTANCE_REJECTION
					.getFaultEventCode()))){
			fail();
		}
	}
}
