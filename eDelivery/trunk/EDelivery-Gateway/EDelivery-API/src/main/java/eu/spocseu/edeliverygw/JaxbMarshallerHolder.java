/* ---------------------------------------------------------------------------
             COMPETITIVENESS AND INNOVATION FRAMEWORK PROGRAMME
                   ICT Policy Support Programme (ICT PSP)
           Preparing the implementation of the Services Directive
                   ICT PSP call identifier: ICT PSP-2008-2
             ICT PSP main Theme identifier: CIP-ICT-PSP.2008.1.1
                           Project acronym: SPOCS
   Project full title: Simple Procedures Online for Cross-border Services
                         Grant agreement no.: 238935
                               www.eu-spocs.eu
------------------------------------------------------------------------------
    WP3 Interoperable delivery, eSafe, secure and interoperable exchanges
                       and acknowledgement of receipt
------------------------------------------------------------------------------
        Open module implementing the eSafe document exchange protocol
------------------------------------------------------------------------------

$URL: svn:https://svnext.bos-bremen.de/SPOCS/AllWpImplementation/EDelivery-Gateway
$Date: 2010-10-14 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */
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
