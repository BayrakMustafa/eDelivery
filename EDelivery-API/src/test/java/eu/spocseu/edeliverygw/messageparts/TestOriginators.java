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
