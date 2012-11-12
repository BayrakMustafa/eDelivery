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

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

/**
 * This class represents the underlying DeliveryConstraints JAXB object. With
 * this class it is possible to create the underlying JAXB object in a simple
 * way. In the constructor all required fields of the JAXB object must be given.
 * The getXSDObject method makes it possible to get the underlying JAXB object
 * to add some detailed information.
 * 
 * @author R. Lindemann
 * 
 */
public class DeliveryConstraints
{
	private org.etsi.uri._02640.soapbinding.v1_.DeliveryConstraints jaxbObj;

	/**
	 * This constructor creates the DeliveryConstraints JAXB object with the
	 * required values.
	 * 
	 * @param initialSend
	 *            The date/time of the initial send time out of the incoming
	 *            message from the MD
	 * @throws DatatypeConfigurationException
	 *             In the case of date conversion error
	 */
	public DeliveryConstraints(Date initialSend)
		throws DatatypeConfigurationException
	{
		jaxbObj = new org.etsi.uri._02640.soapbinding.v1_.DeliveryConstraints();
		jaxbObj.setOrigin(SpocsFragments
				.createXMLGregorianCalendar(initialSend));
	}

	/**
	 * The underlying JAXB object could be get to add some detailed information
	 * with this method.
	 * 
	 * @return The JAXB object.
	 */
	public org.etsi.uri._02640.soapbinding.v1_.DeliveryConstraints getXSDObject()
	{
		return jaxbObj;
	}
}
