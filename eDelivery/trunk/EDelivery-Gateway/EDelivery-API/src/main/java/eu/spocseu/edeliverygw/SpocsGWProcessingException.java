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
package eu.spocseu.edeliverygw;

/**
 * This class represents a Exception of the Gateway Processing. It will be used
 * to return a SOAP Fault to the Client in an Error case.
 * 
 * @author oley
 */

public class SpocsGWProcessingException extends Exception
{
	private static final long serialVersionUID = 1L;
	REMErrorEvent fault;

	public SpocsGWProcessingException(REMErrorEvent _fault)
	{
		fault = _fault;
	}

	public SpocsGWProcessingException(REMErrorEvent _fault, String _details)
	{
		super(_fault.getEventDetails() + " details: " + _details);
		fault = _fault;
	}

	public SpocsGWProcessingException(REMErrorEvent _fault, String _details,
			Throwable _cause)
	{
		super(_fault.getEventDetails() + " details: " + _details, _cause);
		fault = _fault;
	}

	public REMErrorEvent getFault()
	{
		return fault;
	}
}
