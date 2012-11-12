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
