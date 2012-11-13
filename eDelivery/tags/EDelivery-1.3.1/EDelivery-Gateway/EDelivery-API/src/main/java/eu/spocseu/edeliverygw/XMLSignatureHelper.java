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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLSignatureHelper
{
	/**
	 * Algorithm for signature creation, constant for SHA1withRSA.
	 */
	public final static String SIGNATURE_ALGORITHM_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";

	/**
	 * Algorithm for signature creation, constant for SHA1withRSA, JCA/JCE
	 * identifier.
	 */
	@SuppressWarnings("unused")
	private final static String SIGNATURE_ALGORITHM_RSA_SHA1_JCA_JCE = "SHA1withRSA";

	/**
	 * Algorithm for signature creation, constant for SHA256withRSA.
	 */
	public final static String SIGNATURE_ALGORITHM_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

	/**
	 * Algorithm for signature creation, constant for SHA256withRSA, JCA/JCE
	 * identifier.
	 */
	@SuppressWarnings("unused")
	private final static String SIGNATURE_ALGORITHM_RSA_SHA256_JCA_JCE = "SHA256withRSA";

	/**
	 * Algorithm for signature creation, constant for SHA512withRSA.
	 */
	public final static String SIGNATURE_ALGORITHM_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";

	/**
	 * Algorithm for signature creation, constant for SHA512withRSA, JCA/JCE
	 * identifier.
	 */
	@SuppressWarnings("unused")
	private final static String SIGNATURE_ALGORITHM_RSA_SHA512_JCA_JCE = "SHA512withRSA";

	/**
	 * Algorithm for signature creation, constant for RIPEMD160withRSA.
	 */
	public final static String SIGNATURE_ALGORITHM_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
	/** Logger to log the messages. */
	public static Logger LOG = LoggerFactory
			.getLogger(XMLSignatureHelper.class);

	/**
	 * Creates an XML signature with the help of the SUN signature framework.
	 * 
	 * @param references
	 *            List of references that will be included into the signature.
	 *            The Id references must be present in the document. The
	 *            references must be like this "#LetterStyleBody".
	 * @param privateKey
	 *            The private key for the signature.
	 * @param sigCert
	 *            The X509Certificate to put into the signature for validation
	 *            reasons.
	 * @param xml
	 *            The DOM element including the references. The signature will
	 *            be added to this node.
	 * @param transforms
	 *            Additional transformer.
	 */
	public static void createSignature(List<String> references,
			PrivateKey privateKey, X509Certificate sigCert, Element xml,
			Transform... transforms) throws SpocsSystemInstallationException,
			SpocsWrongInputDataException
	{
		ArrayList<Reference> refList = new ArrayList<Reference>();
		Iterator<String> uriList = references.iterator();
		while (uriList.hasNext()) {
			// Adds the references.
			String uri = uriList.next();
			refList.add(createReference(uri, transforms));
		}
		// Creates the signature.
		createSignatureElement(refList, privateKey, sigCert, xml);
	}

	public static void createSignature(Map<String, byte[]> references,
			PrivateKey privateKey, X509Certificate sigCert, Element delElm)
			throws SpocsSystemInstallationException,
			SpocsWrongInputDataException
	{
		ArrayList<Reference> refList = new ArrayList<Reference>();
		Iterator<String> ids = references.keySet().iterator();
		while (ids.hasNext()) {
			String id = ids.next();
			if (references.get(id) == null) {
				refList.add(createReference(id));
			} else {
				LOG.info("Add reference with hash");
				refList.add(createReference(id, references.get(id)));
			}
		}
		createSignatureElement(refList, privateKey, sigCert, delElm);
	}

	protected static Reference createReference(String uri, byte[] digest)
			throws SpocsSystemInstallationException
	{
		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
			ArrayList<Transform> trans = new ArrayList<Transform>();
			trans.add(fac.newTransform(CanonicalizationMethod.EXCLUSIVE,
				new ExcC14NParameterSpec()));
			return fac.newReference(uri,
				fac.newDigestMethod(DigestMethod.SHA512, null), trans,
				"xml/application", uri, digest);
		} catch (NoSuchAlgorithmException ex) {
			throw new SpocsSystemInstallationException(
				"Can`t find right signature environment", ex);
		} catch (GeneralSecurityException ex) {
			throw new SpocsSystemInstallationException(
				"Can`t find right signature environment", ex);
		}
	}

	/**
	 * Creates a new XML signature reference entry for the KeyInfo element.
	 * 
	 * @param uri
	 *            The URI that will be referenced.
	 * @return The created reference object.
	 * @throws GeneralSecurityException
	 * @throws SpocsSystemInstallationException
	 */
	protected static Reference createReference(String uri,
			Transform... transforms) throws SpocsSystemInstallationException
	{
		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
			ArrayList<Transform> trans = new ArrayList<Transform>();
			for (Transform transform : transforms) {
				trans.add(transform);

			}
			trans.add(fac.newTransform(CanonicalizationMethod.EXCLUSIVE,
				new ExcC14NParameterSpec()));
			return fac.newReference(uri,
				fac.newDigestMethod(DigestMethod.SHA512, null), trans,
				"xml/application", "Ref_" + uri);
		} catch (NoSuchAlgorithmException ex) {
			throw new SpocsSystemInstallationException(
				"Can`t find right signature environment", ex);
		} catch (GeneralSecurityException ex) {
			throw new SpocsSystemInstallationException(
				"Can`t find right signature environment", ex);
		}
	}

	/**
	 * Creates the XML signature with the given parameters.
	 * 
	 * @param refs
	 *            A list of references added to the XML KeyInfo element.
	 * @param privateKey
	 *            The private key for the signature.
	 * @param sigCert
	 *            The certificate to put into the signature for validation
	 *            reasons.
	 * @param delElm
	 *            The DOM element including the references. The signature will
	 *            be added to this node.
	 * @throws SpocsSystemInstallationException
	 * @throws GeneralSecurityException
	 * @throws MarshalException
	 * @throws XMLSignatureException
	 * @throws SpocsWrongInputDataException
	 */
	protected static void createSignatureElement(List<Reference> refs,
			PrivateKey privatreKey, X509Certificate sigCert, Element delElm)
			throws SpocsSystemInstallationException,
			SpocsWrongInputDataException
	{
		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
			SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
				CanonicalizationMethod.EXCLUSIVE,
				(C14NMethodParameterSpec) null), fac.newSignatureMethod(
				SIGNATURE_ALGORITHM_RSA_SHA512, null), refs);
			KeyInfoFactory kif = fac.getKeyInfoFactory();

			X509Data kv = kif.newX509Data(Collections.singletonList(sigCert));
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
			XMLSignature signature = fac.newXMLSignature(si, ki);
			DOMSignContext dsc = new DOMSignContext(privatreKey, delElm);
			signature.sign(dsc);
		} catch (InvalidAlgorithmParameterException ex) {
			throw new SpocsSystemInstallationException(
				"Can`t find right signature environment", ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new SpocsSystemInstallationException(
				"Can`t find right signature environment", ex);
		} catch (MarshalException ex) {
			throw new SpocsWrongInputDataException(
				"Can`t process signature with given inputdata", ex);
		} catch (XMLSignatureException ex) {
			throw new SpocsWrongInputDataException(
				"Can`t process signature with given inputdata", ex);
		}
	}

	/**
	 * Validates an XML signature with the help of the SUN framework.
	 * 
	 * @param doc
	 *            The DOM document including the XML signature.
	 * @return False in case of wrong signature validation, otherwise true.
	 */
	public static X509Certificate[] validateSignature(Document doc)
			throws SpocsWrongInputDataException, XMLSignatureException
	{
		// Finds the signature element.
		// LOG.fine("ns for signature: "+doc.getdDocumentElement().)
		ArrayList<X509Certificate> usedCertificates = new ArrayList<X509Certificate>();

		NodeList sigElem = doc.getElementsByTagNameNS(XMLSignature.XMLNS,
			"Signature");
		if (sigElem == null) sigElem = doc.getElementsByTagName("Signature");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			XMLSignatureHelper.dumpDomNode(sigElem.item(0), out);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		// LOG.debug("Signature: " + out);

		LOG.debug("Count of found signatures: " + sigElem.getLength());
		// Creates a DOM XMLSignatureFactory that will be used to unmarshal
		// the
		// document containing the XMLSignature.
//		XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
		// Creates a DOMValidateContext and specifies a KeyValue KeySelector
		// and document context.

		for (int countSigElem = 0; countSigElem < sigElem.getLength(); countSigElem++) {
			LOG.info("Validate the Signature nume: "
					+ countSigElem
					+ "Signature: "
					+ ((Element) sigElem.item(countSigElem))
							.getAttributeNode("Id"));
			X509Certificate cert = XMLSignatureHelper.validateSignature(doc,
				sigElem.item(0));
			addCertificate(usedCertificates, cert);
		}
		if (sigElem.getLength() == 0) {

			// if no signature is available throw exception
			throw new IllegalArgumentException("Cannot find Signature element");
		} else
			return usedCertificates.toArray(new X509Certificate[] {});
	}

	/**
	 * Validates an XML signature with the help of the SUN framework.
	 * 
	 * @param doc
	 *            The DOM document including the XML signature.
	 * @return False in case of wrong signature validation, otherwise true.
	 */
	public static X509Certificate validateSignature(Document doc,
			Node verifyElem) throws SpocsWrongInputDataException,
			XMLSignatureException
	{
		try {

			XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
			// Creates a DOMValidateContext and specifies a KeyValue KeySelector
			// and document context.

			KeyValueKeySelector keyValueKeySelector = new KeyValueKeySelector();
			DOMValidateContext valContext = new DOMValidateContext(
				keyValueKeySelector, verifyElem);

			// Unmarshals the XMLSignature.
			XMLSignature signature = fac.unmarshalXMLSignature(valContext);
			// Validates the XMLSignature (generated above).
			boolean coreValidity = signature.validate(valContext);
			// Checks the core validation status.

			LOG.debug("Certificate used to check: "
					+ keyValueKeySelector.getCertificate().getSubjectDN()
							.getName());
			if (coreValidity == false) {
				LOG.error("Signature failed core validation.");
				boolean sv = signature.getSignatureValue().validate(valContext);
				LOG.debug("Signature validation status: " + sv);
				// Checks the validation status of each reference.
				@SuppressWarnings("unchecked")
				Iterator<Reference> i = signature.getSignedInfo()
						.getReferences().iterator();
				for (int j = 0; i.hasNext(); j++) {
					Reference ref = (Reference) i.next();
					boolean refValid = ref.validate(valContext);
					String logString = "validity status for  {} : {}";
					if (!refValid)
						LOG.error("invalid ref:" + logString, j, ref.getURI());
					else
						LOG.debug("valid ref:" + j + logString, j, ref.getURI());
				}
				throw new XMLSignatureException(
					"Signature failed core validation!");
			} else {
				LOG.info("One signature passed core validation.");
				return keyValueKeySelector.getCertificate();
			}

		} catch (MarshalException ex) {
			throw new SpocsWrongInputDataException(
				"Can`t read signature element. ", ex);
		}
	}

	private static void addCertificate(ArrayList<X509Certificate> certList,
			X509Certificate certToAdd)
	{
		for (X509Certificate x509Certificate : certList) {

			if (x509Certificate.getSerialNumber().equals(
				certToAdd.getSerialNumber())
					&& x509Certificate.getIssuerDN().getName()
							.equals(certToAdd.getIssuerDN().getName())) {
				return;
			}
		}
		LOG.debug("One certificate added.");
		certList.add(certToAdd);
	}

	public static Element createElementFromStream(InputStream xmlIn)
			throws SpocsWrongInputDataException
	{
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			return builder.parse(xmlIn).getDocumentElement();
		} catch (Exception ex) {
			throw new SpocsWrongInputDataException(
				"Error signing the spocs message", ex);
		}
	}

	/**
	 * Writes the DOM node to the given OutputStream.
	 * 
	 * @param node
	 *            The node that will be serialized.
	 * @param out
	 *            The OutputStream to write into.
	 */
	public static ByteArrayOutputStream dumpDomNode(Node node)
			throws TransformerException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		dumpDomNode(node, out);
		return out;
	}

	/**
	 * Writes the DOM node to the given OutputStream.
	 * 
	 * @param node
	 *            The node that will be serialized.
	 * @param out
	 *            The OutputStream to write into.
	 */
	public static void dumpDomNode(Node node, OutputStream out)
			throws TransformerException
	{
		DOMSource domSource = new DOMSource(node);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer xform = null;
		xform = tf.newTransformer();
		xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StreamResult tmpStreamResult = new StreamResult(out);
		xform.transform(domSource, tmpStreamResult);
	}

}
