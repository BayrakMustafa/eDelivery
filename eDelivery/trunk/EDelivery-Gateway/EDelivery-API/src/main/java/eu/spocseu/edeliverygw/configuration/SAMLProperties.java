/*******************************************************************************
 * Copyright (c) 2012 EU LSP SPOCS http://www.eu-spocs.eu/.
 * 
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *  
 *  http://ec.europa.eu/idabc/en/document/7774.html
 *  
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 ******************************************************************************/

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
