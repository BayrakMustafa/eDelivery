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
package eu.spocseu.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SpocsHelper
{

	public static void readStream(InputStream in, OutputStream out)
			throws IOException
	{
		int i = 0;
		byte[] bytes = new byte[1024];
		while ((i = in.read(bytes)) > -1) {
			out.write(bytes, 0, i);
		}
	}

	public static ByteArrayOutputStream readStream(InputStream in)
			throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		readStream(in, out);
		return out;
	}

}
