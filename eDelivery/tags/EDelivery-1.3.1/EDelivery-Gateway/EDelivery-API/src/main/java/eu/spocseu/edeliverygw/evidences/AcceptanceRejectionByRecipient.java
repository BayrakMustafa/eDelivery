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
 * This class represent a AcceptanceRejectionByRecipient evidence. It helps to
 * create the underlying REMEvidenceType JAXB object of the xsd structure.
 * 
 * @author Lindemann
 * 
 */
public class AcceptanceRejectionByRecipient extends Evidence
{

	private static Logger LOG = LoggerFactory
			.getLogger(AcceptanceRejectionByRecipient.class);
	
	private boolean isSuccessful;

	/**
	 * This constructor creates this AcceptanceRejectionByRecipient evidence
	 * with the given JAXB object and the configuration.
	 * 
	 * @param evidenceType
	 *            The JAXB object.
	 */
	public AcceptanceRejectionByRecipient(REMEvidenceType evidenceType)
	{
		super(evidenceType);

	}

	/**
	 * This constructor creates a AcceptanceRejectionByRecipient object based on
	 * a previous RelayREMMDAcceptanceRejection evidence.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param evidence
	 *            The previous RelayREMMDAcceptanceRejection evidence message.
	 */

	public AcceptanceRejectionByRecipient(Configuration config,
			Evidence evidence, boolean isAcceptance)
	{
		super(config);
		init(config, evidence, isAcceptance);
	}
	
	/**
	 * This constructor creates a RejectionByRecipient (false) evidence based on
	 * a previous DeliveryNonDeliveryToRecipient evidence.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param errorEvent 
	 * 			 Error Event for false Evidence
	 * @param evidence
	 *            The previous RelayREMMDAcceptanceRejection evidence message.
	 */

	public AcceptanceRejectionByRecipient(Configuration config,
			Evidence evidence,REMErrorEvent eventReason)
	{
		super(config);
		init(config, evidence, false);
		super.setEventReason(eventReason);
	}

	/**
	 * This constructor creates a AcceptanceRejectionByRecipient object based on
	 * a previous DeliveryNonDeliveryToRecipient evidence.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param evidence
	 *            The previous DeliveryNonDeliveryToRecipient evidence message.
	 */
	public AcceptanceRejectionByRecipient(Configuration config,
			Evidence evidence)
	{
		super(config);
		init(config, evidence, true);

	}

	/**
	 * This constructor can be used to parse a serialized
	 * AcceptanceRejectionByRecipient xml stream to create a JAXB evidence
	 * object.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param evidenceSream
	 *            The xml input stream with the evidence xml data.
	 * @param typeOfInputStream
	 *            The type of the given InputStream. Possible values
	 *            DeliveryNonDeliveryToRecipient or
	 *            RelayREMMDAcceptanceRejection.
	 * @throws JAXBException
	 *             In the case of parsing errors
	 */
	public AcceptanceRejectionByRecipient(Configuration config,
			InputStream evidenceSream, Evidences typeOfInputStream)
		throws SpocsWrongInputDataException
	{
		super(config);
		if (typeOfInputStream
				.equals(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT))
			init(config, new DeliveryNonDeliveryToRecipient(config,
				evidenceSream), true);
		if (typeOfInputStream
				.equals(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION))
			init(config, new RelayREMMDAcceptanceRejection(config,
				evidenceSream), true);
	}

	private void init(Configuration config, Evidence previousEvidence,
			boolean isAcceptance)
	{

		evidenceType = Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT;
		if (isAcceptance) {
			LOG.debug("Create AcceptanceRejectionByRecipient in success case.");
			setEventCode(Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT
					.getSuccessEventCode());
		} else {
			LOG.debug("Create AcceptanceRejectionByRecipient in fault case.");
			setEventCode(Evidences.ACCEPTANCE_REJECTION_BY_RECIPIENT
					.getFaultEventCode());
		}
		isSuccessful = isAcceptance;
		initWithPrevious(previousEvidence.getXSDObject());
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
							.createAcceptanceRejectionByRecipient(jaxbObj),
					out);

	}

	public boolean isSuccessful()
	{
		return isSuccessful;
	}

}
