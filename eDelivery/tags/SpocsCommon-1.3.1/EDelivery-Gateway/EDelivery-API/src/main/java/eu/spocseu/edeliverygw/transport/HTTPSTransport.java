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
