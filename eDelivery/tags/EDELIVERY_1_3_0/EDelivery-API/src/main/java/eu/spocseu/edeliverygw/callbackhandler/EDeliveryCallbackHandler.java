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
$Date: 2010-05-13 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */
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