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
package eu.spocseu.edeliverygw;

import java.security.Key;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal Class
 * 
 * @author oley
 */
class KeyValueKeySelector extends KeySelector
{
	X509Certificate certificate;

	private static Logger LOG = LoggerFactory
			.getLogger(KeyValueKeySelector.class);

	@SuppressWarnings("unchecked")
	public KeySelectorResult select(KeyInfo keyInfo,
			KeySelector.Purpose purpose, AlgorithmMethod method,
			XMLCryptoContext context) throws KeySelectorException
	{
		if (keyInfo == null) {
			throw new KeySelectorException("Null KeyInfo object!");
		}
		List<Object> list = keyInfo.getContent();

		for (int i = 0; i < list.size(); i++) {
			XMLStructure xmlStructure = (XMLStructure) list.get(i);
			if (xmlStructure instanceof KeyValue) {
				PublicKey pk = null;
				try {
					LOG.info("public key found");
					pk = ((KeyValue) xmlStructure).getPublicKey();
				} catch (KeyException ke) {
					throw new KeySelectorException(ke);
				}

				return new SimpleKeySelectorResult(pk);
			} else if (xmlStructure instanceof X509Data) {
				LOG.info("Found x509Data "
						+ ((X509Data) xmlStructure).getContent().get(0)
								.getClass().getName());
				X509Certificate cert = ((X509Certificate) ((X509Data) xmlStructure)
						.getContent().get(0));
				certificate = cert;
				return new SimpleKeySelectorResult(cert);

			} else
				LOG.error("Wrong instance found: "
						+ xmlStructure.getClass().getName());
		}
		throw new KeySelectorException("No KeyValue element found!");
	}

	private static class SimpleKeySelectorResult implements KeySelectorResult
	{

		private PublicKey pk;

		SimpleKeySelectorResult(PublicKey _pk)
		{
			pk = _pk;
		}

		SimpleKeySelectorResult(X509Certificate _certificate)
		{
			pk = _certificate.getPublicKey();

		}

		public Key getKey()
		{
			return pk;
		}
	}

	public X509Certificate getCertificate()
	{
		return certificate;
	}
}
