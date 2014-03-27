package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import game.BadConfigFormatException;
import game.board.Board;
import game.board.cell.RoomCell;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigReadTests {

	private static Board testBoard;

	@BeforeClass
	public static void loadBoard() {
		testBoard = new Board("data/board/PK_ClueLayout.csv", "data/board/PK_ClueLegend.txt");
		testBoard.loadConfigFiles();
	}

	@Test
	public void testLegend() {
		Map<Character, String> rooms = testBoard.getRooms();
		assertEquals(rooms.get('C'), "Conservatory");
		assertEquals(rooms.get('W'), "Walkway");
		assertEquals(rooms.get('K'), "Kitchen");
	}

	@Test
	public void testLegend2() {
		Map<Character, String> rooms = testBoard.getRooms();
		assertEquals(rooms.size(), 11);
	}

	@Test
	public void testBoardDimension() {
		assertEquals(testBoard.getNumRows(), 26);
		assertEquals(testBoard.getNumColumns(), 26);
		assertEquals(testBoard.calcIndex(0, 0), 0);
	}

	@Test
	public void testCalcIndex() {
		int rows = testBoard.getNumRows();
		int columns = testBoard.getNumColumns();
		assertEquals(rows, 26);
		assertEquals(columns, 26);
		assertEquals(testBoard.calcIndex(0, 0), 0);
		assertEquals(testBoard.calcIndex(1, 0), 26);
		assertEquals(testBoard.calcIndex(0, 1), 1);
		assertEquals(testBoard.calcIndex(1, 1), 27);
		assertEquals(testBoard.calcIndex(10, 5), 265);
		assertEquals(testBoard.calcIndex(22, 4), 576);
		assertEquals(testBoard.calcIndex(25, 25), 675);
	}

	@Test
	public void testDoorDirections() {
		assertEquals(testBoard.getRoomCellAt(5, 4).getDoorDirection(),
				RoomCell.DoorDirection.DOWN);
		assertEquals(testBoard.getRoomCellAt(17, 2).getDoorDirection(),
				RoomCell.DoorDirection.UP);
		assertEquals(testBoard.getRoomCellAt(13, 7).getDoorDirection(),
				RoomCell.DoorDirection.RIGHT);
		assertEquals(testBoard.getRoomCellAt(25, 4).getDoorDirection(),
				RoomCell.DoorDirection.LEFT);

		// make sure non-door cells don't think that they are doors
		assertFalse(testBoard.getCell(testBoard.calcIndex(3, 15)).isDoorway());
	}

	@Test
	public void testDoorCount() {
		int count = 0;
		for (int col = 0; col < testBoard.getNumColumns(); col++) {
			for (int row = 0; row < testBoard.getNumRows(); row++) {
				if (testBoard.getCellAt(row, col).isDoorway())
					count++;
			}
		}

		assertEquals(16, count);
	}

	@Test
	public void testRoomInitial() {
		assertEquals('H', testBoard.getRoomCellAt(0, 0).getInitial());
		assertEquals('C', testBoard.getRoomCellAt(0, 12).getInitial());
		assertEquals('B', testBoard.getRoomCellAt(20, 0).getInitial());
		assertEquals('D', testBoard.getRoomCellAt(11, 25).getInitial());
		assertEquals('O', testBoard.getRoomCellAt(20, 9).getInitial());
	}

	@Test(expected = BadConfigFormatException.class)
	public void testBadFormat() throws BadConfigFormatException {
		Board badBoard = new Board("data/board/BadLayout.csv", "data/board/BadLegend.txt");
		badBoard.loadRoomConfig();
	}
}
