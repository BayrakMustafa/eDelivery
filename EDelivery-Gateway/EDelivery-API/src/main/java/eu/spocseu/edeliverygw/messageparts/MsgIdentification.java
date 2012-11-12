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
