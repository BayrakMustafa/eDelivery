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
package eu.spocseu.edeliverygw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eu.spocseu.edeliverygw.configuration.TestConfiguration;
import eu.spocseu.edeliverygw.configuration.TestTSL;
import eu.spocseu.edeliverygw.evidences.TestEvidences;
import eu.spocseu.edeliverygw.messageparts.TestMsgIdentification;
import eu.spocseu.edeliverygw.messageparts.TestOriginators;
import eu.spocseu.edeliverygw.messageparts.TestSpocsFragments;
import eu.spocseu.edeliverygw.messages.TestDispatchMessage;
import eu.spocseu.edeliverygw.messages.TestSignature;

/*
 * -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true
 */
@RunWith(Suite.class)
@SuiteClasses({ TestConfiguration.class, TestEvidences.class,
		TestMsgIdentification.class, TestOriginators.class,
		TestSpocsFragments.class, TestDispatchMessage.class, TestTSL.class,TestSignature.class })
public class ModuleTestSuite
{
}
