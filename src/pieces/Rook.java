package pieces;

import board.GameBoard;

import java.awt.*;

/**
 * Created by Matthew on 3/22/2016.
 */
public class Rook extends ChessObject
{

	public Rook(Point position, ChessType type)
	{
		super(position, type);
	}

	public Rook(ChessType type)
	{
		this(null, type);
	}

	@Override
	public void evaluatePiece(GameBoard board)
	{
		Point p = new Point();

        /* go up */
		p.setLocation(position.x, position.y);
		while (--p.x >= 0)
		{
			if (!checkMove(board, p))
			{
				break;
			}
		}
		/* go down */
		p.setLocation(position.x, position.y);
		while (++p.x < 8)
		{
			if (!checkMove(board, p))
			{
				break;
			}
		}

        /* go left */
		p.setLocation(position.x, position.y);
		while (--p.y >= 0)
		{
			if (!checkMove(board, p))
			{
				break;
			}
		}
		/* go right */
		p.setLocation(position.x, position.y);
		while (++p.y < 8)
		{
			if (!checkMove(board, p))
			{
				break;
			}
		}
	}

	@Override
	public int getValue()
	{
		return 5;
	}

	@Override
	public ChessObject copy()
	{
		return new Rook(position, type);
	}

}
