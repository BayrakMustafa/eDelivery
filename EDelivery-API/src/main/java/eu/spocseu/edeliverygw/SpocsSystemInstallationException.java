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
 * This exception will be thrown in the case of installation error. For example
 * if the hash algorithm is not supported by the system. In the most cases the
 * JDK or the must be improved or same libraries are missing.
 * 
 * @author Lindemann
 * 
 */
public class SpocsSystemInstallationException extends Exception
{
	private static final long serialVersionUID = -3191051364594522380L;

	public SpocsSystemInstallationException(String _message, Throwable _cause)
	{
		super(_message, _cause);
	}

	public SpocsSystemInstallationException(Throwable _cause)
	{
		super(_cause);
	}

	public SpocsSystemInstallationException(String _message)
	{
		super(_message);
	}

}
