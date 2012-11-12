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
