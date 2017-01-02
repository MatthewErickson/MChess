import board.BoardManager;
import board.GameBoard;
import exception.IllegalMoveException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pieces.ChessObject;
import pieces.ChessType;
import strategy.Move;
import strategy.MoveMaker;
import util.Util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.Semaphore;

/**
 * Created by Matthew on 3/24/2016.
 */
public class Chess extends Application
{

	private Stack<GameBoard> boards;
	private BorderPane container;
	private ProgressBar progress;
	private Semaphore madeMove = new Semaphore(0);
	private Semaphore confirm = new Semaphore(0);

	private MoveMaker moveMaker;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		initUI(primaryStage);
		BoardManager.blackDiff = BoardManager.Difficulty.HARD;
		Thread runner = new Thread(() -> {
			int whiteWins = 0;
			int blackWins = 0;
			int nullWins = 0;
			while (true)
			{
				System.out.printf("\rWhite: %d --- Black: %d --- Tie: %d", whiteWins, blackWins, nullWins);
				ChessType winner = runGame();
				Platform.runLater(() -> showWinner(winner));

				if (winner == ChessType.WHITE)
				{
					whiteWins++;
				}
				if (winner == ChessType.BLACK)
				{
					blackWins++;
				}
				if (winner == null)
				{
					nullWins++;
				}
			}
		});
		runner.start();

		primaryStage.setOnCloseRequest(event -> {
			System.exit(0);
		});
	}

	private void initUI(Stage primaryStage)
	{
		GridPane grid = new GridPane();
		progress = new ProgressBar();
		progress.setAccessibleText("Thinking...");
		progress.setPrefSize(400, 25);
		progress.setMaxSize(400, 25);
		progress.setVisible(false);
		container = new BorderPane(grid);
		StackPane stackPane = new StackPane(container, progress);
		primaryStage.setScene(new Scene(stackPane, 900, 900));
		primaryStage.show();
	}

	private void showWinner(ChessType winner)
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText("Winner: " + winner.name().toLowerCase());
		alert.setContentText("Play again?");
		Optional<ButtonType> result = alert.showAndWait();
		if ((result.isPresent()) && (result.get() == ButtonType.OK))
		{
			confirm.release();
			alert.close();
		}
		else
		{
			System.exit(0);
		}
	}

	public ChessType runGame()
	{
		boards = new Stack<>();
		GameBoard board = BoardManager.getFirstBoard();
		boards.push(board);

		ChessType currentPlayer = ChessType.WHITE;
		int count = 0;
		moveMaker = new MoveMaker(MoveMaker.MAX_LEVEL);
		while (true)
		{
			board.evaluateBoard();
			setBoard(boards.peek(), currentPlayer);
			if (BoardManager.hasCheckmate(board, currentPlayer))
			{
				progress.setVisible(false);
				Util.sleepWithoutInterruption(2000);
				return BoardManager.switchPlayer(currentPlayer);
			}
			count++;
			try
			{
				if (currentPlayer == ChessType.BLACK)
				{
					Platform.runLater(() -> {
						progress.setProgress(0);
						progress.setVisible(true);
					});
					Move move = moveMaker.getBestMove(board, currentPlayer, progress);
					Platform.runLater(() -> {
						progress.setVisible(false);
					});
					GameBoard newBoard = BoardManager.makeMove(board, move);
					boards.push(newBoard);
				}
				else
				{
					/* wait for user to make a move */
					madeMove.acquireUninterruptibly();
				}
			}
			catch (IllegalMoveException e)
			{
				System.err.println(e.getMessage());
				return BoardManager.switchPlayer(currentPlayer);
			}
			currentPlayer = BoardManager.switchPlayer(currentPlayer);
			board = boards.peek();
			if (count > 250)
			{
				return null;
			}
		}
	}

	public void setBoard(GameBoard board, ChessType currentPlayer)
	{
		GridPane grid = new GridPane();
		grid.setStyle("-fx-background-color: #000;");
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(4);
		grid.setVgap(4);
		for (int i = 0; i < GameBoard.BOARD_SIZE; i++)
		{
			for (int j = 0; j < GameBoard.BOARD_SIZE; j++)
			{
				int finalI = i;
				int finalJ = j;
				Node node = getImageView(board.at(i, j));
				BorderPane box = new BorderPane(node);
				box.setMinSize(100, 100);
				box.setPrefSize(100, 100);
				String back = ((i + j) % 2 == 0 ? "#555" : "#CCC");
				box.setStyle("-fx-background-color: " + back);
				grid.add(box, j, i);
				if (board.at(i, j) != null)
				{
					if (currentPlayer == ChessType.WHITE)
					{
						box.setOnMouseClicked(startEvent -> {
							resetGridColors();
							ChessObject obj = board.at(finalI, finalJ);
							if (obj.type == ChessType.WHITE)
							{
								for (Move m : obj.allMoves)
								{
									int idx = m.end.x * 8 + m.end.y;
									Node dest = grid.getChildren().get(idx);
									dest.setStyle("-fx-background-color: #ffd700;");
									dest.setOnMouseClicked(endEvent -> {
										Move move = Move.make(obj.getPosition(), m.end);
										try
										{
											GameBoard newBoard = BoardManager.makeMove(board, move);
											dest.setOnMouseClicked(null);
											boards.push(newBoard);
											madeMove.release();
										}
										catch (IllegalMoveException e)
										{
											e.printStackTrace();
											resetGridColors();
											box.setStyle("-fx-background-color: red");
										}
									});
								}
							}
						});
					}
				}
			}
		}
		Platform.runLater(() -> {
			container.setCenter(grid);
		});
	}

	private Node getImageView(ChessObject obj)
	{
		if (obj != null)
		{
			InputStream stream;
			try
			{
				stream = new FileInputStream("img/" + obj.toString() + ".png");
			}
			catch (FileNotFoundException ignored)
			{
				stream = getClass().getClassLoader().getResourceAsStream(obj.toString() + ".png");
			}

			if (stream != null)
			{
				return new ImageView(new Image(stream, 100, 100, true, true));
			}
			return new Label(obj.toString());
		}
		return new Label("");
	}

	private void resetGridColors()
	{
		GridPane grid = (GridPane) container.getCenter();
		for (int i = 0; i < GameBoard.BOARD_SIZE; i++)
		{
			for (int j = 0; j < GameBoard.BOARD_SIZE; j++)
			{
				int idx = i * 8 + j;
				Node node = grid.getChildren().get(idx);
				String back = ((i + j) % 2 == 0 ? "#555" : "#CCC");
				node.setStyle("-fx-background-color: " + back);
			}
		}
	}

}
