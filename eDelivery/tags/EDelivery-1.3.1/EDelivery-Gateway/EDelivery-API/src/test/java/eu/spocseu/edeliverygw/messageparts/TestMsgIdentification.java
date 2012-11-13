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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestMsgIdentification
{

	@Test
	public void msgID()
	{
		MsgIdentification msgparts = new MsgIdentification("123");
		assertEquals(msgparts.getXSDObject().getMessageID(), "123");
	}

	@Test
	public void replyTo()
	{
		MsgIdentification msgparts = new MsgIdentification("123",
			"wo@bos-bremen.de");

		assertEquals(msgparts.getXSDObject().getMessageID(), "123");
		assertEquals(msgparts.getXSDObject().getInReplyTo().get(0),
			"wo@bos-bremen.de");

	}

	@Test
	public void replyToMany()
	{
		MsgIdentification msgparts = new MsgIdentification("123",
			"wo@bos-bremen.de", "ja@bos-bremen.de", "rl@bos-bremen.de");

		assertEquals(msgparts.getXSDObject().getMessageID(), "123");
		assertEquals(msgparts.getXSDObject().getInReplyTo().get(0),
			"wo@bos-bremen.de");
		assertEquals(msgparts.getXSDObject().getInReplyTo().get(1),
			"ja@bos-bremen.de");
		assertEquals(msgparts.getXSDObject().getInReplyTo().get(2),
			"rl@bos-bremen.de");

	}

	@Test
	public void addReference()
	{
		MsgIdentification msgparts = new MsgIdentification("123");
		msgparts.addReferences("123456789");

		assertEquals(msgparts.getXSDObject().getReferences().get(0),
			"123456789");
	}
}
