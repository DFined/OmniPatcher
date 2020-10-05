package dfined.javavpk.exceptions;

import java.io.IOException;

public class EntryException extends IOException
{
	/**
	 * Create a new VPK archive entry exception.
	 * @param message the message
	 */
	public EntryException(String message)
	{
		super(message);
	}
	
	public static final long serialVersionUID = 1;
}
