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
import java.io.Serializable;
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
import eu.spocseu.edeliverygw.evidences.RetrievalNonRetrievalByRecipient;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;

/**
 * 
 * @author lindemann
 * 
 */
public class RetrievalNonRetrievalByRecipientMessage extends EvidenceMessage
		implements Serializable
{
	private static final long serialVersionUID = 1L;
	RetrievalNonRetrievalByRecipient retrievalNonRetrievalByRecipient;

	/**
	 * On the recipient side this constructor will be used to build the object
	 * constructor. Only for parsing reasons. Internal use only.
	 * 
	 * @param evidenceType
	 *            The underlying JAXB object.
	 */
	public RetrievalNonRetrievalByRecipientMessage(
			REMEvidenceType evidenceType, Document doc)
		throws SpocsConfigurationException
	{
		document = doc;
		retrievalNonRetrievalByRecipient = new RetrievalNonRetrievalByRecipient(
			evidenceType);
		receivedEvidence = retrievalNonRetrievalByRecipient;
	}

	public RetrievalNonRetrievalByRecipientMessage(
			RetrievalNonRetrievalByRecipient _evidence,
			SAMLProperties _samlProperties) throws SpocsConfigurationException
	{
		retrievalNonRetrievalByRecipient = _evidence;
		samlProperties = _samlProperties;
		init();
	}

	public RetrievalNonRetrievalByRecipientMessage(
			RetrievalNonRetrievalByRecipient _evidence)
		throws SpocsConfigurationException
	{
		retrievalNonRetrievalByRecipient = _evidence;
		samlProperties = null;
		init();
	}

	/**
	 * Creates a <code>RetrievalNonRetrievalByRecipientMessage</code> with the
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
	public RetrievalNonRetrievalByRecipientMessage(InputStream evidence,
			Evidences typeOfInputStream, SAMLProperties _samlProperties)
		throws SpocsWrongInputDataException, SpocsConfigurationException
	{
		retrievalNonRetrievalByRecipient = new RetrievalNonRetrievalByRecipient(
			config, evidence, typeOfInputStream);
		samlProperties = _samlProperties;
		init();

	}

	/**
	 * Creates a EvidenceMessage with a given REMMDMessageType object.
	 * 
	 * @param _evidence
	 *            A JAXB object to build from.
	 */
	public RetrievalNonRetrievalByRecipientMessage(REMMDMessageType _evidence,
			Document doc) throws SpocsConfigurationException,
		SpocsWrongInputDataException
	{
		super(_evidence, doc);

		if (getRetrievalNonRetrievalByRecipients() == null)
			throw new SpocsWrongInputDataException(
				"No acceptanceRejectionByRecipient found in the message");
		retrievalNonRetrievalByRecipient = new RetrievalNonRetrievalByRecipient(
			getRetrievalNonRetrievalByRecipients());
		receivedEvidence = retrievalNonRetrievalByRecipient;
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

		JAXBElement<RetrievalNonRetrievalByRecipient> jaxbObj = (JAXBElement<RetrievalNonRetrievalByRecipient>) readObjectJaxB(in);
		retrievalNonRetrievalByRecipient = jaxbObj.getValue();
		init();
	}

	protected void init()
	{
		addRetrievalNonRetrievalByRecipient(retrievalNonRetrievalByRecipient,
			false);
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

				getEvidence(Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT,
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
		sendEvidenceMessage(retrievalNonRetrievalByRecipient);
	}

	/**
	 * Returns the created evidence object.
	 */
	public RetrievalNonRetrievalByRecipient getRetrievalNonRetrievalByRecipientObj()
	{
		return retrievalNonRetrievalByRecipient;
	}

	/**
	 * Returns the requested Evidence or null if not already set.
	 */
	public REMEvidenceType getRetrievalNonRetrievalByRecipients()
	{
		return getEvidence(Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT, jaxbObj.getREMMDEvidenceList());
	}
}
