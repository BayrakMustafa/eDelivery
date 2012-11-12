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


import org.etsi.uri._02640.v2_.EventReasonType;
import org.etsi.uri._02640.v2_.EventReasonsType;

import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.evidences.Evidence;

/**
 * The EvidenceException will be thrown in the case of evidences with fault
 * codes.
 * 
 * @author Lindemann
 * 
 */
public class EvidenceException extends Exception
{

	private static final long serialVersionUID = 1L;
	private Evidence evidence;
	private REMErrorEvent errorEvent;

	protected EvidenceException(String message, Evidence _evidence,
			REMErrorEvent _errorEvent, Throwable _cause)
	{
		super(message, _cause);
		evidence = _evidence;
		errorEvent = _errorEvent;
	}

	protected EvidenceException(Evidence _evidence, REMErrorEvent _errorEvent,
			Throwable _cause)
	{
		super(_cause);
		evidence = _evidence;
		if (evidence.getXSDObject().getEventReasons() == null) {
			errorEvent = _errorEvent;
			EventReasonsType reasons = new EventReasonsType();
			EventReasonType reason = new EventReasonType();
			reason.setCode(errorEvent.getEventCode());
			if (_cause != null) {
				reason.setDetails(_cause.getMessage());
			} else {
				reason.setDetails(errorEvent.getEventDetails());
			}
			reasons.getEventReason().add(reason);
			evidence.getXSDObject().setEventReasons(reasons);
		}

	}

	protected EvidenceException(String message, Evidence _evidence,
			REMErrorEvent _errorEvent)
	{
		super(message);
		evidence = _evidence;
		errorEvent = _errorEvent;
	}

	public Evidence getEvidence()
	{
		return evidence;
	}

	public void setEvidence(Evidence _evidence)
	{
		this.evidence = _evidence;
	}

	public REMErrorEvent getErrorEvent()
	{
		return errorEvent;
	}
}
