package pieces;

import board.GameBoard;

import java.awt.*;

/**
 * Created by Matthew on 3/22/2016.
 */
public class Pawn extends ChessObject
{

	public Pawn(Point position, ChessType type)
	{
		super(position, type);
	}

	public Pawn(ChessType type)
	{
		this(null, type);
	}

	@Override
	public void evaluatePiece(GameBoard board)
	{
		int step = (type == ChessType.BLACK ? 1 : -1);
		int start = (type == ChessType.BLACK ? 1 : 6);

		if (board.at(position.x + step, position.y) == null)
		{
			checkMove(board, new Point(position.x + step, position.y));
			if (position.x == start && board.at(position.x + 2 * step, position.y) == null)
			{
				checkMove(board, new Point(position.x + 2 * step, position.y));
			}
		}

		if (position.y > 0)
		{
			Point leftPoint = new Point(position.x + step, position.y - 1);
			ChessObject leftObj = board.at(leftPoint);
			if (leftObj != null && leftObj.type != this.type)
			{
				checkMove(board, leftPoint);
			}
		}

		if (position.y < 7)
		{
			Point rightPoint = new Point(position.x + step, position.y + 1);
			ChessObject rightObj = board.at(rightPoint);
			if (rightObj != null && rightObj.type != this.type)
			{
				checkMove(board, rightPoint);
			}
		}
	}

	@Override
	public int getValue()
	{
		int row = getPosition().x;
		int end = (type == ChessType.BLACK ? 7 : 0);
		int diff = row - end;
		if (diff <= 2)
		{
			return 5;
		}
		return 1;
	}

	@Override
	public ChessObject copy()
	{
		return new Pawn(position, type);
	}

}
