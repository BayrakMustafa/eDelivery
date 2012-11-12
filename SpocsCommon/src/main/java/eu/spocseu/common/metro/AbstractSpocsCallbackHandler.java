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
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.x500.X500Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.callback.CertificateValidationCallback;
import com.sun.xml.wss.impl.callback.SignatureKeyCallback;
import com.sun.xml.wss.impl.misc.DefaultCallbackHandler;

import eu.spocseu.common.configuration.SpocsConfigurationException;

/**
 * 
 * @author Lindemann
 * 
 */
public abstract class AbstractSpocsCallbackHandler implements CallbackHandler
{

	protected Logger LOG = LoggerFactory
			.getLogger(AbstractSpocsCallbackHandler.class);

	protected CallbackHandler defaultCallbackHandler;

	public AbstractSpocsCallbackHandler() throws XWSSecurityException
	{
		defaultCallbackHandler = new DefaultCallbackHandler("server", null);
	}

	/**
	 * @param inpLogSuffix
	 *            to be used for logging
	 * @param inpDefaultCallbackHandlerProperties
	 *            to be used for DefaultCallbackHandler
	 * @throws XWSSecurityException
	 *             raised
	 */
	public AbstractSpocsCallbackHandler(String inpLogSuffix,
			Properties inpDefaultCallbackHandlerProperties)
		throws XWSSecurityException
	{
		Properties tmpDefaultCallbackHandlerProperties = new Properties(
			inpDefaultCallbackHandlerProperties);
		defaultCallbackHandler = new DefaultCallbackHandler("server",
			tmpDefaultCallbackHandlerProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	// @Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException
	{
		for (Callback tmpCallback : callbacks) {
			// Naehere Erlaeuterungen, was in den einzelnen callbacks zu tun
			// ist, gibt es hier:
			// http://www.j2ee.me/webservices/docs/1.6/tutorial/doc/XWS-SecurityIntro5.html#wp567203

			LOG.debug("handle() callback: " + tmpCallback);
			// Privaten Schluessel zur Entschluesselung empfangener Nachrichten
			// bereitstellen
			// if (tmpCallback instanceof DecryptionKeyCallback)
			// {
			// handleDecryptionKeyCallback((DecryptionKeyCallback)tmpCallback);
			// continue;
			// }
			//
			// // oeffentlichen Schluessel fuer Signaturpruefung bereitstellen
			// if (tmpCallback instanceof SignatureVerificationKeyCallback)
			// {
			// handleSignatureVerificationKeyCallback((SignatureVerificationKeyCallback)tmpCallback);
			// continue;
			// }
			//
			// // Privaten Schluessel zur Signierung der Nachrichten
			// bereitstellen
			if (tmpCallback instanceof SignatureKeyCallback) {
				handleSignatureKeyCallback((SignatureKeyCallback) tmpCallback);
				continue;
			}
			//
			// // oeffentlichen Schluessel zur Verschluesselung von Nachrichten
			// bereitstellen
			// if (tmpCallback instanceof EncryptionKeyCallback)
			// {
			// handleEncryptionKeyCallback((EncryptionKeyCallback)tmpCallback);
			// continue;
			// }
			//
			// // Allgemeine Zertifikatpruefung eines BinarySecurityToken in der
			// Request-Nachricht
			if (tmpCallback instanceof CertificateValidationCallback) {
				handleCertificateValidationCallback((CertificateValidationCallback) tmpCallback);
				continue;
			}
			LOG.debug("handle() callback will be delegated to the default callback handler: "
					+ tmpCallback);
			Callback[] tmpDelegateCallbacks = new Callback[] { tmpCallback };
			defaultCallbackHandler.handle(tmpDelegateCallbacks);
		}
	}

	private void handleCertificateValidationCallback(
			CertificateValidationCallback tmpCallback)
	{
		tmpCallback.setValidator(new X509CertificateValidatorImpl());

	}

	/**
	 * Gives the transport signature certificate
	 * 
	 * @return the transport signature certificate
	 * @throws IOException
	 */
	protected abstract X509Certificate getTransportSignatureCertificate()
			throws SpocsConfigurationException;

	/**
	 * 
	 * Gives the transport signature key
	 * 
	 * @return the transport signature key
	 */
	protected abstract PrivateKey getTransportSignatureKey()
			throws SpocsConfigurationException;

	private void handleSignatureKeyCallback(SignatureKeyCallback inpCallback)
			throws UnsupportedCallbackException
	{
		SignatureKeyCallback.Request tmpUjoRequest = inpCallback.getRequest();
		if (tmpUjoRequest instanceof SignatureKeyCallback.DefaultPrivKeyCertRequest) {
			try {
				SignatureKeyCallback.DefaultPrivKeyCertRequest tmpRequest = (SignatureKeyCallback.DefaultPrivKeyCertRequest) tmpUjoRequest;
				LOG.debug("handleSignatureKeyCallback() gefunden "
						+ tmpRequest.getClass().getName());
				tmpRequest.setPrivateKey(getTransportSignatureKey());
				tmpRequest
						.setX509Certificate(getTransportSignatureCertificate());
				return;
			} catch (SpocsConfigurationException ex) {
				throw new IllegalStateException(
					"Could not get the signature keys.", ex);
			}
		}

		LOG.error("handleSignatureKeyCallback() request not supported. Please implement: "
				+ tmpUjoRequest.getClass().getName());
		throw new UnsupportedCallbackException(null,
			"Request not supported. Please implement: "
					+ tmpUjoRequest.getClass().getName());
	}

	/**
	 * @param inpCertificate
	 *            from which the issuer name should be retrieved
	 * @return the issuer name of the certificate
	 */
	public static String getIssuerName(X509Certificate inpCertificate)
	{
		return inpCertificate.getIssuerX500Principal().getName(
			X500Principal.RFC2253);
	}

	// TODO Should be changed in further versions
	class X509CertificateValidatorImpl implements
			CertificateValidationCallback.CertificateValidator
	{

		public boolean validate(X509Certificate certificate)
				throws CertificateValidationCallback.CertificateValidationException
		{
			try {
				certificate.checkValidity();
			} catch (CertificateExpiredException e) {
				LOG.warn("Certificate is expired!",e);
				return true;
			} catch (CertificateNotYetValidException e) {
				LOG.warn("Certificate is not yet valid",e);
				return true;
			}
			return true;
		}
	}
}
