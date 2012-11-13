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
