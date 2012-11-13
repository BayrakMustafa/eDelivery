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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * This Class represents a Holder for Jaxb Marshaller and Unmarshaller, it
 * should be used to avoid concurrency Problems with the Jaxb Un- and
 * Marshaller.
 * 
 * @author oley
 */

public class JaxbMarshallerHolder
{

	protected JAXBContext context;
	private static JaxbMarshallerHolder addressingMarshallerHolder = null;
	private static JaxbMarshallerHolder spocsMarshallerHolder = null;
	private final List<SoftReference<Marshaller>> marshallers = new ArrayList<SoftReference<Marshaller>>();

	private final List<SoftReference<Unmarshaller>> unmarshallers = new ArrayList<SoftReference<Unmarshaller>>();

	/**
	 * Returns a Marshaller for the org.w3._2005._08.addressing context for
	 * performance reasons.
	 * 
	 * @throws JAXBException If context could not be created
	 */
	public static JaxbMarshallerHolder getAddressingMarshallerHolder()
			throws JAXBException
	{
		if (JaxbMarshallerHolder.addressingMarshallerHolder == null) {
			addressingMarshallerHolder = new JaxbMarshallerHolder(
				JaxbContextHolder.getAddressingJaxBContext());
		}
		return addressingMarshallerHolder;
	}

	/**
	 * Returns a Marshaller for the eu.eu_spocs.uri.edelivery.v1 context for
	 * performance reasons.
	 * 
	 * @throws JAXBException If context could not be created
	 */
	public static JaxbMarshallerHolder getSpocsMarshallerHolder()
			throws JAXBException
	{
		if (JaxbMarshallerHolder.spocsMarshallerHolder == null) {
			spocsMarshallerHolder = new JaxbMarshallerHolder(
				JaxbContextHolder.getSpocsJaxBContext());
		}
		return spocsMarshallerHolder;
	}

	/**
	 * Initialize a {@link JaxbMarshallerHolder} with a given
	 * {@link JAXBContext}
	 * 
	 * @param context The given {@link JAXBContext} 
	 * @throws JAXBException If context could not be created
	 */
	public JaxbMarshallerHolder(JAXBContext context) throws JAXBException
	{
		this.context = context;
	}

	/**
	 * This Method returns an Marschaller matching the JAXB context managed by
	 * this object. After using it, you should give it back by calling
	 * {@link #recycle(Marshaller)}
	 * 
	 * @throws JAXBException If context could not be created
	 * 
	 */
	public Marshaller getMarshaller() throws JAXBException
	{
		synchronized (marshallers) {
			while (!marshallers.isEmpty()) {
				SoftReference<Marshaller> ref = marshallers.remove(0);
				if (ref.get() != null) {
					return ref.get();
				}
			}
		}
		return context.createMarshaller();
	}

	/**
	 * Recycle a {@link Marshaller} object back to be re-used later.
	 * 
	 * @param marshaller
	 *            to be recycled
	 * 
	 */
	public void recycle(Marshaller marshaller)
	{
		synchronized (marshallers) {
			SoftReference<Marshaller> ref = new SoftReference<Marshaller>(
				marshaller);
			marshallers.add(ref);
		}
	}

	/**
	 * This Method returns an Unmarshaller matching the JAXB context managed by
	 * this object. After using it, you should give it back by calling
	 * {@link #recycle(Unmarschaller)}
	 * 
	 * @throws JAXBException If context could not be created
	 */
	public Unmarshaller getUnmarshaller() throws JAXBException
	{
		synchronized (unmarshallers) {
			while (!unmarshallers.isEmpty()) {
				SoftReference<Unmarshaller> ref = unmarshallers.remove(0);
				if (ref.get() != null) {
					return ref.get();
				}
			}
		}
		return context.createUnmarshaller();
	}

	/**
	 * Recycle a {@link Unmarshaller} object back to be re-used later.
	 * 
	 * @param unmarshaller
	 *            to be recycled
	 * 
	 */
	public void recycle(Unmarshaller unmarshaller)
	{
		synchronized (unmarshallers) {
			SoftReference<Unmarshaller> ref = new SoftReference<Unmarshaller>(
				unmarshaller);
			unmarshallers.add(ref);
		}
	}
}
