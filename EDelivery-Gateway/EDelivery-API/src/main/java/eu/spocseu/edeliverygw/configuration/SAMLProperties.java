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
package eu.spocseu.edeliverygw.configuration;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.spocseu.common.SpocsConstants;

/**
 * This class represents the Properties of a SAMLToken
 * 
 * @see <a href="http://www.eid-stork.eu">EID Stork</a> 
 * 
 */
public class SAMLProperties
{
	
	private int citizenQAAlevel;
	private String surname;
	private String givenName;
	private String eIdentifier;
	private Date dateOfBirth;
	private Date authenticationTime;
	private String authenticationMethod;
	private SpocsConstants.ActorRole role;
	/**
	 * Get the level of the authentication quality (look into the stork
	 * specification)
	 * 
	 * @return citizenQAAlevel
	 */
	public int getCitizenQAAlevel()
	{
		return citizenQAAlevel;
	}

	/**
	 * Set the level of the authentication quality (look into the stork
	 * specification)
	 * 
	 * @param citizenQAAlevel
	 *            The address of the sender if available.
	 * 
	 */
	public void setCitizenQAAlevel(int citizenQAAlevel)
	{
		this.citizenQAAlevel = citizenQAAlevel;
	}

	/**
	 * The identifier of the stork authentication.
	 */
	public String geteIdentifier()
	{
		return eIdentifier;
	}

	/**
	 * Set the identifier of the stork authentication (look into the stork
	 * specification)
	 * 
	 * @param eIdentifier
	 *            The identifier
	 * 
	 */
	public void seteIdentifier(String eIdentifier)
	{
		this.eIdentifier = eIdentifier;
	}

	/**
	 * Returns the date of sender's birth
	 * 
	 */
	public Date getDateOfBirth()
	{
		return dateOfBirth;
	}

	/**
	 * Set the date of sender's birth. The Hour, Minute, Secound and Milisecound
	 * values will be removed, because they are not serialized to the SAML
	 * Token.
	 * 
	 * @param dateOfBirth
	 *            Date of sender's birth
	 * 
	 */
	public void setDateOfBirth(Date dateOfBirth)
	{
		Calendar cal = new GregorianCalendar();
		cal.setTime(dateOfBirth);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.dateOfBirth = cal.getTime();
	}

	/**
	 * Returns the sender's surname
	 */
	public String getSurname()
	{
		return surname;
	}

	/**
	 * Returns the given sender's name.
	 */
	public String getGivenName()
	{
		return givenName;
	}

	public void setGivenName(String givenName)
	{
		this.givenName = givenName;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public Date getAuthenticationTime()
	{
		return authenticationTime;
	}
	
	/**
	 * Set the date of sender's birth. The Hour, Minute, Secound and Milisecound
	 * values will be removed, because they are not serialized to the SAML
	 * Token.
	 * 
	 * @param authenticationTime
	 *            Date of authentication
	 * 
	 */

	public void setAuthenticationTime(Date authenticationTime)
	{
		Calendar cal = new GregorianCalendar();
		cal.setTime(authenticationTime);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.authenticationTime = cal.getTime();
	}

	public String getAuthenticationMethod()
	{
		return authenticationMethod;
	}

	public void setAuthenticationMethod(String authenticationMethod)
	{
		this.authenticationMethod = authenticationMethod;
	}

	/**
	 * @return the role
	 */
	public SpocsConstants.ActorRole getRole()
	{
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(SpocsConstants.ActorRole role)
	{
		this.role = role;
	}

}
