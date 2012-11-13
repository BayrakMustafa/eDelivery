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

package eu.spocseu.edeliverygw.messageparts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.etsi.uri._02640.soapbinding.v1_.AttachmentType;
import org.etsi.uri._02640.soapbinding.v1_.Informational;
import org.etsi.uri._02640.soapbinding.v1_.KeywordType;
import org.etsi.uri._02640.soapbinding.v1_.NormalizedMsg;
import org.etsi.uri._02640.soapbinding.v1_.NormalizedMsg.Text;

import eu.spocseu.common.SpocsHelper;

/**
 * This class represents the underlying NormalizedMsg JAXB object. This class
 * makes it possible to create the underlying JAXB object in a simple way. In
 * the constructor all required fields of the JAXB object must be given. The
 * getXSDObject method makes it possible to get the underlying JAXB object to
 * add some detailed information.
 * 
 * @author R. Lindemann
 * 
 */

public class Normalized
{

	public static enum TEXT_FORMATS
	{
		HTML("html"), TEXT("text");
		private String value;

		private TEXT_FORMATS(String _value)
		{
			value = _value;
		}

		public String getValue()
		{
			return value;
		}
	}

	private final NormalizedMsg jaxbObj;

	/**
	 * This constructor creates the NormalizedMsg JAXB object with the required
	 * values
	 */
	public Normalized()
	{
		jaxbObj = new NormalizedMsg();
	}

	/**
	 * Set the most common additional information related to the content.
	 * 
	 * @param subject
	 *            The subject of the content.
	 * @param comments
	 *            Some comments related to the content
	 * @param keyword
	 *            Some keywords for search reasons.
	 * @return The created Informational JAXB object for further configuration.
	 */
	public Informational setInformational(String subject, String comments)
	{
		Informational obj = new Informational();
		obj.setComments(comments);
		obj.setSubject(subject);
		jaxbObj.setInformational(obj);
		return obj;
	}

	public Informational getInformational()
	{
		return jaxbObj.getInformational();
	}

	/**
	 * Adds a new keyword to the normalized structure.
	 * 
	 * @param value
	 *            The value of the keyword
	 * @param schema
	 *            The schema to find the separator if present
	 * @param meaning
	 *            The meaning of the keyword
	 */
	public void addKeywords(String value, String schema, String meaning)
	{
		KeywordType keyword = new KeywordType();
		keyword.setValue(value);
		keyword.setScheme(schema);
		keyword.setMeaning(meaning);
		jaxbObj.getInformational().getKeywords().add(keyword);
	}

	/**
	 * Sets the content of the Normalized JAXB object and adds it to the JAXB
	 * object.
	 * 
	 * @param text
	 *            The content as String
	 * @param format
	 *            the format of the content
	 * @return The created JAXB object for further configuration.
	 */
	public Text setText(String text, TEXT_FORMATS format)
	{
		Text textType = new Text();
		textType.setFormat(format.getValue());
		textType.setValue(text);
		jaxbObj.getText().add(textType);
		return textType;
	}

	/**
	 * Adds a new attachment to the jaxb structure
	 * 
	 * @param _in
	 *            The input stream of the attachment.
	 * @param _filename
	 *            The filename of the original stream .
	 * @param _contentType
	 *            The type related to the input stream
	 * @param _size
	 *            the size in bytes of the input stream.
	 * @return The created jaxb object.
	 */
	public AttachmentType addAttachment(InputStream _in, String _filename,
			String _contentType, BigInteger _size) throws IOException
	{
		AttachmentType attObj = new AttachmentType();
		attObj.setFilename(_filename);
		attObj.setContentType(_contentType);
		attObj.setEmbedded(SpocsHelper.readStream(_in).toByteArray());
		if(_size.intValue() >= 0){
			attObj.setSize(_size);			
		}else{
			throw new IOException("Size could not be negateive!");
		}
		jaxbObj.getAttachment().add(attObj);
		return attObj;
	}
	

	/**
	 * Adds a new attachment to the jaxb structure
	 * 
	 * @param _in
	 *            The input stream of the attachment.
	 * @param _filename
	 *            The filename of the original stream .
	 * @param _contentType
	 *            The type related to the input stream
	 * @return The created jaxb object.
	 */
	public AttachmentType addAttachment(InputStream _in, String _filename,
			String _contentType) throws IOException
	{
		AttachmentType attObj = new AttachmentType();
		attObj.setFilename(_filename);
		attObj.setContentType(_contentType);
		byte[] byteArray = SpocsHelper.readStream(_in).toByteArray();
		attObj.setEmbedded(byteArray);
		attObj.setSize(BigInteger.valueOf(byteArray.length));
		jaxbObj.getAttachment().add(attObj);
		return attObj;
	}

	/**
	 * Adds a new attachment to the jaxb structure
	 * 
	 * @param _file
	 *            The file of the attachment.
	 * @param _contentType
	 *            The type related to the input stream
	 * @param _size
	 *            the size in bytes of the input stream.
	 * @return The created jaxb object.
	 */
	public AttachmentType addAttachment(File _file, String _contentType)
			throws FileNotFoundException, IOException
	{

		FileInputStream in = new FileInputStream(_file);
		AttachmentType attObj = new AttachmentType();
		try {
			attObj.setSize(BigInteger.valueOf(_file.length()));
			attObj.setFilename(_file.getName());
			attObj.setContentType(_contentType);
			attObj.setEmbedded(SpocsHelper.readStream(in).toByteArray());
			jaxbObj.getAttachment().add(attObj);
		} finally {
			in.close();
		}
		return attObj;
	}

	/**
	 * Gets the underlying JAXB object, for example to add some detailed
	 * information with this method.
	 * 
	 * @return The JAXB object.
	 */
	public NormalizedMsg getXSDObject()
	{
		return jaxbObj;
	}

}
