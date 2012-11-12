package eu.spocseu.edeliverygw.configuration;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.tsl.TSL;
import eu.spocseu.tsl.TrustedServiceImpl;

public class TestTSL
{
	private static final Logger LOG = LoggerFactory.getLogger(TestTSL.class);

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void testSyndicationAT() throws Exception
	{

		Configuration conf = Configuration.getConfiguration();
		TSL tsl = new TSL(conf.getTSLProperties());
		TSL.init();
		LOG.info("**********NEW SEARCH*************");
		tsl.setsearchParameter_eDeliveryDomainName("OSCI.BOS-BREMEN.DE");
		// tsl.setsearchParameter_Name("SPOCSGateway@osci-bos-bremen.de");
		// tsl.setsearchParameter_Type("http://uri.spocs-eu.eu/Svctype/syndicationModule/v1");
		// tsl.setsearchParameter_Type("http://uri.spocs-eu.eu/Svctype/eDelivery/v1");
		// LOG.info("Test"
		// + tsl.getTrustedDomain("OSCI.BOS-BREMEN.DE")
		// .getServiceName());
		// tsl.setsearchParameter_eDeliveryDomainName("OSCI.BOS-BREMEN.DE");
		// tsl.setsearchParameter_CountryCode("AT");
		LOG.info("Search by type: " + tsl.getsearchParameter_Type());

		ArrayList<TrustedServiceImpl> servlist = (ArrayList<TrustedServiceImpl>) tsl
				.startParametersSearch();

		LOG.info("Found services : " + servlist.size());
		assertEquals("Wrong count of found tsl services.", 1, servlist.size());

		int i = 0;
		for (TrustedServiceImpl t : servlist) {

			LOG.info("Service n. " + ++i);
			LOG.info("Service name: " + t.getServiceName());
			LOG.info("Service type: " + t.getServiceType());
			LOG.info("Service cc: " + t.getTSLCountryCode());
			LOG.info("Service realmname: " + t.getRealmName());
			LOG.info("Service supply point: "
					+ t.getServiceSupplyPoints().toString());
			LOG.info("Service certificate subjectDN: "
					+ t.getDigitalId().getSubjectDN());
		}
		LOG.info("**********END SEARCH*************");
		LOG.info(" ");
	}

}
