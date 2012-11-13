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

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.etsi.uri._02640.v2_.ObjectFactory;
import org.etsi.uri._02640.v2_.REMEvidenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.Configuration;

/**
 * This class represents a DeliveryNonDeliveryToRecipient evidence. It helps to
 * create the underlying REMEvidenceType JAXB object of the xsd structure.
 * 
 * @author Lindemann
 * 
 */
public class DeliveryNonDeliveryToRecipient extends Evidence
{

	private static Logger LOG = LoggerFactory
			.getLogger(DeliveryNonDeliveryToRecipient.class);
	
	private boolean isSuccessful;
	/**
	 * This constructor creates this DeliveryNonDeliveryToRecipient evidence
	 * with the given JAXB object and the configuration.
	 * 
	 * @param evidenceType
	 *            The JAXB object.
	 */
	public DeliveryNonDeliveryToRecipient(REMEvidenceType evidenceType)
	{
		super(evidenceType);

	}

	/**
	 * This constructor can be used to parse a serialized
	 * DeliveryNonDeliveryToRecipient xml stream to create a JAXB evidence
	 * object.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param deliveryNonDeliveryInpStream
	 *            The xml input stream with the evidence xml data.
	 * @throws JAXBException
	 *             In the case of parsing errors
	 */
	public DeliveryNonDeliveryToRecipient(Configuration config,
			InputStream deliveryNonDeliveryInpStream)
		throws SpocsWrongInputDataException
	{
		try {
			@SuppressWarnings("unchecked")
			JAXBElement<REMEvidenceType> obj = (JAXBElement<REMEvidenceType>) JaxbContextHolder
					.getSpocsJaxBContext().createUnmarshaller()
					.unmarshal(deliveryNonDeliveryInpStream);
			jaxbObj = obj.getValue();
		} catch (JAXBException ex) {
			throw new SpocsWrongInputDataException(
				"Error reading the DeliveryNonDeliveryToRecipient xml stream.",
				ex);
		}
	}

	/**
	 * This constructor creates a DeliveryNonDeliveryToRecipient object based on
	 * SubmissionAcceptanceRejection evidence.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param submissionAcceptanceRejection
	 *            The previous SubmissionAcceptanceRejection
	 */
	public DeliveryNonDeliveryToRecipient(Configuration config,
			Evidence submissionAcceptanceRejection)
	{
		super(config);
		init(config, submissionAcceptanceRejection, true);
	}
	
	/**
	 * This constructor creates a NonDeliveryToRecipient (false) evidence based on
	 * SubmissionAcceptanceRejection evidence.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param submissionAcceptanceRejection
	 *            The previous SubmissionAcceptanceRejection
	 */
	public DeliveryNonDeliveryToRecipient(Configuration config,
			Evidence submissionAcceptanceRejection, REMErrorEvent eventReson)
	{
		super(config);
		init(config, submissionAcceptanceRejection, false);
		super.setEventReason(eventReson);
	}

	/**
	 * This constructor creates a DeliveryNonDeliveryToRecipient object based on
	 * previous SubmissionAcceptanceRejection evidence.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param submissionAcceptanceRejection
	 *            The previous SubmissionAcceptanceRejection
	 * @param isDelivery
	 *            If this value is false a fault evidence event will be set.
	 */
	public DeliveryNonDeliveryToRecipient(Configuration config,
			Evidence submissionAcceptanceRejection, boolean isDelivery)
	{
		super(config);
		init(config, submissionAcceptanceRejection, isDelivery);
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
							.createDeliveryNonDeliveryToRecipient(jaxbObj),
					out);

	}

	private void init(Configuration config,
			Evidence submissionAcceptanceRejection, boolean isDelivery)
	{

		evidenceType = Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT;
		if (isDelivery) {
			LOG.debug("Create DeliveryNonDeliveryToRecipient in success case.");
			setEventCode(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
					.getSuccessEventCode());
			
		} else {
			LOG.debug("Create DeliveryNonDeliveryToRecipient in fault case.");
			setEventCode(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT
					.getFaultEventCode());
		}
		initWithPrevious(submissionAcceptanceRejection.getXSDObject());
		isSuccessful = isDelivery;
	}

	public boolean isSuccessful()
	{
		return isSuccessful;
	}

}
