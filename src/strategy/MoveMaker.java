package strategy;

import board.BoardManager;
import board.GameBoard;
import exception.IllegalMoveException;
import exception.KingInCheckException;
import exception.NoMoreMovesException;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import pieces.ChessObject;
import pieces.ChessType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Matthew on 3/22/2016.
 */
public class MoveMaker
{
	public static final int MAX_LEVEL = 3;
	private static final Random random = new Random(System.nanoTime());

	private int maxLevel;
	private List<Move> bestMoves;

	public MoveMaker(int maxLevel)
	{
		this.maxLevel = maxLevel;
		bestMoves = new ArrayList<>();
	}

	public Move getBestMove(GameBoard board, ChessType type) throws IllegalMoveException
	{
		return getBestMove(board, type, null);
	}

	public Move getBestMove(GameBoard board, ChessType type, ProgressBar pBar) throws IllegalMoveException
	{
		return getBestMove(board, type, pBar, 0);
	}

	public Move getBestMove(GameBoard board, ChessType type, ProgressBar pBar, int currentLevel) throws IllegalMoveException
	{
		getBestMoveRecursiveHelper(board, type, pBar, currentLevel);
		return bestMoves.get(random.nextInt(bestMoves.size()));
	}

	private int getBestMoveRecursiveHelper(GameBoard board, ChessType type, ProgressBar pBar, int currentLevel) throws IllegalMoveException
	{
		List<ChessObject> pieces = (type == ChessType.WHITE ? board.whitePieces : board.blackPieces);
		ChessType otherPlayer = BoardManager.switchPlayer(type);
		boolean kingInCheck = false;
		int bestBenefit = Integer.MIN_VALUE;

		int totalMoves = 1;
		for (ChessObject piece : pieces)
		{
			totalMoves += piece.allMoves.size();
		}

		int index = 0;
		for (ChessObject piece : pieces)
		{
			for (Move move : piece.allMoves)
			{
				if (currentLevel == 0 && pBar != null)
				{
					final double progress = ((double) index++) / totalMoves;
					Platform.runLater(() -> pBar.setProgress(progress));
				}
				try
				{
					GameBoard newBoard = BoardManager.makeMove(board, move);
					int benefit = newBoard.benefit;
					try
					{
						if (currentLevel + 1 == maxLevel)
						{
							benefit -= getBestMoveBenefit(newBoard, otherPlayer);
						}
						else
						{
							benefit -= getBestMoveRecursiveHelper(newBoard, otherPlayer, pBar, currentLevel + 1);
						}
					}
					catch (IllegalMoveException e)
					{
						/* they couldn't respond to this which is good */
						benefit += 10000;
					}

					if (benefit > bestBenefit)
					{
						bestBenefit = benefit;
						if (currentLevel == 0)
						{
							bestMoves.clear();
							bestMoves.add(move);
						}
					}
					else if (benefit == bestBenefit)
					{
						if (currentLevel == 0)
						{
							bestMoves.add(move);
						}
					}
				}
				catch (IllegalMoveException e)
				{
					/* we couldn't make this move so go to the next one */
					if (e instanceof KingInCheckException)
					{
						kingInCheck = true;
					}
				}
			}
		}

		if (bestBenefit == Integer.MIN_VALUE)
		{
			if (kingInCheck)
			{
				throw new KingInCheckException("Checkmate: " + type);
			}
			throw new NoMoreMovesException("No more moves");
		}
		return bestBenefit;
	}

	private int getBestMoveBenefit(GameBoard board, ChessType type) throws IllegalMoveException
	{
		List<ChessObject> pieces = (type == ChessType.WHITE ? board.whitePieces : board.blackPieces);
		int bestBenefit = Integer.MIN_VALUE;
		GameBoard newBoard;

		for (ChessObject piece : pieces)
		{
			for (Move move : piece.allMoves)
			{
				try
				{
					newBoard = BoardManager.makeMove(board, move);
					if (newBoard.benefit > bestBenefit)
					{
						bestBenefit = newBoard.benefit;
					}
				}
				catch (IllegalMoveException ignored)
				{
				}
			}
		}

		if (bestBenefit == Integer.MIN_VALUE)
		{
			throw new IllegalMoveException("getBestMoveBenefit");
		}
		return bestBenefit;
	}

}
