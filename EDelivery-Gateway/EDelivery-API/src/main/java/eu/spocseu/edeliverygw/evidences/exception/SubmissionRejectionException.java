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
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;

public class SubmissionRejectionException extends EvidenceException
{

	private static final long serialVersionUID = 1L;

	public SubmissionRejectionException(SubmissionAcceptanceRejection sub,
			REMErrorEvent _errorEvent, Throwable cause)
	{
		super(sub, _errorEvent, cause);

	}

	public SubmissionRejectionException(String message,
			SubmissionAcceptanceRejection sub)
	{
		super(message, sub, null);

	}

	public SubmissionRejectionException(String message,
			SubmissionAcceptanceRejection sub, REMErrorEvent _errorEvent,
			Throwable cause)
	{
		super(message, sub, _errorEvent, cause);

	}

}
