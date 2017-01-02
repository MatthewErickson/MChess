package exception;

/**
 * Created by Matthew on 3/23/2016.
 */
public class NoMoreMovesException extends IllegalMoveException
{

	public NoMoreMovesException(String message)
	{
		super(message);
	}

}
