package exception;

/**
 * Created by Matthew on 3/22/2016.
 */
public class KingInCheckException extends IllegalMoveException
{

	public KingInCheckException(String message)
	{
		super(message);
	}

}
