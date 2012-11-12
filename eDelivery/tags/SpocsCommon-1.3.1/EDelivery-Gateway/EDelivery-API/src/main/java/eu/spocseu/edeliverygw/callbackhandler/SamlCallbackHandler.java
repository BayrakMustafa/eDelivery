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

package eu.spocseu.edeliverygw.callbackhandler;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.sun.xml.wss.impl.callback.SAMLCallback;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.Attribute;
import com.sun.xml.wss.saml.AttributeStatement;
import com.sun.xml.wss.saml.AuthnContext;
import com.sun.xml.wss.saml.AuthnStatement;
import com.sun.xml.wss.saml.Conditions;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.SAMLAssertionFactory;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.SubjectConfirmation;

import eu.spocseu.common.SpocsConstants;
import eu.spocseu.common.configuration.SpocsConfigurationException;
import eu.spocseu.edeliverygw.XMLSignatureHelper;
import eu.spocseu.edeliverygw.configuration.Configuration;
import eu.spocseu.edeliverygw.configuration.SAMLProperties;

/**
 * internal Class
 * 
 * @author R. Lindemann
 */
public class SamlCallbackHandler implements CallbackHandler
{
	/** Logger to log the messages. */
	public static Logger LOG = LoggerFactory
			.getLogger(XMLSignatureHelper.class);

	public static final String SAML_QA_LEVEL = "saml.citizenQAAlevel";
	public static String SAML_SUBEJCT_SURENAME = "saml.subject.surname";
	public static String SAML_SUBEJCT_GIVENNAME = "givenName";
	public static String SAML_SUBEJCT_EIDENTIFIER = "eIdentifier";
	public static String SAML_SUBEJCT_DATE_OF_BIRTH = "dateOfBirth";

	private int count = 0;
	private UnsupportedCallbackException unsupported = new UnsupportedCallbackException(
		null, "Unsupported Callback Type Encountered");

	public SamlCallbackHandler()
	{
	}

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException
	{
		Subject sub = Subject.getSubject(AccessController.getContext());
		if (sub == null) {
			throw new RuntimeException("No context found.");
		}
		SAMLProperties senderConfig = sub
				.getPublicCredentials(SAMLProperties.class).iterator().next();
		LOG.info("Name of sender: " + senderConfig.getSurname());
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof SAMLCallback) {
				try {
					SAMLCallback samlCallback = (SAMLCallback) callbacks[i];
					if (samlCallback.getConfirmationMethod().equals(
						SAMLCallback.SV_ASSERTION_TYPE)
							&& count == 0) {
						samlCallback
								.setAssertionElement(createSenderVouchesSAMLAssertion(
									Configuration.getConfiguration(),
									senderConfig));
					}
				} catch (SpocsConfigurationException ex) {
					ex.printStackTrace();
				}
			} else {
				throw unsupported;
			}
		}
	}

	private Element createSenderVouchesSAMLAssertion(Configuration config,
			SAMLProperties senderConfig)
	{
		Assertion assertion = null;
		try {
			String issuerName = config.geteDeliveryDetails()
					.getGatewayAddress();
			// create the assertion id
			String aID = "ID_" + String.valueOf(System.currentTimeMillis());
			GregorianCalendar c = new GregorianCalendar();
			long beforeTime = c.getTimeInMillis();
			// roll the time by one hour
			long offsetHours = 60 * 60 * 1000;
			c.setTimeInMillis(beforeTime - offsetHours);
			GregorianCalendar before = (GregorianCalendar) c.clone();
			c = new GregorianCalendar();
			long afterTime = c.getTimeInMillis();
			c.setTimeInMillis(afterTime + offsetHours);
			GregorianCalendar after = (GregorianCalendar) c.clone();
			GregorianCalendar issueInstant = new GregorianCalendar();
			// statements
			List<Object> statements = new LinkedList<Object>();
			SAMLAssertionFactory factory = SAMLAssertionFactory
					.newInstance(SAMLAssertionFactory.SAML2_0);
			NameID issuerNameID = factory.createNameID(issuerName, null,
				"urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
			SubjectConfirmation scf = factory.createSubjectConfirmation(
				issuerNameID, "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches");

			NameID senderNameID = factory.createNameID("SenderIDname",
				issuerName,
				"urn:oasis:names:tc:SAML:2.0:nameid-format:unspecified");
			com.sun.xml.wss.saml.Subject subj = factory.createSubject(
				senderNameID, scf);
			AuthnContext ctx = factory.createAuthnContext(
				"urn:oasis:names:tc:SAML:2.0:ac:classes:X509", null);
			final AuthnStatement statement = factory.createAuthnStatement(
				issueInstant, null, ctx, null, null);
			statements.add(statement);
			statements.add(createAttributeStatementList(factory, senderConfig));

			Conditions conditions = factory.createConditions(before, after,
				null, null, null, null);
			assertion = factory.createAssertion(aID, issuerNameID,
				issueInstant, conditions, null, subj, statements);
			// SAML Token will not be signed!
			// sign(config, assertion);
			return assertion.toElement(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Element createSAMLSenderVouchesToken(Configuration config,
			SAMLProperties samlProperties)
	{
		return createSenderVouchesSAMLAssertion(config, samlProperties);

	}

	@SuppressWarnings("unused")
	private void sign(Configuration config, Assertion assertion)
			throws SAMLException
	{

		XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
		try {

			PrivateKey privKey = config.getSignatureKey();
			X509Certificate cert = config.getSignatureCertificate();
			Transform transformEnv = fac.newTransform(Transform.ENVELOPED,
				(TransformParameterSpec) null);
			XMLSignatureHelper.createSignature(
				Arrays.asList("#" + assertion.getID()), privKey, cert,
				assertion.toElement(null), transformEnv);
			// Go on without escalation so far
		} catch (Exception ex) {
			throw new SAMLException(ex);
		}
	}

	private AttributeStatement createAttributeStatementList(
			SAMLAssertionFactory samlAssertFactory, SAMLProperties senderConfig)
			throws SAMLException
	{
		List<Attribute> attrs = new ArrayList<Attribute>();
		// creates the AttributeStatement QAAlevel
		attrs.add(createAttributeStatement(samlAssertFactory,
			"http://www.stork.gov.eu/1.0/citizenQAAlevel",
			"" + senderConfig.getCitizenQAAlevel()));
		// creates the AttributeStatement Surname
		if (senderConfig.getSurname() != null)
			attrs.add(createAttributeStatement(samlAssertFactory,
				"http://www.stork.gov.eu/1.0/surname",
				senderConfig.getSurname()));
		// creates the AttributeStatement givenname
		if (senderConfig.getGivenName() != null)
			attrs.add(createAttributeStatement(samlAssertFactory,
				"http://www.stork.gov.eu/1.0/givenName",
				senderConfig.getGivenName()));
		// creates the AttributeStatement eIDentifier
		if (senderConfig.geteIdentifier() != null)
			attrs.add(createAttributeStatement(samlAssertFactory,
				"http://www.stork.gov.eu/1.0/eIdentifier",
				senderConfig.geteIdentifier()));
		// creates the AttributeStatement date of birth
		if (senderConfig.getDateOfBirth() != null)
			attrs.add(createAttributeStatement(samlAssertFactory,
				"http://www.stork.gov.eu/1.0/dateOfBirth",
				getDateAsString(senderConfig.getDateOfBirth())));

		if (senderConfig.getRole() != null) {
			List<String> values = new ArrayList<String>();
			values.add(senderConfig.getRole().toString());
			attrs.add(samlAssertFactory.createAttribute("ActorRole",
				"urn:oasis:names:tc:SAML:2.0:attrname-format:basic", values));
		}else{
			// Default Value for Actor Role is Indeterminate!
			List<String> values = new ArrayList<String>();
			values.add(SpocsConstants.ActorRole.INDETERMINATE.toString());
			attrs.add(samlAssertFactory.createAttribute("ActorRole",
				"urn:oasis:names:tc:SAML:2.0:attrname-format:basic", values));
		}

		AttributeStatement statement = samlAssertFactory
				.createAttributeStatement(attrs);
		return statement;

	}

	private String getDateAsString(Date date)
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		String ret = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH)
				+ "-" + cal.get(Calendar.DAY_OF_MONTH);
		return ret;

	}

	private Attribute createAttributeStatement(
			SAMLAssertionFactory samlAssertFactory, String _key,
			String... _values) throws SAMLException
	{
		List<String> values = new ArrayList<String>();
		for (String value : _values) {
			values.add(value);
		}
		Attribute attr = samlAssertFactory.createAttribute(_key,
			"urn:oasis:names:tc:SAML:2.0:attrname-format:uri", values);
		return attr;
	}

}
