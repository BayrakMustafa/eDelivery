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

import java.io.IOException;
import java.math.BigInteger;

import org.etsi.uri._02640.soapbinding.v1_.OriginalMsgType;

/**
 * This class represents the underlying OriginaMsg JAXB object. This class makes
 * it possible to create the underlying JAXB object in a simple way. In the
 * constructor all required fields of the JAXB object must be given. The
 * getXSDObject method makes it possible to get the underlying JAXB object to
 * add some detailed information.
 * 
 * @author R. Lindemann
 * 
 */
public class Original
{
	private final OriginalMsgType jaxbObj;

	/**
	 * This constructor creates the OriginaMsg JAXB object with the required
	 * values.
	 * 
	 * @param bytes
	 *            The content of the original message (base64 encoded).
	 * @param contentType
	 *            The content type in mime format like "text/xml"
	 * @param sizeByte
	 *            The size of the content in bytes for streaming reasons.
	 */
	public Original(byte[] bytes, String contentType, long sizeByte)
		throws IOException
	{
		jaxbObj = new OriginalMsgType();
		if (contentType != null) {
			jaxbObj.setContentType(contentType);
		} else {
			throw new IOException("content could not be null!");
		}
		jaxbObj.setSize(BigInteger.valueOf(sizeByte));
		jaxbObj.setValue(bytes);
	}

	/**
	 * This constructor creates the OriginaMsg JAXB object with the required
	 * values.
	 * 
	 * @param bytes
	 *            The content of the original message (base64 encoded).
	 * @param contentType
	 *            The content type in mime format like "text/xml"
	 */
	public Original(byte[] bytes, String contentType) throws IOException
	{
		jaxbObj = new OriginalMsgType();
		if (contentType != null) {
			jaxbObj.setContentType(contentType);
		} else {
			throw new IOException("conent could not be null!");
		}
		jaxbObj.setSize(BigInteger.valueOf(bytes.length));
		jaxbObj.setValue(bytes);
	}

	/**
	 * Gets the underlying JAXB object, for example to add some detailed
	 * information with this method.
	 * 
	 * @return The JAXB object.
	 */
	public OriginalMsgType getXSDObject()
	{
		return jaxbObj;
	}
}
