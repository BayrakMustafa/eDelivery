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
