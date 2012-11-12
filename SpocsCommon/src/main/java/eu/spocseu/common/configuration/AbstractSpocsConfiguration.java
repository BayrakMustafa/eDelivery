package eu.spocseu.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.CryptoTools;
import eu.spocseu.common.SpocsConstants;
import eu.spocseu.common.SpocsConstants.COUNTRY_CODES;
import eu.spocseu.common.configuration.xsd.SpocsConfiguration;
import eu.spocseu.common.configuration.xsd.SpocsConfiguration.Common.Proxy;

public abstract class AbstractSpocsConfiguration
{
	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractSpocsConfiguration.class);
	private static InputStream inputXMLFile;
	private COUNTRY_CODES country;
	SpocsConfiguration jaxBObject;

	private char[] signatureStorePassword;
	// private KeyStore trustStore;
	private KeyStore signatureStore;
	private File configFile;
	private File resourcesDir;

	Proxy proxy;

	protected AbstractSpocsConfiguration() throws SpocsConfigurationException
	{
		try {
			if (inputXMLFile != null) {
				jaxBObject = readConfigXML(inputXMLFile);
			} else {
				configFile = getConfigFile();
				jaxBObject = readConfigXML(new FileInputStream(configFile));
			}
			parseConfigXML();
		} catch (IOException ex) {
			throw new SpocsConfigurationException(
				"Error instanciate the abstract common configuration. ", ex);
		}
	}

	protected AbstractSpocsConfiguration(InputStream _in)
		throws SpocsConfigurationException
	{
		try {
			if (_in != null)
				jaxBObject = readConfigXML(_in);
			else
				jaxBObject = readConfigXML(new FileInputStream(getConfigFile()));
			parseConfigXML();
		} catch (IOException ex) {
			throw new SpocsConfigurationException(
				"Error instanciate the abstract common configuration. ", ex);
		}
	}

	public InputStream getInputstream(String resource) throws IOException
	{
		File file = new File(resourcesDir, resource);
		try {
			if (file.exists())
				return new FileInputStream(file);
			else
				LOG.debug("Try to load from classpath. Resource: " + resource);
		} catch (FileNotFoundException ex) {
			LOG.error("could not find the stream for resource: " + resource);
		}
		InputStream in = AbstractSpocsConfiguration.class
				.getResourceAsStream("/" + resource);
		if (in == null) {
			throw new IOException("No Inputstream present for resource: "
					+ resource + " and no file found: " + file);
		}
		return in;
	}

	/**
	 * Set the InputStream of the configuration that contains the xml
	 * configuration for the eDelivery project including tsl, common and
	 * eDelivery values
	 * 
	 * @param _in
	 */
	public static void setInputDataXML(InputStream _in)
	{
		inputXMLFile = _in;

	}

	private File getConfigFile() throws IOException
	{

		File xmlDir = null;
		if (configFile != null) {
			return configFile;
		}
		// second try custom property
		else if (System.getProperty("spocs.base") != null) {
			xmlDir = new File(System.getProperty("spocs.base"));
			configFile = new File(xmlDir, "spocsConfig.xml");
			if (!configFile.isFile())
				throw new IOException("no config file found in directory: "
						+ configFile);
		}
		// third try tomcat
		else if (System.getProperty("catalina.base") != null) {
			xmlDir = new File(System.getProperty("catalina.base")
					+ "/conf/spocs");
			configFile = new File(xmlDir, "spocsConfig.xml");
			if (!configFile.isFile())
				throw new IOException("no config file found in directory: "
						+ configFile);
		}
		// 4. try jboss
		else if (System.getProperty("jboss.server.config.url") != null) {
			xmlDir = new File(new URL(
				System.getProperty("jboss.server.config.url")).getPath()
					+ "/spocs");
			configFile = new File(xmlDir, "spocsConfig.xml");
			if (!configFile.isFile())
				throw new IOException("no config file found in directory: "
						+ configFile);
		}
		resourcesDir = xmlDir;
		return configFile;

	}

	protected abstract SpocsConfiguration readConfigXML(InputStream in)
			throws SpocsConfigurationException;

	protected void parseConfigXML() throws SpocsConfigurationException
	{
		try {

			proxy = jaxBObject.getCommon().getProxy();
			jaxBObject.getEDelivery();
			country = SpocsConstants.COUNTRY_CODES.getCountryCode(jaxBObject
					.getCommon().getMemberstateNation());
			if (jaxBObject.getCommon().getResourcesDir() != null)
				resourcesDir = new File(jaxBObject.getCommon()
						.getResourcesDir());
			// // ########### signature trust store ########
			if (jaxBObject.getCommon().getSignature() != null) {			
				InputStream trustIn = getInputstream(jaxBObject.getCommon()
						.getSignature().getValue());
				
				if(jaxBObject.getCommon().getSignature()
						.getPin() == null){
					throw new SpocsConfigurationException("please provide a pin for the Signature Element in the config.xml!");
				}
				if(jaxBObject
						.getCommon().getSignature().getType() == null){
					throw new SpocsConfigurationException("please provide a type for the Signature Element in the config.xml!");
				}
				
				char[] password = jaxBObject.getCommon().getSignature()
						.getPin().toCharArray();
				setSignatureTrustStore(
					CryptoTools.loadKeyStore(trustIn, password, jaxBObject
							.getCommon().getSignature().getType()), password);
			} else				
				LOG.info("no signature store store declared");
		} catch (IOException ex) {
			throw new SpocsConfigurationException(
				"Error instanciate the abstract common configuration. ", ex);
		} catch (GeneralSecurityException ex) {
			throw new SpocsConfigurationException(
				"Error instanciate the abstract common configuration. ", ex);
		}

	}

	public COUNTRY_CODES getCountry()
	{
		return country;
	}

	public void setCountry(COUNTRY_CODES country)
	{
		this.country = country;
	}

	public File getResourcesDir()
	{
		return resourcesDir;
	}

	public void setResourcesDir(File _resourcesDir)
	{
		resourcesDir = _resourcesDir;
	}

	public void setConfigDir(File configDir)
	{
		this.resourcesDir = configDir;
	}

	public Proxy getProxy()
	{
		return proxy;
	}

	public void setProxy(Proxy proxy)
	{
		this.proxy = proxy;
	}

	public SpocsConfiguration getJaxBObject()
	{
		return jaxBObject;
	}

	public void setJaxBObject(SpocsConfiguration jaxBObject)
	{
		this.jaxBObject = jaxBObject;
	}

	public PrivateKey getSignatureKey() throws SpocsConfigurationException
	{
		try {
			return CryptoTools.getPrivateKey(signatureStore,
				signatureStorePassword);
		} catch (GeneralSecurityException ex) {
			throw new SpocsConfigurationException(
				"Could not get the signature private key", ex);
		}

	}

	public X509Certificate getSignatureCertificate()
			throws SpocsConfigurationException
	{
		try {
			return CryptoTools.getX509Certificate(signatureStore);
		} catch (GeneralSecurityException ex) {
			throw new SpocsConfigurationException(
				"Could not get the signature certificate", ex);
		}
	}

	public void setSignatureTrustStore(KeyStore _signatureTruststore,
			char[] _password)
	{
		this.signatureStore = _signatureTruststore;
		this.signatureStorePassword = _password;
	}

}
