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

package eu.spocseu.edeliverygw.evidences.exception;

import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.messages.DispatchMessage;

/**
 * This Exception represents a {@link DeliveryNonDeliveryToRecipient} Evidence
 * in the fault case. It can be thrown to send a DeliveryNonDelivery Evidence to
 * the Sender.
 * 
 * @author lindemann
 */

public class NonDeliveryException extends EvidenceException
{

	private static final long serialVersionUID = 1L;

	public NonDeliveryException(Configuration config, DispatchMessage message,
			REMErrorEvent _errorEvent, Throwable cause)
	{
		super(new DeliveryNonDeliveryToRecipient(config,
			message.getSubmissionAcceptanceRejection(), false), _errorEvent,
				cause);
	}

	public NonDeliveryException(DeliveryNonDeliveryToRecipient evidence,
			REMErrorEvent _errorEvent, Throwable cause)
	{
		super(evidence, _errorEvent, cause);

	}

}
