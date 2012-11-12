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

import eu.spocseu.edeliverygw.SpocsGWProcessingException;
import eu.spocseu.edeliverygw.evidences.exception.NonDeliveryException;
import eu.spocseu.edeliverygw.evidences.exception.RelayREMMDRejectionException;
import eu.spocseu.edeliverygw.messages.AcceptanceRejectionByRecipientMessage;
import eu.spocseu.edeliverygw.messages.DeliveryNonDeliveryToRecipientMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessageResponse;
import eu.spocseu.edeliverygw.messages.RetrievalNonRetrievalByRecipientMessage;
import eu.spocseu.tsl.TrustedServiceImpl;

/**
 * The Interface which is used to process a incomming {@link DispatchMessage},
 * {@link AcceptanceRejectionByRecipientMessage}
 * {@link DeliveryNonDeliveryToRecipientMessage} and
 * {@link RetrievalNonRetrievalByRecipientMessage}. The
 * <code>initOnStartup()</code> method is called on the <code>jBoss</code>
 * startup, e.g. you can use it to intitialize a timer .
 * 
 * @see DefaultImpl
 */

public interface SpocsGWInterface
{
	public DispatchMessageResponse processDispatch(DispatchMessage message,
			TrustedServiceImpl tsl) throws NonDeliveryException,
			RelayREMMDRejectionException;

	public void processEvidence(AcceptanceRejectionByRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException;

	public void processEvidence(DeliveryNonDeliveryToRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException;

	public void processEvidence(
			RetrievalNonRetrievalByRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException;

	public void initOnStartup();

}
