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
package eu.spocseu.edeliverygw.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;

import org.etsi.uri._02640.soapbinding.v1_.REMMDEvidenceListType;
import org.etsi.uri._02640.soapbinding.v1_.REMMDMessageType;
import org.etsi.uri._02640.v2_.ObjectFactory;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.security.CallbackHandlerFeature;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.BackoffAlgorithm;
import com.sun.xml.wss.XWSSecurityException;

import eu.spocseu.common.SpocsConstants;
import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.SpocsHelper;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.LogHelper;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.XMLSignatureHelper;
import eu.spocseu.edeliverygw.callbackhandler.EDeliveryCallbackHandler;
import eu.spocseu.edeliverygw.evidences.AcceptanceRejectionByRecipient;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.Evidence;
import eu.spocseu.edeliverygw.evidences.RelayREMMDAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.RelayREMMDFailure;
import eu.spocseu.edeliverygw.evidences.RetrievalNonRetrievalByRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.evidences.exception.NonDeliveryException;
import eu.spocseu.edeliverygw.evidences.exception.NonRetrievalRecipientException;
import eu.spocseu.edeliverygw.evidences.exception.RejectionByRecipientException;
import eu.spocseu.edeliverygw.evidences.exception.RelayREMMDRejectionException;
import eu.spocseu.edeliverygw.evidences.exception.RelayToREMMDFailureException;
import eu.spocseu.edeliverygw.evidences.exception.SubmissionRejectionException;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import eu.spocseu.edeliverygw.transport.HTTPSTransport;
import eu.spocseu.tsl.TrustedServiceImpl;

public class EvidenceMessage extends GeneralMessage
{

	private static Logger LOG = LoggerFactory.getLogger(EvidenceMessage.class);
	protected boolean isSuccessfull = false;
	private EvidenceException evidenceException = null;
	public REMMDMessageType jaxbObj;
	protected Evidence receivedEvidence;

	public EvidenceMessage() throws SpocsConfigurationException
	{
		super();
		jaxbObj = new REMMDMessageType();
		jaxbObj.setId(UUID.randomUUID().toString());
		jaxbObj.setREMMDEvidenceList(new REMMDEvidenceListType());
	}

	/**
	 * Creates a EvidenceMessage with a given REMMDMessageType object.
	 * 
	 * @param _evidence
	 *            A JAXB object to build from.
	 */
	protected EvidenceMessage(REMMDMessageType _evidence, Document doc)
		throws SpocsConfigurationException
	{
		document = doc;
		initInstance(_evidence);
	}

	/**
	 * Creates a EvidenceMessage with a given REMMDMessageType object.
	 * 
	 * @param _evidence
	 *            A JAXB object to build from.
	 * @throws SpocsSystemInstallationException
	 * @throws ParserConfigurationException
	 */
	protected EvidenceMessage(REMMDMessageType _evidence, Source payload)
		throws SpocsConfigurationException, SpocsSystemInstallationException,
		ParserConfigurationException
	{
		DOMResult result = new DOMResult();
		transform(payload, result, "UTF-8", -1);

		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		doc.appendChild(doc.importNode(result.getNode(), true));
		document = doc;
		initInstance(_evidence);
	}

	/**
	 * Creates a EvidenceMessage with a given REMMDMessageType object.
	 * 
	 * @param _evidence
	 *            A JAXB object to build from.
	 * @throws SpocsSystemInstallationException
	 * @throws ParserConfigurationException
	 */
	protected EvidenceMessage(REMMDMessageType _evidence)
		throws SpocsConfigurationException, SpocsSystemInstallationException,
		ParserConfigurationException
	{
		initInstance(_evidence);
	}

	protected void initInstance(REMMDMessageType _evidence)
	{
		jaxbObj = _evidence;
		if (jaxbObj.getId() == null)
			jaxbObj.setId(UUID.randomUUID().toString());
		REMEvidenceType acceptanceRejection = getEvidence(
			Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT,
			jaxbObj.getREMMDEvidenceList());

		if (acceptanceRejection != null) {
			addAcceptanceRejectionByRecipient(
				new AcceptanceRejectionByRecipient(acceptanceRejection), true);
		}
		REMEvidenceType deliveryNonDelivery = getEvidence(
			Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT,
			jaxbObj.getREMMDEvidenceList());
		if (deliveryNonDelivery != null) {
			addDeliveryNonDeliveryToRecipient(
				new DeliveryNonDeliveryToRecipient(deliveryNonDelivery), true);
		}

		REMEvidenceType relayAcceptanceRejection = getEvidence(
			Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION,
			jaxbObj.getREMMDEvidenceList());
		if (relayAcceptanceRejection != null) {
			addRelayToREMMDAcceptanceRejection(
				new RelayREMMDAcceptanceRejection(relayAcceptanceRejection),
				true);
		}

		REMEvidenceType remmdFailure = getEvidence(
			Evidences.RELAY_REM_MD_FAILURE, jaxbObj.getREMMDEvidenceList());
		if (remmdFailure != null) {
			addRelayToREMMDFailure(new RelayREMMDFailure(remmdFailure), true);
		}

		REMEvidenceType retrievalNonRetrieval = getEvidence(
			Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT,
			jaxbObj.getREMMDEvidenceList());
		if (retrievalNonRetrieval != null) {
			addRetrievalNonRetrievalByRecipient(
				new RetrievalNonRetrievalByRecipient(retrievalNonRetrieval),
				true);
		}

		if (isSuccessfull)
			LOG.debug("No failed evidence found.");
		else
			LOG.debug("Found evidence with faults.");

	}

	/**
	 * Returns the found fault evidence.
	 */
	public EvidenceException getEvidenceException()
	{
		return evidenceException;
	}

	/**
	 * Returns false if a failure event was found.
	 */
	public boolean isSuccessfull()
	{
		return isSuccessfull;
	}

	protected void addRelayToREMMDAcceptanceRejection(
			RelayREMMDAcceptanceRejection _relayToREMMDAcceptanceRejection,
			boolean parseObject)
	{
		LOG.info("RelayToREMMDAcceptanceRejection added.");
		if (parseObject) {
			if (_relayToREMMDAcceptanceRejection
					.getXSDObject()
					.getEventCode()
					.equals(
						SpocsConstants.Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
								.getFaultEventCode())) {
				isSuccessfull = false;
				LOG.info("Add fault case: "
						+ SpocsConstants.Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
								.getName());
				evidenceException = new RelayREMMDRejectionException(
					_relayToREMMDAcceptanceRejection,
					REMErrorEvent
							.getRemErrorEventForJaxB(_relayToREMMDAcceptanceRejection
									.getXSDObject().getEventReasons()), null);

			} else {
				isSuccessfull = true;
				receivedEvidence = _relayToREMMDAcceptanceRejection;

			}
		} else {
			jaxbObj.getREMMDEvidenceList()
					.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure()
					.add(
						new ObjectFactory()
								.createRelayREMMDAcceptanceRejection(_relayToREMMDAcceptanceRejection
										.getXSDObject()));
		}

	}

	protected void addRelayToREMMDFailure(
			RelayREMMDFailure _relayToREMMDFailure, boolean parseObject)
	{
		LOG.info("RelayToREMMDFailure added.");
		if (parseObject) {
			if (_relayToREMMDFailure
					.getXSDObject()
					.getEventCode()
					.equals(
						SpocsConstants.Evidences.RELAY_REM_MD_FAILURE
								.getFaultEventCode())) {
				isSuccessfull = false;
				evidenceException = new RelayToREMMDFailureException(
					_relayToREMMDFailure,
					REMErrorEvent.getRemErrorEventForJaxB(_relayToREMMDFailure
							.getXSDObject().getEventReasons()), null);

			} else {
				isSuccessfull = true;
				receivedEvidence = _relayToREMMDFailure;
			}
		} else {
			jaxbObj.getREMMDEvidenceList()
					.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure()
					.add(
						new ObjectFactory()
								.createRelayREMMDFailure(_relayToREMMDFailure
										.getXSDObject()));
		}
	}

	protected void addDeliveryNonDeliveryToRecipient(
			DeliveryNonDeliveryToRecipient _deliveryNonDeliveryToRecipient,
			boolean parseObject)
	{
		LOG.info("DeliveryNonDeliveryToRecipient added.");
		if (parseObject) {
			if (_deliveryNonDeliveryToRecipient
					.getXSDObject()
					.getEventCode()
					.equals(
						SpocsConstants.Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
								.getFaultEventCode())) {

				isSuccessfull = false;
				evidenceException = new NonDeliveryException(
					_deliveryNonDeliveryToRecipient,
					REMErrorEvent
							.getRemErrorEventForJaxB(_deliveryNonDeliveryToRecipient
									.getXSDObject().getEventReasons()), null);
			} else {
				isSuccessfull = true;
				receivedEvidence = _deliveryNonDeliveryToRecipient;
			}
		} else {

			jaxbObj.getREMMDEvidenceList()
					.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure()
					.add(
						new ObjectFactory()
								.createDeliveryNonDeliveryToRecipient(_deliveryNonDeliveryToRecipient
										.getXSDObject()));
		}

	}

	protected void addRetrievalNonRetrievalByRecipient(
			RetrievalNonRetrievalByRecipient _retrievalNonRetrievalByRecipient,
			boolean parseObject)
	{
		LOG.info("RetrievalNonRetrievalByRecipient added.");
		if (parseObject) {
			if (_retrievalNonRetrievalByRecipient
					.getXSDObject()
					.getEventCode()
					.equals(
						SpocsConstants.Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT
								.getFaultEventCode())) {
				isSuccessfull = false;
				evidenceException = new NonRetrievalRecipientException(
					_retrievalNonRetrievalByRecipient,
					REMErrorEvent
							.getRemErrorEventForJaxB(_retrievalNonRetrievalByRecipient
									.getXSDObject().getEventReasons()), null);
			} else {
				receivedEvidence = _retrievalNonRetrievalByRecipient;
				isSuccessfull = true;
			}
		} else {

			jaxbObj.getREMMDEvidenceList()
					.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure()
					.add(
						new ObjectFactory()
								.createRetrievalNonRetrievalByRecipient(_retrievalNonRetrievalByRecipient
										.getXSDObject()));
		}
	}

	protected void addAcceptanceRejectionByRecipient(
			AcceptanceRejectionByRecipient _acceptanceRejectionByRecipient,
			boolean parseObject)
	{
		LOG.info("AcceptanceRejectionByRecipient added.");
		if (parseObject) {
			if (_acceptanceRejectionByRecipient
					.getXSDObject()
					.getEventCode()
					.equals(
						SpocsConstants.Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT
								.getFaultEventCode())) {
				isSuccessfull = false;
				evidenceException = new RejectionByRecipientException(
					_acceptanceRejectionByRecipient,
					REMErrorEvent
							.getRemErrorEventForJaxB(_acceptanceRejectionByRecipient
									.getXSDObject().getEventReasons()), null);
			} else {
				receivedEvidence = _acceptanceRejectionByRecipient;
				isSuccessfull = true;
			}
		} else {

			jaxbObj.getREMMDEvidenceList()
					.getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure()
					.add(
						new ObjectFactory()
								.createAcceptanceRejectionByRecipient(_acceptanceRejectionByRecipient
										.getXSDObject()));
		}

	}

	/**
	 * Serializes the underlying JAXB object into the given OutputStream
	 * 
	 * @throws SpocsSystemInstallationException
	 * @throws SpocsConfigurationException
	 */
	public void serialize(OutputStream out) throws SpocsConfigurationException,
			SpocsSystemInstallationException
	{
		if (document == null) {
			LOG.debug("No Document declared serializing through JAXB");
			try {
				JaxbContextHolder
						.getSpocsJaxBContext()
						.createMarshaller()
						.marshal(
							new org.etsi.uri._02640.soapbinding.v1_.ObjectFactory()
									.createREMMDMessage(getXSDObject()), out);
			} catch (JAXBException e) {
				throw new SpocsSystemInstallationException(e);
			}
		} else {
			LOG.info("serializing through DOM");
			Result result = new StreamResult(out);
			transform(new DOMSource(document), result, "UTF-8", -1);

		}
	}

	public Evidence getReceivedEvidence()
	{
		return receivedEvidence;
	}

	/**
	 * Gets the underlying JAXB object, for example to add some detailed
	 * information with this method.
	 * 
	 * @return The JAXB object.
	 */
	public REMMDMessageType getXSDObject()
	{
		return jaxbObj;
	}

	public String findToAddress(Evidence evidenceObj)
	{

		// first find the reply address
		if (evidenceObj.getXSDObject().getReplyToAddress() != null)
			return evidenceObj.getXSDObject().getReplyToAddress().getValue();
		// second find the reply
		if (evidenceObj.getXSDObject().getReplyTo() != null)
			return evidenceObj.getXSDObject().getReplyTo();
		// third get the sender addr
		return SpocsFragments.getFirstElectronicAddressWithURI(
			evidenceObj.getXSDObject().getSenderDetails()).getValue();
	}

	/**
	 * 
	 * @throws EvidenceException
	 * @throws SpocsWrongInputDataException
	 * @throws SpocsSystemInstallationException
	 * @throws SpocsConfigurationException
	 */
	protected void sendEvidenceMessage(Evidence evidenceObj)
			throws EvidenceException, SpocsWrongInputDataException,
			SpocsSystemInstallationException, SpocsConfigurationException
	{

		if (!isSuccessfull()) {
			if (evidenceObj.getEventReson() == null) {
				evidenceObj.setEventReason(REMErrorEvent.OTHER);
			}
		}
		String eAdress = findToAddress(evidenceObj);
		LOG.debug("To Address: " + eAdress);
		TrustedServiceImpl tslData = getTSLData(eAdress, config);

		String supplyPoint = tslData.getServiceSupplyPoints().iterator().next();

		if (supplyPoint == null)
			throw new SpocsWrongInputDataException(
				"no service supply point found!");

		LOG.info("Send evidence message to: " + supplyPoint);
		synchronized (DispatchMessage.class) {
			GenericService service;
			try {
				HttpsURLConnection
						.setDefaultHostnameVerifier(new HTTPSTransport(tslData)
								.createHostnameVerifier());
				HttpsURLConnection
						.setDefaultSSLSocketFactory(new HTTPSTransport(tslData)
								.createSSLFactory());
				service = getService(new URL(supplyPoint), new QName(
					"http://spocsinterconnect.gateway.eu", "GatewayService"));
				QName port = new QName("http://spocsinterconnect.gateway.eu",
					"IntergatewayPort");

				jaxbDispatch = service.createDispatch(port, Message.class,
					Service.Mode.MESSAGE, new CallbackHandlerFeature(
						new EDeliveryCallbackHandler()), SpocsFragments
							.buildSpocsRMFheatures());
				jaxbDispatch.getRequestContext().put(
					JAXWSProperties.SSL_SOCKET_FACTORY,
					new HTTPSTransport(tslData).createSSLFactory());
				jaxbDispatch.getRequestContext().put(
					JAXWSProperties.HOSTNAME_VERIFIER,
					new HTTPSTransport(tslData).createHostnameVerifier()); 
				jaxbDispatch.getRequestContext().put(
					BindingProvider.SESSION_MAINTAIN_PROPERTY, true);		
				
				// ###### Sign the evidence message ##############

				Element signedElement = signEvidences(jaxbObj);
				message = com.sun.xml.ws.api.message.Messages
						.createUsingPayload(
							new StreamSource(new ByteArrayInputStream(
								XMLSignatureHelper.dumpDomNode(signedElement)
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
			} catch (TransformerException ex) {
				throw new SubmissionRejectionException(
					createSubmissionRejection(),
					REMErrorEvent.WRONG_INPUT_DATA, ex);
			} catch (WebServiceException ex) {
				if (isConnectionException(ex.getCause()))
					throw new SubmissionRejectionException(
						createSubmissionRejection(), REMErrorEvent.UNREACHABLE,
						ex);
				else {
					throw new SubmissionRejectionException(
						createSubmissionRejection(), REMErrorEvent.OTHER, ex);
				}
			} catch (Throwable ex) {
				throw new SubmissionRejectionException(
					createSubmissionRejection(), REMErrorEvent.OTHER, ex);
			}

			// ################ now sending #########
			try {
				Subject subject = new Subject();
				subject.getPublicCredentials().add(samlProperties);
				Subject.doAs(subject, new GeneralAction());
				LogHelper.logOutgoingEvidenceMessage(this);

			} catch (Throwable ex) {
				throw new SubmissionRejectionException(
					"Error sending the AcceptanceRejection evidence.",
					createSubmissionRejection(), REMErrorEvent.SOAP_FAULT, ex);
			}
		}
	}

	private boolean isConnectionException(Throwable ex)
	{
		Throwable exception = ex;
		if (exception != null) {
			if (ex.getClass().equals(java.net.ConnectException.class))
				return true;

			return isConnectionException(ex.getCause());
		}
		return false;
	}

	private SubmissionAcceptanceRejection createSubmissionRejection()
	{
		return new SubmissionAcceptanceRejectionError(config, this);
	}

	private class GeneralAction implements PrivilegedExceptionAction<Object>
	{

		public GeneralAction()
		{
		}

		@Override
		public Object run() throws Exception
		{
			jaxbDispatch.invoke(message);
			// ((Closeable) jaxbDispatch).close();
			return null;

		}
	}

	@SuppressWarnings("unchecked")
	protected JAXBElement<?> readObjectJaxB(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException
	{

		try {
			return (JAXBElement<DeliveryNonDeliveryToRecipient>) JaxbContextHolder
					.getSpocsJaxBContext().createUnmarshaller().unmarshal(in);

		} catch (JAXBException ex) {
			throw new IOException(ex);
		}
	}

}
