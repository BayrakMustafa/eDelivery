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

import java.util.Properties;

import eu.spocseu.common.configuration.AbstractSpocsConfiguration;

public class TSLDetails
{

	Properties tslProperties;

	public TSLDetails(
			eu.spocseu.tsl.configuration.xsd.TSLDetails jaxBObject,
			AbstractSpocsConfiguration commonConfig)
	{

		tslProperties = new Properties();
		ifNotNullAddProperty("TSLSource", jaxBObject.getTSLSourceList()
				.getTSLSource());
		ifNotNullAddProperty("CertificateRepository",
			jaxBObject.getCertificateRepository());
		ifNotNullAddProperty("SchemeSignatureVerification",
			jaxBObject.getSchemeSignatureVerification());
		ifNotNullAddProperty("SchemeCertificateVerificationType",
			jaxBObject.getSchemeCertificateVerificationType());
		ifNotNullAddProperty("ServiceCertificateVerificationType",
			jaxBObject.getServiceCertificateVerificationType());
		ifNotNullAddProperty("FollowTSLPointer",
			jaxBObject.getFollowTSLPointer());
		ifNotNullAddProperty("ForceCacheUpdate",
			jaxBObject.getForceCacheUpdate());
		ifNotNullAddProperty("TCPConnectionTimeout",
			jaxBObject.getTCPConnectionTimeout());
		ifNotNullAddProperty("UseCRLCache", jaxBObject.getUseCRLCache());

		if (commonConfig.getProxy() != null) {
			ifNotNullAddProperty("UseProxy", commonConfig.getProxy()
					.getUseProxy());
			ifNotNullAddProperty("ProxyHost", commonConfig.getProxy().getHost());
			ifNotNullAddProperty("ProxyPort", commonConfig.getProxy().getPort());
			ifNotNullAddProperty("ProxyUser", commonConfig.getProxy().getUser());
			ifNotNullAddProperty("ProxyPassword", commonConfig.getProxy()
					.getPassword());
		}

	}

	private void ifNotNullAddProperty(String key, String value)
	{
		if (value != null) {
			tslProperties.put(key, value);
		}
	}

	public Properties getTSLProperties()
	{
		return tslProperties;
	}

	public void setTslProperties(Properties tslProperties)
	{
		this.tslProperties = tslProperties;
	}
}
