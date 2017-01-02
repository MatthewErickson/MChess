package board;

import exception.IllegalMoveException;
import exception.KingInCheckException;
import pieces.*;
import strategy.Move;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 3/22/2016.
 */
public class GameBoard
{

	public static final Integer BOARD_SIZE = 8;
	public List<ChessObject> whitePieces, blackPieces;
	public int benefit = 0;
	private int whiteValue = 0, blackValue = 0;
	private ChessObject whiteKing, blackKing;
	private ChessObject[][] tiles;

	public GameBoard()
	{
		tiles = new ChessObject[BOARD_SIZE][BOARD_SIZE];
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
	}

	public GameBoard copy() throws IllegalMoveException
	{
		GameBoard board = new GameBoard();
		for (int i = 0; i < BOARD_SIZE; i++)
		{
			for (int j = 0; j < BOARD_SIZE; j++)
			{
				ChessObject piece = this.at(i, j);
				if (piece != null)
				{
					board.set(piece.copy(), i, j);
				}
			}
		}
		return board;
	}

	public ChessObject at(int row, int col)
	{
		return tiles[row][col];
	}

	public ChessObject at(Point p)
	{
		return at(p.x, p.y);
	}

	public void set(ChessObject piece, int row, int col)
	{
		if (piece != null)
		{
			Point prev = piece.getPosition();
			if (prev != null)
			{
				tiles[prev.x][prev.y] = null;
			}
			piece.setPosition(row, col);
		}
		tiles[row][col] = piece;
	}

	public void set(ChessObject piece, Point p)
	{
		set(piece, p.x, p.y);
	}

	public void evaluateBoard()
	{
		whitePieces.clear();
		blackPieces.clear();

		/* clear data for all pieces */
		for (int row = 0; row < BOARD_SIZE; row++)
		{
			for (int col = 0; col < BOARD_SIZE; col++)
			{
				ChessObject piece = tiles[row][col];
				if (piece != null)
				{
					piece.clearData();
				}
			}
		}

        /* evaluate each piece */
		for (int row = 0; row < BOARD_SIZE; row++)
		{
			for (int col = 0; col < BOARD_SIZE; col++)
			{
				ChessObject piece = tiles[row][col];
				if (piece != null)
				{
					if (piece instanceof Pawn && piece.finished())
					{
						tiles[row][col] = new Queen(piece.getPosition(), piece.type);
						evaluateBoard();
						return;
					}
					else if (piece instanceof King)
					{
						if (piece.type == ChessType.WHITE)
						{
							whiteKing = piece;
						}
						else
						{
							blackKing = piece;
						}
					}
					piece.evaluatePiece(this);
					if (piece.type == ChessType.WHITE)
					{
						whitePieces.add(piece);
						whiteValue += piece.getValue();
					}
					else
					{
						blackPieces.add(piece);
						blackValue += piece.getValue();
					}
				}
			}
		}
	}

	public void calculateBenefitEasy(ChessType type) throws KingInCheckException
	{
		ChessObject king = (type == ChessType.WHITE ? whiteKing : blackKing);
		if (king.attackerList.size() > 0)
		{
			throw new KingInCheckException("Check");
		}
		benefit = (type == ChessType.WHITE ? whiteValue - blackValue : blackValue - whiteValue);
	}

	public void calculateBenefitNormal(ChessType type) throws KingInCheckException
	{
		calculateBenefitEasy(type);
	}

	public void calculateBenefitHard(ChessType type) throws KingInCheckException
	{
		calculateBenefitEasy(type);

		List<ChessObject> pieces = (type == ChessType.BLACK ? blackPieces : whitePieces);
		List<ChessObject> otherPieces = (type == ChessType.BLACK ? whitePieces : blackPieces);

		int maxVictimValue = 0;
		for (ChessObject obj : pieces)
		{
			for (Move move : obj.attackList)
			{
				ChessObject victim = this.at(move.end);
				if (victim.defenderList.isEmpty())
				{
					maxVictimValue = Math.max(maxVictimValue, victim.getValue());
				}
				else
				{
					maxVictimValue = Math.max(maxVictimValue, victim.getValue() - obj.getValue());
				}
			}
		}
		benefit += maxVictimValue;
	}

}
