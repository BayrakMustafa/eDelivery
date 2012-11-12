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

import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.etsi.uri._02640.soapbinding.v1_.ObjectFactory;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.soapbinding.v1_.REMMDMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.EvidenceMessage;
import eu.spocseu.edeliverygw.messages.GeneralMessage;

/**
 * Helper class which provides some static Method to log Messages.
 */
public class LogHelper
{
	public static final Logger LOG = LoggerFactory.getLogger(LogHelper.class);
	public static final Logger protocolLogger = LoggerFactory
			.getLogger("ProtocolLogger");
	public static final Logger escaltionLogger = LoggerFactory
			.getLogger("EscalationLogger");

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

	/**
	 * Logs the
	 * <code>InitialMsgId,OriginatorFrom,OriginatorSender,Recipent </code> of
	 * the given {@link REMDispatchType}
	 * 
	 * @param dispatchType the {@link REMDispatchType} which should be logged
	 */

	public static void logIncomingMessage(REMDispatchType dispatchType)
	{
		try {

			StringBuffer buf = new StringBuffer();
			buf.append("IncomingMsg");
			buf.append("|InitialMsgId:"
					+ dispatchType.getMsgMetaData().getMsgIdentification()
							.getMessageID());
			if (dispatchType.getMsgMetaData().getOriginators() != null
					&& dispatchType.getMsgMetaData().getOriginators().getFrom() != null)
				buf.append("|OriginatorFrom:"
						+ GeneralMessage.getAttributedElectronicAdress(
							dispatchType.getMsgMetaData().getOriginators()
									.getFrom()).getValue());
			if (dispatchType.getMsgMetaData().getOriginators() != null
					&& dispatchType.getMsgMetaData().getOriginators()
							.getSender() != null)
				buf.append("|OriginatorSender:"
						+ GeneralMessage.getAttributedElectronicAdress(dispatchType.getMsgMetaData().getOriginators()
								.getSender()).getValue());
			if (dispatchType.getMsgMetaData().getDestinations() != null
					&& dispatchType.getMsgMetaData().getDestinations()
							.getRecipient() != null)

				if (GeneralMessage.getAttributedElectronicAdress(dispatchType.getMsgMetaData().getDestinations()
						.getRecipient()).getValue() != null
						&& GeneralMessage.getAttributedElectronicAdress(dispatchType.getMsgMetaData().getDestinations()
								.getRecipient())
								.getDisplayName() != null
						&& GeneralMessage.getAttributedElectronicAdress(dispatchType.getMsgMetaData().getDestinations()
								.getRecipient())
								.getValue() != null) {
					buf.append("|Recipent:"
							+ GeneralMessage.getAttributedElectronicAdress(dispatchType.getMsgMetaData().getDestinations()
									.getRecipient())
									.getDisplayName()
							+ ", "
							+ GeneralMessage.getAttributedElectronicAdress(dispatchType.getMsgMetaData().getDestinations()
									.getRecipient())
									.getValue());
				}

			protocolLogger.info(buf.toString() + "\n");
		} catch (NullPointerException ex) {
			LOG.error("Error log the incoming message", ex);
		}
	}

	public static void logIncomingMessage(REMMDMessageType messageType)
	{
//		try {
//
//			StringBuffer buf = new StringBuffer();
//			buf.append("IncomingEvidence");
//			buf.append("|InitialMsgId:" + messageType.getId());

//			if (GeneralMessage.getEvidence(Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT, list)messageType.getREMMDSingleEvidence()
//					.getAcceptanceRejectionByRecipient() != null) {
//				buf.append("|EvidenceType: AcceptanceRejectionByRecipient");
//
//				if (messageType.getREMMDSingleEvidence()
//						.getAcceptanceRejectionByRecipient().getSenderDetails() != null
//						&& messageType.getREMMDSingleEvidence()
//								.getAcceptanceRejectionByRecipient()
//								.getSenderDetails().getElectronicAddress()
//								.get(0) != null) {
//					buf.append("| Sender: "
//							+ messageType.getREMMDSingleEvidence()
//									.getAcceptanceRejectionByRecipient()
//									.getSenderDetails().getElectronicAddress()
//									.get(0).getValue());
//				}
//				buf.append("| EventCode:"
//						+ messageType.getREMMDSingleEvidence()
//								.getAcceptanceRejectionByRecipient()
//								.getEventCode());
//
//			} else if (messageType.getREMMDSingleEvidence()
//					.getDeliveryNonDeliveryToRecipient() != null) {
//				buf.append("|EvidenceType: DeliveryNonDeliveryToRecipient");
//
//				if (messageType.getREMMDSingleEvidence()
//						.getDeliveryNonDeliveryToRecipient().getSenderDetails() != null
//						&& messageType.getREMMDSingleEvidence()
//								.getDeliveryNonDeliveryToRecipient()
//								.getSenderDetails().getElectronicAddress()
//								.get(0) != null) {
//					buf.append("| Sender: "
//							+ messageType.getREMMDSingleEvidence()
//									.getDeliveryNonDeliveryToRecipient()
//									.getSenderDetails().getElectronicAddress()
//									.get(0).getValue());
//				}
//
//				buf.append("| EventCode:"
//						+ messageType.getREMMDSingleEvidence()
//								.getDeliveryNonDeliveryToRecipient()
//								.getEventCode());
//			} else if (messageType.getREMMDSingleEvidence()
//					.getRetrievalNonRetrievalByRecipient() != null) {
//				buf.append("|EvidenceType: RetrievalNonRetrievalByRecipient");
//
//				if (messageType.getREMMDSingleEvidence()
//						.getRetrievalNonRetrievalByRecipient()
//						.getSenderDetails() != null
//						&& messageType.getREMMDSingleEvidence()
//								.getRetrievalNonRetrievalByRecipient()
//								.getSenderDetails().getElectronicAddress()
//								.get(0) != null) {
//					buf.append("| Sender: "
//							+ messageType.getREMMDSingleEvidence()
//									.getRetrievalNonRetrievalByRecipient()
//									.getSenderDetails().getElectronicAddress()
//									.get(0).getValue());
//				}
//				buf.append("| EventCode:"
//						+ messageType.getREMMDSingleEvidence()
//								.getRetrievalNonRetrievalByRecipient()
//								.getEventCode());
//			}
//			protocolLogger.info(buf.toString() + "\n");
//		} catch (NullPointerException ex) {
//			LOG.error("Error log the incoming message", ex);
//		}
	}

	public static void logOutgoingDispatchMessage(DispatchMessage message)
	{
//		StringBuffer buf = new StringBuffer();
//		buf.append("OutgoingDispatchMsg");
//
//		if (message.getXSDObject() != null) {
//			MsgMetaData metaData = message.getXSDObject().getMsgMetaData();
//			if (metaData != null) {
//				if (metaData.getMsgIdentification() != null)
//					buf.append("|InitialMsgId:"
//							+ metaData.getMsgIdentification().getMessageID());
//				if (metaData.getDestinations() != null
//						&& metaData.getDestinations().getRecipient() != null
//						&& metaData.getDestinations().getRecipient()
//								.getElectronicAddress() != null) {
//
//					if (metaData.getDestinations().getRecipient()
//							.getElectronicAddress().getValue() != null)
//						buf.append("|Recipient: "
//								+ metaData.getDestinations().getRecipient()
//										.getElectronicAddress().getValue());
//
//				}
//			}
//		}
//
//		protocolLogger.info(buf.toString() + "\n");
	}

	public static void logOutgoingEvidenceMessage(EvidenceMessage message)
	{
//		StringBuffer buf = new StringBuffer();
//		buf.append("OutgoingEvidenceMsg");
//		if (message.getXSDObject() != null) {
//			REMMDMessageType xsdObject = message.getXSDObject();
//			if (xsdObject.getREMMDSingleEvidence()
//					.getAcceptanceRejectionByRecipient() != null) {
//				buf.append("|EvidenceType: AcceptanceRejectionByRecipient");
//				if (xsdObject.getREMMDSingleEvidence()
//						.getAcceptanceRejectionByRecipient().getSenderDetails() != null
//						&& xsdObject.getREMMDSingleEvidence()
//								.getAcceptanceRejectionByRecipient()
//								.getSenderDetails().getElectronicAddress() != null) {
//					buf.append("|InitialMsgId:"
//							+ xsdObject.getREMMDSingleEvidence()
//									.getAcceptanceRejectionByRecipient()
//									.getId());
//					buf.append("| Sender: "
//							+ xsdObject.getREMMDSingleEvidence()
//									.getAcceptanceRejectionByRecipient()
//									.getSenderDetails().getElectronicAddress()
//									.get(0).getValue());
//					buf.append("| EventCode:"
//							+ xsdObject.getREMMDSingleEvidence()
//									.getAcceptanceRejectionByRecipient()
//									.getEventCode());
//
//				}
//
//			} else if (xsdObject.getREMMDSingleEvidence()
//					.getDeliveryNonDeliveryToRecipient() != null) {
//				buf.append("|EvidenceType: DeliveryNonDeliveryToRecipient");
//				if (xsdObject.getREMMDSingleEvidence()
//						.getDeliveryNonDeliveryToRecipient().getSenderDetails() != null
//						&& xsdObject.getREMMDSingleEvidence()
//								.getDeliveryNonDeliveryToRecipient()
//								.getSenderDetails().getElectronicAddress() != null) {
//					buf.append("|InitialMsgId:"
//							+ xsdObject.getREMMDSingleEvidence()
//									.getDeliveryNonDeliveryToRecipient()
//									.getId());
//					buf.append("| Sender: "
//							+ xsdObject.getREMMDSingleEvidence()
//									.getDeliveryNonDeliveryToRecipient()
//									.getSenderDetails().getElectronicAddress()
//									.get(0).getValue());
//
//					buf.append("| EventCode:"
//							+ xsdObject.getREMMDSingleEvidence()
//									.getDeliveryNonDeliveryToRecipient()
//									.getEventCode());
//				}
//			} else if (xsdObject.getREMMDSingleEvidence()
//					.getRetrievalNonRetrievalByRecipient() != null) {
//				buf.append("|EvidenceType: RetrievalNonRetrievalByRecipient");
//				if (xsdObject.getREMMDSingleEvidence()
//						.getRetrievalNonRetrievalByRecipient()
//						.getSenderDetails() != null
//						&& xsdObject.getREMMDSingleEvidence()
//								.getRetrievalNonRetrievalByRecipient()
//								.getSenderDetails().getElectronicAddress() != null) {
//					buf.append("|InitialMsgId:"
//							+ xsdObject.getREMMDSingleEvidence()
//									.getRetrievalNonRetrievalByRecipient()
//									.getId());
//					buf.append("| Sender: "
//							+ xsdObject.getREMMDSingleEvidence()
//									.getRetrievalNonRetrievalByRecipient()
//									.getSenderDetails().getElectronicAddress()
//									.get(0).getValue());
//					buf.append("| EventCode:"
//							+ xsdObject.getREMMDSingleEvidence()
//									.getRetrievalNonRetrievalByRecipient()
//									.getEventCode());
//				}
//			}
//
//		}
//		protocolLogger.info(buf.toString() + "\n");

	}

	/**
	 * This Method will log an incoming Message on the Logger of the class
	 * LogHelper in a pretty Format.
	 * 
	 * @param messageType The {@link REMDispatchType} which should be logged
	 */

	public static void logPrettyIncomingMessage(REMDispatchType dispatchType)
	{
		LOG.info(prettyPrint(new ObjectFactory()
				.createREMDispatch(dispatchType)));
	}

	/**
	 * This Method will log an incoming Message on the Logger of the class
	 * LogHelper in a pretty Format.
	 * 
	 * @param messageType the {@link REMMDMessageType} which should be logged
	 */

	public static void logPrettyIncomingMessage(REMMDMessageType messageType)
	{
		LOG.info(prettyPrint(new ObjectFactory()
				.createREMMDMessage(messageType)));
	}

	/**
	 * This Method will log an incoming Message on the Logger provided in a
	 * pretty Format.
	 * 
	 * @param messageType the {@link REMMDMessageType} which should be logged
	 * @param log to where it should be logged
	 */
	public static void logPrettyIncomingMessage(Logger log,
			REMMDMessageType messageType)
	{
		log.info(prettyPrint(new ObjectFactory()
				.createREMMDMessage(messageType)));
	}

	/**
	 * This Method will log an incoming Message on the Logger provided in a
	 * pretty Format.
	 * 
	 * @param messageType the {@link REMMDMessageType} which should be logged
	 * @param log to where it should be logged
	 */
	public static void logPrettyIncomingMessage(Logger log,
			REMDispatchType dispatchType)
	{
		LOG.info(prettyPrint(new ObjectFactory()
				.createREMDispatch(dispatchType)));
	}

	/**
	 * This Method should only be used if you know what you are doing, to just
	 * log an incoming Message in a pretty format use
	 * {@link logPrettyIncomingMessage}
	 * 
	 * @param message
	 *            This must be a Object created from the JAXB Context
	 *            eu.eu_spocs.uri.edelivery.v1_.ObjectFactory.class
	 * 
	 */

	public static String prettyPrint(Object message)
	{
		try {
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer serializer;

			serializer = tfactory.newTransformer();
			// Setup indenting to "pretty print"
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "1");
			StreamResult result = new StreamResult(new StringWriter());

			serializer.transform(new JAXBSource(JaxbMarshallerHolder
					.getSpocsMarshallerHolder().getMarshaller(), message),
				result);
			return result.getWriter().toString();
		} catch (Exception ex) {
			LOG.error("Error Logging Message: ", ex);
			return null;
		}
	}
}
