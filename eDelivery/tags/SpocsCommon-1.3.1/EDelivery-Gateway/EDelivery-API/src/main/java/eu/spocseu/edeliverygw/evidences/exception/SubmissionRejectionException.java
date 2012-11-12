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
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;

public class SubmissionRejectionException extends EvidenceException
{

	private static final long serialVersionUID = 1L;

	public SubmissionRejectionException(SubmissionAcceptanceRejection sub,
			REMErrorEvent _errorEvent, Throwable cause)
	{
		super(sub, _errorEvent, cause);

	}

	public SubmissionRejectionException(String message,
			SubmissionAcceptanceRejection sub)
	{
		super(message, sub, null);

	}

	public SubmissionRejectionException(String message,
			SubmissionAcceptanceRejection sub, REMErrorEvent _errorEvent,
			Throwable cause)
	{
		super(message, sub, _errorEvent, cause);

	}

}
