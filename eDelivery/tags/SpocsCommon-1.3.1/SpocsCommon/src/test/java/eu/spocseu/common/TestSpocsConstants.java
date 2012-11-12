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
package eu.spocseu.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestSpocsConstants
{

	@Test
	public void testCountryCodes()
	{

		assertEquals("Wrong coutry code", SpocsConstants.COUNTRY_CODES.GERMANY,
			SpocsConstants.COUNTRY_CODES.valueOf("GERMANY"));
		assertEquals("Wrong coutry code", SpocsConstants.COUNTRY_CODES.GERMANY,
			SpocsConstants.COUNTRY_CODES.getCountryCode("GERMANY"));
		assertEquals("Wrong coutry code", SpocsConstants.COUNTRY_CODES.GERMANY,
			SpocsConstants.COUNTRY_CODES.getCountryCode("DE"));
		assertEquals("Wrong coutry code", SpocsConstants.COUNTRY_CODES.AUSTRIA,
			SpocsConstants.COUNTRY_CODES.getCountryCode("AT"));
		assertEquals("Wrong coutry code", SpocsConstants.COUNTRY_CODES.GERMANY,
			SpocsConstants.COUNTRY_CODES.getCountryCode("Germany"));
	}
}
