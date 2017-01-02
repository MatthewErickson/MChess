package board;

import exception.IllegalMoveException;
import pieces.*;
import strategy.Move;

import java.util.List;

/**
 * Created by Matthew on 3/22/2016.
 */
public class BoardManager
{

	public enum Difficulty
	{
		EASY, NORMAL, HARD
	}

	public static Difficulty whiteDiff = Difficulty.HARD;
	public static Difficulty blackDiff = Difficulty.HARD;

	public static GameBoard getFirstBoard()
	{
		GameBoard first = new GameBoard();
		first.benefit = 0;
		ChessType BLACK = ChessType.BLACK;
		ChessType WHITE = ChessType.WHITE;

        /* add black pieces */
		first.set(new Rook(BLACK), 0, 0);
		first.set(new Knight(BLACK), 0, 1);
		first.set(new Bishop(BLACK), 0, 2);
		first.set(new Queen(BLACK), 0, 3);
		first.set(new King(BLACK), 0, 4);
		first.set(new Bishop(BLACK), 0, 5);
		first.set(new Knight(BLACK), 0, 6);
		first.set(new Rook(BLACK), 0, 7);
		for (int col = 0; col < GameBoard.BOARD_SIZE; col++)
		{
			first.set(new Pawn(BLACK), 1, col);
		}

        /* add white pieces */
		for (int col = 0; col < GameBoard.BOARD_SIZE; col++)
		{
			first.set(new Pawn(WHITE), 6, col);
		}
		first.set(new Rook(WHITE), 7, 0);
		first.set(new Knight(WHITE), 7, 1);
		first.set(new Bishop(WHITE), 7, 2);
		first.set(new Queen(WHITE), 7, 3);
		first.set(new King(WHITE), 7, 4);
		first.set(new Bishop(WHITE), 7, 5);
		first.set(new Knight(WHITE), 7, 6);
		first.set(new Rook(WHITE), 7, 7);

		return first;
	}

	public static GameBoard makeMove(GameBoard board, Move move) throws IllegalMoveException
	{
		GameBoard newBoard = board.copy();
		ChessObject source = newBoard.at(move.start);
		Difficulty difficulty = (source.type == ChessType.WHITE ? whiteDiff : blackDiff);
		newBoard.set(null, move.start);
		newBoard.set(source.copy(), move.end);
		newBoard.evaluateBoard();

		if (difficulty == Difficulty.EASY)
		{
			newBoard.calculateBenefitEasy(source.type);
		}
		else if (difficulty == Difficulty.NORMAL)
		{
			newBoard.calculateBenefitNormal(source.type);
		}
		else if (difficulty == Difficulty.HARD)
		{
			newBoard.calculateBenefitHard(source.type);
		}

		return newBoard;
	}

	public static ChessType switchPlayer(ChessType currentPlayer)
	{
		return (currentPlayer == ChessType.WHITE ? ChessType.BLACK : ChessType.WHITE);
	}

	/* Check if any piece can make any move */
	public static boolean hasCheckmate(GameBoard board, ChessType currentPlayer)
	{
		List<ChessObject> pieces = (currentPlayer == ChessType.WHITE ? board.whitePieces : board.blackPieces);
		for (ChessObject obj : pieces)
		{
			for (Move m : obj.allMoves)
			{
				try
				{
					makeMove(board, m);
					return false;
				}
				catch (IllegalMoveException ignored)
				{
				}
			}
		}
		return true;
	}

}
