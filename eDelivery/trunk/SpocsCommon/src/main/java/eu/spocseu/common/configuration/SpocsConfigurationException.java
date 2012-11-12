package eu.spocseu.common.configuration;

public class SpocsConfigurationException extends Exception
{
	private static final long serialVersionUID = -3191051364594522380L;

	public SpocsConfigurationException(String _message, Throwable _cause)
	{
		super(_message, _cause);
	}

	public SpocsConfigurationException(Throwable _cause)
	{
		super(_cause);
	}

	public SpocsConfigurationException(String _message)
	{
		super(_message);
	}
}
