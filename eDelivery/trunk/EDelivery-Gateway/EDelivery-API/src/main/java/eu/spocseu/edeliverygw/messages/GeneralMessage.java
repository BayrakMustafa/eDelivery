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

package eu.spocseu.edeliverygw.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.etsi.uri._02640.soapbinding.v1_.ObjectFactory;
import org.etsi.uri._02640.soapbinding.v1_.REMMDEvidenceListType;
import org.etsi.uri._02640.soapbinding.v1_.REMMDMessageType;
import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EntityDetailsType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xml.ws.api.message.Message;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.XMLSignatureHelper;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.transport.HTTPSTransport;
import eu.spocseu.tsl.TSL;
import eu.spocseu.tsl.TrustedServiceImpl;
import eu.spocseu.tsl.internal.exception.TSLException;
import eu.spocseu.tsl.internal.exception.UnknownDomainException;

/**
 * For internal use only
 * 
 * @author Lindemann
 * 
 */
abstract public class GeneralMessage {

	private static Logger LOG = LoggerFactory.getLogger(GeneralMessage.class);
	protected Configuration config;
	SAMLProperties samlProperties;
	protected Dispatch<Message> jaxbDispatch = null;
	protected Message message = null;

	protected Document document;

	public GeneralMessage() throws SpocsConfigurationException {
		config = Configuration.getConfiguration();
	}

	public static REMEvidenceType getEvidence(Evidences type,
			REMMDEvidenceListType list) {
		List<JAXBElement<REMEvidenceType>> evidenceList = list
				.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure();

		for (JAXBElement<REMEvidenceType> jaxbElement : evidenceList) {
			if (jaxbElement.getName().getLocalPart().equals(type.getName())) {
				return jaxbElement.getValue();
			}
		}
		return null;
	}

	public static AttributedElectronicAddressType getAttributedElectronicAdress(
			EntityDetailsType details) {
		List<Object> elecAdress = details
				.getAttributedElectronicAddressOrElectronicAddress();
		for (Object object : elecAdress) {
			if (object instanceof AttributedElectronicAddressType) {
				return (AttributedElectronicAddressType) object;
			}
		}
		return null;
	}

	public static TrustedServiceImpl getTSLData(String address,
			Configuration _config) throws SpocsWrongInputDataException {
		String[] cropped;
		if (address.contains("@"))
			cropped = address.split("@");
		else
			throw new SpocsWrongInputDataException(new UnknownDomainException(
					address + ": missing @"));

		LOG.debug("try to find tsl entry by domain:" + cropped[1]);
		TSL tsl = _config.getTSLInstance();
		tsl.setsearchParameter_eDeliveryDomainName(cropped[1]);
		Collection<TrustedServiceImpl> implList = null;
		try {

			implList = tsl.startParametersSearch();
		} catch (TSLException ex) {
			throw new SpocsWrongInputDataException(
					"Could not process the parameter search for tsl.", ex);
		}
		if (implList.size() < 1)
			throw new SpocsWrongInputDataException(new UnknownDomainException(
					"Can't find entry in the TSL for domain: " + cropped[1]));
		TrustedServiceImpl foundTSL = implList.iterator().next();
		LOG.debug("TSL found! Now using the URL:"
				+ foundTSL.getServiceSupplyPoints().iterator().next());
		return foundTSL;
	}

	protected void prepareHTTPS(TrustedServiceImpl tsl)
			throws SpocsSystemInstallationException,
			SpocsConfigurationException

	{
		LOG.debug("Prepare SSL configuration.");
		// try {
		// HTTPSTransport transport;
		// transport = new HTTPSTransport(tsl);
		// HttpsURLConnection.setDefaultHostnameVerifier(transport
		// .createHostnameVerifier());
		// HttpsURLConnection.setDefaultSSLSocketFactory(transport
		// .createSSLFactory());
		// } catch (NoSuchAlgorithmException ex) {
		// throw new SpocsSystemInstallationException("Problems init https.",
		// ex);
		// } catch (KeyStoreException ex) {
		// throw new SpocsSystemInstallationException("Problems init https.",
		// ex);
		// }
	}

	/**
	 * 
	 * for internal use only!
	 * 
	 * @param tslData
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws SpocsSystemInstallationException
	 * @throws SpocsConfigurationException
	 */
	protected File getWSDL(TrustedServiceImpl tslData) throws IOException,
			NoSuchAlgorithmException, KeyStoreException,
			SpocsSystemInstallationException, SpocsConfigurationException {
		String serviceSupplyPoint = tslData.getServiceSupplyPoints().iterator()
				.next();
		URL wsdlLocation = new URL(serviceSupplyPoint);
		String filename = wsdlLocation.getHost() + "_"
				+ wsdlLocation.getPath().split("/")[1];

		File file = File.createTempFile(filename, ".wsdl");

		HttpsURLConnection connection = (HttpsURLConnection) wsdlLocation
				.openConnection();
		connection.setSSLSocketFactory(new HTTPSTransport(tslData)
				.createSSLFactory());
		connection.setHostnameVerifier(new HTTPSTransport(tslData)
				.createHostnameVerifier());
		connection.connect();
		ReadableByteChannel rbc = Channels.newChannel(connection
				.getInputStream());
		LOG.debug("File stored in {}", file.getAbsolutePath());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, false);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		} finally {
			if (fos != null)
				fos.close();
		}
		return file;
	}

	abstract public void serialize(OutputStream out)
			throws SpocsConfigurationException,
			SpocsSystemInstallationException;

	public Element signEvidences(REMMDMessageType messageType)
			throws SpocsWrongInputDataException, SpocsConfigurationException,
			SpocsSystemInstallationException

	{

		List<JAXBElement<REMEvidenceType>> evidenceList = messageType
				.getREMMDEvidenceList()
				.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure();

		messageType.setREMMDEvidenceList(new ObjectFactory()
				.createREMMDEvidenceListType());

		JAXBElement<REMEvidenceType> jaxbElement = evidenceList.get(0);

		if (jaxbElement.getValue().getId() == null) {
			if (jaxbElement.getValue().getEvidenceIdentifier() != null)
				jaxbElement.getValue().setId(
						jaxbElement.getValue().getEvidenceIdentifier());
			else
				jaxbElement.getValue().setId("REMEvidence");
		}
		// serialize the Evidence
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JaxbContextHolder.getETSIV2JaxBContext().createMarshaller()
					.marshal(jaxbElement, baos);
		} catch (JAXBException e) {
			throw new SpocsSystemInstallationException(e);
		}
		Element ev = XMLSignatureHelper
				.createElementFromStream(new ByteArrayInputStream(baos
						.toByteArray()));
		Element evidenceElement = signEnveloped(ev);

		// serialize the Message
		ByteArrayOutputStream baosMessage = new ByteArrayOutputStream();
		try {
			JaxbContextHolder
					.getSpocsJaxBContext()
					.createMarshaller()
					.marshal(
							new ObjectFactory().createREMMDMessage(messageType),
							baosMessage);
		} catch (JAXBException e) {
			throw new SpocsSystemInstallationException(e);
		}

		Element returnElement = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document evidenceDocument = builder.parse(new ByteArrayInputStream(
					baosMessage.toByteArray()));

			Node firstDocImportedNode = evidenceDocument.importNode(
					evidenceElement, true);
			returnElement = evidenceDocument.getDocumentElement();

			returnElement.getElementsByTagName("ns7:REMMDEvidenceList").item(0)
					.appendChild(firstDocImportedNode);
		} catch (Exception ex) {
			throw new SpocsSystemInstallationException(ex);
		}

		return returnElement;

	}

	protected Element signEnveloped() throws SpocsWrongInputDataException,
			SpocsConfigurationException, SpocsSystemInstallationException {

		Element element = null;
		try {
			// ########## First the dispatch or evidence signature ########
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			serialize(out);
			element = XMLSignatureHelper
					.createElementFromStream(new ByteArrayInputStream(out
							.toByteArray()));
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
			PrivateKey privKey = config.getSignatureKey();
			X509Certificate cert = config.getSignatureCertificate();
			Transform transformEnv = fac.newTransform(Transform.ENVELOPED,
					(TransformParameterSpec) null);
			LOG.info("" + element.getAttributeNode("Id"));
			XMLSignatureHelper.createSignature(
					Arrays.asList("#"
							+ element.getAttributeNode("Id").getValue()),
					privKey, cert, element, transformEnv);
		} catch (InvalidAlgorithmParameterException ex) {
			throw new SpocsWrongInputDataException(
					"Error signing the spocs message", ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new SpocsWrongInputDataException(
					"Error signing the spocs message", ex);
		}
		return element;
	}

	protected Element signEnveloped(Element element)
			throws SpocsWrongInputDataException, SpocsConfigurationException,
			SpocsSystemInstallationException {
		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
			PrivateKey privKey = config.getSignatureKey();
			X509Certificate cert = config.getSignatureCertificate();
			Transform transformEnv = fac.newTransform(Transform.ENVELOPED,
					(TransformParameterSpec) null);
			LOG.info("" + element.getAttributeNode("Id"));
			XMLSignatureHelper.createSignature(
					Arrays.asList("#"
							+ element.getAttributeNode("Id").getValue()),
					privKey, cert, element, transformEnv);
		} catch (InvalidAlgorithmParameterException ex) {
			throw new SpocsWrongInputDataException(
					"Error signing the spocs message", ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new SpocsWrongInputDataException(
					"Error signing the spocs message", ex);
		}
		return element;
	}

	protected static void transform(Source source, Result result,
			String encoding, int indent) throws SpocsConfigurationException,
			SpocsSystemInstallationException {
		Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException ex) {
			throw new SpocsConfigurationException(
					"Transforming Message failed", ex);
		} catch (TransformerFactoryConfigurationError ex) {
			throw new SpocsConfigurationException(
					"Transforming Message failed", ex);
		} catch (TransformerException ex) {
			throw new SpocsSystemInstallationException(
					"Transforming Message failed", ex);
		}

	}

	protected class SubmissionAcceptanceRejectionError extends
			SubmissionAcceptanceRejection {
		public SubmissionAcceptanceRejectionError(Configuration config,
				EvidenceMessage message) {
			super(config, false);

		}

	}

	protected GenericService getService(URL wsdlLocation, QName serviceName)
			throws PrivilegedActionException {
		Subject subject = new Subject();
		subject.getPublicCredentials().add("eDeliveryMode");
		return Subject.doAs(subject, new SSLAction(wsdlLocation, serviceName));

	}

	protected class SSLAction implements
			PrivilegedExceptionAction<GenericService> {
		URL wsdlLocation;
		QName serviceName;

		public SSLAction(URL _wsdlLocation, QName _serviceName) {
			wsdlLocation = _wsdlLocation;
			serviceName = _serviceName;
		}

		@Override
		public GenericService run() throws JAXBException, IOException {
			return new GenericService(wsdlLocation, serviceName);
		}

	}

	protected static class GenericService extends Service {

		/**
		 * <b>For internal usage only!</b>
		 * 
		 */
		public GenericService(URL wsdlLocation, QName serviceName) {
			super(wsdlLocation, serviceName);
		}
	}

}
