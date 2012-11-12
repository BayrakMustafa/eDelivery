package eu.spocseu.edeliverygw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eu.spocseu.edeliverygw.messages.IntegrationTestAcceptanceRejectionByRecipientMessage;
import eu.spocseu.edeliverygw.messages.IntegrationTestDeliveryNonDeliveryToRecipientMessage;
import eu.spocseu.edeliverygw.messages.IntegrationTestDispatchMessage;
import eu.spocseu.edeliverygw.messages.IntegrationTestRetrievalNonRetrievalByRecipientMessage;
import eu.spocseu.edeliverygw.messages.IntegrationTestSimulation;
import eu.spocseu.edeliverygw.messages.IntegrationTestThreadSend;

/*
 * -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true
 */
@RunWith(Suite.class)
@SuiteClasses({ IntegrationTestDeliveryNonDeliveryToRecipientMessage.class,
		IntegrationTestDispatchMessage.class,
		IntegrationTestRetrievalNonRetrievalByRecipientMessage.class,
		IntegrationTestAcceptanceRejectionByRecipientMessage.class,
		IntegrationTestSimulation.class,
		IntegrationTestThreadSend.class})
public class IntegrationTestSuite
{

}
