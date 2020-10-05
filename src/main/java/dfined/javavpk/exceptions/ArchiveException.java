package dfined.javavpk.exceptions;

import java.io.IOException;

public class ArchiveException extends IOException
{
	/**
	 * Create a new VPK archive exception.
	 * @param message the message
	 */
	public ArchiveException(String message)
	{
		super(message);
	}
	
	public static final long serialVersionUID = 1;
}
