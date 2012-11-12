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

import javax.xml.bind.JAXBException;

import org.etsi.uri._02640.v2_.ObjectFactory;
import org.etsi.uri._02640.v2_.REMEvidenceType;

import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.configuration.Configuration;

/**
 * This class represents a ReceivedByNonREMSystem evidence. It helps to create
 * the underlying REMEvidenceType JAXB object of the xsd structure.
 * 
 * @author Lindemann
 * 
 */
public class ReceivedByNonREMSystem extends Evidence
{


	/**
	 * This constructor creates this ReceivedByNonREMSystem evidence:
	 * 
	 * @param config
	 *            Configuration object to set some properties
	 */
	public ReceivedByNonREMSystem(Configuration config)
	{
		super(config);

	}

	/**
	 * This constructor creates this ReceivedByNonREMSystem evidence with the
	 * given JAXB object and the configuration.
	 * 
	 * @param evidenceType
	 *            The JAXB object.
	 */
	public ReceivedByNonREMSystem(REMEvidenceType evidenceType)
	{
		super(evidenceType);

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
					new ObjectFactory().createReceivedFromNonREMSystem(jaxbObj),
					out);

	}

}
