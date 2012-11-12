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
