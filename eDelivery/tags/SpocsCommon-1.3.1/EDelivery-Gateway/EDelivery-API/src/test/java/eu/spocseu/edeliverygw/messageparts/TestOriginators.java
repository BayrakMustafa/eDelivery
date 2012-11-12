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

import static org.junit.Assert.assertNotNull;

import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EntityDetailsType;
import org.junit.Test;


public class TestOriginators
{

	@Test
	public void initWithCommaOriginators() throws Exception
	{
		org.etsi.uri._02640.soapbinding.v1_.Originators jaxbObj = new org.etsi.uri._02640.soapbinding.v1_.Originators();

		EntityDetailsType eDeliveryActorType = new EntityDetailsType();
		AttributedElectronicAddressType elec = new AttributedElectronicAddressType();
		elec.setDisplayName("Oley,Wilko");
		elec.setValue("wo@bos-bremen.de");
		eDeliveryActorType.getAttributedElectronicAddressOrElectronicAddress().add(elec);
		jaxbObj.setFrom(eDeliveryActorType);

		Originators ori = new Originators(jaxbObj);
		assertNotNull(ori);
	}

	public void initWithNoCommaOriginators() throws Exception

	{

		org.etsi.uri._02640.soapbinding.v1_.Originators jaxbObj = new org.etsi.uri._02640.soapbinding.v1_.Originators();

		EntityDetailsType eDeliveryActorType = new EntityDetailsType();
		AttributedElectronicAddressType elec = new AttributedElectronicAddressType();
		elec.setDisplayName("OleyWilko");
		elec.setValue("wo@bos-bremen.de");
		eDeliveryActorType.getAttributedElectronicAddressOrElectronicAddress().add(elec);
		jaxbObj.setFrom(eDeliveryActorType);

		Originators ori = new Originators(jaxbObj);
		assertNotNull(ori);

	}
}
