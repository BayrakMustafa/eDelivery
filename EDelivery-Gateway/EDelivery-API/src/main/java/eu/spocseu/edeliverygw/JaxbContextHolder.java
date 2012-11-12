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
package eu.spocseu.edeliverygw;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.w3._2005._08.addressing.ObjectFactory;

/**
 * This class represents a Holder for the adressing, spocs and etsi JAXB
 * Context.
 * 
 * @author R. Lindemann
 */
public class JaxbContextHolder
{

	private static javax.xml.bind.JAXBContext spocsContext = null;
	private static javax.xml.bind.JAXBContext addressingContext = null;

	private static javax.xml.bind.JAXBContext etsi_vi = null;

	/**
	 * Internal method to get the JAXB context to marshal and unmarshal spocs
	 * objects.
	 * 
	 * @return The created JAXB context.
	 * @throws JAXBException
	 *             In case of errors creating the JAXB context.
	 */
	public static javax.xml.bind.JAXBContext getSpocsJaxBContext()
			throws JAXBException
	{
		if (spocsContext == null) {
			spocsContext = JAXBContext
					.newInstance(org.etsi.uri._02640.v2_.ObjectFactory.class, org.etsi.uri._02640.soapbinding.v1_.ObjectFactory.class);
		}
		return spocsContext;
	}
	
//	public static javax.xml.bind.JAXBContext getSoapBindingJaxBContext()
//			throws JAXBException
//	{
//		if (soapContext == null) {
//			soapContext = JAXBContext
//					.newInstance(org.etsi.uri._02640.soapbinding.v1_.ObjectFactory.class);
//		}
//		return soapContext;
//	}

	/**
	 * Internal method to get the JAXB context to marshal and unmarshal
	 * addressing objects.
	 * 
	 * @return The created JAXB context.
	 * @throws JAXBException
	 *             In case of errors creating the JAXB context.
	 */
	public static javax.xml.bind.JAXBContext getAddressingJaxBContext()
			throws JAXBException
	{
		if (addressingContext == null) {
			addressingContext = JAXBContext.newInstance(ObjectFactory.class);
		}
		return addressingContext;
	}

	/**
	 * Internal method to get the JAXB context to marshal and unmarshal etsiV1
	 * objects.
	 * 
	 * @return The created JAXB context.
	 * @throws JAXBException
	 *             In case of errors creating the JAXB context.
	 */
	public static javax.xml.bind.JAXBContext getETSIV2JaxBContext()
			throws JAXBException
	{
		if (etsi_vi == null) {
			etsi_vi = JAXBContext
					.newInstance(org.etsi.uri._02640.v2_.ObjectFactory.class);
		}
		return etsi_vi;
	}
}
