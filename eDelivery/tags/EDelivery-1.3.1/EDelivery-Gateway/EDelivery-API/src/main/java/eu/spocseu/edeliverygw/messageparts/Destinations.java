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
