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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;

import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EntityDetailsType;
import org.junit.Test;

public class TestSpocsFragments
{

	@Test
	public void validateElectronicAdress() throws MalformedURLException
	{
		AttributedElectronicAddressType electronicAddressType = new AttributedElectronicAddressType();
		electronicAddressType.setValue("blaa@blaa.de");

		SpocsFragments.validateElectronicAdress(electronicAddressType);

		electronicAddressType.setValue("foobar@foobar.de");
		SpocsFragments.validateElectronicAdress(electronicAddressType);

		electronicAddressType.setValue("foo-bar@foo-bar.com");
		SpocsFragments.validateElectronicAdress(electronicAddressType);

		electronicAddressType.setValue("FooBar@foobar.de");
		SpocsFragments.validateElectronicAdress(electronicAddressType);

		try {
			electronicAddressType.setValue("de");
			SpocsFragments.validateElectronicAdress(electronicAddressType);
			fail("Exception expected for de");
		} catch (MalformedURLException e) {
		}
		try {
			electronicAddressType.setValue("www.google.de");
			SpocsFragments.validateElectronicAdress(electronicAddressType);
			fail("Exception expected for de");
		} catch (MalformedURLException e) {
		}

	}

	@Test
	public void getElectronicAdress() throws IOException,
			GeneralSecurityException
	{

		AttributedElectronicAddressType electronicAddressType = new AttributedElectronicAddressType();
		electronicAddressType.setValue("wo@bos-bremen.de");

		EntityDetailsType entityDetails = new EntityDetailsType();
		entityDetails.getAttributedElectronicAddressOrElectronicAddress().add(electronicAddressType);

		AttributedElectronicAddressType electronicAddress = SpocsFragments
				.getFirstElectronicAddressWithURI(entityDetails);

		assertEquals("wo@bos-bremen.de", electronicAddress.getValue());

	}
}
