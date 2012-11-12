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
package eu.spocseu.edeliverygw.transport;

import java.security.AccessController;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.protocol.https.DefaultHostnameVerifier;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.tsl.TrustedServiceImpl;

/**
 * This class implements a X509TrustManager which checks the Server Certificate
 * against the TSL.
 * 
 * @author oley
 */

@SuppressWarnings("restriction")
public class HTTPSTransport implements X509TrustManager
{

	private TrustedServiceImpl tslData;
	private static Logger LOG = LoggerFactory.getLogger(HTTPSTransport.class);
	private X509TrustManager defaultX509TrustManager;

	public HTTPSTransport(TrustedServiceImpl tslData)
		throws NoSuchAlgorithmException, KeyStoreException
	{
		this.tslData = tslData;
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init((KeyStore)null);
		//trustManagerFactory.init(KeyStore.getInstance(KeyStore.getDefaultType()));

		for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
			if (trustManager instanceof X509TrustManager) {
				defaultX509TrustManager = (X509TrustManager) trustManager;
			}
		}
	}

	public synchronized SSLSocketFactory createSSLFactory()
			throws SpocsSystemInstallationException,
			SpocsConfigurationException
	{
		TrustManager[] trustManager = new TrustManager[] { this };
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException ex) {
			throw new SpocsSystemInstallationException(
				"Could not instanciateTLS context.", ex);
		}
		try {
			sc.init(null, trustManager, new java.security.SecureRandom());
		} catch (KeyManagementException ex) {
			throw new SpocsConfigurationException(
				"Could not set the TSL turststore", ex);
		}
		return sc.getSocketFactory();
	}

	public HostnameVerifier createHostnameVerifier()
	{
		return new spocsHostnameVerifier();
	}

	private boolean isEDelivery() {
		try {
			Subject sub = Subject.getSubject(AccessController.getContext());
			if (sub != null) {
				Set<Object> pcreds = sub.getPublicCredentials();
				synchronized (pcreds) {
					for (Object pcred : pcreds) {
						if (pcred != null && pcred.equals("eDeliveryMode")) {
							LOG.info("HTTPS Transport eDelivery mode is on");
							return true;
						}
					}
				}
			}
			LOG.info("HTTPS Transport eDelivery mode is off");
		} catch (Exception e) {
			LOG.info("HTTPS Transport eDelivery mode error: " + e, e);
		}
		return false;
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException
	{
		if (!isEDelivery()) {
			defaultX509TrustManager.checkServerTrusted(chain, authType);
		} else {

			X509Certificate sslCert = tslData.getServiceSSLCertificate();
			if (sslCert != null) {
				for (X509Certificate x509Certificate : chain) {
					if (x509Certificate != null) {
						LOG.debug(
							"SSL Handshake: comparing SSL Certificates: \n{}\n{}",
							sslCert.getSubjectDN(),
							x509Certificate.getSubjectDN());

						if (x509Certificate.equals(sslCert)) {
							LOG.debug("Server trusted!");
							return;
						}
					}
				}
			}
			StringBuffer sb = new StringBuffer();
			for (X509Certificate cert : chain) {
				if (cert != null) sb.append('\n').append(cert.getSubjectDN());
			}
			LOG.error("Server not trusted! \n chain from server: {}",
				sb.toString());
			throw new CertificateException(
				"Server not trusted! Chain from server: " + sb.toString());
		}
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException
	{
		defaultX509TrustManager.checkClientTrusted(chain, authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
		return defaultX509TrustManager.getAcceptedIssuers();
	}

	private class spocsHostnameVerifier implements
			javax.net.ssl.HostnameVerifier
	{
		@Override
		public boolean verify(String hostname, SSLSession session)
		{
			System.err.println("Hostname " + hostname);
			DefaultHostnameVerifier verifier = new DefaultHostnameVerifier();
			if (!hostname.equals("localhost")) {
				verifier.verify(hostname, session);
			} else {
				return true;
			}
			return false;
		}
	}
}
