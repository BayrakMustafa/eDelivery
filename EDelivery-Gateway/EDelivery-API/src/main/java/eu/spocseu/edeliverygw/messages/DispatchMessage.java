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
$Date: 2010-10-14 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */
package eu.spocseu.edeliverygw.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.Subject;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.AttributeStatementType;
import oasis.names.tc.saml._2_0.assertion.AttributeType;
import oasis.names.tc.saml._2_0.assertion.StatementAbstractType;

import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02640.soapbinding.v1_.MsgMetaData;
import org.etsi.uri._02640.soapbinding.v1_.NormalizedMsg;
import org.etsi.uri._02640.soapbinding.v1_.ObjectFactory;
import org.etsi.uri._02640.soapbinding.v1_.OriginalMsgType;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.soapbinding.v1_.REMMDEvidenceListType;
import org.etsi.uri._02640.soapbinding.v1_.REMMDMessageType;
import org.etsi.uri._02640.v2_.AuthenticationDetailsType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.security.CallbackHandlerFeature;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.Attribute;
import com.sun.xml.wss.saml.SAMLAssertionFactory;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.assertion.saml20.jaxb20.AttributeStatement;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.JaxbMarshallerHolder;
import eu.spocseu.edeliverygw.LogHelper;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.XMLSignatureHelper;
import eu.spocseu.edeliverygw.callbackhandler.EDeliveryCallbackHandler;
import eu.spocseu.edeliverygw.callbackhandler.SamlCallbackHandler;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.ReceivedByNonREMSystem;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.evidences.exception.SubmissionRejectionException;
import eu.spocseu.edeliverygw.messageparts.DeliveryConstraints;
import eu.spocseu.edeliverygw.messageparts.Destinations;
import eu.spocseu.edeliverygw.messageparts.MsgIdentification;
import eu.spocseu.edeliverygw.messageparts.Normalized;
import eu.spocseu.edeliverygw.messageparts.Original;
import eu.spocseu.edeliverygw.messageparts.Originators;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import eu.spocseu.edeliverygw.transport.HTTPSTransport;
import eu.spocseu.tsl.TrustedServiceImpl;

/**
 * The <code>DispatchMessage</code> class helps to create and send a Spocs
 * dispatch message. The underlying DispatchMessage JAXB object will be created
 * by this class and all required values will be set. In the case that the user
 * adds some detailed information to the JAXB object it is possible to get the
 * underlying JAXB object and add this information. The method to get the
 * created JAXB object is the getXSDObject method.
 * <p>
 * 
 * @since SPOCS_WP3_INITIAL_RELEASE_LABEL
 */

public class DispatchMessage extends GeneralMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger LOG = LoggerFactory.getLogger(DispatchMessage.class
			.getName());
	REMDispatchType dispatch;
	private SubmissionAcceptanceRejection submissionAcceptanceRejection;

	private ReceivedByNonREMSystem receivedByNonREMSystem;

	/**
	 * This is most common constructor to create the a Dispatch message. All
	 * required values must be set to configure this instance.
	 * 
	 * @param _deliveryConstraints
	 *            Some detailed information about the message like creation time
	 *            and obsolete after time.
	 * @param _originators
	 *            Information about the originators of the dispatch message.
	 * @param _destinations
	 *            Information about the destinations of the dispatch message.
	 * @param _msgIdentification
	 *            Information about the transported message like relay to or the
	 *            initial send.
	 */
	public DispatchMessage(DeliveryConstraints _deliveryConstraints,
			Originators _originators, Destinations _destinations,
			MsgIdentification _msgIdentification, SAMLProperties _samProperties)
			throws SpocsConfigurationException {
		samlProperties = _samProperties;
		dispatch = new REMDispatchType();
		dispatch.setId(UUID.randomUUID().toString());
		MsgMetaData metaData = new MsgMetaData();
		dispatch.setMsgMetaData(metaData);
		metaData.setDeliveryConstraints(_deliveryConstraints.getXSDObject());
		metaData.setOriginators(_originators.getXSDObject());
		metaData.setDestinations(_destinations.getXSDObject());
		metaData.setMsgIdentification(_msgIdentification.getXSDObject());
		// now at the mandentory sub evidence
		dispatch.setREMMDEvidenceList(new ObjectFactory()
				.createREMMDEvidenceListType());
		addSubmissionAcceptance();
	}

	/**
	 * In the case of a serialized dispatch message this constructor can be used
	 * to create a DispatchMessage object.
	 * 
	 * @param dispatchTypeIn
	 *            The input stream of the according serialized dispatchMessage.
	 */
	@SuppressWarnings("unchecked")
	public DispatchMessage(InputStream dispatchTypeIn)
			throws SpocsConfigurationException, SpocsWrongInputDataException {
		JAXBElement<REMDispatchType> dispatchType;
		try {
			dispatchType = (JAXBElement<REMDispatchType>) JaxbContextHolder
					.getSpocsJaxBContext().createUnmarshaller()
					.unmarshal(dispatchTypeIn);
			dispatch = dispatchType.getValue();
			submissionAcceptanceRejection = new SubmissionAcceptanceRejection(
					getEvidence(Evidences.SUBMISSION_ACCEPTANCE_REJECTION,
							dispatch.getREMMDEvidenceList()));

			receivedByNonREMSystem = new ReceivedByNonREMSystem(getEvidence(
					Evidences.RECEIVED_BY_NON_REM_SYSTEM,
					dispatch.getREMMDEvidenceList()));
		} catch (JAXBException ex) {
			throw new SpocsWrongInputDataException(
					"Error reading the dispatch xml stream.", ex);
		}
	}

	/**
	 * In the case of incoming message on the receiving side the constructor
	 * will be used.
	 * 
	 * @param dispatchType
	 *            The underlying JAXB object.
	 */
	public DispatchMessage(REMDispatchType dispatchType, Document doc)
			throws SpocsConfigurationException {
		document = doc;
		dispatch = dispatchType;
		if (dispatch.getId() == null)
			dispatch.setId(UUID.randomUUID().toString());
		if (getEvidence(Evidences.SUBMISSION_ACCEPTANCE_REJECTION,
				dispatch.getREMMDEvidenceList()) != null)
			submissionAcceptanceRejection = new SubmissionAcceptanceRejection(
					getEvidence(Evidences.SUBMISSION_ACCEPTANCE_REJECTION,
							dispatch.getREMMDEvidenceList()));
		if (getEvidence(Evidences.RECEIVED_BY_NON_REM_SYSTEM,
				dispatch.getREMMDEvidenceList()) != null) {
			receivedByNonREMSystem = new ReceivedByNonREMSystem(getEvidence(
					Evidences.RECEIVED_BY_NON_REM_SYSTEM,
					dispatch.getREMMDEvidenceList()));
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		try {
			serialize(out);
		} catch (SpocsConfigurationException e) {
			throw new IOException(e);
		} catch (SpocsSystemInstallationException e) {
			throw new IOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		JAXBElement<REMDispatchType> dispatchType;
		try {
			dispatchType = (JAXBElement<REMDispatchType>) JaxbContextHolder
					.getSpocsJaxBContext().createUnmarshaller().unmarshal(in);
			dispatch = dispatchType.getValue();
			submissionAcceptanceRejection = new SubmissionAcceptanceRejection(
					getEvidence(Evidences.SUBMISSION_ACCEPTANCE_REJECTION,
							dispatch.getREMMDEvidenceList()));
			receivedByNonREMSystem = new ReceivedByNonREMSystem(getEvidence(
					Evidences.RECEIVED_BY_NON_REM_SYSTEM,
					dispatch.getREMMDEvidenceList()));
		} catch (JAXBException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * Gets the underlying JAXB object, for example to add some detailed
	 * information with this method.
	 * 
	 * @return The JAXB object.
	 */
	public REMDispatchType getXSDObject() {
		return dispatch;

	}

	/**
	 * This method serializes the underlying JAXB object. Be careful with this
	 * method because after calling this method it is not possible to change
	 * something.
	 * 
	 * @param out
	 *            The output stream that the information will be streamed into.
	 * @throws SpocsSystemInstallationException
	 *             if something is wrong with the installation
	 * @throws SpocsConfigurationException
	 *             if the system is configured wrong
	 * 
	 */
	@Override
	public void serialize(OutputStream out)
			throws SpocsSystemInstallationException,
			SpocsConfigurationException {
		if (document == null) {
			LOG.debug("No Document declared serializing through JAXB");
			try {
				JaxbContextHolder
						.getSpocsJaxBContext()
						.createMarshaller()
						.marshal(
								new ObjectFactory()
										.createREMDispatch(getXSDObject()),
								out);
			} catch (JAXBException e) {
				throw new SpocsSystemInstallationException(e);
			}
		} else {
			LOG.info("Serializing with Document");
			Result result = new StreamResult(out);
			transform(new DOMSource(document), result, "UTF-8", -1);

		}
	}

	public static NormalizedMsg getNormalizedElement(REMDispatchType jaxBObject) {

		return jaxBObject.getNormalizedMsg();
	}

	public static OriginalMsgType getOriginalElement(REMDispatchType jaxBObject) {
		return jaxBObject.getOriginalMsg();
	}

	public NormalizedMsg getNormalizedElement() {
		return getNormalizedElement(dispatch);
	}

	public OriginalMsgType getOriginalElement() {
		return getOriginalElement(dispatch);
	}

	/**
	 * This Method must be invoked before <code>sendDispatchMessage()</code> to
	 * set the Content.
	 * 
	 * 
	 * @param normalized
	 *            The content of this dispatch message including normalized
	 *            text. Could be null if no normalized text is available.
	 * @param original
	 *            The content of this dispatch message including the original
	 *            text base64 coded. Could be null if no original text is
	 *            available.
	 */
	public void setContent(Normalized normalized, Original original) {
		if (normalized != null)
			dispatch.setNormalizedMsg(normalized.getXSDObject());
		if (original != null)
			dispatch.setOriginalMsg(original.getXSDObject());
	}

	protected void prepareSendMessage() throws SpocsSystemInstallationException {
		if (dispatch.getMsgMetaData().getOriginators() == null
				|| dispatch.getMsgMetaData().getOriginators().getFrom() == null
				|| dispatch.getMsgMetaData().getOriginators().getFrom()
						.getAttributedElectronicAddressOrElectronicAddress() == null)
			throw new IllegalStateException(
					"Sender information is not avaliable.");
		try {

			// set the subject to the Submission Acceptance

			String subject = dispatch.getNormalizedMsg().getInformational()
					.getSubject();
			getEvidence(Evidences.SUBMISSION_ACCEPTANCE_REJECTION,
					dispatch.getREMMDEvidenceList()).getSenderMessageDetails()
					.setMessageSubject(subject);

			// set the sending time
			dispatch.getMsgMetaData()
					.getDeliveryConstraints()
					.setInitialSend(
							SpocsFragments
									.createXMLGregorianCalendar(new Date()));

			// Add the SAML sender vouches token
			if (samlProperties != null) {
				Element elem = new SamlCallbackHandler()
						.createSAMLSenderVouchesToken(config, samlProperties);
				AnyType any = new AnyType();
				AuthenticationDetailsType auth = new AuthenticationDetailsType();
				auth.setAdditionalDetails(any);
				if (samlProperties.getAuthenticationMethod() != null) {
					auth.setAuthenticationMethod(samlProperties
							.getAuthenticationMethod());
				} else {
					auth.setAuthenticationMethod("http:uri.etsi.org/REM/AuthMethod#Enhanced");
				}
				if (samlProperties.getAuthenticationTime() != null) {
					auth.setAuthenticationTime(SpocsFragments
							.createXMLGregorianCalendar(samlProperties
									.getAuthenticationTime()));
				} else {
					auth.setAuthenticationTime(SpocsFragments
							.createXMLGregorianCalendar(new Date()));
				}

				any.getContent().add(elem);
				REMEvidenceType evidence = getEvidence(
						Evidences.SUBMISSION_ACCEPTANCE_REJECTION,
						dispatch.getREMMDEvidenceList());

				evidence.setSenderAuthenticationDetails(auth);

			}

		} catch (DatatypeConfigurationException ex) {
			throw new SpocsSystemInstallationException(ex);
		}
	}

	/**
	 * This method sends the dispatch message to the configured destination. The
	 * TSL module will be used to get information about the destination and for
	 * the client security the configuration will be used. In the case of
	 * processing errors on the receiving side the fault evidences will be
	 * thrown as EvidenceExceptions
	 * 
	 * @return The evidences related to the dispatch message. Including always a
	 *         SummissionAcceptanceRejection and in synchronous cases a
	 *         DeliveryNonDeliveryTorecipient, in asynchronous cases a
	 *         RelayRemMDAcceptanceRejection.
	 * @throws EvidenceException
	 *             In the cases of processing errors on the receiving side the
	 *             fault evidences will be thrown as EvidenceExceptions
	 */
	public DispatchMessageResponse sendDispatchMessage()
			throws EvidenceException, SpocsConfigurationException,
			SpocsSystemInstallationException {
		TrustedServiceImpl tslData;

		if (dispatch.getNormalizedMsg() == null
				|| dispatch.getOriginalMsg() == null) {
			throw new IllegalStateException(
					"Normalized and Original not defined!");
		}
		try {
			tslData = getTSLData(
					getAttributedElectronicAdress(
							dispatch.getMsgMetaData().getDestinations()
									.getRecipient()).getValue(), config);
		} catch (SpocsWrongInputDataException ex) {
			throw new SubmissionRejectionException(
					new SubmissionAcceptanceRejection(config, this, false),
					REMErrorEvent.UNKNOWN_RECIPIENT_ADDRESS, ex);
		}

		prepareSendMessage();

		String serviceSupplyPoint = tslData.getServiceSupplyPoints().iterator()
				.next();
		if (serviceSupplyPoint == null)
			throw new SubmissionRejectionException(createSubmissionRejection(),
					REMErrorEvent.UNKNOWN_RECIPIENT_ADDRESS, new Throwable(
							"no Service Supply point found!"));

		synchronized (DispatchMessage.class) {
			GenericService service;
			try {
				HttpsURLConnection
						.setDefaultHostnameVerifier(new HTTPSTransport(tslData)
								.createHostnameVerifier());
				HttpsURLConnection
						.setDefaultSSLSocketFactory(new HTTPSTransport(tslData)
								.createSSLFactory());
				service = getService(new URL(serviceSupplyPoint),
						new QName("http://spocsinterconnect.gateway.eu",
								"GatewayService"));
				QName port = new QName("http://spocsinterconnect.gateway.eu",
						"IntergatewayPort");

				jaxbDispatch = service.createDispatch(port, Message.class,
						Service.Mode.MESSAGE, new CallbackHandlerFeature(
								new EDeliveryCallbackHandler()), SpocsFragments
								.buildSpocsRMFheatures());

				Map<String, Object> ctxt = ((BindingProvider) jaxbDispatch)
						.getRequestContext();
				ctxt.put(JAXWSProperties.SSL_SOCKET_FACTORY,
						new HTTPSTransport(tslData).createSSLFactory());
				ctxt.put(JAXWSProperties.HOSTNAME_VERIFIER, new HTTPSTransport(
						tslData).createHostnameVerifier());
				ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

				// ###### Sign the dispatch message ##############

				Element signedElement = signEvidenceAndEnveloped();
				message = com.sun.xml.ws.api.message.Messages
						.createUsingPayload(new StreamSource(
								new ByteArrayInputStream(XMLSignatureHelper
										.dumpDomNode(signedElement)
										.toByteArray())),
								com.sun.xml.ws.api.SOAPVersion.SOAP_12);

			} catch (PrivilegedActionException ex) {
				throw new SubmissionRejectionException(
						createSubmissionRejection(),
						REMErrorEvent.UNKNOWN_RECIPIENT_ADDRESS, ex);
			} catch (MalformedURLException ex) {
				throw new SubmissionRejectionException(
						createSubmissionRejection(),
						REMErrorEvent.UNKNOWN_RECIPIENT_ADDRESS, ex);
			} catch (XWSSecurityException ex) {
				throw new SubmissionRejectionException(
						createSubmissionRejection(),
						REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, ex);
			} catch (SpocsWrongInputDataException ex) {
				throw new SubmissionRejectionException(
						createSubmissionRejection(),
						REMErrorEvent.WRONG_INPUT_DATA, ex);
			} catch (TransformerException ex) {
				throw new SubmissionRejectionException(
						createSubmissionRejection(),
						REMErrorEvent.WRONG_INPUT_DATA, ex);
			} catch (WebServiceException ex) {
				if (isConnectionException(ex.getCause()))
					throw new SubmissionRejectionException(
							createSubmissionRejection(),
							REMErrorEvent.UNREACHABLE, ex);
				else {
					throw new SubmissionRejectionException(
							createSubmissionRejection(), REMErrorEvent.OTHER,
							ex);
				}
			} catch (Throwable ex) {
				throw new SubmissionRejectionException(
						createSubmissionRejection(), REMErrorEvent.OTHER, ex);
			}
			// ################ now sending #########

			REMMDMessageType retEvidence = null;
			Document recievedDoc;
			try {
				Subject subject = new Subject();
				subject.getPublicCredentials().add("eDeliveryMode");
				subject.getPublicCredentials().add(samlProperties);
				GeneralAction action = new GeneralAction();
				retEvidence = Subject.doAs(subject, action);
				recievedDoc = action.getDocument();
			} catch (Throwable ex) {
				throw new SubmissionRejectionException(
						createSubmissionRejection(), REMErrorEvent.SOAP_FAULT,
						ex);
			}

			DispatchMessageResponse evidenceMessage;

			LogHelper.logOutgoingDispatchMessage(this);

			try {
				evidenceMessage = new DispatchMessageResponse(retEvidence,
						recievedDoc);
				if (!evidenceMessage.isSuccessfull())
					throw evidenceMessage.getEvidenceException();
			} catch (ParserConfigurationException ex) {
				throw new SpocsSystemInstallationException(ex);
			}

			return evidenceMessage;
		}
	}

	private boolean isConnectionException(Throwable ex) {
		Throwable exception = ex;
		if (exception != null) {
			if (ex.getClass().equals(java.net.ConnectException.class))
				return true;

			return isConnectionException(ex.getCause());
		}
		return false;
	}

	protected Element signEvidenceAndEnveloped()
			throws SpocsWrongInputDataException, JAXBException,
			SpocsConfigurationException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException,
			SpocsSystemInstallationException, ParserConfigurationException,
			SAXException, IOException, TransformerException {

		// sign the Evidence
		REMEvidenceType evidence = getEvidence(
				Evidences.SUBMISSION_ACCEPTANCE_REJECTION, getXSDObject()
						.getREMMDEvidenceList());
		// clearing the old EvidenceList !!
		getXSDObject().setREMMDEvidenceList(new REMMDEvidenceListType());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JaxbContextHolder
				.getETSIV2JaxBContext()
				.createMarshaller()
				.marshal(
						new org.etsi.uri._02640.v2_.ObjectFactory()
								.createSubmissionAcceptanceRejection(evidence),
						baos);

		Element ev = XMLSignatureHelper
				.createElementFromStream(new ByteArrayInputStream(baos
						.toByteArray()));

		Element evidenceElement = signEnveloped(ev);

		// sign the Dispatch

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(out);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document dispatchDocument = builder.parse(new ByteArrayInputStream(out
				.toByteArray()));

		Node firstDocImportedNode = dispatchDocument.importNode(
				evidenceElement, true);
		Element ele = dispatchDocument.getDocumentElement();
		ele.getElementsByTagName("ns7:REMMDEvidenceList").item(0)
				.appendChild(firstDocImportedNode);

		signEnveloped(ele);

		return ele;
	}

	private void addSubmissionAcceptance() {
		LOG.debug("Entry addSubmissionAcceptance");
		if (submissionAcceptanceRejection != null) {
			throw new IllegalStateException(
					"Evidence submissionAcceptanceRejection already set.");
		}
		submissionAcceptanceRejection = new SubmissionAcceptanceRejection(
				config, this, true);

		dispatch.getREMMDEvidenceList()
				.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure()
				.add(new org.etsi.uri._02640.v2_.ObjectFactory()
						.createSubmissionAcceptanceRejection(submissionAcceptanceRejection
								.getXSDObject()));
	}

	/**
	 * Do not need to be used because the submissionAcceptance will be created
	 * by this class before sending. Only in the case that the
	 * submissionAcceptance will be customized by the user this method must be
	 * used.
	 * 
	 */
	public void addSubmissionAcceptance(
			SubmissionAcceptanceRejection _submissionAcceptanceRejection) {
		submissionAcceptanceRejection = _submissionAcceptanceRejection;
		dispatch.getREMMDEvidenceList()
				.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure()
				.add(new org.etsi.uri._02640.v2_.ObjectFactory()
						.createSubmissionAcceptanceRejection(submissionAcceptanceRejection
								.getXSDObject()));

	}

	/**
	 * Do not need to be used because the submissionAcceptance will be created
	 * by this class before sending. Only in the case that the
	 * submissionAcceptance will be customized by the user this method must be
	 * used.
	 * 
	 */
	public void addSubmissionAcceptance(
			ReceivedByNonREMSystem _receivedByNonREMSystem) {
		receivedByNonREMSystem = _receivedByNonREMSystem;
		dispatch.getREMMDEvidenceList()
				.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure()
				.add(new org.etsi.uri._02640.v2_.ObjectFactory()
						.createReceivedFromNonREMSystem(receivedByNonREMSystem
								.getXSDObject()));

	}

	private SubmissionAcceptanceRejection createSubmissionRejection() {
		return new SubmissionAcceptanceRejection(config, this, false);
	}

	/**
	 * Returns the already set SubmissionAcceptanceRejection or null
	 * 
	 */
	public SubmissionAcceptanceRejection getSubmissionAcceptanceRejection() {
		// if (submissionAcceptanceRejection != null) {
		return submissionAcceptanceRejection;
		// } else {
		// submissionAcceptanceRejection = new SubmissionAcceptanceRejection(
		// getEvidence(Evidences.SUBMISSION_ACCEPTANCE_REJECTION,
		// getXSDObject().getREMMDEvidenceList()));
		// return submissionAcceptanceRejection;
		// }
	}

	/**
	 * Returns the already set ReceivedByNonREMSystem or null
	 * 
	 */
	public ReceivedByNonREMSystem getReceivedByNonREMSystem() {
		return receivedByNonREMSystem;
	}

	public Map<String, String> getSamlAttributes() throws XWSSecurityException,
			SAMLException {
		Map<String, String> samlAttributes = new HashMap<String, String>();

		if (submissionAcceptanceRejection != null
				&& submissionAcceptanceRejection.getXSDObject()
						.getSenderAuthenticationDetails() != null) {
			AnyType details = submissionAcceptanceRejection.getXSDObject()
					.getSenderAuthenticationDetails().getAdditionalDetails();
			if (details.getContent() != null && details.getContent().size() > 0) {
				SAMLAssertionFactory factory = SAMLAssertionFactory
						.newInstance(SAMLAssertionFactory.SAML2_0);
				Object obj = details.getContent().get(0);
				Assertion assertion = null;
				AssertionType assertionType = null;
				if (obj instanceof JAXBElement<?>) {
					if (((JAXBElement<?>) obj).getValue() instanceof Element) {
						assertion = factory
								.createAssertion((Element) ((JAXBElement<?>) obj)
										.getValue());
					} else if (((JAXBElement<?>) obj).getValue() instanceof AssertionType) {
						assertionType = (AssertionType) ((JAXBElement<?>) obj)
								.getValue();
					}
				} else if (obj instanceof Element) {
					assertion = factory.createAssertion((Element) obj);
				}

				if (assertion != null) {
					List<Object> statements = assertion.getStatements();
					for (Object statement : statements) {
						if (statement instanceof AttributeStatement) {
							AttributeStatement attributeStatement = (AttributeStatement) statement;
							for (Attribute attribute : attributeStatement
									.getAttributes()) {
								List<Object> value = attribute.getAttributes();
								if (null != value && value.size() != 0) {
									samlAttributes.put(attribute.getName(),
											value.get(0).toString());
								}
							}
						}
					}
				} else if (assertionType != null) {
					List<StatementAbstractType> statements = assertionType
							.getStatementOrAuthnStatementOrAuthzDecisionStatement();
					for (StatementAbstractType statement : statements) {
						if (statement instanceof AttributeStatementType) {
							AttributeStatementType attributeStatement = (AttributeStatementType) statement;
							for (Object attributeOrEncAttr : attributeStatement
									.getAttributeOrEncryptedAttribute()) {
								if (attributeOrEncAttr instanceof AttributeType) {
									AttributeType attribute = (AttributeType) attributeOrEncAttr;
									List<Object> value = attribute
											.getAttributeValue();
									if (null != value && value.size() != 0) {
										samlAttributes.put(attribute.getName(),
												value.get(0).toString());
									}
								}
							}
						}
					}
				}
			}
		}
		return samlAttributes;
	}

	public SAMLProperties getSamlProperties() throws XWSSecurityException,
			SAMLException {

		Map<String, String> map = getSamlAttributes();

		SAMLProperties samlProps = new SAMLProperties();

		if (map.get("http://www.stork.gov.eu/1.0/citizenQAAlevel") != null)
			samlProps.setCitizenQAAlevel(Integer.parseInt(map
					.get("http://www.stork.gov.eu/1.0/citizenQAAlevel")));
		if (map.get("http://www.stork.gov.eu/1.0/surname") != null)
			samlProps
					.setSurname(map.get("http://www.stork.gov.eu/1.0/surname"));
		if (map.get("http://www.stork.gov.eu/1.0/givenName") != null)
			;
		samlProps
				.setGivenName(map.get("http://www.stork.gov.eu/1.0/givenName"));
		if (map.get("http://www.stork.gov.eu/1.0/eIdentifier") != null)
			samlProps.seteIdentifier(map
					.get("http://www.stork.gov.eu/1.0/eIdentifier"));
		if (map.get("http://www.stork.gov.eu/1.0/dateOfBirth") != null)
			samlProps.setDateOfBirth(convertSAMLDate(map
					.get("http://www.stork.gov.eu/1.0/dateOfBirth")));

		return samlProps;

	}

	private Date convertSAMLDate(String date) {

		String[] dateArray = date.split("-");
		GregorianCalendar cal = new GregorianCalendar();

		cal.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
		cal.set(Calendar.MONTH, Integer.parseInt(dateArray[1]));
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));

		return cal.getTime();
	}

	protected class GeneralAction implements
			PrivilegedExceptionAction<REMMDMessageType> {
		private Document document = null;

		public GeneralAction() {
		}

		@Override
		public REMMDMessageType run() throws JAXBException, IOException,
				SpocsWrongInputDataException, XMLSignatureException {
			final Logger LOG = LoggerFactory.getLogger(DispatchMessage.class
					.getName());
			Message retMsg = jaxbDispatch.invoke(message);
			Unmarshaller unmarshaller = null;
			JAXBElement<?> jaxbObject = null;
			try {
				unmarshaller = JaxbMarshallerHolder.getSpocsMarshallerHolder()
						.getUnmarshaller();

				document = getReceiveMessageStream(retMsg);
				XMLSignatureHelper.validateSignature(document);
				if (config.geteDeliveryDetails().isCheckMessage()) {
					SchemaFactory sf = SchemaFactory
							.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
					URL url = DispatchMessage.class
							.getClassLoader()
							.getResource(
									"WEB-INF/wsdl/SPOCS_ts102640_soap_body.xsd");
					Schema schema = sf.newSchema(url);
					unmarshaller.setSchema(schema);
					unmarshaller.setEventHandler(new ValidationEventHandler() {

						@Override
						public boolean handleEvent(ValidationEvent val) {
							LOG.error("======== Validation Failed! ========");
							LOG.error("Line: "
									+ val.getLocator().getLineNumber()
									+ " Column: "
									+ val.getLocator().getColumnNumber());
							LOG.error("Details:" + val.toString());
							LOG.error("======== Validation Failed! ========");
							return true;
						}
					});
				}

				jaxbObject = (JAXBElement<?>) unmarshaller.unmarshal(document);
			} catch (SAXException e) {
				LOG.warn("Error Validating the Message! ", e);
			} catch (TransformerException e) {
				LOG.warn("Error while parsing InboundMessage! ", e);
			} finally {
				try {
					JaxbMarshallerHolder.getSpocsMarshallerHolder().recycle(
							unmarshaller);
				} catch (JAXBException e) {
					LOG.info("Could not recycle the spocs unmarshaller");
				}

			}
			return (REMMDMessageType) jaxbObject.getValue();
		}

		public Document getDocument() {
			return document;
		}

	}

	public static Document getReceiveMessageStream(Message soap)
			throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		DOMResult domResult = new DOMResult();
		transformer.transform(soap.readPayloadAsSource(), domResult);
		return (Document) domResult.getNode();
	}

}
