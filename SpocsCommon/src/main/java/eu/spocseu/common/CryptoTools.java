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
package eu.spocseu.common;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Random;

/**
 * 
 * @author R. Lindemann
 */
public class CryptoTools
{

	private static CertificateFactory factory = null;

	private static Random random = new Random();

	/**
	 * @param stream
	 *            The stream to load as key store.
	 * @param password
	 *            The password for the key store.
	 * @return The loaded key store.
	 * @throws GeneralSecurityException
	 *             In case of parsing errors.
	 * @throws IOException
	 *             In case of unexpected streams.
	 */
	public static KeyStore loadPKCS12(InputStream stream, char[] password)
			throws GeneralSecurityException, IOException
	{
		if (stream == null) {
			throw new IllegalArgumentException("No inputstream present");
		}
		KeyStore store = KeyStore.getInstance("PKCS12");
		store.load(stream, password);
		return store;
	}

	/**
	 * @param stream
	 *            The stream to load as key store.
	 * @param password
	 *            The password for the key store.
	 * @return The loaded key store.
	 * @throws GeneralSecurityException
	 *             In case of parsing errors.
	 * @throws IOException
	 *             In case of unexpected streams.
	 */
	public static KeyStore loadKeyStore(InputStream stream, char[] password,
			String keystoreType) throws GeneralSecurityException, IOException
	{
		if (stream == null) {
			throw new IllegalArgumentException("No inputstream present");
		}
		KeyStore store = KeyStore.getInstance(keystoreType);
		store.load(stream, password);
		return store;
	}

	/**
	 * Loads an X509Certificate object out of a given InputStream.
	 * 
	 * @param certificate
	 *            The certificate stream.
	 * @return The created X509Certificate object.
	 * @throws CertificateException
	 *             In case of parsing errors.
	 */
	public static X509Certificate loadCertificate(InputStream certificate)
			throws CertificateException
	{
		if (certificate == null) {
			throw new IllegalArgumentException("No inputstream present");
		}

		if (factory == null) {
			factory = CertificateFactory.getInstance("X.509");
		}
		return (X509Certificate) factory.generateCertificate(certificate);
	}

	/**
	 * @param store
	 *            The key store to use.
	 * @return The first X509Certificate out of the given key store.
	 * @throws KeyStoreException
	 *             In case of key store exceptions.
	 */
	public static X509Certificate getX509Certificate(KeyStore store)
			throws KeyStoreException
	{
		Enumeration<String> aliases = store.aliases();
		if (!aliases.hasMoreElements()) {
			throw new IllegalArgumentException("No alias present");
		}
		return (X509Certificate) store.getCertificate(aliases.nextElement());
	}

	/**
	 * @param store
	 *            A key store to look for the private key.
	 * @param password
	 *            The password to use for key store.
	 * @return The excluded private key.
	 * @throws GeneralSecurityException
	 * @throws UnrecoverableKeyException
	 */
	public static PrivateKey getPrivateKey(KeyStore store, char[] password)
			throws GeneralSecurityException, UnrecoverableKeyException
	{
		Enumeration<String> aliases = store.aliases();
		if (!aliases.hasMoreElements()) {
			throw new IllegalArgumentException("No alias present.");
		}
		return (PrivateKey) store.getKey(aliases.nextElement(), password);
	}

	/**
	 * Creates a simple random value.
	 */
	public static int createRandom()
	{
		return random.nextInt();
	}

}
