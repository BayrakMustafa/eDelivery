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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.bind.JAXBException;

import org.etsi.uri._02640.soapbinding.v1_.Informational;
import org.etsi.uri._02640.soapbinding.v1_.KeywordType;
import org.etsi.uri._02640.v2_.EntityDetailsType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.rpc.tools.wsdeploy.DeploymentException;

import eu.spocseu.common.SpocsConstants;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.LogHelper;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.SpocsGWProcessingException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;
import eu.spocseu.edeliverygw.evidences.AcceptanceRejectionByRecipient;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.RelayREMMDAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.RetrievalNonRetrievalByRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.evidences.exception.NonDeliveryException;
import eu.spocseu.edeliverygw.evidences.exception.RelayREMMDRejectionException;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import eu.spocseu.edeliverygw.messages.AcceptanceRejectionByRecipientMessage;
import eu.spocseu.edeliverygw.messages.DeliveryNonDeliveryToRecipientMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessageResponse;
import eu.spocseu.edeliverygw.messages.GeneralMessage;
import eu.spocseu.edeliverygw.messages.RetrievalNonRetrievalByRecipientMessage;
import eu.spocseu.tsl.TrustedServiceImpl;

/**
 * The <code>DefaultImp</code> class represents the generic Implementation of
 * the {@link SpocsGWInterface}, which is uses for the TestEnvironment. This
 * implementation returns TestEvidences and a Dispatch on a request via a
 * Dispatch.s
 * 
 * @author oley
 */

public class DefaultImpl implements SpocsGWInterface
{

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultImpl.class);
	private static int timerPause = 10000;

	Configuration config;

	public static enum EvidenceType
	{
		AsyncDelivery("AsyncDelivery"),
		AsyncNonDelivery("AsyncNonDelivery"),
		DeliveryNonDeliveryToRecipient("DeliveryNonDeliveryToRecipient"),
		RelayREMMDRejection("RelayREMMDRejection"),
		RelayREMMDAcceptance("RelayREMMDAcceptance"),

		NonRetrievalByRecipient("NonRetrievalByRecipient"),
		RetrievalByRecipient("RetrievalByRecipient"),
		AcceptanceRejectionByRecipientMessage(
				"AcceptanceRejectionByRecipientMessage"), ReturnDispatch(
				"ReturnDispatch");

		EvidenceType(String name)
		{
			values.add(name);
		}

		private ArrayList<String> values = new ArrayList<String>();

		public boolean containsType(String value)
		{
			return values.contains(value);
		}
	}

	public DefaultImpl()
	{
	}

	@Override
	public DispatchMessageResponse processDispatch(DispatchMessage message,
			TrustedServiceImpl tsl) throws NonDeliveryException,
			RelayREMMDRejectionException
	{
		LOG.warn("processDispatch, default Implementation!");
		LogHelper.logPrettyIncomingMessage(message.getXSDObject());
		Informational informational = message.getNormalizedElement()
				.getInformational();

		List<KeywordType> keywords = informational.getKeywords();
		for (KeywordType key : keywords) {
			if (key.getMeaning().equals("SimulateEvidences")) {
				EvidenceType keyword = null;
				try {
					keyword = EvidenceType.valueOf(key.getValue());
				} catch (IllegalArgumentException ex) {
					LOG.debug("EvidenceType not found", ex);
				}
				Timer timer = new Timer();
				switch (keyword) {
				case DeliveryNonDeliveryToRecipient:
					throw new NonDeliveryException(config, message,
						REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR,
						new Throwable(
							"DeliveryNonDeliveryToRecipient simulation"));
				case RelayREMMDRejection:
					throw new RelayREMMDRejectionException(config, message,
						REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR,
						new Throwable("RelayREMMDRejection simulation"));
				case RelayREMMDAcceptance:
				{

					RelayREMMDAcceptanceRejection acceptance = new RelayREMMDAcceptanceRejection(
						config, message.getSubmissionAcceptanceRejection(),
						true);

					try {
						DispatchMessageResponse response = new DispatchMessageResponse(
							config, message.getSubmissionAcceptanceRejection(),
							acceptance.getEvidenceType());
						return response;
					} catch (Exception ex) {
						throw new RelayREMMDRejectionException(config, message,
							REMErrorEvent.WRONG_INPUT_DATA, new Throwable(
								"RelayREMMDRejection simulation"));
					}
				}
				case AsyncDelivery:
					GenericTimerTask task1 = new GenericTimerTask(
						EvidenceType.AsyncDelivery, config,
						message.getSubmissionAcceptanceRejection());
					timer.schedule(task1, timerPause);

				case AsyncNonDelivery:
					GenericTimerTask task2 = new GenericTimerTask(
						EvidenceType.AsyncNonDelivery, config,
						message.getSubmissionAcceptanceRejection());
					timer.schedule(task2, timerPause);
				case NonRetrievalByRecipient:
					GenericTimerTask task3 = new GenericTimerTask(
						EvidenceType.NonRetrievalByRecipient, config,
						message.getSubmissionAcceptanceRejection());
					timer.schedule(task3, timerPause);
					break;
				case RetrievalByRecipient:
					GenericTimerTask task4 = new GenericTimerTask(
						EvidenceType.RetrievalByRecipient, config,
						message.getSubmissionAcceptanceRejection());
					timer.schedule(task4, timerPause);
					break;
				case AcceptanceRejectionByRecipientMessage:
					GenericTimerTask task5 = new GenericTimerTask(
						EvidenceType.AcceptanceRejectionByRecipientMessage,
						config, message.getSubmissionAcceptanceRejection());
					timer.schedule(task5, timerPause);
					break;
				case ReturnDispatch:
					GenericTimerTask task6 = new GenericTimerTask(
						EvidenceType.ReturnDispatch, config, message);
					timer.schedule(task6, timerPause);
					break;
				}

			}
		}

		return null;

	}

	@Override
	public void initOnStartup()
	{
		try {
			config = Configuration.getConfiguration();
		} catch (Exception e) {
			throw new DeploymentException("getting Config failed!");
		}
	}

	private void sendAcceptanceRejectionByRecipient(Configuration config2,
			SubmissionAcceptanceRejection submission)
			throws SpocsWrongInputDataException,
			SpocsSystemInstallationException, SpocsConfigurationException,
			EvidenceException
	{

		DeliveryNonDeliveryToRecipient subEvidence = new DeliveryNonDeliveryToRecipient(
			config, submission);

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("User");
		samlProperties.setSurname("from Reference Gateway");

		AcceptanceRejectionByRecipientMessage evidence = new AcceptanceRejectionByRecipientMessage(
			new AcceptanceRejectionByRecipient(config, subEvidence),
			samlProperties);
		evidence.sendEvidenceMessage();

	}

	private void sendRetrievalByRecipient(Configuration config,
			SubmissionAcceptanceRejection submission)
			throws SpocsConfigurationException, GeneralSecurityException,
			IOException, EvidenceException, JAXBException,
			SpocsWrongInputDataException, SpocsSystemInstallationException
	{

		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("User");
		samlProperties.setSurname("from Reference Gateway");

		LOG.debug("Submission Recipient:"
				+ GeneralMessage.getAttributedElectronicAdress(
					submission.getXSDObject().getRecipientsDetails()
							.getEntityDetails().get(0)).getValue());

		LOG.debug("Submission Originator:"
				+ GeneralMessage.getAttributedElectronicAdress(
					submission.getXSDObject().getSenderDetails()));

		RetrievalNonRetrievalByRecipient evidence = new RetrievalNonRetrievalByRecipient(
			config, submission, true);
		RetrievalNonRetrievalByRecipientMessage evidenceMessage = new RetrievalNonRetrievalByRecipientMessage(
			evidence, samlProperties);

		evidenceMessage.sendEvidenceMessage();
	}

	private void sendNonRetrievalByRecipient(Configuration config,
			SubmissionAcceptanceRejection submission) throws IOException,
			SpocsConfigurationException, GeneralSecurityException,
			EvidenceException, JAXBException, SpocsWrongInputDataException,
			SpocsSystemInstallationException
	{
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("User");
		samlProperties.setSurname("from Reference Gateway");
		RetrievalNonRetrievalByRecipient evidence = new RetrievalNonRetrievalByRecipient(
			config, submission, false);
		RetrievalNonRetrievalByRecipientMessage evidenceMessage = new RetrievalNonRetrievalByRecipientMessage(
			evidence, samlProperties);
		evidenceMessage.sendEvidenceMessage();

	}

	private void sendDispatchMessageBack(Configuration config2,
			DispatchMessage message) throws EvidenceException,
			SpocsConfigurationException, SpocsSystemInstallationException
	{
		EntityDetailsType dest = message.getXSDObject().getMsgMetaData()
				.getOriginators().getFrom();
		message.getXSDObject().getMsgMetaData().getDestinations()
				.setRecipient(dest);

		message.sendDispatchMessage();
	}

	private void sendAsyncNonDelivery(Configuration config2,
			SubmissionAcceptanceRejection submission) throws EvidenceException,
			SpocsConfigurationException, SpocsSystemInstallationException,
			SpocsWrongInputDataException
	{
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("User");
		samlProperties.setSurname("from Reference Gateway");
		DeliveryNonDeliveryToRecipient nonDelivery = new DeliveryNonDeliveryToRecipient(
			Configuration.getConfiguration(), submission, false);

		new DeliveryNonDeliveryToRecipientMessage(nonDelivery, samlProperties)
				.sendEvidenceMessage();
	}

	private void sendAsyncDelivery(Configuration config2,
			SubmissionAcceptanceRejection submission) throws EvidenceException,
			SpocsConfigurationException, SpocsSystemInstallationException,
			SpocsWrongInputDataException
	{
		SAMLProperties samlProperties = new SAMLProperties();
		samlProperties.setCitizenQAAlevel(1);
		samlProperties.setGivenName("User");
		samlProperties.setSurname("from Reference Gateway");
		DeliveryNonDeliveryToRecipient nonDelivery = new DeliveryNonDeliveryToRecipient(
			Configuration.getConfiguration(), submission, true);

		new DeliveryNonDeliveryToRecipientMessage(nonDelivery, samlProperties)
				.sendEvidenceMessage();
	}

	private class GenericTimerTask extends TimerTask
	{
		EvidenceType type;
		Configuration config;
		SubmissionAcceptanceRejection submission;

		DispatchMessage message;

		public GenericTimerTask(EvidenceType _type, Configuration _config,
				SubmissionAcceptanceRejection _submission)
		{
			type = _type;
			config = _config;
			submission = _submission;
		}

		public GenericTimerTask(EvidenceType _type, Configuration _config,
				DispatchMessage _message)
		{
			type = _type;
			config = _config;
			message = _message;
		}

		@Override
		public void run()
		{
			try {
				LOG.info("=== Running Timer ===");
				switch (type) {
				case AsyncNonDelivery:
					sendAsyncNonDelivery(config, submission);
					break;
				case AsyncDelivery:
					sendAsyncDelivery(config, submission);
					break;
				case NonRetrievalByRecipient:
					sendNonRetrievalByRecipient(config, submission);
					break;
				case RetrievalByRecipient:
					sendRetrievalByRecipient(config, submission);
					break;
				case AcceptanceRejectionByRecipientMessage:
					sendAcceptanceRejectionByRecipient(config, submission);
					break;
				case ReturnDispatch:
					sendDispatchMessageBack(config, message);
				default:
					break;
				}

			} catch (Exception ex) {
				LOG.error("Error:", ex);
			}
		}
	}

	@Override
	public void processEvidence(AcceptanceRejectionByRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException
	{

		logEvidenceInformation(
			SpocsConstants.Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT,
			evidence.getAcceptanceRejectionByRecipient());
		LogHelper.logPrettyIncomingMessage(evidence.getXSDObject());

	}

	@Override
	public void processEvidence(DeliveryNonDeliveryToRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException
	{

		logEvidenceInformation(
			SpocsConstants.Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT,
			evidence.getDeliveryNonDeliveryToRecipient());
		LogHelper.logPrettyIncomingMessage(evidence.getXSDObject());

	}

	@Override
	public void processEvidence(
			RetrievalNonRetrievalByRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException
	{

		logEvidenceInformation(
			SpocsConstants.Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT,
			evidence.getRetrievalNonRetrievalByRecipients());

		LogHelper.logPrettyIncomingMessage(evidence.getXSDObject());

	}

	private void logEvidenceInformation(SpocsConstants.Evidences type,
			REMEvidenceType evidence)
	{
		try {
			String sender = SpocsFragments.getFirstElectronicAddressWithURI(
				evidence.getSenderDetails()).getValue();
			String receiver = SpocsFragments.getFirstElectronicAddressWithURI(
				evidence.getRecipientsDetails().getEntityDetails().get(0))
					.getValue();
			String issuer = SpocsFragments.getFirstElectronicAddressWithURI(
				evidence.getEvidenceIssuerDetails()).getValue();
			LOG.debug("Incoming evidence sender: '" + sender
					+ "' ,the issuer '" + issuer + "' and the recipient '"
					+ receiver + "'.");
		} catch (NullPointerException ex) {
			// nothing to do
		}
		LOG.debug("Event code: " + evidence.getEventCode());

	}
}
