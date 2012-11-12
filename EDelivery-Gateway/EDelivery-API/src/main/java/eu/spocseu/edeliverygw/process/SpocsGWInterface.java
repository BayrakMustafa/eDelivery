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

package eu.spocseu.edeliverygw.process;

import eu.spocseu.edeliverygw.SpocsGWProcessingException;
import eu.spocseu.edeliverygw.evidences.exception.NonDeliveryException;
import eu.spocseu.edeliverygw.evidences.exception.RelayREMMDRejectionException;
import eu.spocseu.edeliverygw.messages.AcceptanceRejectionByRecipientMessage;
import eu.spocseu.edeliverygw.messages.DeliveryNonDeliveryToRecipientMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessageResponse;
import eu.spocseu.edeliverygw.messages.RetrievalNonRetrievalByRecipientMessage;
import eu.spocseu.tsl.TrustedServiceImpl;

/**
 * The Interface which is used to process a incomming {@link DispatchMessage},
 * {@link AcceptanceRejectionByRecipientMessage}
 * {@link DeliveryNonDeliveryToRecipientMessage} and
 * {@link RetrievalNonRetrievalByRecipientMessage}. The
 * <code>initOnStartup()</code> method is called on the <code>jBoss</code>
 * startup, e.g. you can use it to intitialize a timer .
 * 
 * @see DefaultImpl
 */

public interface SpocsGWInterface
{
	public DispatchMessageResponse processDispatch(DispatchMessage message,
			TrustedServiceImpl tsl) throws NonDeliveryException,
			RelayREMMDRejectionException;

	public void processEvidence(AcceptanceRejectionByRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException;

	public void processEvidence(DeliveryNonDeliveryToRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException;

	public void processEvidence(
			RetrievalNonRetrievalByRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException;

	public void initOnStartup();

}
