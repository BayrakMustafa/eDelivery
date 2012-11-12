package eu.spocseu.edeliverygw.messages;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.spocseu.common.SpocsConstants.Evidences;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.messages.GeneralMessage.SubmissionAcceptanceRejectionError;

public class TestSubmissionARError
{
	@BeforeClass
	public static void configure() throws FileNotFoundException
	{
		Configuration.setInputDataXML(new FileInputStream(new File(
			"src/test/resources/spocsConfig.xml")));
	}
	
	@Test	
	public void testSubmissionAcceptranceRehjectionError() throws SpocsConfigurationException, IOException, GeneralSecurityException, DatatypeConfigurationException, JAXBException {
		GeneralMessage message = new GeneralMessage() {
			
			@Override
			public void serialize(OutputStream out)
			{				
			}
		};
		SubmissionAcceptanceRejection subError = message.new SubmissionAcceptanceRejectionError(Configuration.getConfiguration(), new EvidenceMessage());
		if(!subError.getXSDObject().getEventCode().equals((Evidences.SUBMISSION_ACCEPTANCE_REJECTION
					.getFaultEventCode()))){
			fail();
		}
	}
}
