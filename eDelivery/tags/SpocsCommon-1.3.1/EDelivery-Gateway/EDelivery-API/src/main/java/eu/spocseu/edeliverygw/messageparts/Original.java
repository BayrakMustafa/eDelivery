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
