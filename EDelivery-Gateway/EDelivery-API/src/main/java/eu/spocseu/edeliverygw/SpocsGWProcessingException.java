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
package eu.spocseu.edeliverygw;

/**
 * This class represents a Exception of the Gateway Processing. It will be used
 * to return a SOAP Fault to the Client in an Error case.
 * 
 * @author oley
 */

public class SpocsGWProcessingException extends Exception
{
	private static final long serialVersionUID = 1L;
	REMErrorEvent fault;

	public SpocsGWProcessingException(REMErrorEvent _fault)
	{
		fault = _fault;
	}

	public SpocsGWProcessingException(REMErrorEvent _fault, String _details)
	{
		super(_fault.getEventDetails() + " details: " + _details);
		fault = _fault;
	}

	public SpocsGWProcessingException(REMErrorEvent _fault, String _details,
			Throwable _cause)
	{
		super(_fault.getEventDetails() + " details: " + _details, _cause);
		fault = _fault;
	}

	public REMErrorEvent getFault()
	{
		return fault;
	}
}
