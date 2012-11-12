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
package eu.spocseu.edeliverygw.process;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.soapbinding.v1_.REMMDMessageType;
import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.LogHelper;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.SpocsGWProcessingException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.XMLSignatureHelper;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.evidences.Evidence;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.NonDeliveryException;
import eu.spocseu.edeliverygw.evidences.exception.RelayREMMDRejectionException;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import eu.spocseu.edeliverygw.messages.AcceptanceRejectionByRecipientMessage;
import eu.spocseu.edeliverygw.messages.DeliveryNonDeliveryToRecipientMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessageResponse;
import eu.spocseu.edeliverygw.messages.EvidenceMessage;
import eu.spocseu.edeliverygw.messages.GeneralMessage;
import eu.spocseu.edeliverygw.messages.RetrievalNonRetrievalByRecipientMessage;
import eu.spocseu.tsl.TrustedServiceImpl;

public class ProcessMessage
{
	private static final Logger LOG = LoggerFactory
			.getLogger(ProcessMessage.class);

	public static void initOnStartup(Configuration conf)
	{

		conf.geteDeliveryDetails().getGwImplementation().initOnStartup();
	}

	public static REMMDMessageType processDispatchMessage(
			REMDispatchType _dispatch, Document doc, Configuration config)
			throws RelayREMMDRejectionException, NonDeliveryException,
			SOAPException
	{
		try {
			DispatchMessage incomingMsg = new DispatchMessage(_dispatch, doc);
			if (incomingMsg.getXSDObject().getMsgMetaData().getOriginators() == null
					|| incomingMsg.getXSDObject().getMsgMetaData()
							.getOriginators().getFrom() == null
					|| GeneralMessage.getAttributedElectronicAdress(incomingMsg
							.getXSDObject().getMsgMetaData().getOriginators()
							.getFrom()) == null) {
				throw new RelayREMMDRejectionException(config, incomingMsg,
					REMErrorEvent.WRONG_INPUT_DATA,
					new SpocsWrongInputDataException(
						"Sender information is not avaliable."));
			}
			String adress = GeneralMessage.getAttributedElectronicAdress(
				incomingMsg.getXSDObject().getMsgMetaData().getOriginators()
						.getFrom()).getValue();

			SubmissionAcceptanceRejection submissionAcceptanceRejection = new SubmissionAcceptanceRejection(
				DispatchMessage.getEvidence(
					Evidences.SUBMISSION_ACCEPTANCE_REJECTION,
					_dispatch.getREMMDEvidenceList()));
			EvidenceMessage retEvidences = null;
			TrustedServiceImpl tsl = null;
			try {
				tsl = DispatchMessage.getTSLData(adress, config);
			} catch (SpocsWrongInputDataException ex) {
				LOG.error("Error: ", ex);
				throw new RelayREMMDRejectionException(config, incomingMsg,
					REMErrorEvent.UNKNOWN_ORIGINATOR_ADDRESS, ex);
			}
			try {
				checkContentSignature(doc,
					tsl.getServiceSignatureCertificate(), config);
			} catch (Exception ex) {
				throw new RelayREMMDRejectionException(config, incomingMsg,
					REMErrorEvent.WRONG_INPUT_DATA, ex);
			}
			retEvidences = config.geteDeliveryDetails().getGwImplementation()
					.processDispatch(incomingMsg, tsl);
			if (retEvidences == null) {
				if (config.geteDeliveryDetails().isSynchronGatewayMD()) {

					LOG.debug("System add the DeliveryNonDeliveryToRecipient evident");
					try {
						return new DispatchMessageResponse(config,
							submissionAcceptanceRejection,
							Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT)
								.getXSDObject();
					} catch (Exception ex) {
						throw new NonDeliveryException(config, incomingMsg,
							REMErrorEvent.WRONG_INPUT_DATA, ex);

					}
				} else {
					LOG.debug("System add the RelayToREMMDAcceptanceRejection evident");
					try {
						return new DispatchMessageResponse(config,
							submissionAcceptanceRejection,
							Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION)
								.getXSDObject();
					} catch (Exception ex) {
						LOG.warn("Error", ex);
						throw new NonDeliveryException(config, incomingMsg,
							REMErrorEvent.WRONG_INPUT_DATA, ex);

					}
				}
			} else {
				return retEvidences.getXSDObject();
			}
		} catch (SpocsConfigurationException ex) {
			LOG.error("not possible", ex);
			throw new SOAPException(ex);
		}
	}

	public static void processEvidenceMessage(REMMDMessageType _evidenceJaxB,
			Document doc, Configuration config)
			throws SpocsWrongInputDataException, SpocsConfigurationException,
			SpocsGWProcessingException, SpocsSystemInstallationException
	{
		LogHelper.logIncomingMessage(_evidenceJaxB);

		SpocsGWInterface gwImpl = config.geteDeliveryDetails()
				.getGwImplementation();
		if (GeneralMessage.getEvidence(
			Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT,
			_evidenceJaxB.getREMMDEvidenceList()) != null) {
			DeliveryNonDeliveryToRecipientMessage message = new DeliveryNonDeliveryToRecipientMessage(
				_evidenceJaxB, doc);
			TrustedServiceImpl tsl = getTSLData(
				message.getDeliveryNonDeliveryToRecipientObj(), config);
			LOG.debug("checking EvidenceMessage Signature");
			checkEvidenceSignature(doc, tsl.getServiceSignatureCertificate(),
				config);

			LOG.debug("Process DeliveryNonDeliveryToRecipientMessage");
			gwImpl.processEvidence(message, tsl);

		} else if (GeneralMessage.getEvidence(
			Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT,
			_evidenceJaxB.getREMMDEvidenceList()) != null) {
			AcceptanceRejectionByRecipientMessage message = new AcceptanceRejectionByRecipientMessage(
				_evidenceJaxB, doc);
			TrustedServiceImpl tsl = getTSLData(
				message.getAcceptanceRejectionByRecipientObj(), config);
			LOG.debug("checking EvidenceMessage Signature");
			checkEvidenceSignature(doc, tsl.getServiceSignatureCertificate(),
				config);
			LOG.debug("Process AcceptanceRejectionByRecipientMessage");
			gwImpl.processEvidence(
				message,
				getTSLData(message.getAcceptanceRejectionByRecipientObj(),
					config));

		} else if (GeneralMessage.getEvidence(
			Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT,
			_evidenceJaxB.getREMMDEvidenceList()) != null) {
			RetrievalNonRetrievalByRecipientMessage message = new RetrievalNonRetrievalByRecipientMessage(
				_evidenceJaxB, doc);
			TrustedServiceImpl tsl = getTSLData(
				message.getRetrievalNonRetrievalByRecipientObj(), config);
			LOG.debug("checking EvidenceMessage Signature");
			checkEvidenceSignature(doc, tsl.getServiceSignatureCertificate(),
				config);
			LOG.debug("Process RetrievalNonRetrievalByRecipientMessage");
			gwImpl.processEvidence(
				message,
				getTSLData(message.getRetrievalNonRetrievalByRecipientObj(),
					config));
		}

	}

	private static TrustedServiceImpl getTSLData(Evidence message,
			Configuration config) throws SpocsWrongInputDataException
	{

		AttributedElectronicAddressType elAddr = SpocsFragments
				.getFirstElectronicAddressWithURI(message.getXSDObject()
						.getEvidenceIssuerDetails());
		String adress = elAddr.getValue();
		return GeneralMessage.getTSLData(adress, config);

	}

	public static void checkEvidenceSignature(Document doc,
			X509Certificate tslCert, Configuration config)
			throws SpocsWrongInputDataException,
			SpocsSystemInstallationException
	{

		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new SpocsSystemInstallationException(e);
		}
		StringWriter buffer = new StringWriter();

		Node signedEvidenceMessage = doc.getElementsByTagName(
			"ns7:REMMDEvidenceList").item(0);
		try {
			transformer.transform(new DOMSource(signedEvidenceMessage),
				new StreamResult(buffer));
		} catch (TransformerException e) {
			throw new SpocsWrongInputDataException(e);
		}
		String str = buffer.toString();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder;
		Document evidenceDocument;
		try {
			builder = dbf.newDocumentBuilder();
			evidenceDocument = builder.parse(new InputSource(new StringReader(
				str)));
		} catch (ParserConfigurationException e) {
			throw new SpocsSystemInstallationException(e);

		} catch (SAXException e) {
			throw new SpocsSystemInstallationException(e);

		} catch (IOException e) {
			throw new SpocsSystemInstallationException(e);

		}

		checkContentSignature(evidenceDocument, tslCert, config);
	}

	public static void checkContentSignature(Document doc,
			X509Certificate tslCert, Configuration config)
			throws SpocsWrongInputDataException,
			SpocsSystemInstallationException
	{
		MessageDigest dg;
		if (config.geteDeliveryDetails().isCheckSignature()) {

			try {

				// LOG.debug("Checking content signature.");
				// // First check the dispatch signature
				// NodeList childList =
				// doc.getDocumentElement().getChildNodes();
				// Node signatureNode = null;
				// for (int i = 0; i < childList.getLength(); i++) {
				// if (childList.item(i).getLocalName().equals("Signature")) {
				// signatureNode = childList.item(i);
				// LOG.debug("signature element found");
				// break;
				// }
				// }
				// if (signatureNode == null)
				// throw new XMLSignatureException("No signature found");

				X509Certificate[] senderCert = XMLSignatureHelper
						.validateSignature(doc);
				dg = MessageDigest.getInstance("SHA1");

				byte[] tslCertHash = dg.digest(tslCert.getEncoded());

				byte[] senderCertHash = dg.digest(senderCert[0].getEncoded());

				if (!Arrays.equals(tslCertHash, senderCertHash))
					throw new XMLSignatureException(
						"Signature certificate of sender is not the expected one out of the TSL");
			} catch (XMLSignatureException ex) {
				throw new SpocsWrongInputDataException(
					"Error validate the signature.", ex);
			} catch (NoSuchAlgorithmException ex) {
				throw new SpocsSystemInstallationException(
					"Error validate the signature.", ex);
			} catch (CertificateEncodingException ex) {
				throw new SpocsWrongInputDataException(
					"TSL or signature certificate not parsable.", ex);
			}
		} else {
			LOG.info("Dispatch signature check is disabled.");
		}
	}
}
