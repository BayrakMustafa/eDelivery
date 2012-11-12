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
