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

import org.etsi.uri._02640.soapbinding.v1_.Destinations.OtherRecipients;
import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EntityDetailsType;

/**
 * This class represent the underlying Destinations JAXB object. With this class
 * it is possible to create the underlying JAXB object in a simple way. In the
 * constructor all required fields of the JAXB object must be given. The
 * getXSDObject method makes it possible to get the underlying JAXB object to
 * add some detailed information.
 * 
 * @author R. Lindemann
 * 
 */
public class Destinations
{

	private org.etsi.uri._02640.soapbinding.v1_.Destinations jaxbObj;

	/**
	 * This constructor creates the Destinations JAXB object with the required
	 * values. The given parameter will be set to the recipient element into the
	 * electronicAddress. The created recipient element will be automatic added
	 * to the "To" element.
	 * 
	 * @param address
	 *            Value of the electronic address of the recipient in RFC5322
	 *            format.
	 * @param displayName
	 *            The friendly name of the recipient.
	 * @throws MalformedURLException if address is not a URL
	 */
	public Destinations(String address, String displayName)
		throws MalformedURLException
	{
		init(address, displayName);
	}

	/**
	 * This constructor creates the Destinations JAXB object with the required
	 * values.. The created recipient element will be automatic added to the
	 * "To" element.
	 * 
	 * @param address
	 *            Value of the electronic address of the recipient in RFC5322
	 *            format.
	 * @throws MalformedURLException
	 *             If the given address is no e-mail address.
	 */
	public Destinations(String address) throws MalformedURLException
	{
		init(address, null);
	}

	/**
	 * This constructor creates the Destinations JAXB object. The needed "To"
	 * element must be added by your own.
	 * 
	 * @param electronicAddressType
	 *            The complete electronic address of the recipient.
	 * @throws MalformedURLException
	 *             If the given address is no e-mail address.
	 */
	public Destinations(AttributedElectronicAddressType electronicAddressType)
		throws MalformedURLException
	{
		jaxbObj = new org.etsi.uri._02640.soapbinding.v1_.Destinations();
		jaxbObj.setRecipient(SpocsFragments.createEntityDetails(null,
			electronicAddressType));
		jaxbObj.setOtherRecipients(new OtherRecipients());
	}

	/**
	 * Adds a carbon copy to the destinations.
	 * 
	 * @param address
	 *            eAddress of the other recipient
	 * @param displayName
	 *            the display name or null.
	 * @return The created object.
	 * @throws MalformedURLException if address is not valid
	 */
	public EntityDetailsType addCarbonCopy(String address, String displayName)
			throws MalformedURLException
	{
		if (address == null)
			throw new IllegalArgumentException("Address must not be null");
		AttributedElectronicAddressType electronicAdress = new AttributedElectronicAddressType();
		electronicAdress.setValue(address);
		electronicAdress.setDisplayName(displayName);

		return addCarbonCopy(SpocsFragments.createEntityDetails(null,
			electronicAdress));
	}

	/**
	 * Adds a carbon copy to the destinations.
	 * 
	 * @param electronicAddressType
	 *            The elecAddress of the Carbon Copy
	 * @return The created Object.
	 * @throws MalformedURLException If the given address is no e-mail address.
	 */
	public EntityDetailsType addCarbonCopy(
			EntityDetailsType electronicAddressType)
			throws MalformedURLException
	{

		SpocsFragments
				.validateElectronicAdress((AttributedElectronicAddressType) electronicAddressType
						.getAttributedElectronicAddressOrElectronicAddress()
						.get(0));
		jaxbObj.getOtherRecipients().getCc().add(electronicAddressType);
		return electronicAddressType;
	}

	private void init(String address, String displayName)
			throws MalformedURLException
	{

		jaxbObj = new org.etsi.uri._02640.soapbinding.v1_.Destinations();
		jaxbObj.setRecipient(SpocsFragments.createEntityDetails(
			SpocsFragments.createNamePostalAdress(displayName),
			SpocsFragments.createElectoricAddress(address, displayName)));
		jaxbObj.setOtherRecipients(new OtherRecipients());
		jaxbObj.getOtherRecipients().getTo().add(jaxbObj.getRecipient());

	}

	/**
	 * Gets the underlying JAXB object for detailed informations
	 * with this method.
	 * 
	 * @return The JAXB object.
	 */
	public org.etsi.uri._02640.soapbinding.v1_.Destinations getXSDObject()
	{
		return jaxbObj;
	}
}
