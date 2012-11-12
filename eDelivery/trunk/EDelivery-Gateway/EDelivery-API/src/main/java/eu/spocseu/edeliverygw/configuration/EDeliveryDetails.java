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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import eu.spocseu.edeliverygw.process.SpocsGWInterface;

/**
 * This Class represents the holds the <code>Configuration</code> of the
 * eDelivery Project.
 * 
 * @author oley
 */
public class EDeliveryDetails
{

	private static final Logger LOG = LoggerFactory
			.getLogger(EDeliveryDetails.class);
	private String gatewayName;

	private String streetAdress;
	private String locality;
	private String postalCode;
	private String country;
	
	private boolean checkSignature = false;
	private boolean checkMessage = false;
	
	private String gatewayAddress;
	private String gatewayDomain;
	private int defaultCitizenQAAlevel = 1;
	private SpocsGWInterface gwImplementation;

	private boolean synchronGatewayMD = true;
	private static Properties spocsProperties;

	public EDeliveryDetails(EDeliveryDetail jaxBObject, Configuration config)
	{
		setGatewayAddress(jaxBObject.getServer().getGatewayAddress());
		setGatewayName(jaxBObject.getServer().getGatewayName());
		setSynchronGatewayMD(jaxBObject.getServer().isSynchronGW());
		setCheckMessage(jaxBObject.getServer().isMessageValidation());
		// PostalAdress
		if (jaxBObject.getPostalAdress() != null) {
			setStreetAdress(jaxBObject.getPostalAdress().getStreetAddress());
			setLocality(jaxBObject.getPostalAdress().getLocality());
			setPostalCode(jaxBObject.getPostalAdress().getPostalCode());
			setCountry(jaxBObject.getPostalAdress().getCountry());
		}
		setCheckSignature(jaxBObject.getServer().isSignatureValidation());
		if (jaxBObject.getServer().getGatewayDomain() != null)
			setGatewayDomain(jaxBObject.getServer().getGatewayDomain());
		// gw impl class
		String className = jaxBObject.getServer().getGwImplClass();
		if (className == null)
			LOG.error("Please define a property with the key 'gwImpl.class'");
		else
			try {
				setGwImplementation((SpocsGWInterface) Class.forName(className)
						.newInstance());
			} catch (Exception e) {
				LOG.error("No class found for: " + className);
			}
		jaxBObject.getServer().getDefaultCitizenQAAlevel();
		if (jaxBObject.getServer().getDefaultCitizenQAAlevel() != null)
			setDefaultCitizenQAAlevel(jaxBObject.getServer()
					.getDefaultCitizenQAAlevel());
		// // // ########### signature trust store ########
		// if (jaxBObject.getSignatureTrustStore() != null) {
		// InputStream trustIn = config.getInputstream(jaxBObject
		// .getSignatureTrustStore().getValue());
		// char[] password = jaxBObject.getSignatureTrustStore().getPin()
		// .toCharArray();
		// setSignatureTrustStore(CryptoTools.loadKeyStore(
		// trustIn, password, jaxBObject.getSignatureTrustStore().getType()),
		// password);
		// } else
		// LOG.info("no signature store store declared");
		// ########### Set the trust store for the signature certificate check
		// if (spocsProperties.getProperty("trustStore") != null) {
		//
		// InputStream trustIn = getInputstream(getConfigDir(),
		// spocsProperties.getProperty("trustStore"));
		// eDeliveryDetails.settrustStorePassword = spocsProperties.getProperty(
		// "trustStorePassword").toCharArray();
		// trustStore = CryptoTools.loadKeyStore(trustIn, trustStorePassword,
		// getKeyStoreType(spocsProperties.getProperty("trustStore")));
		// Enumeration<String> en = trustStore.aliases();
		// while (en.hasMoreElements()) {
		// Certificate[] certs = trustStore.getCertificateChain(en
		// .nextElement());
		// if (certs != null) {
		// for (Certificate certificate : certs) {
		// X509Certificate cert = (X509Certificate) certificate;
		// trustedCertificates.add(cert);
		// }
		// }
		// }
		// } else
		// LOG.info("no trust store declared");
		// ########### signature trust store ########
		// if (spocsProperties.getProperty("signatureTrustStore") != null) {
		// InputStream trustIn = getInputstream(getConfigDir(),
		// spocsProperties.getProperty("signatureTrustStore"));
		// eDeliveryDetails.signatureStorePassword = spocsProperties
		// .getProperty("signatureTrustPassword").toCharArray();
		// signatureStore = CryptoTools.loadKeyStore(trustIn,
		// signatureStorePassword, getKeyStoreType(spocsProperties
		// .getProperty("signatureTrustStore")));
		// } else
		// LOG.info("no signature store store declared");
		//
		// // SSL Keystore
		// if (spocsProperties.getProperty("sslTrustStore") != null) {
		// char[] password = spocsProperties.getProperty(
		// "sslTrustStorePassword").toCharArray();
		//
		// KeyStore store = KeyStore
		// .getInstance(getKeyStoreType(spocsProperties
		// .getProperty("sslTrustStore")));
		// store.load(
		// getInputstream(getConfigDir(),
		// spocsProperties.getProperty("sslTrustStore")),
		// password);
		// sslTrustStore = store;
		//
		// } else
		// LOG.info("no SSL Trust store declared!");

		// if (spocsProperties.getProperty("tslPropertiesFile") != null) {
		// try {
		// tslProperties.loadFromXML(getInputstream(configDir,
		// spocsProperties.getProperty("tslPropertiesFile")));
		// } catch (IOException e) {
		// throw new FileNotFoundException(
		// "TSL Properties File not found!");
		// }
		// } else
		// LOG.info("no path to TSL Properties File declared");

		// setGatewayAddress(spocsProperties.getProperty("gatewayAddress",
		// "NoGatewayAddressSet"));
		// setGatewayName(spocsProperties.getProperty("gatewayName",
		// "NoGatewayNameSet"));
		// setSynchronGatewayMD(Boolean.valueOf(spocsProperties.getProperty(
		// "SynchronGatewayMD", "true")));
		// setDefaultCitizenQAAlevel(Integer.valueOf(spocsProperties.getProperty(
		// "DefaultCitizenQAAlevel", "1")));
		// setSynchronGatewayMD(Boolean.getBoolean(spocsProperties.getProperty(
		// "IsSynchronGateway", "true")));
		// setCountry(COUNTRY_CODES.getCountryCode(spocsProperties.getProperty(
		// "GWCountry", "DE")));
	}

	public SpocsGWInterface getGwImplementation()
	{
		return gwImplementation;
	}

	public void setGwImplementation(SpocsGWInterface gwImplementation)
	{
		this.gwImplementation = gwImplementation;
	}

	public String getKeyStoreType(String keystore)
	{
		if (keystore.toLowerCase().endsWith(".jks")) {
			return "JKS";
		} else
			return "PKCS12";

	}

	public static Properties getSpocsProperties()
	{
		return spocsProperties;
	}

	public String getGatewayName()
	{
		return gatewayName;
	}

	public void setGatewayName(String gatewayName)
	{
		this.gatewayName = gatewayName;
	}

	public String getGatewayAddress()
	{
		return gatewayAddress;
	}

	public void setGatewayAddress(String gatewayAddress)
	{
		this.gatewayAddress = gatewayAddress;
	}

	public boolean isSynchronGatewayMD()
	{
		return synchronGatewayMD;
	}

	public void setSynchronGatewayMD(boolean synchronGatewayMD)
	{
		this.synchronGatewayMD = synchronGatewayMD;
	}

	public int getDefaultCitizenQAAlevel()
	{
		return defaultCitizenQAAlevel;
	}

	public void setDefaultCitizenQAAlevel(int _defaultCitizenQAAlevel)
	{
		this.defaultCitizenQAAlevel = _defaultCitizenQAAlevel;
	}

	public String getGatewayDomain()
	{
		return gatewayDomain;
	}

	public void setGatewayDomain(String gatewayDomain)
	{
		this.gatewayDomain = gatewayDomain;
	}

	public String getStreetAdress()
	{
		return streetAdress;
	}

	public void setStreetAdress(String streetAdress)
	{
		this.streetAdress = streetAdress;
	}

	public String getLocality()
	{
		return locality;
	}

	public void setLocality(String locality)
	{
		this.locality = locality;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}


	public boolean isCheckSignature()
	{
		return checkSignature;
	}

	public void setCheckSignature(boolean checkSignature)
	{
		this.checkSignature = checkSignature;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public boolean isCheckMessage()
	{
		return checkMessage;
	}

	public void setCheckMessage(boolean checkMessage)
	{
		this.checkMessage = checkMessage;
	}
}
