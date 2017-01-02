package pieces;

import board.GameBoard;

import java.awt.*;

/**
 * Created by Matthew on 3/22/2016.
 */
public class Bishop extends ChessObject
{

	public Bishop(Point position, ChessType type)
	{
		super(position, type);
	}

	public Bishop(ChessType type)
	{
		this(null, type);
	}

	@Override
	public void evaluatePiece(GameBoard board)
	{
		Point p = new Point();

        /* go up-right */
		p.setLocation(position.x, position.y);
		while (++p.x < 8 && --p.y >= 0)
		{
			if (!checkMove(board, p))
			{
				break;
			}
		}

        /* go up-left */
		p.setLocation(position.x, position.y);
		while (--p.x >= 0 && --p.y >= 0)
		{
			if (!checkMove(board, p))
			{
				break;
			}
		}

        /* go down-right */
		p.setLocation(position.x, position.y);
		while (++p.x < 8 && ++p.y < 8)
		{
			if (!checkMove(board, p))
			{
				break;
			}
		}

        /* go down-left */
		p.setLocation(position.x, position.y);
		while (--p.x >= 0 && ++p.y < 8)
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
		return 3;
	}

	@Override
	public ChessObject copy()
	{
		return new Bishop(position, type);
	}

}
