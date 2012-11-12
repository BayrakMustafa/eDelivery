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
package eu.spocseu.edeliverygw.messageparts;

import java.net.MalformedURLException;

import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EntityDetailsType;

/**
 * This class represent the underlying Originators JAXB object. With this class
 * it is possible to create the underlying JAXB object in a simple way. In the
 * constructor all required fields of the JAXB object must be given. With the
 * getXSDObject method it is possible to get the underlying JAXB object to add
 * some detail information.
 * 
 * @author R. Lindemann
 * 
 */
public class Originators {
	private final org.etsi.uri._02640.soapbinding.v1_.Originators jaxbObj;

	/**
	 * This constructor creates the Originators JAXB object with the given
	 * values. All the values will be put into the
	 * originators/from/electronicaddress .
	 * 
	 * @param _elAddress
	 *            The address of the sender if available.
	 * @param _surname
	 *            The surname of the sender could be 'null'
	 * @param _givenname
	 *            The given name of the sender could be 'null'
	 * @throws MalformedURLException
	 *             In the case of wrong addresses.
	 */
	public Originators(String _elAddress, String displayName)
			throws MalformedURLException {
		jaxbObj = new org.etsi.uri._02640.soapbinding.v1_.Originators();
		setFromElement(_elAddress, displayName);

	}

	/**
	 * This constructor creates the Originators JAXB object with the given
	 * values. All the values will be put into
	 * originators/from/electronicaddress .
	 * 
	 * @param _elAddress
	 *            The address of the sender if available.
	 * @throws MalformedURLException
	 *             In the case of wrong addresses.
	 */
	public Originators(String _elAddress) throws MalformedURLException {
		jaxbObj = new org.etsi.uri._02640.soapbinding.v1_.Originators();
		setFromElement(_elAddress, null);

	}

	public Originators(org.etsi.uri._02640.soapbinding.v1_.Originators jaxbObj)
			throws MalformedURLException {
		this.jaxbObj = jaxbObj;
	}

	/**
	 * Include the reply element for expected response messasges.
	 * 
	 * @param _address
	 *            The address where the reply will be expected.
	 * @param _name
	 *            The friendly name of the reply address. This value could be
	 *            null
	 * @throws MalformedURLException
	 *             If the given address is no e-mail address.
	 */
	public void addReplyTo(String _address, String _displayName)
			throws MalformedURLException {
		AttributedElectronicAddressType eAddress = SpocsFragments
				.createElectoricAddress(_address, _displayName);
		EntityDetailsType entityDetails = new EntityDetailsType();
		entityDetails.getAttributedElectronicAddressOrElectronicAddress().add(
				eAddress);
		jaxbObj.setReplyTo(entityDetails);
	}

	/**
	 * Gets the underlying JAXB object, for example to add some detailed
	 * information with this method.
	 * 
	 * @return The JAXB object.
	 * 
	 */
	public org.etsi.uri._02640.soapbinding.v1_.Originators getXSDObject() {
		return jaxbObj;
	}

	private void setFromElement(String _address, String displayName)
			throws MalformedURLException {
		AttributedElectronicAddressType eAddress = SpocsFragments
				.createElectoricAddress(_address, displayName);
		EntityDetailsType entityDetails = new EntityDetailsType();
		entityDetails.getAttributedElectronicAddressOrElectronicAddress().add(
				eAddress);

		// NamesPostalAddressListType namePostalList = new
		// NamesPostalAddressListType();
		// namePostalList.getNamePostalAddress().add(SpocsFragments.createNamePostalAdress(displayName));
		// entityDetails.setNamesPostalAddresses(namePostalList);
		EntityDetailsType details = SpocsFragments.createEntityDetails(
				SpocsFragments.createNamePostalAdress(displayName),
				SpocsFragments.createElectoricAddress(_address, displayName));
		// jaxbObj.setSender(details);
		jaxbObj.setFrom(details);
		// jaxbObj.setFrom(entityDetails);
	}
}
