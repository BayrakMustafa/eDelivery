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
