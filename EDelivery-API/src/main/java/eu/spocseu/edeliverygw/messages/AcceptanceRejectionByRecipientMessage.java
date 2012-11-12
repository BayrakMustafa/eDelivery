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
public class AcceptanceRejectionByRecipientMessage extends EvidenceMessage
{
	AcceptanceRejectionByRecipient acceptanceRejectionByRecipient;

	/**
	 * Creates a instance of AcceptanceRejectionByRecipientMessage with the
	 * given jaxB object. Only for parsing reasons. Internal use only.
	 * 
	 * @param evidenceType
	 *            The jaxB object
	 */
	public AcceptanceRejectionByRecipientMessage(REMEvidenceType evidenceType,
			Document doc) throws SpocsConfigurationException
	{
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
		SpocsWrongInputDataException
	{
		super(_evidence, doc);
		if (getAcceptanceRejectionByRecipient() == null)
			throw new SpocsWrongInputDataException(
				"No acceptanceRejectionByRecipient found in the message");
		acceptanceRejectionByRecipient = new AcceptanceRejectionByRecipient(
			getAcceptanceRejectionByRecipient());
		receivedEvidence = acceptanceRejectionByRecipient;
	}

	/**
	 * 
	 * @param evidence
	 * @throws SpocsConfigurationException
	 */
	public AcceptanceRejectionByRecipientMessage(
			AcceptanceRejectionByRecipient evidence)
		throws SpocsConfigurationException
	{
		samlProperties = null;
		acceptanceRejectionByRecipient = evidence;
		init();
	}

	/**
	 * 
	 * @param evidence
	 * @param _samlProperties
	 * @throws SpocsConfigurationException
	 */
	public AcceptanceRejectionByRecipientMessage(
			AcceptanceRejectionByRecipient evidence,
			SAMLProperties _samlProperties) throws SpocsConfigurationException
	{
		samlProperties = _samlProperties;
		acceptanceRejectionByRecipient = evidence;
		init();
	}

	protected void prepareSendMessage() throws SpocsSystemInstallationException
	{
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
			EvidenceException
	{
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
		throws SpocsConfigurationException, SpocsWrongInputDataException
	{
		samlProperties = _samProperties;
		acceptanceRejectionByRecipient = new AcceptanceRejectionByRecipient(
			config, evidence, typeOfInputStream);
		init();
	}

	private void init()
	{
		addAcceptanceRejectionByRecipient(acceptanceRejectionByRecipient, false);
		isSuccessfull = acceptanceRejectionByRecipient.isSuccessful();
	}

	public AcceptanceRejectionByRecipient getAcceptanceRejectionByRecipientObj()
	{
		return acceptanceRejectionByRecipient;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
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
			ClassNotFoundException
	{
		JAXBElement<AcceptanceRejectionByRecipient> jaxbObj = (JAXBElement<AcceptanceRejectionByRecipient>) readObjectJaxB(in);
		acceptanceRejectionByRecipient = jaxbObj.getValue();
		init();
	}

	/**
	 * Returns the requested Evidence or null if not already set.
	 */
	public REMEvidenceType getAcceptanceRejectionByRecipient()
	{
		return getEvidence(Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT, jaxbObj
			.getREMMDEvidenceList());
	}
}
