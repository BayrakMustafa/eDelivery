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
