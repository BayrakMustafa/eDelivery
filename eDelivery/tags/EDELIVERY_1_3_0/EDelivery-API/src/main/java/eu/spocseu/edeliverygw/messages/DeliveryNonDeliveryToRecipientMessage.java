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

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
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
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.exception.EvidenceException;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;

/**
 * The <code>DeliveryNonDeliveryToRecipientMessage</code> class helps to create
 * and send a Spocs DeliveryNonDeliveryToRecipient evidence message. In
 * asynchronous cases the DeliveryNonDeliveryToRecipient evidence must be placed
 * in a new SOAP message. This class will help to send this new SOAP message.
 * The underlying 'REMMDEvidenceListType' JAXB object would be created by this
 * class and all required values will be set. In the case that the user will add
 * some detailed information to the JAXB object it is possible to get the
 * underlying JAXB object and add this information. The method to get the
 * created JAXB object is the getXSDObject method.
 * <p>
 * 
 * @author Lindemann
 * 
 */
public class DeliveryNonDeliveryToRecipientMessage extends EvidenceMessage
		implements Serializable
{
	private static final long serialVersionUID = 1L;

	DeliveryNonDeliveryToRecipient deliveryNonDeliveryEvidence;

	/**
	 * On the recipient side this constructor will be used to build the object
	 * constructor
	 * 
	 * @param evidenceType
	 *            The underlying JAXB object.
	 * @throws IOException
	 *             In the case of parsing errors.
	 * @throws GeneralSecurityException
	 *             If there are signature or certificate errors.
	 */
	public DeliveryNonDeliveryToRecipientMessage(REMEvidenceType evidenceType, Document doc)
		throws SpocsConfigurationException
	{
		document = doc;
		deliveryNonDeliveryEvidence = new DeliveryNonDeliveryToRecipient(
			evidenceType);
		init();
		receivedEvidence = deliveryNonDeliveryEvidence;
	}

	/**
	 * Creates a <code>DeliveryNonDeliveryToRecipientMessage</code> with the
	 * previous SubmissionAcceptanceRejection evidence as input.
	 * 
	 * @param evidence
	 *            The previous evidence
	 */
	public DeliveryNonDeliveryToRecipientMessage(
			DeliveryNonDeliveryToRecipient _evidence)
		throws SpocsConfigurationException
	{
		samlProperties = null;
		deliveryNonDeliveryEvidence = _evidence;
		init();
	}

	/**
	 * Creates a <code>DeliveryNonDeliveryToRecipientMessage</code> with the
	 * previous SubmissionAcceptanceRejection evidence and SAMLProperties for a
	 * SAMLToken as input.
	 * 
	 * @param evidence
	 *            The previous evidence
	 * @param _samlProperties
	 */
	public DeliveryNonDeliveryToRecipientMessage(
			DeliveryNonDeliveryToRecipient _evidence,
			SAMLProperties _samlProperties) throws SpocsConfigurationException
	{
		samlProperties = _samlProperties;
		deliveryNonDeliveryEvidence = _evidence;
		init();
	}

	/**
	 * Creates a EvidenceMessage with a given REMMDMessageType object.
	 * 
	 * @param _evidence
	 *            A JAXB object to build from.
	 */
	public DeliveryNonDeliveryToRecipientMessage(REMMDMessageType _evidence, Document doc)
		throws SpocsConfigurationException, SpocsWrongInputDataException
	{
		super(_evidence, doc);
		if (getDeliveryNonDeliveryToRecipient() == null)
			throw new SpocsWrongInputDataException(
				"No acceptanceRejectionByRecipient found in the message");
		deliveryNonDeliveryEvidence = new DeliveryNonDeliveryToRecipient(
			getDeliveryNonDeliveryToRecipient());
		receivedEvidence = deliveryNonDeliveryEvidence;
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

		JAXBElement<DeliveryNonDeliveryToRecipient> jaxbObj = (JAXBElement<DeliveryNonDeliveryToRecipient>) readObjectJaxB(in);
		deliveryNonDeliveryEvidence = jaxbObj.getValue();
		init();
	}

	protected void init()
	{
		addDeliveryNonDeliveryToRecipient(deliveryNonDeliveryEvidence, false);
		isSuccessfull = deliveryNonDeliveryEvidence.isSuccessful();
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
				getEvidence(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT, jaxbObj.getREMMDEvidenceList()).setSenderAuthenticationDetails(auth);
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
		sendEvidenceMessage(deliveryNonDeliveryEvidence);
	}

	/**
	 * Returns the related evidence object.
	 */
	public DeliveryNonDeliveryToRecipient getDeliveryNonDeliveryToRecipientObj()
	{
		return deliveryNonDeliveryEvidence;
	}

	/**
	 * Returns the requested Evidence or null if not already set.
	 */
	public REMEvidenceType getDeliveryNonDeliveryToRecipient()
	{
		return getEvidence(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT, jaxbObj.getREMMDEvidenceList());
	}

}
