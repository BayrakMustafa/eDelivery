package eu.spocseu.edeliverygw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/*
 * -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ ModuleTestSuite.class, IntegrationTestSuite.class })
public class AllTestSuite
{

}
