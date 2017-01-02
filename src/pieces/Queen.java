package pieces;

import board.GameBoard;

import java.awt.*;

/**
 * Created by Matthew on 3/22/2016.
 */
public class Queen extends ChessObject
{

	public Queen(Point position, ChessType type)
	{
		super(position, type);
	}

	public Queen(ChessType type)
	{
		this(null, type);
	}

	@Override
	public void evaluatePiece(GameBoard board)
	{
		Point p = new Point();
		for (int stepRow = -1; stepRow <= 1; stepRow++)
		{
			for (int stepCol = -1; stepCol <= 1; stepCol++)
			{
				if (!(stepCol == 0 && stepRow == 0))
				{
					p.setLocation(position.x, position.y);
					do
					{
						p.x += stepRow;
						p.y += stepCol;
					}
					while (checkMove(board, p));
				}
			}
		}
	}

	@Override
	public int getValue()
	{
		return 9;
	}

	@Override
	public ChessObject copy()
	{
		return new Queen(position, type);
	}
}
