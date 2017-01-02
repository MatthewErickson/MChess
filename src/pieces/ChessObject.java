package pieces;

import board.GameBoard;
import strategy.Move;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 3/22/2016.
 */
public abstract class ChessObject
{

	public final ChessType type;
	public List<Move> moveList, attackList, allMoves, defendList, defenderList, attackerList;
	protected Point position;

	protected ChessObject(Point position, ChessType type)
	{
		this.position = position;
		this.type = type;
		moveList = new ArrayList<>();
		attackList = new ArrayList<>();
		defendList = new ArrayList<>();
		defenderList = new ArrayList<>();
		attackerList = new ArrayList<>();
		allMoves = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		return type.name() + "-" + getClass().getSimpleName();
	}

	public void setPosition(int x, int y)
	{
		setPosition(new Point(x, y));
	}

	public Point getPosition()
	{
		return position;
	}

	public void setPosition(Point position)
	{
		this.position = position;
	}

	public void clearData()
	{
		moveList.clear();
		attackList.clear();
		defendList.clear();
		defenderList.clear();
		attackerList.clear();
		allMoves.clear();
	}

	public abstract void evaluatePiece(GameBoard board);

	public abstract int getValue();

	protected boolean checkMove(GameBoard board, Point point)
	{
		Point end = new Point(point);
		if (end.equals(position))
		{
			return false;
		}
		/* bound check */
		if (end.x < 0 || end.y < 0 || end.x >= 8 || end.y >= 8)
		{
			return false;
		}

		ChessObject object = board.at(end);
		Move move = Move.make(position, end);

		if (object == null)
		{
			this.addMove(move);
		}
		else
		{
			if (this.type == object.type)
			{
				this.addDefend(move);
				object.addDefender(move);
			}
			else
			{
				this.addAttack(move);
				object.addAttacker(move);
			}
			return false;
		}
		return true;
	}

	protected void addMove(Move move)
	{
		moveList.add(move);
		allMoves.add(move);
	}

	protected void addAttack(Move move)
	{
		attackList.add(move);
		allMoves.add(move);
	}

	protected void addDefend(Move move)
	{
		defendList.add(move);
	}

	protected void addAttacker(Move move)
	{
		attackerList.add(move);
	}

	protected void addDefender(Move move)
	{
		defenderList.add(move);
	}

	public abstract ChessObject copy();

	public boolean finished()
	{
		return (this instanceof Pawn) &&
				(this.type == ChessType.BLACK && this.getPosition().x == 7 ||
						this.type == ChessType.WHITE && this.getPosition().x == 0);
	}
}
