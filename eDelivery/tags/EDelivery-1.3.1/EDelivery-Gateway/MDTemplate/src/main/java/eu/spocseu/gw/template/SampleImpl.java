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

$URL: $HeadURL$ $
$Date: $Date$ $
$Revision: $Revision$ $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */

package eu.spocseu.gw.template;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.etsi.uri._02640.soapbinding.v1_.MsgMetaData;
import org.etsi.uri._02640.soapbinding.v1_.ObjectFactory;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.LogHelper;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.SpocsGWProcessingException;
import eu.spocseu.edeliverygw.SpocsSystemInstallationException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.evidences.exception.NonDeliveryException;
import eu.spocseu.edeliverygw.evidences.exception.RelayREMMDRejectionException;
import eu.spocseu.edeliverygw.messages.AcceptanceRejectionByRecipientMessage;
import eu.spocseu.edeliverygw.messages.DeliveryNonDeliveryToRecipientMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessage;
import eu.spocseu.edeliverygw.messages.DispatchMessageResponse;
import eu.spocseu.edeliverygw.messages.GeneralMessage;
import eu.spocseu.edeliverygw.messages.RetrievalNonRetrievalByRecipientMessage;
import eu.spocseu.edeliverygw.process.ProcessMessage;
import eu.spocseu.edeliverygw.process.SpocsGWInterface;
import eu.spocseu.tsl.TrustedServiceImpl;

/**
 * The <code>SampleImpl</code> class represents a sample implementation of the
 * Server side for a MD Implementation. <br />
 * <br />
 * It will handle the incoming <code>DispatchMessage</code> and shows how to use
 * Exception in this context. To show some logic which could happen here, we
 * first compare the <code>Destinations</code> Mail address with just one User
 * we will accept ('wo@templatetest.com'). <br />
 * <br />
 * If the sender is not the accepted, we will throw a
 * <code> RelayREMMDRejectionException </code> to reject the dispatch. <br>
 * In the other case - the <code>Destinations</code> matches our accepted - we
 * will try to store the message in the file System. <br />
 * At this point we will throw a <code>NonDeliveryException</code> if something
 * IO-related happens.
 * 
 * @author woley
 * 
 * @version SPOCS_WP3_CURRENT_RELEASE_LABEL
 * @since SPOCS_WP3_INITIAL_RELEASE_LABEL
 * @see <a href="SPOCS_WP3_SPECIFICATION_URL">SPOCS_WP3_SPECIFICATION_LABEL</a>
 * @see eu.spocseu.edeliverygw.process.SpocsGWInterface
 */

public class SampleImpl implements SpocsGWInterface
{

	// let's get the logger and the line.seperator for logging
	private static final Logger LOG = LoggerFactory
			.getLogger(ProcessMessage.class.getName());
	private final String lineSeperator;

	public SampleImpl()
	{
		lineSeperator = System.getProperty("line.separator");
	}

	/**
	 * In this method the DispatchMessage processing will be handled.
	 * 
	 * @param message
	 *            The DispatchMessage which will be processed
	 * 
	 * @throws SpocsConfigurationException
	 * 
	 * @throws NonDeliveryException
	 *             If you throw this Exception, a DeliveryNonDeliveryEvicence
	 *             will be send back to the Client.
	 * @throws RelayREMMDRejectionException
	 *             If you throw this Exception, a RelayREMMDREjectionException
	 *             will be send back to the Client.
	 * 
	 * @since SPOCS_WP3_INITIAL_LABEL
	 */

	@Override
	public DispatchMessageResponse processDispatch(DispatchMessage message,
			TrustedServiceImpl tsl) throws NonDeliveryException,
			RelayREMMDRejectionException
	{
		Configuration config;
		// getting the Configuration; if an Exception is thrown, we will catch
		// it
		// and send an 'Evidence Exception'
		try {
			config = Configuration.getConfiguration();
		} catch (Exception ex) {
			LOG.warn("Error getting the Configuration", ex);
			throw new RelayREMMDRejectionException(null, message,
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, ex);
		}
		// we will try to log some of the MetaData which should be in the
		// message
		MsgMetaData messageMetaData = message.getXSDObject().getMsgMetaData();

		String originatorsAdress = GeneralMessage.getAttributedElectronicAdress(messageMetaData.getOriginators().getFrom()
				).getValue();
		String destinationsAdress =GeneralMessage.getAttributedElectronicAdress(messageMetaData.getDestinations()
				.getRecipient()).getValue();
		String MsgID = messageMetaData.getMsgIdentification().getMessageID();

		LOG.info(lineSeperator + lineSeperator + "From: " + originatorsAdress
				+ lineSeperator + "TO: " + destinationsAdress + lineSeperator);

		logIncomingMsg(message.getXSDObject());

		// Example for message processing, just storing it in the File System if
		// the User is wo@localhost-spocs-edelivery.eu. If not, we will throw a
		// RelayReMMDREjectionException to return an Evidence to the sender.
		if (destinationsAdress.equals("wo@localhost-spocs-edelivery.eu")) {
			LOG.info("found User processing message...");
			storeMessage(message, config);
		} else {
			LOG.info("User not found throwing RelayREMMDRejectionException now!");
			throw new RelayREMMDRejectionException(config, message,
				REMErrorEvent.UNKNOWN_RECIPIENT_ADDRESS, new Throwable(
					"User not found!"));
		}

		return null;
	}

	/**
	 * 
	 * This Method stores a <code>DispatchMessage</code> in the File System, the
	 * Name will be example-message.txt
	 * 
	 * @param message
	 *            The DispatchMessage which will be stored
	 * @param config
	 *            The Configuration of the actual Transaction
	 * 
	 * @throws NonDeliveryException
	 *             This Exception will be thrown to indicate whether some
	 *             Problems occur while storing the File.
	 * 
	 * @since SPOCS_WP3_INITIAL_LABEL
	 * @see "Any description text"
	 * @see <a href="URL#value">label</a>
	 * @see package.class#member optional_label
	 * 
	 */
	private void storeMessage(DispatchMessage message, Configuration config)
			throws NonDeliveryException
	{

		try {
			// This file will be stored in JBOSS_HOME/bin
			FileWriter fw = new FileWriter("example-message.txt");
			REMDispatchType dispatch = message.getXSDObject();
			// LogHelper is a helper class which uses JAXB to marshal the
			// REMDDispatchype
			// and pretty prints it. Normally you should use
			// logPrettyIncomingMessage()!
			fw.append(LogHelper.prettyPrint(new ObjectFactory()
					.createREMDispatch(dispatch)));
			fw.close();
		} catch (Exception e) {
			throw new NonDeliveryException(config, message,
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, e);
		}
	}

	private static void logIncomingMsg(REMDispatchType dispatch)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			JaxbContextHolder
					.getSpocsJaxBContext()
					.createMarshaller()
					.marshal(new ObjectFactory().createREMDispatch(dispatch),
						out);
		} catch (JAXBException ex) {
			LOG.error("Error: ", ex);
		}
		LOG.debug("Incoming message: " + out);
	}

	@Override
	public void initOnStartup()
	{
		LOG.info("Sample Impl called!");
	}

	@Override
	public void processEvidence(AcceptanceRejectionByRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException
	{
		try {
			FileOutputStream out = new FileOutputStream(
				"AcceptanceRejectionByRecipientMessage.xml");
			evidence.serialize(out);
		} catch (SpocsConfigurationException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		} catch (SpocsSystemInstallationException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		} catch (IOException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		}

	}

	@Override
	public void processEvidence(DeliveryNonDeliveryToRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException
	{
		try {
			FileOutputStream out = new FileOutputStream(
				"DeliveryNonDeliveryToRecipientMessage.xml");
			evidence.serialize(out);
			out.close();
		} catch (SpocsConfigurationException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		} catch (SpocsSystemInstallationException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		} catch (IOException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		}
	}

	@Override
	public void processEvidence(
			RetrievalNonRetrievalByRecipientMessage evidence,
			TrustedServiceImpl tsl) throws SpocsGWProcessingException
	{
		try {
			FileOutputStream out = new FileOutputStream(
				"RetrievalNonRetrievalByRecipientMessage.xml");
			evidence.serialize(out);
			out.close();
		} catch (SpocsConfigurationException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		} catch (SpocsSystemInstallationException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		} catch (IOException e) {
			throw new SpocsGWProcessingException(
				REMErrorEvent.UNSPECIFIC_PROCESSING_ERROR, "IOError", e);
		}
	}
}
