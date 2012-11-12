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
import eu.spocseu.edeliverygw.SpocsWrongInputDataException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.messages.EvidenceMessage;

/**
 * This class represents a RelayREMMDAcceptanceRejection evidence. It helps to
 * create the underlying REMEvidenceType JAXB object of the xsd structure.
 * 
 * @author Lindemann
 * 
 */
public class RelayREMMDAcceptanceRejection extends Evidence
{
	private static Logger LOG = LoggerFactory
			.getLogger(RelayREMMDAcceptanceRejection.class);

	/**
	 * This constructor creates this RelayREMMDAcceptanceRejection evidence with
	 * the given JAXB object and the configuration.
	 * 
	 * @param evidenceType
	 *            The JAXB object.
	 */
	public RelayREMMDAcceptanceRejection(REMEvidenceType evidenceType)
	{
		super(evidenceType);

	}

	/**
	 * This constructor can be used to parse a serialized
	 * RelayREMMDAcceptanceRejection xml stream to create a JAXB evidence
	 * object.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param relayREMMDAcceptanceRejectionInpStream
	 *            The xml input stream with the evidence xml data.
	 * @throws SpocsWrongInputDataException
	 *             In the case of parsing errors
	 */
	public RelayREMMDAcceptanceRejection(Configuration config,
			InputStream relayREMMDAcceptanceRejectionInpStream)
		throws SpocsWrongInputDataException
	{
		try {
			@SuppressWarnings("unchecked")
			JAXBElement<REMEvidenceType> obj = (JAXBElement<REMEvidenceType>) JaxbContextHolder
					.getSpocsJaxBContext().createUnmarshaller()
					.unmarshal(relayREMMDAcceptanceRejectionInpStream);
			jaxbObj = obj.getValue();
		} catch (JAXBException ex) {
			throw new SpocsWrongInputDataException(
				"Error reading the RelayREMMDAcceptanceRejection xml stream.",
				ex);
		}
	}

	/**
	 * This constructor creates a RelayREMMDAcceptanceRejection object on base
	 * of a previous SubmissionAcceptanceRejection evidence. A success event
	 * will be set by this constructor.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param submissionAcceptanceRejection
	 *            The previous SubmissionAcceptanceRejection
	 */
	public RelayREMMDAcceptanceRejection(Configuration config,
			Evidence submissionAcceptanceRejection)
	{
		super(config);
		init(submissionAcceptanceRejection, true);
	}

	/**
	 * This constructor creates a DeliveryNonDeliveryToRecipient object based on
	 * previous SubmissionAcceptanceRejection evidence.
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 * @param submissionAcceptanceRejection
	 *            The previous SubmissionAcceptanceRejection
	 * @param isAcceptance
	 *            If this value is false a fault evidence event will be set.
	 */

	public RelayREMMDAcceptanceRejection(Configuration config,
			Evidence submissionAcceptanceRejection, boolean isAcceptance)
	{
		super(config);
		init(submissionAcceptanceRejection, isAcceptance);
	}

	private void init(Evidence submissionAcceptanceRejection,
			boolean isAcceptance)
	{

		evidenceType = Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION;
		if (isAcceptance) {
			LOG.debug("Create RelayREMMDAcceptanceRejection in success case.");
			setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
					.getSuccessEventCode());
		} else {
			LOG.debug("Create RelayREMMDAcceptanceRejection in fault case.");
			setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
					.getFaultEventCode());
		}
		initWithPrevious(submissionAcceptanceRejection.getXSDObject());
	}

	/**
	 * for internal use only!
	 * 
	 */

	public RelayREMMDAcceptanceRejection(EvidenceMessage message,
			Configuration config)
	{
		super(config);
		evidenceType = Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION;
		LOG.debug("Create RelayREMMDAcceptanceRejection in fault case.");
		setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION
				.getFaultEventCode());
		REMEvidenceType singleEvidence = EvidenceMessage.getEvidence(Evidences.SUBMISSION_ACCEPTANCE_REJECTION, message.getXSDObject().getREMMDEvidenceList());
		initWithPrevious(singleEvidence);
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
							.createRelayREMMDAcceptanceRejection(jaxbObj),
					out);

	}

}
