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

public class MsgIdentification
{
	/**
	 * This class represents the underlying MsgIdentification JAXB object. With
	 * this class it is possible to create the underlying JAXB object in a
	 * simple way. In the constructor all required fields of the JAXB object
	 * must be given. With the getXSDObject method it is possible to get the
	 * underlying JAXB object to add some detail information.
	 * 
	 * @author R. Lindemann
	 * 
	 */
	private org.etsi.uri._02640.soapbinding.v1_.MsgIdentification jaxbObj;

	/**
	 * This constructor creates the MsgIdentification JAXB object with the
	 * required values
	 * 
	 * @param initialMsgId
	 *            The messageID out of the incoming message from the MD.
	 * @param replyTos
	 *            If the message applies to a previous message some reply to
	 *            addresses.
	 */
	public MsgIdentification(String initialMsgId, String... replyTos)
	{
		jaxbObj = new org.etsi.uri._02640.soapbinding.v1_.MsgIdentification();
		// TODO this should be clarified
		jaxbObj.setMessageID(initialMsgId);
		for (String replyTo : replyTos) {
			jaxbObj.getInReplyTo().add(replyTo);
		}
	}

	/**
	 * Add some references to the MsgIdentification object
	 * 
	 * @param references references
	 */
	public void addReferences(String... references)
	{
		for (String reference : references) {
			jaxbObj.getReferences().add(reference);
		}
	}

	/**
	 * Gets the underlying JAXB object, for example to add some detailed
	 * information with this method.
	 * 
	 * @return The JAXB object.
	 */
	public org.etsi.uri._02640.soapbinding.v1_.MsgIdentification getXSDObject()
	{
		return jaxbObj;
	}
}
