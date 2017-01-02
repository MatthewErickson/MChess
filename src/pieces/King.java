package pieces;

import board.GameBoard;

import java.awt.*;

/**
 * Created by Matthew on 3/22/2016.
 */
public class King extends ChessObject
{

	public King(Point position, ChessType type)
	{
		super(position, type);
	}

	public King(ChessType type)
	{
		this(null, type);
	}

	@Override
	public void evaluatePiece(GameBoard board)
	{
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				if (!(i == 0 && j == 0))
				{
					checkMove(board, new Point(position.x + i, position.y + j));
				}
			}
		}
	}

	@Override
	public int getValue()
	{
		return 1;
	}

	@Override
	public ChessObject copy()
	{
		return new King(position, type);
	}

}
