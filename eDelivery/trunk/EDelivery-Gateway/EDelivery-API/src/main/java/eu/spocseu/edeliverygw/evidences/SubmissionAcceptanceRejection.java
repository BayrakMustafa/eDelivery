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
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.v2_.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2_.EntityDetailsListType;
import org.etsi.uri._02640.v2_.EntityDetailsType;
import org.etsi.uri._02640.v2_.MessageDetailsType;
import org.etsi.uri._02640.v2_.NamePostalAddressType;
import org.etsi.uri._02640.v2_.ObjectFactory;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.GeneralMessage;

/**
 * This class represents a SubmissionAcceptanceRejection evidence. It helps to
 * create the underlying REMEvidenceType JAXB object of the xsd structure.
 * 
 * @author Lindemann
 * 
 */
public class SubmissionAcceptanceRejection extends Evidence
{
	private static Logger LOG = LoggerFactory
			.getLogger(SubmissionAcceptanceRejection.class);

	protected SubmissionAcceptanceRejection(Configuration config)
	{
		super(config);

	}

	protected SubmissionAcceptanceRejection(Configuration config,
			boolean isAcceptance)
	{
		super(config);
		if (isAcceptance) {
			LOG.debug("Create SubmissionAcceptanceRejection in success case.");
			setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION
					.getSuccessEventCode());
		} else {
			LOG.debug("Create SubmissionAcceptanceRejection in fault case.");
			setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION
					.getFaultEventCode());
		}
	}

	/**
	 * This constructor creates a SubmissionAcceptanceRejection object based on
	 * a given DispatchMessage. The purpose is to create the
	 * SubmissionAcceptanceRejection which will be attached to the Dispatch
	 * Message which will be send out.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param dispatch
	 *            DispatchMessage as input information for this object. This is
	 *            a Dispatch which should be send out!
	 * @param isAcceptance
	 *            If true is given a success event will be created.
	 */
	public SubmissionAcceptanceRejection(Configuration config,
			DispatchMessage dispatch, boolean isAcceptance)
	{
		super(config);
		initWithDispatch(config, dispatch.getXSDObject(), isAcceptance);
	}

	/**
	 * Creates a SubmissionAcceptanceRejection evidence object with the given
	 * JAXB object.
	 * 
	 * @param evidenceType
	 *            The JAXB object
	 */
	public SubmissionAcceptanceRejection(REMEvidenceType evidenceType)
	{
		super(evidenceType);

	}

	private void initWithDispatch(Configuration config,
			REMDispatchType dispatch, boolean isAcceptance)
	{

		evidenceType = Evidences.SUBMISSION_ACCEPTANCE_REJECTION;
		if (isAcceptance) {
			LOG.debug("Create SubmissionAcceptanceRejection in success case.");
			setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION
					.getSuccessEventCode());
		} else {
			LOG.debug("Create SubmissionAcceptanceRejection in fault case.");
			setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION
					.getFaultEventCode());
		}

		EntityDetailsType from = dispatch.getMsgMetaData().getOriginators()
				.getFrom();
		String senderEAddress = ((AttributedElectronicAddressType) from
				.getAttributedElectronicAddressOrElectronicAddress().get(0))
				.getValue();
		String senderName = ((AttributedElectronicAddressType) from
				.getAttributedElectronicAddressOrElectronicAddress().get(0))
				.getDisplayName();
		
		
		String senderPostalName = null;
		String senderPostalStreet = null;
		String senderPostalZip = null;
		String senderPostalCountry = null;
		String senderPostalLocality = null;
		if (dispatch.getMsgMetaData().getOriginators().getFrom()
				.getNamesPostalAddresses() != null ) {
			
			NamePostalAddressType namePostalAddressType = dispatch.getMsgMetaData().getOriginators().getFrom()
					.getNamesPostalAddresses().getNamePostalAddress().get(0);
			
			if(namePostalAddressType != null && namePostalAddressType.getPostalAddress() != null){
				senderPostalName = namePostalAddressType.getEntityName().getName().get(0);
				List<String> streetAddress = namePostalAddressType.getPostalAddress().getStreetAddress();
				if(streetAddress != null)
					senderPostalStreet = streetAddress.get(0);
				senderPostalZip = namePostalAddressType.getPostalAddress().getPostalCode();
				senderPostalCountry = namePostalAddressType.getPostalAddress().getCountryName();
				senderPostalLocality = namePostalAddressType.getPostalAddress().getLocality();				
			}
			
			
		}
		
		jaxbObj.setSenderDetails(createEntityDetailsType(senderEAddress,
			senderName, senderPostalStreet,senderPostalLocality,senderPostalZip,senderPostalCountry,senderPostalName));
		MessageDetailsType messageDetailsType = new MessageDetailsType();
		messageDetailsType.setMessageIdentifierByREMMD(dispatch
				.getMsgMetaData().getMsgIdentification().getMessageID());
//		NormalizedMsg norMsg = dispatch.getNormalizedMsg();
//		if (norMsg != null && norMsg.getInformational() != null)
//			messageDetailsType.setMessageSubject(norMsg.getInformational()
//					.getSubject());
		jaxbObj.setSenderMessageDetails(messageDetailsType);

		EntityDetailsType recipient = dispatch.getMsgMetaData()
				.getDestinations().getRecipient();
		String recipientAddress = GeneralMessage.getAttributedElectronicAdress(
			recipient).getValue();
		String recipientName = GeneralMessage.getAttributedElectronicAdress(
			recipient).getDisplayName();
		String[] recipientPostalNames = new String[0];

		if (dispatch.getMsgMetaData().getDestinations().getRecipient()
				.getNamesPostalAddresses() != null
				&& dispatch.getMsgMetaData().getDestinations().getRecipient()
						.getNamesPostalAddresses().getNamePostalAddress()
						.get(0) != null) {
			recipientPostalNames = dispatch.getMsgMetaData().getDestinations()
					.getRecipient().getNamesPostalAddresses()
					.getNamePostalAddress().get(0).getEntityName().getName()
					.toArray(recipientPostalNames);
		}
		EntityDetailsListType detailList = new EntityDetailsListType();
		detailList.getEntityDetails().add(
			createEntityDetailsType(recipientAddress, recipientName,
				recipientPostalNames));
		jaxbObj.setRecipientsDetails(detailList);
		jaxbObj.setSubmissionTime(dispatch.getMsgMetaData()
				.getDeliveryConstraints().getInitialSend());
		if (dispatch.getMsgMetaData().getOriginators() != null
				&& dispatch.getMsgMetaData().getOriginators().getReplyTo() != null
				&& dispatch.getMsgMetaData().getOriginators().getReplyTo()
						.getAttributedElectronicAddressOrElectronicAddress() != null) {
			AttributedElectronicAddressType eAddress = SpocsFragments.getFirstElectronicAddressWithURI(dispatch
				.getMsgMetaData().getOriginators().getReplyTo());
			jaxbObj.setReplyToAddress(eAddress);
		} else if (dispatch.getMsgMetaData().getOriginators() != null) {
			AttributedElectronicAddressType eAddress = GeneralMessage
					.getAttributedElectronicAdress(dispatch.getMsgMetaData()
							.getOriginators().getFrom());
			jaxbObj.setReplyToAddress(eAddress);
		}
		jaxbObj.setId(UUID.randomUUID().toString());
	}

	/**
	 * This method serializes the underlying JAXB object.
	 * 
	 * @param out
	 *            The output stream that the information will be streamed into.
	 */
	public void serialize(OutputStream out) throws JAXBException
	{
		JaxbContextHolder
				.getSpocsJaxBContext()
				.createMarshaller()
				.marshal(
					new ObjectFactory()
							.createSubmissionAcceptanceRejection(jaxbObj),
					out);

	}
}
