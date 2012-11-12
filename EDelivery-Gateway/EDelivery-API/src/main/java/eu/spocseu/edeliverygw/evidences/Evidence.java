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

package eu.spocseu.edeliverygw.evidences;

import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.etsi.uri._02231.v2_.ElectronicAddressType;
import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EntityDetailsListType;
import org.etsi.uri._02640.v2_.EntityDetailsType;
import org.etsi.uri._02640.v2_.EntityNameType;
import org.etsi.uri._02640.v2_.EventReasonType;
import org.etsi.uri._02640.v2_.EventReasonsType;
import org.etsi.uri._02640.v2_.EvidenceIssuerPolicyIDType;
import org.etsi.uri._02640.v2_.NamePostalAddressType;
import org.etsi.uri._02640.v2_.NamesPostalAddressListType;
import org.etsi.uri._02640.v2_.PostalAddressType;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;

/**
 * Internal class
 * 
 * @author Lindemann
 * 
 */
public abstract class Evidence
{
	private static Logger LOG = LoggerFactory.getLogger(Evidence.class);
	protected REMEvidenceType jaxbObj;
	protected Evidences evidenceType;
	protected Configuration config;

	protected Evidence(REMEvidenceType _jaxbObj)
	{
		jaxbObj = _jaxbObj;
	}

	protected Evidence()
	{
	}

	protected Evidence(Configuration _config)
	{
		config = _config;
		jaxbObj = new REMEvidenceType();
		jaxbObj.setVersion("2.1.1");
		EvidenceIssuerPolicyIDType issuerPolicy = new EvidenceIssuerPolicyIDType();
		issuerPolicy.getPolicyID()
				.add("http://uri.eu-spocs.eu/eDeliveryPolicy");

		jaxbObj.setEvidenceIssuerPolicyID(issuerPolicy);
		jaxbObj.setEvidenceIdentifier(UUID.randomUUID().toString());

		EDeliveryDetails details = config.geteDeliveryDetails();

		EntityDetailsType issuerDetails = createEntityDetailsType(
			null, details.getGatewayName(),
			details.getStreetAdress(), details.getLocality(),
			details.getPostalCode(), details.getCountry(),
			details.getGatewayName());

		AttributedElectronicAddressType elAddre = new AttributedElectronicAddressType();
		elAddre.setValue(config.geteDeliveryDetails().getGatewayAddress());
		elAddre.setScheme("mailto");
		issuerDetails.getAttributedElectronicAddressOrElectronicAddress().add(elAddre);
		jaxbObj.setEvidenceIssuerDetails(issuerDetails);
		try {
			jaxbObj.setEventTime(SpocsFragments
					.createXMLGregorianCalendar(new Date()));
		} catch (DatatypeConfigurationException e) {
			LOG.error("Date error:" + e.getMessage());
		}
	}

	protected void initWithPrevious(REMEvidenceType previousJaxB)
	{
		// set the sender details
		jaxbObj.setSenderDetails(previousJaxB.getSenderDetails());
		// set the recipients details
		jaxbObj.setRecipientsDetails(previousJaxB.getRecipientsDetails());

		// jaxbObj.setSubmissionTime(submissionAcceptanceRejection.getMetaData()
		// .getDeliveryConstraints().getInitialSend());

		jaxbObj.setReplyToAddress(previousJaxB.getReplyToAddress());
		jaxbObj.setSenderMessageDetails(previousJaxB.getSenderMessageDetails());

	}

	public void setEventCode(String _eventCode)
	{
		jaxbObj.setEventCode(_eventCode);

	}

	public Evidences getEvidenceType()
	{
		return evidenceType;
	}

	public void setEvidenceType(Evidences evidenceType)
	{
		this.evidenceType = evidenceType;

	}

	public REMEvidenceType getXSDObject()
	{
		return jaxbObj;
	}

	public void setEventReason(REMErrorEvent errorEvent)
	{
		EventReasonsType eventResonsType = new EventReasonsType();
		EventReasonType eventResonType = new EventReasonType();

		eventResonType.setCode(errorEvent.getEventCode());
		eventResonType.setDetails(errorEvent.getEventDetails());

		eventResonsType.getEventReason().add(eventResonType);
		jaxbObj.setEventReasons(eventResonsType);
	}

	public REMErrorEvent getEventReson()
	{
		if (jaxbObj.getEventReasons() == null)
			return null;
		else
			return REMErrorEvent.getRemErrorEventForJaxB(jaxbObj
					.getEventReasons());
	}

	protected REMEvidenceType createRemEvidenceType(Configuration config,
			String senderEAddress, String recipientAddress)
	{
		REMEvidenceType remEvi = new REMEvidenceType();
		jaxbObj.setVersion("1.0");
		remEvi.setEvidenceIdentifier("uuid:" + UUID.randomUUID().toString());

		remEvi.setEvidenceIssuerDetails(createEntityDetailsType(config
				.geteDeliveryDetails().getGatewayAddress(), config
				.geteDeliveryDetails().getGatewayName(), config
				.geteDeliveryDetails().getStreetAdress(), config
				.geteDeliveryDetails().getLocality(), config
				.geteDeliveryDetails().getPostalCode(), config
				.geteDeliveryDetails().getCountry(), config
				.geteDeliveryDetails().getGatewayName()));
		try {
			remEvi.setEventTime(SpocsFragments
					.createXMLGregorianCalendar(new Date()));
		} catch (DatatypeConfigurationException e) {
			LOG.error("Date error:" + e.getMessage());
		}
		remEvi.setSenderDetails(createEntityDetailsType(senderEAddress));
		EntityDetailsListType detailList = new EntityDetailsListType();
		detailList.getEntityDetails().add(
			createEntityDetailsType(recipientAddress));
		remEvi.setRecipientsDetails(detailList);

		return remEvi;
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress)
	{

		return createEntityDetailsType(eAddress, null, null, null, null, null,
			(String) null);
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress,
			String displayName, String postalName)
	{
		String[] array = { postalName };
		return createEntityDetailsType(eAddress, displayName, null, null, null,
			null, array);
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress,
			String displayName, String[] postalName)
	{
		return createEntityDetailsType(eAddress, displayName, null, null, null,
			null, postalName);
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress,
			String displayName, String street, String locality, String zipcode,
			String country, String postalName)
	{
		String[] array = { postalName };
		return createEntityDetailsType(eAddress, displayName, street, locality,
			zipcode, country, array);
	}

	protected EntityDetailsType createEntityDetailsType(String eAddress,
			String displayName, String street, String locality, String zipcode,
			String country, String[] postalName)
	{

		// prepare
		EntityDetailsType detailsType = new EntityDetailsType();

		// set the values
		AttributedElectronicAddressType elAddre = new AttributedElectronicAddressType();

		if (postalName != null) {
			NamePostalAddressType postAddre = new NamePostalAddressType();
			EntityNameType name = new EntityNameType();
			for (String string : postalName) {
				name.getName().add(string);
			}
			postAddre.setEntityName(name);

			PostalAddressType postalAddressType = new PostalAddressType();
			if (street != null)
				postalAddressType.getStreetAddress().add(street);
			if (locality != null) postalAddressType.setLocality(locality);
			if (country != null) postalAddressType.setCountryName(country);
			if (zipcode != null) postalAddressType.setPostalCode(zipcode);

			if (street != null || locality != null || zipcode != null) {
				postAddre.setPostalAddress(postalAddressType);

			}
			NamesPostalAddressListType postList = new NamesPostalAddressListType();
			postList.getNamePostalAddress().add(postAddre);
			detailsType.setNamesPostalAddresses(postList);
		}

		if (displayName != null) elAddre.setDisplayName(displayName);
		if (eAddress != null) {
			elAddre.setValue(eAddress);
			// elAddre.setScheme(SpocsConstants.E_ADDRESS_SCHEMES.RFC5322ADDRESS
			// .name());
			detailsType.getAttributedElectronicAddressOrElectronicAddress()
					.add(elAddre);
		}
		return detailsType;
	}

	protected ElectronicAddressType createElectronicAddressType(String eAddress)
	{
		ElectronicAddressType elecAddressType = new ElectronicAddressType();
		elecAddressType.getURI().add(eAddress);
		return elecAddressType;
	}

	protected NamesPostalAddressListType createPostalAddress(String name)
	{
		if (name == null) return null;
		NamesPostalAddressListType postAddresses = new NamesPostalAddressListType();
		NamePostalAddressType address = new NamePostalAddressType();
		EntityNameType entityName = new EntityNameType();
		entityName.getName().add(name);
		address.setEntityName(entityName);
		postAddresses.getNamePostalAddress().add(address);
		return postAddresses;
	}

	public abstract void serialize(OutputStream out) throws JAXBException;
}
