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
package eu.spocseu.common.metro;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import com.sun.xml.wss.XWSSecurityException;

import eu.spocseu.common.configuration.SpocsCommonConfiguration;
import eu.spocseu.common.configuration.SpocsConfigurationException;

public class SpocsCallbackHandler extends AbstractSpocsCallbackHandler
{

	public SpocsCallbackHandler() throws XWSSecurityException
	{

	}

	/**
	 * Gives the transport signature certificate
	 * 
	 * @return the transport signature certificate
	 * @throws IOException
	 */
	@Override
	protected X509Certificate getTransportSignatureCertificate()
			throws SpocsConfigurationException
	{
		return SpocsCommonConfiguration.getConfiguration()
				.getSignatureCertificate();

	}

	/**
	 * 
	 * Gives the transport signature key
	 * 
	 * @return the transport signature key
	 */
	@Override
	protected PrivateKey getTransportSignatureKey()
			throws SpocsConfigurationException
	{
		return SpocsCommonConfiguration.getConfiguration().getSignatureKey();
	}
}
