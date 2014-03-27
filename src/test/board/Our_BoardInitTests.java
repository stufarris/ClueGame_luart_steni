package test.board;

import game.board.Board;

import org.junit.BeforeClass;

public class Our_BoardInitTests {

	private static Board board;
	public static final int NUM_ROOMS = 11;
	public static final int NUM_ROWS = 26;
	public static final int NUM_COLUMNS = 26;

	@BeforeClass
	public static void setUp() {
		board = new Board("ClueLayout.csv", "ClueLegend.txt");
		board.loadConfigFiles();
	}

}
