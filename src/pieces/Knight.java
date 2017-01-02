package pieces;

import board.GameBoard;

import java.awt.*;

/**
 * Created by Matthew on 3/22/2016.
 */
public class Knight extends ChessObject
{

	public Knight(Point position, ChessType type)
	{
		super(position, type);
	}

	public Knight(ChessType type)
	{
		this(null, type);
	}

	@Override
	public void evaluatePiece(GameBoard board)
	{
		checkMove(board, new Point(position.x + 2, position.y + 1));
		checkMove(board, new Point(position.x + 2, position.y - 1));
		checkMove(board, new Point(position.x + 1, position.y + 2));
		checkMove(board, new Point(position.x + 1, position.y - 2));
		checkMove(board, new Point(position.x - 1, position.y + 2));
		checkMove(board, new Point(position.x - 1, position.y - 2));
		checkMove(board, new Point(position.x - 2, position.y + 1));
		checkMove(board, new Point(position.x - 2, position.y - 1));
	}

	@Override
	public int getValue()
	{
		return 3;
	}

	@Override
	public ChessObject copy()
	{
		return new Knight(position, type);
	}
}
