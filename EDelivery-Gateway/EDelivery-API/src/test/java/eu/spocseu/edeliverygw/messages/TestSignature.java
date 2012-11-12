package eu.spocseu.edeliverygw.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.xml.bind.JAXBException;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.XMLSignatureHelper;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;

public class TestSignature
{

	private static Logger LOG = LoggerFactory.getLogger(TestSignature.class
			.getName());

	Configuration config = null;

	@Before
	public void configure() throws Exception
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
		config = Configuration.getConfiguration();

	}

	@Test
	public void testSignature() throws Exception
	{
		// create doc
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element rootElem = doc.createElement("test");
		rootElem.setAttribute("Id", "myTestID");
		doc.appendChild(rootElem);
		// ####### sign the element
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance();

		PrivateKey privKey = config.getSignatureKey();
		X509Certificate cert = config.getSignatureCertificate();
		Transform transformEnv = fac.newTransform(Transform.ENVELOPED,
			(TransformParameterSpec) null);
		LOG.info("" + doc.getDocumentElement().getAttributeNode("Id"));
		XMLSignatureHelper.createSignature(
			Arrays.asList("#"
					+ doc.getDocumentElement().getAttributeNode("Id")
							.getValue()), privKey, cert,
			doc.getDocumentElement(), transformEnv);

		// ############ verify the signgature
		XMLSignatureHelper.validateSignature(doc);
	}

	@Test
	public void testDispatchSignature() throws IOException,
			GeneralSecurityException, DatatypeConfigurationException,
			JAXBException, SpocsConfigurationException,
			SpocsWrongInputDataException, SpocsSystemInstallationException,
			TransformerException, SAXException, ParserConfigurationException,
			XMLSignatureException
	{
		DispatchMessage message = IntegrationTestCommon.createDispatchMessage();

		Element element = message.signEvidenceAndEnveloped();

		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		// "yes");
		transformer.transform(new DOMSource(element), new StreamResult(buffer));
		String str = buffer.toString();

		System.err.println(str);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document dispatchDocument = builder.parse(new InputSource(
			new StringReader(str)));

		// Validating both signatures!
		XMLSignatureHelper.validateSignature(dispatchDocument);

	}

	@Test
	public void testEvidenceinDispatchSignature() throws IOException,
			GeneralSecurityException, DatatypeConfigurationException,
			JAXBException, SpocsConfigurationException,
			SpocsWrongInputDataException, SpocsSystemInstallationException,
			ParserConfigurationException, SAXException, XMLSignatureException,
			TransformerException
	{

		DispatchMessage message = IntegrationTestCommon.createDispatchMessage();

		Element signed = message.signEvidenceAndEnveloped();
		Node node = signed
				.getElementsByTagName("SubmissionAcceptanceRejection").item(0);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		// "yes");
		transformer.transform(new DOMSource(node), new StreamResult(buffer));
		String str = buffer.toString();

		System.err.println(str);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document dispatchDocument = builder.parse(new InputSource(
			new StringReader(str)));

		XMLSignatureHelper.validateSignature(dispatchDocument);

	}

	@Test
	public void testEvidenceMessageSignature() throws IOException,
			GeneralSecurityException, DatatypeConfigurationException,
			JAXBException, SpocsConfigurationException,
			SpocsWrongInputDataException, SpocsSystemInstallationException,
			ParserConfigurationException, SAXException, TransformerException, XMLSignatureException
	{
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("Wilko");
		samlProperties.setSurname("Oley");

		DispatchMessage dispatchMessage = IntegrationTestCommon
				.createDispatchMessage();

		SubmissionAcceptanceRejection subEvidence = new SubmissionAcceptanceRejection(
			Configuration.getConfiguration(), dispatchMessage, true);
		DeliveryNonDeliveryToRecipientMessage message = new DeliveryNonDeliveryToRecipientMessage(
			new DeliveryNonDeliveryToRecipient(
				Configuration.getConfiguration(), subEvidence), samlProperties);
		Element signedEvidenceMessage = message.signEvidences(message.jaxbObj);

		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		// "yes");
		transformer.transform(new DOMSource(signedEvidenceMessage),
			new StreamResult(buffer));
		String str = buffer.toString();

		System.err.println(str);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document evidenceDocument = builder.parse(new InputSource(
			new StringReader(str)));

		XMLSignatureHelper.validateSignature(evidenceDocument);	
	}

}
