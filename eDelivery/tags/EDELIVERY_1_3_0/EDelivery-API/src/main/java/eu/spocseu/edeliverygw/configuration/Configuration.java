/* ---------------------------------------------------------------------------
             COMPETITIVENESS AND INNOVATION FRAMEWORK PROGRAMME
                   ICT Policy Support Programme (ICT PSP)
           Preparing the implementation of the Services Directive
                   ICT PSP call identifier: ICT PSP-2008-2
             ICT PSP main Theme identifier: CIP-ICT-PSP.2008.1.1
                           Project acronym: SPOCS
   Project full title: Simple Procedures Online for Cross-border Services
                         Grant agreement no.: 238935
                               www.eu-spocs.eu
------------------------------------------------------------------------------
    WP3 Interoperable delivery, eSafe, secure and interoperable exchanges
                       and acknowledgement of receipt
------------------------------------------------------------------------------
        Open module implementing the eSafe document exchange protocol
------------------------------------------------------------------------------

$URL: svn:https://svnext.bos-bremen.de/SPOCS/AllWpImplementation/EDelivery-Gateway
$Date: 2010-10-14 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */
package eu.spocseu.edeliverygw.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.configuration.AbstractSpocsConfiguration;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.common.configuration.TSLDetails;
import eu.spocseu.common.configuration.xsd.ObjectFactory;
import eu.spocseu.common.configuration.xsd.SpocsConfiguration;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import eu.spocseu.tsl.TSL;
import eu.spocseu.tsl.internal.exception.TSLException;
import eu.spocseu.tsl.internal.exception.VerifyException;

/**
 * The <code>Configuration</code> load and parse the configuration of the
 * eDelivery project
 * <p>
 * 
 * @author lindemann
 */

public class Configuration extends AbstractSpocsConfiguration
{
	private static final Logger LOG = LoggerFactory
			.getLogger(Configuration.class);

	private static Configuration config;
	private static EDeliveryDetails eDeliveryDetails;
	private static TSLDetails tslDetails;
	private static InputStream inputXMLFile;

	protected Configuration() throws SpocsConfigurationException
	{
		super(inputXMLFile);
		LOG.info("Instanciate the eDelivery configuration");
		eDeliveryDetails = new EDeliveryDetails(
			(EDeliveryDetail) getJaxBObject().getEDelivery().getAny(), this);
		tslDetails = new eu.spocseu.common.configuration.TSLDetails(
			(eu.spocseu.tsl.configuration.xsd.TSLDetails) getJaxBObject()
					.getTSL().getAny(), this);
		new TSL(tslDetails.getTSLProperties());

		try {
			TSL.init();
		} catch (TSLException ex) {
			throw new SpocsConfigurationException("Error init tsl.", ex);
		} catch (VerifyException ex) {
			throw new SpocsConfigurationException("Error init tsl.", ex);
		}
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

	/**
	 * This method parse the configuration with the specified unmarshaller.
	 */
	@Override
	protected SpocsConfiguration readConfigXML(InputStream in)
			throws SpocsConfigurationException
	{
		try {

			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class,
				eu.spocseu.edeliverygw.configuration.xsd.ObjectFactory.class,
				eu.spocseu.tsl.configuration.xsd.ObjectFactory.class);

			SpocsConfiguration jaxBObject = (SpocsConfiguration) context
					.createUnmarshaller().unmarshal(in);
			return jaxBObject;
		} catch (JAXBException ex) {
			throw new SpocsConfigurationException(
				"Error reading the jaxb-object.", ex);
		}
	}

	/**
	 * Returns a instance of the configuration. Only the first time this method
	 * will be called a new instance of the configuration will be created
	 * 
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static synchronized Configuration getConfiguration()
			throws SpocsConfigurationException
	{
		if (config == null) {
			config = new Configuration();
		}
		return config;

	}

	public TSL getTSLInstance()
	{
		TSL tsl = new TSL();
		return tsl;
	}

	/**
	 * Returns the properties read out of the configuration and put into a
	 * properties object. This Properties should be used by the TSL project.
	 */
	public Properties getTSLProperties()
	{
		return tslDetails.getTSLProperties();
	}

	public TSLDetails getTslDetails()
	{
		return tslDetails;
	}

	/**
	 * If the configuraton will be created in a dynamic way, the tsl details
	 * could be set with this method
	 * 
	 * @param _EDeliveryDetails
	 */
	public static void setTSLDetails(TSLDetails _tslDetails)
	{
		tslDetails = _tslDetails;
	}

	/**
	 * Returns the details of the eDelivery configuration.
	 * 
	 * @return
	 */
	public EDeliveryDetails geteDeliveryDetails()
	{
		return eDeliveryDetails;
	}

	/**
	 * If the configuraton will be created in a dynamic way the, eDelivery
	 * details could be set with this method
	 * 
	 * @param _EDeliveryDetails
	 */
	public static void setEDeliveryDetails(EDeliveryDetails _EDeliveryDetails)
	{
		eDeliveryDetails = _EDeliveryDetails;
	}

}
