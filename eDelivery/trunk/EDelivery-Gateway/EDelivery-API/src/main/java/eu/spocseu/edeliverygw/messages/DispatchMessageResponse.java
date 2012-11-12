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

import javax.xml.parsers.ParserConfigurationException;

import org.etsi.uri._02640.soapbinding.v1_.REMMDMessageType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import eu.spocseu.common.SpocsConstants;
import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.evidences.AcceptanceRejectionByRecipient;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.Evidence;
import eu.spocseu.edeliverygw.evidences.RelayREMMDAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.RelayREMMDFailure;
import eu.spocseu.edeliverygw.evidences.RetrievalNonRetrievalByRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;

/**
 * The <code>DispatchMessageResponse</code> class represents a Holder for
 * several Evidences which will be returned if a {@link DispatchMessage} is
 * send.
 * 
 * @author Lindemann
 */
public class DispatchMessageResponse extends EvidenceMessage
{
	private static Logger LOG = LoggerFactory
			.getLogger(DispatchMessageResponse.class);

	public DispatchMessageResponse() throws SpocsConfigurationException
	{
		super();
	}

	public DispatchMessageResponse(Configuration config,
			SubmissionAcceptanceRejection submissionAcceptanceRejection,
			SpocsConstants.Evidences evidenceType)
		throws SpocsConfigurationException, SpocsWrongInputDataException
	{
		if (evidenceType != SpocsConstants.Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
				&& evidenceType != SpocsConstants.Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION) {
			throw new SpocsWrongInputDataException(
				"Only evidence of type DELIVERY_NON_DELIVERY_TO_RECIPIENT or RELAY_REM_MD_ACCEPTANCE_REJECTION allowed");
		}
		if (evidenceType == SpocsConstants.Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT) {
			addDeliveryNonDeliveryToRecipient(
				new DeliveryNonDeliveryToRecipient(config,
					submissionAcceptanceRejection), false);

		} else {
			addRelayToREMMDAcceptanceRejection(
				new RelayREMMDAcceptanceRejection(config,
					submissionAcceptanceRejection), false);
		}

	}

	protected DispatchMessageResponse(REMMDMessageType _evidence, Document doc)
		throws SpocsConfigurationException, SpocsSystemInstallationException, ParserConfigurationException
	{
		super(_evidence,doc);
	}
	public DispatchMessageResponse(EvidenceException ex)
		throws SpocsConfigurationException
	{
		if (ex.getEvidence().getEvidenceType() == null) {
			if (!config.geteDeliveryDetails().isSynchronGatewayMD()) {
				ex.getEvidence().setEventCode(
					Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
							.getFaultEventCode());
			}
		} else if (!(ex
				.getEvidence()
				.getEvidenceType()
				.getFaultEventCode()
				.equals(
					Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
							.getFaultEventCode()) || ex
				.getEvidence()
				.getEvidenceType()
				.getFaultEventCode()
				.equals(
					Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
							.getFaultEventCode()))) {
			LOG.error("Wrong evidence thrown by the gateway implementation. Thrown event:"
					+ ex.getEvidence().getEvidenceType());
		}
		Evidence _evidence = ex.getEvidence();
		if (_evidence instanceof AcceptanceRejectionByRecipient)
			addAcceptanceRejectionByRecipient(
				(AcceptanceRejectionByRecipient) _evidence, false);
		else if (_evidence instanceof DeliveryNonDeliveryToRecipient)
			addDeliveryNonDeliveryToRecipient(
				(DeliveryNonDeliveryToRecipient) _evidence, false);
		else if (_evidence instanceof RelayREMMDAcceptanceRejection)
			addRelayToREMMDAcceptanceRejection(
				(RelayREMMDAcceptanceRejection) _evidence, false);
		else if (_evidence instanceof RelayREMMDFailure)
			addRelayToREMMDFailure((RelayREMMDFailure) _evidence, false);
		else if (_evidence instanceof RetrievalNonRetrievalByRecipient)
			addRetrievalNonRetrievalByRecipient(
				(RetrievalNonRetrievalByRecipient) _evidence, false);
		else
			throw new IllegalArgumentException("Wrong evidence type given");
	}

	public void addRelayToREMMDAcceptanceRejection(
			RelayREMMDAcceptanceRejection _relayToREMMDAcceptanceRejection)
	{
		addRelayToREMMDAcceptanceRejection(_relayToREMMDAcceptanceRejection,
			false);
	}

	public void addDeliveryNonDeliveryToRecipient(
			DeliveryNonDeliveryToRecipient _deliveryNonDeliveryToRecipient)
	{
		addDeliveryNonDeliveryToRecipient(_deliveryNonDeliveryToRecipient,
			false);
	}

	/**
	 * Returns the requested Evidence or null if not already set.
	 * 
	 * @return REMEvidenceType
	 */
	public REMEvidenceType getDeliveryNonDeliveryToRecipient()
	{
		return getEvidence(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT, jaxbObj.getREMMDEvidenceList());
	}

	/**
	 * Returns the requested Evidence or null if not already set.
	 * 
	 * @return REMEvidenceType
	 */
	public DeliveryNonDeliveryToRecipient getDeliveryNonDeliveryToRecipientObj()
	{
		if (receivedEvidence instanceof DeliveryNonDeliveryToRecipient)
			return (DeliveryNonDeliveryToRecipient) receivedEvidence;
		else
			return null;
	}

	/**
	 * Returns the requested Evidence or null if not already set.
	 * 
	 * @return REMEvidenceType
	 */
	public REMEvidenceType getRelayToREMMDAcceptanceRejection()
	{
		return getEvidence(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION, jaxbObj.getREMMDEvidenceList());
	}

	/**
	 * Returns the requested Evidence or null if not already set.
	 * 
	 * @return REMEvidenceType
	 */
	public RelayREMMDAcceptanceRejection getRelayToREMMDAcceptanceRejectionObj()
	{
		if (receivedEvidence instanceof RelayREMMDAcceptanceRejection)
			return (RelayREMMDAcceptanceRejection) receivedEvidence;
		else
			return null;
	}

}
