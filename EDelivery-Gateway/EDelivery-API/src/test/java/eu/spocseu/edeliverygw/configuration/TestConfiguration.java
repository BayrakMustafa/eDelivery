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
package eu.spocseu.edeliverygw.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.SpocsConstants.COUNTRY_CODES;
import eu.spocseu.common.configuration.TSLDetails;

public class TestConfiguration
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory
			.getLogger(TestConfiguration.class);

	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}

	@Test
	public void testLoadWithGivenFile() throws Exception
	{
		InputStream in = new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml"));
		assertNotNull("InputStream is null", in);
		Configuration.setInputDataXML(in);
		assertNotNull(Configuration.getConfiguration());
	}

	// <spocs:SpocsConfiguration
	// xmlns:spocs="http://uri.eu-spocs.eu/configuration/common"
	// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	// xsi:schemaLocation="http://uri.eu-spocs.eu/configuration/common
	// D:\ws_spocs\SpocsCommon\src\main\resources\spocsCommon.xsd">
	// <spocs:Common MemberstateNation="DE" />
	// <spocs:TSL />
	// <spocs:Routing />
	// <spocs:eDelivery>
	// <eDel:EDeliveryDetail
	// xmlns:eDel="http://uri.eu-spocs.eu/configuration/eDelivery"
	// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	// xsi:schemaLocation="http://uri.eu-spocs.eu/configuration/eDelivery
	// D:\ws_spocs\EDelivery\EDelivery-API\src\main\resources\eDeliveryDetails.xsd">
	// <eDel:Server gatewayName="MD_Germany" gatewayAddress="http://testserver"
	// synchronGW="false"
	// gwImplClass="eu.spocseu.edeliverygw.process.DefaultImpl" />
	// <eDel:SignatureTrustStore type="PKCS12"
	// pin="123456">test_osci-manager_signature.p12</eDel:SignatureTrustStore>
	// <eDel:SSLTrustStore type="PKCS12"
	// pin="4307">sslTrustStore.p12</eDel:SSLTrustStore>
	// </eDel:EDeliveryDetail>
	// </spocs:eDelivery>
	// <spocs:Extention />
	// </spocs:SpocsConfiguration>

	@Test
	public void testCheckTheValues() throws Exception
	{
		Configuration conf = Configuration.getConfiguration();
		assertNotNull("Configuration is null", conf);

		assertNotNull("eDelivery config is null", conf.geteDeliveryDetails());
		EDeliveryDetails eDel = conf.geteDeliveryDetails();
		assertEquals("Country is wrong", new File("src/test/resources"),
			conf.getResourcesDir());
		assertEquals("Country is wrong", COUNTRY_CODES.GERMANY,
			conf.getCountry());
		assertEquals("Wrong gateway name", "MD_Germany", eDel.getGatewayName());
		assertEquals("gateway address", "gateway@localhost-spocs-edelivery.eu",
			eDel.getGatewayAddress());
		assertNotNull("Impl is null", eDel.getGwImplementation());
		assertNotNull("Impl class is null", eDel.getGwImplementation()
				.getClass());
		assertEquals("wrong impl name",
			"eu.spocseu.edeliverygw.process.DefaultImpl", eDel
					.getGwImplementation().getClass().getName());
		assertNotNull("Certificate is null", conf.getSignatureCertificate());
		assertNotNull("Signature private key is null", conf.getSignatureKey());

	}

	@Test
	public void testTSLCheckTheValues() throws Exception
	{
		Configuration conf = Configuration.getConfiguration();
		assertNotNull("Configuration is null", conf);

		assertNotNull("TSL config is null", conf.getTslDetails());
		TSLDetails tslDeatils = conf.getTslDetails();
		assertEquals("Country is wrong", new File("src/test/resources"),
			conf.getResourcesDir());
		assertEquals("Country is wrong", COUNTRY_CODES.GERMANY,
			conf.getCountry());
		assertEquals("Wrong gateway name", "SPOCS_TSL_00008.xml", tslDeatils
				.getTSLProperties().get("TSLSource"));
		// assertEquals("gateway address", "http://testserver",
		// tslDeatils.getGatewayAddress());
		// assertEquals("wrong impl name",
		// "eu.spocseu.edeliverygw.process.DefaultImpl",
		// tslDeatils.getGwImplementation().getClass().getName());
		// assertNotNull("Certificate is null",
		// tslDeatils.getSignatureCertificate());
		// assertNotNull("Signature private key is null",
		// tslDeatils.getSignatureKey());

	}

}
