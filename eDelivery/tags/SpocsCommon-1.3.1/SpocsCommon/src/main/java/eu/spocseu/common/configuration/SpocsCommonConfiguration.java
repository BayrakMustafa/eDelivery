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
package eu.spocseu.common.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.configuration.xsd.ObjectFactory;
import eu.spocseu.common.configuration.xsd.SpocsConfiguration;

public class SpocsCommonConfiguration extends AbstractSpocsConfiguration
{
	private static final Logger LOG = LoggerFactory
			.getLogger(SpocsCommonConfiguration.class);
	private static SpocsCommonConfiguration config;

	public SpocsCommonConfiguration() throws SpocsConfigurationException
	{
		LOG.debug("Create a new instance of SpocsCommonConfiguration");

	}

	@Override
	protected SpocsConfiguration readConfigXML(InputStream in)
			throws SpocsConfigurationException
	{
		try {

			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			JAXBElement<SpocsConfiguration> jaxBObject = (JAXBElement<SpocsConfiguration>) context
					.createUnmarshaller().unmarshal(in);
			return jaxBObject.getValue();
		} catch (JAXBException ex) {

			throw new SpocsConfigurationException(
				"Can not parse the configuration. ", ex);
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
	public static synchronized SpocsCommonConfiguration getConfiguration()
			throws SpocsConfigurationException
	{
		if (config == null) {
			config = new SpocsCommonConfiguration();
		}
		return config;

	}
}
