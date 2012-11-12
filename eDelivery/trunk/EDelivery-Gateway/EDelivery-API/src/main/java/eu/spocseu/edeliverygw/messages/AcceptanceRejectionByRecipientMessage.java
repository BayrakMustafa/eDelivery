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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;

import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02640.soapbinding.v1_.REMMDMessageType;
import org.etsi.uri._02640.v2_.AuthenticationDetailsType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.callbackhandler.SamlCallbackHandler;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.AcceptanceRejectionByRecipient;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;

/**
 * 
 * @author Lindemann
 * 
 */
public class AcceptanceRejectionByRecipientMessage extends EvidenceMessage {
	AcceptanceRejectionByRecipient acceptanceRejectionByRecipient;

	/**
	 * Creates a instance of AcceptanceRejectionByRecipientMessage with the
	 * given jaxB object. Only for parsing reasons. Internal use only.
	 * 
	 * @param evidenceType
	 *            The jaxB object
	 */
	public AcceptanceRejectionByRecipientMessage(REMEvidenceType evidenceType,
			Document doc) throws SpocsConfigurationException {
		document = doc;
		acceptanceRejectionByRecipient = new AcceptanceRejectionByRecipient(
				evidenceType);
		receivedEvidence = acceptanceRejectionByRecipient;
	}

	/**
	 * Creates a EvidenceMessage with a given REMMDMessageType object.
	 * 
	 * @param _evidence
	 *            A JAXB object to build from.
	 */
	public AcceptanceRejectionByRecipientMessage(REMMDMessageType _evidence,
			Document doc) throws SpocsConfigurationException,
			SpocsWrongInputDataException {
		super(_evidence, doc);
		if (getAcceptanceRejectionByRecipient() == null)
			throw new SpocsWrongInputDataException(
					"No acceptanceRejectionByRecipient found in the message");
		acceptanceRejectionByRecipient = new AcceptanceRejectionByRecipient(
				getAcceptanceRejectionByRecipient());
		receivedEvidence = acceptanceRejectionByRecipient;
	}

	/**
	 * Creates a EvidenceMessage with a given
	 * {@link AcceptanceRejectionByRecipient} object.
	 * 
	 * @param evidence
	 *            The {@link AcceptanceRejectionByRecipient} Object to initalize
	 *            from.
	 */
	public AcceptanceRejectionByRecipientMessage(
			AcceptanceRejectionByRecipient evidence)
			throws SpocsConfigurationException {
		samlProperties = null;
		acceptanceRejectionByRecipient = evidence;
		init();
	}

	/**
	 * Creates a EvidenceMessage with a given
	 * {@link AcceptanceRejectionByRecipient} object.
	 * 
	 * @param evidence
	 *            The {@link AcceptanceRejectionByRecipient} Object to initalize
	 *            from.
	 * @param _samlProperties
	 *            {@link SAMLProperties}
	 */
	public AcceptanceRejectionByRecipientMessage(
			AcceptanceRejectionByRecipient evidence,
			SAMLProperties _samlProperties) throws SpocsConfigurationException {
		samlProperties = _samlProperties;
		acceptanceRejectionByRecipient = evidence;
		init();
	}

	protected void prepareSendMessage() throws SpocsSystemInstallationException {
		try {

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
				getEvidence(Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT,
						jaxbObj.getREMMDEvidenceList())
						.setSenderAuthenticationDetails(auth);
			}

		} catch (DatatypeConfigurationException ex) {
			throw new SpocsSystemInstallationException(ex);
		}
	}

	/**
	 * This method sends the evidence message to the configured destination. The
	 * TSL module will be used to get information about the destination and for
	 * the client security the configuration will be used. In the cases of
	 * processing errors on the receiving side a SOAP fault will be created.
	 * 
	 * 
	 */
	public void sendEvidenceMessage() throws SpocsWrongInputDataException,
			SpocsSystemInstallationException, SpocsConfigurationException,
			EvidenceException {
		prepareSendMessage();
		sendEvidenceMessage(acceptanceRejectionByRecipient);
	}

	/**
	 * Creates a <code>AcceptanceRejectionByRecipientMessage</code> with the
	 * previous DeliveryNonDeliveryToRecipient or RelayREMMDAcceptanceRejection
	 * evidence InputStream.
	 * 
	 * @param evidence
	 *            The stream of the previous evidence
	 * @param typeOfInputStream
	 *            Type of the previous stream. Could be
	 *            (DELIVERY_NON_DELIVERY_TO_RECIPIENT or
	 *            RELAY_REM_MD_ACCEPTANCE_REJECTION)
	 */
	public AcceptanceRejectionByRecipientMessage(InputStream evidence,
			Evidences typeOfInputStream, SAMLProperties _samProperties)
			throws SpocsConfigurationException, SpocsWrongInputDataException {
		samlProperties = _samProperties;
		acceptanceRejectionByRecipient = new AcceptanceRejectionByRecipient(
				config, evidence, typeOfInputStream);
		init();
	}

	private void init() {
		addAcceptanceRejectionByRecipient(acceptanceRejectionByRecipient, false);
		isSuccessfull = acceptanceRejectionByRecipient.isSuccessful();
	}

	public AcceptanceRejectionByRecipient getAcceptanceRejectionByRecipientObj() {
		return acceptanceRejectionByRecipient;
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
		JAXBElement<AcceptanceRejectionByRecipient> jaxbObj = (JAXBElement<AcceptanceRejectionByRecipient>) readObjectJaxB(in);
		acceptanceRejectionByRecipient = jaxbObj.getValue();
		init();
	}

	/**
	 * Returns the requested Evidence or null if not already set.
	 */
	public REMEvidenceType getAcceptanceRejectionByRecipient() {
		return getEvidence(Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT,
				jaxbObj.getREMMDEvidenceList());
	}
}
