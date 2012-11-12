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
package eu.spocseu.edeliverygw.evidences.exception;

import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.messages.DispatchMessage;

/**
 * This Exception represents a {@link DeliveryNonDeliveryToRecipient} Evidence
 * in the fault case. It can be thrown to send a DeliveryNonDelivery Evidence to
 * the Sender.
 * 
 * @author lindemann
 */

public class NonDeliveryException extends EvidenceException
{

	private static final long serialVersionUID = 1L;

	public NonDeliveryException(Configuration config, DispatchMessage message,
			REMErrorEvent _errorEvent, Throwable cause)
	{
		super(new DeliveryNonDeliveryToRecipient(config,
			message.getSubmissionAcceptanceRejection(), false), _errorEvent,
				cause);
	}

	public NonDeliveryException(DeliveryNonDeliveryToRecipient evidence,
			REMErrorEvent _errorEvent, Throwable cause)
	{
		super(evidence, _errorEvent, cause);

	}

}
