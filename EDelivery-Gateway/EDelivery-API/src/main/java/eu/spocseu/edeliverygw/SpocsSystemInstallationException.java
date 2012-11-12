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
 * This exception will be thrown in the case of installation error. For example
 * if the hash algorithm is not supported by the system. In the most cases the
 * JDK or the must be improved or same libraries are missing.
 * 
 * @author Lindemann
 * 
 */
public class SpocsSystemInstallationException extends Exception
{
	private static final long serialVersionUID = -3191051364594522380L;

	public SpocsSystemInstallationException(String _message, Throwable _cause)
	{
		super(_message, _cause);
	}

	public SpocsSystemInstallationException(Throwable _cause)
	{
		super(_cause);
	}

	public SpocsSystemInstallationException(String _message)
	{
		super(_message);
	}

}
