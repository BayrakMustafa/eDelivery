package eu.spocseu.edeliverygw.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;

import javax.jws.WebMethod;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPFaultException;

import org.etsi.uri._02640.soapbinding.v1_.ObjectFactory;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.soapbinding.v1_.REMMDMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2005._08.addressing.AttributedURIType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;

import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.JaxbMarshallerHolder;
import eu.spocseu.edeliverygw.LogHelper;
import eu.spocseu.edeliverygw.SpocsGWProcessingException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.XMLSignatureHelper;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.messages.DispatchMessageResponse;
import eu.spocseu.edeliverygw.messages.GeneralMessage;
import eu.spocseu.edeliverygw.process.ProcessMessage;

@WebServiceProvider(wsdlLocation = "WEB-INF/wsdl/eDeliveryGateway.wsdl")
@ServiceMode(value = Service.Mode.MESSAGE)
// @ReliableMessaging
// @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({ org.etsi.uri._01903.v1_3.ObjectFactory.class,
		org.w3._2000._09.xmldsig_.ObjectFactory.class,
		org.etsi.uri._02640.soapbinding.v1_.ObjectFactory.class,
		org.etsi.uri._02640.v2_.ObjectFactory.class,
		org.etsi.uri._02231.v2_.ObjectFactory.class,
		org.w3._2005._05.xmlmime.ObjectFactory.class })
public class SpocsGatewayProvider implements Provider<Message>
{
	private static Logger LOG = LoggerFactory
			.getLogger(SpocsGatewayProvider.class);
	private static boolean initCalled = false;

	private Configuration config;

	public SpocsGatewayProvider() throws SpocsConfigurationException
	{
		LOG.debug("========== init on Startup called ================");
		if (!initCalled) {
			ProcessMessage.initOnStartup(Configuration.getConfiguration());
			initCalled = true;
		}
		config = Configuration.getConfiguration();
	}

	@Override
	@WebMethod
	public Message invoke(Message soapMessage)
	{
		LOG.info("Entry spocs provider processing.");
		String action = null;
		try {
			action = ((AttributedURIType) createAddressingHeader(
				soapMessage.getHeaders(), AddressingVersion.W3C.actionTag))
					.getValue();
		} catch (JAXBException ex) {
			LOG.error("Error: ", ex);
		}
		LOG.debug("Action found: " + action);
		Document dom = null;
		Unmarshaller unmarshaller = null;
		JAXBElement<?> jaxbObject = null;
		try {
			dom = getReceiveMessageStream(soapMessage);
			unmarshaller = JaxbMarshallerHolder.getSpocsMarshallerHolder()
					.getUnmarshaller();
			jaxbObject = (JAXBElement<?>) unmarshaller.unmarshal(dom);
		} catch (Exception ex) {
			LOG.error("Error: ", ex);
			throw throwSoapFault("Could not parse the incoming spocs message.",
				SOAPConstants.SOAP_SENDER_FAULT, ex);
		} finally {
			try {
				JaxbMarshallerHolder.getSpocsMarshallerHolder().recycle(
					unmarshaller);
			} catch (JAXBException e) {
				LOG.info("Could not recycle the spocs unmarshaller");
			}

		}
		if (action.equals("urn:#AcceptREMDispatchOperation")) {
			LOG.info("found action AcceptREMDispatchOperation");
			try {
				REMMDMessageType evidence = ProcessMessage
						.processDispatchMessage(
							(REMDispatchType) jaxbObject.getValue(), dom,
							Configuration.getConfiguration());
				
				DispatchMessageResponse dmr = new DispatchMessageResponse();
			
				return createResponseMsg(dmr.signEvidences(evidence));
			} catch (EvidenceException ex) {
				try {
					
					DispatchMessageResponse dmr = new DispatchMessageResponse(ex);
					return createResponseMsg(dmr.signEvidences(dmr.jaxbObj));
				} catch (SpocsConfigurationException ex2) {
					LOG.error("Error processing acceptDispatchOperation.", ex2);
					throw throwSoapFault(ex2.getMessage(),
						SOAPConstants.SOAP_RECEIVER_FAULT, ex2);
				} catch (SpocsWrongInputDataException ex2) {
					LOG.error("Error processing acceptDispatchOperation.", ex2);
					throw throwSoapFault(ex2.getMessage(),
						SOAPConstants.SOAP_RECEIVER_FAULT, ex2);
				} catch (SpocsSystemInstallationException ex2) {
					LOG.error("Error processing acceptDispatchOperation.", ex2);
					throw throwSoapFault(ex2.getMessage(),
						SOAPConstants.SOAP_RECEIVER_FAULT, ex2);
				} catch (TransformerException ex2) {
					LOG.error("Error processing acceptDispatchOperation.", ex2);
					throw throwSoapFault(ex2.getMessage(),
						SOAPConstants.SOAP_RECEIVER_FAULT, ex2);
				}
			} catch (SOAPException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault(ex.getMessage(),
					SOAPConstants.SOAP_RECEIVER_FAULT, ex);
			} catch (SpocsConfigurationException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault(ex.getMessage(),
					SOAPConstants.SOAP_RECEIVER_FAULT, ex);
			} catch (SpocsWrongInputDataException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault(ex.getMessage(),
						SOAPConstants.SOAP_SENDER_FAULT, ex);
			} catch (SpocsSystemInstallationException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault(ex.getMessage(),
						SOAPConstants.SOAP_RECEIVER_FAULT, ex);
			} catch (TransformerException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault(ex.getMessage(),
						SOAPConstants.SOAP_RECEIVER_FAULT, ex);
			} finally {
				LOG.debug("Exit message processing");
			}

		} else if (action.equals("urn:#AcceptREMMDMessageOperation")) {
			LOG.info("found action AcceptREMMDMessageOperation");
			try {
				ProcessMessage.processEvidenceMessage(
					(REMMDMessageType) jaxbObject.getValue(), dom,
					Configuration.getConfiguration());
			} catch (SpocsGWProcessingException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault(ex);
			} catch (SpocsConfigurationException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault("", SOAPConstants.SOAP_RECEIVER_FAULT, ex);
			} catch (SpocsWrongInputDataException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault("Wrong input data.",
					SOAPConstants.SOAP_RECEIVER_FAULT, ex);
			} catch (SpocsSystemInstallationException ex) {
				LOG.error("Error processing acceptDispatchOperation.", ex);
				throw throwSoapFault("Wrong configured Gateway.",
					SOAPConstants.SOAP_RECEIVER_FAULT, ex);
			}
		} else {
			LOG.error("Wrong action found");
			throw throwSoapFault("Wrong action or wrong message type found",
				SOAPConstants.SOAP_SENDER_FAULT, null);
		}
		LOG.info("Exit spocs provider processing.");
		return null;
	}

	/**
	 * <b>For internal usage only!</b> Creates a addressing object out of the
	 * given header list.
	 * 
	 * @param hl
	 *            Header list that includes the addressing header.
	 * @param addressingHeaderTag
	 *            QName of the addressing element that would be created.
	 * @return The created addressing object or <code>null</code> in the case of
	 *         no occurrence.
	 * @throws JAXBException
	 *             If the structure of the element is not the expected.
	 */
	public static Object createAddressingHeader(HeaderList hl,
			QName addressingHeaderTag) throws JAXBException
	{
		Iterator<Header> h = hl.getHeaders(addressingHeaderTag, true);
		if ((h != null) && h.hasNext()) {
			LOG.debug("Found header: " + addressingHeaderTag.getLocalPart());
			while (h.hasNext()) {
				Header header = h.next();
				Unmarshaller unmarshaller = JaxbMarshallerHolder
						.getAddressingMarshallerHolder().getUnmarshaller();
				JAXBElement<?> obj = header.readAsJAXB(unmarshaller);
				JaxbMarshallerHolder.getAddressingMarshallerHolder().recycle(
					unmarshaller);

				return obj.getValue();
			}
		}
		return null;
	}

	// private SOAPFaultException throwSoapFault(String faultMessage, Exception
	// e)
	// throws RuntimeException
	// {
	// try {
	// SOAPFault soapFault = SOAPFactory.newInstance(
	// javax.xml.soap.SOAPConstants.SOAP_1_2_PROTOCOL).createFault();
	// soapFault.setFaultString(faultMessage);
	// soapFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
	// soapFault.setFaultActor("SPOCS Gateway MD");
	// return new SOAPFaultException(soapFault);
	// } catch (Exception e2) {
	// throw new RuntimeException(
	// "Problem processing SOAP Fault on service-side."
	// + e2.getMessage());
	// }
	// }

	private SOAPFaultException throwSoapFault(String faultMessage,
			QName faultCode, Exception e) throws RuntimeException
	{
		try {
			SOAPFault soapFault = SOAPFactory.newInstance(
				javax.xml.soap.SOAPConstants.SOAP_1_2_PROTOCOL).createFault();
			soapFault.setFaultString(faultMessage);
			soapFault.setFaultCode(faultCode);
			soapFault.setFaultActor("SPOCS Gateway MD");
			return new SOAPFaultException(soapFault);
		} catch (Exception e2) {
			throw new RuntimeException(
				"Problem processing SOAP Fault on service-side."
						+ e2.getMessage());
		}
	}

	private SOAPFaultException throwSoapFault(SpocsGWProcessingException ex)
			throws RuntimeException
	{
		try {
			return throwSoapFault(ex.getMessage(), ex.getFault().getActor(), ex);
		} catch (Exception e2) {
			throw new RuntimeException(
				"Problem processing SOAP Fault on service-side."
						+ e2.getMessage());
		}
	}

	private Message createResponseMsg(Element returnObject)
			throws SpocsWrongInputDataException, SpocsConfigurationException,
			SpocsSystemInstallationException, TransformerException
	{
			Message message = com.sun.xml.ws.api.message.Messages
					.createUsingPayload(new StreamSource(
						new ByteArrayInputStream(XMLSignatureHelper
								.dumpDomNode(returnObject).toByteArray())),
						com.sun.xml.ws.api.SOAPVersion.SOAP_12);
			return message;
	}

	private Element signResponseMessage(ByteArrayOutputStream out)
			throws SpocsWrongInputDataException, SpocsConfigurationException,
			SpocsSystemInstallationException
	{
		LOG.info("Signing Response Message!");
		Element element = null;
		try {
			element = XMLSignatureHelper
					.createElementFromStream(new ByteArrayInputStream(out
							.toByteArray()));
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
			PrivateKey privKey = config.getSignatureKey();
			X509Certificate cert = config.getSignatureCertificate();
			Transform transformEnv = fac.newTransform(Transform.ENVELOPED,
				(TransformParameterSpec) null);
			LOG.debug("" + element.getAttributeNode("Id"));
			XMLSignatureHelper.createSignature(
				Arrays.asList("#" + element.getAttributeNode("Id").getValue()),
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

	/**
	 * Returns the whole message of associated request message. The whole
	 * message will be returned with all header information and the payload in
	 * the body.
	 * 
	 * @param out
	 *            An OutputStream where the data could be written to.
	 * @throws TransformerException
	 *             If there are errors getting the stream.
	 */
	public static Document getReceiveMessageStream(Message soap)
			throws TransformerException
	{
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		DOMResult domResult = new DOMResult();
		transformer.transform(soap.readPayloadAsSource(), domResult);
		return (Document) domResult.getNode();
	}
}
