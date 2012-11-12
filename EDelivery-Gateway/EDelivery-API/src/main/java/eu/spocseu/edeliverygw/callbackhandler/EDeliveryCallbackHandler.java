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
package eu.spocseu.edeliverygw.callbackhandler;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.security.auth.callback.CallbackHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.wss.XWSSecurityException;

import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.common.metro.AbstractSpocsCallbackHandler;
import eu.spocseu.edeliverygw.configuration.Configuration;

/**
 * internal Class
 * 
 * @author Lindemann
 * 
 */
public class EDeliveryCallbackHandler extends AbstractSpocsCallbackHandler
{

	protected Logger LOG = LoggerFactory
			.getLogger(EDeliveryCallbackHandler.class);

	protected CallbackHandler defaultCallbackHandler;

	public EDeliveryCallbackHandler() throws XWSSecurityException
	{
	}

	/**
	 * @param inpLogSuffix
	 *            to be used for logging
	 * @param inpDefaultCallbackHandlerProperties
	 *            to be used for DefaultCallbackHandler
	 * @throws XWSSecurityException
	 *             raised
	 */
	public EDeliveryCallbackHandler(String inpLogSuffix,
			Properties inpDefaultCallbackHandlerProperties)
		throws XWSSecurityException
	{
		super(inpLogSuffix, inpDefaultCallbackHandlerProperties);
	}

	@Override
	protected X509Certificate getTransportSignatureCertificate()
			throws SpocsConfigurationException
	{
		return Configuration.getConfiguration().getSignatureCertificate();

	}

	@Override
	protected PrivateKey getTransportSignatureKey()
			throws SpocsConfigurationException
	{
		return Configuration.getConfiguration().getSignatureKey();
	}
}
