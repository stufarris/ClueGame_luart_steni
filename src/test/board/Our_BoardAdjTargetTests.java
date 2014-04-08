package test.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import game.BadConfigFormatException;
import game.board.Board;
import game.board.cell.BoardCell;
import game.board.cell.RoomCell;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class Our_BoardAdjTargetTests {

	private static Board board;

	@BeforeClass
	public static void setUp() throws BadConfigFormatException {
		board = new Board("data/board/PK_ClueLayout.csv", "data/board/PK_ClueLegend.txt");
		board.loadConfigFiles();
		board.calcAdjacencies();
	}

	@Test
	public void testWalkwayLocations() {
		ArrayList<Integer> list = board.getAdjList(board.calcIndex(0, 7));
		boolean[] test = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++) {
			test[i] = board.getCellAt(list.get(i)).isWalkway();
		}
		for (int i = 0; i < test.length; i++) {
			Assert.assertEquals(test[i], true);
		}
	}

	@Test
	public void testEdgeLeft() {
		ArrayList<Integer> list = board.getAdjList(board.calcIndex(6, 0));
		Assert.assertEquals(list.size(), 2);
		list = board.getAdjList(board.calcIndex(8, 0));
		Assert.assertEquals(list.size(), 3);
		list = board.getAdjList(board.calcIndex(16, 0));
		Assert.assertEquals(list.size(), 2);
	}

	@Test
	public void testEdgeRight() {
		ArrayList<Integer> list = board.getAdjList(board.calcIndex(3, 25));
		Assert.assertEquals(list.size(), 2);
		list = board.getAdjList(board.calcIndex(5, 25));
		Assert.assertEquals(list.size(), 3);
		list = board.getAdjList(board.calcIndex(7, 25));
		Assert.assertEquals(list.size(), 2);
	}

	@Test
	public void testEdgeUp() {
		ArrayList<Integer> list = board.getAdjList(board.calcIndex(0, 6));
		Assert.assertEquals(list.size(), 2);
		list = board.getAdjList(board.calcIndex(0, 7));
		Assert.assertEquals(list.size(), 3);
		list = board.getAdjList(board.calcIndex(0, 9));
		Assert.assertEquals(list.size(), 2);
	}

	@Test
	public void testEdgeDown() {
		ArrayList<Integer> list = board.getAdjList(board.calcIndex(25, 5));
		Assert.assertEquals(list.size(), 2);
		list = board.getAdjList(board.calcIndex(25, 9));
		Assert.assertEquals(list.size(), 3);
		list = board.getAdjList(board.calcIndex(25, 19));
		Assert.assertEquals(list.size(), 2);
	}

	@Test
	public void testBesideRoomLocationsWalkways() {
		ArrayList<Integer> list = board.getAdjList(board.calcIndex(6, 0));
		boolean[] test = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++) {
			test[i] = board.getCellAt(list.get(i)).isRoom();
		}
		Assert.assertEquals(2, test.length);
		Assert.assertEquals(test[0], false);
		Assert.assertEquals(test[1], false);
		list = board.getAdjList(board.calcIndex(16, 0));
		test = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++) {
			test[i] = board.getCellAt(list.get(i)).isRoom();
		}
		Assert.assertEquals(2, test.length);
		Assert.assertEquals(test[0], false);
		Assert.assertEquals(test[1], false);
	}

	@Test
	public void testBesideRoomLocationsRooms() {
		ArrayList<Integer> list = board.getAdjList(board.calcIndex(11, 3));
		boolean[] test = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++) {
			test[i] = board.getCellAt(list.get(i)).isRoom();
		}
		for (int i = 0; i < test.length; i++) {
			Assert.assertEquals(test[i], true);
		}
		list = board.getAdjList(board.calcIndex(25, 0));
		test = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++) {
			test[i] = board.getCellAt(list.get(i)).isRoom();
		}
		for (int i = 0; i < test.length; i++) {
			Assert.assertEquals(test[i], true);
		}
	}

	@Test
	public void testAdjToDoorWayRightDirection() {
		Assert.assertEquals(board.getRoomCellAt(2, 24).getDoorDirection(),
				RoomCell.DoorDirection.DOWN);
		Assert.assertEquals(board.getRoomCellAt(8, 24).getDoorDirection(),
				RoomCell.DoorDirection.UP);
		Assert.assertEquals(board.getRoomCellAt(4, 16).getDoorDirection(),
				RoomCell.DoorDirection.RIGHT);
		Assert.assertEquals(board.getRoomCellAt(4, 14).getDoorDirection(),
				RoomCell.DoorDirection.LEFT);
	}

	@Test
	public void testAdjToDoorWayWrongDirection() {
		Assert.assertFalse(board.getRoomCellAt(23, 12).getDoorDirection() == RoomCell.DoorDirection.RIGHT);
		Assert.assertFalse(board.getRoomCellAt(17, 15).getDoorDirection() == RoomCell.DoorDirection.RIGHT);
	}

	@Test
	public void testDoorwaysTrue() {
		Assert.assertEquals(board.getCellAt(5, 4).isDoorway(), true);
		Assert.assertEquals(board.getCellAt(8, 3).isDoorway(), true);
		Assert.assertEquals(board.getCellAt(17, 2).isDoorway(), true);
		Assert.assertEquals(board.getCellAt(25, 4).isDoorway(), true);
		Assert.assertEquals(board.getCellAt(2, 24).isDoorway(), true);
	}

	@Test
	public void testDoorwaysFalse() {
		Assert.assertEquals(board.getCellAt(0, 0).isDoorway(), false);
		Assert.assertEquals(board.getCellAt(7, 0).isDoorway(), false);
		Assert.assertEquals(board.getCellAt(25, 25).isDoorway(), false);
		Assert.assertEquals(board.getCellAt(0, 25).isDoorway(), false);
		Assert.assertEquals(board.getCellAt(2, 14).isDoorway(), false);
	}

	@Test
	public void testTargetsWithOne() {
		board.startTargets(6, 4, 1);
		Set<BoardCell> targets = board.getTargets();
		Assert.assertTrue(board.getCellAt(board.calcIndex(5, 4)).isDoorway());
		Assert.assertTrue(targets.contains(board.getCellAt(board
				.calcIndex(5, 4))));
		board.startTargets(6, 4, 2);
		targets = board.getTargets();
		Assert.assertTrue(targets.contains(board.getCellAt(board
				.calcIndex(5, 4))));
		board.startTargets(6, 4, 3);
		targets = board.getTargets();
		Assert.assertTrue(targets.contains(board.getCellAt(board
				.calcIndex(5, 4))));
	}

	@Test
	public void testTargetsWithTwo() {
		board.startTargets(7, 4, 2);
		Set<BoardCell> targets = board.getTargets();
		assertTrue(board.getCellAt(board.calcIndex(5, 4)).isDoorway());
		assertTrue(targets.contains(board.getCellAt(5, 4)));
		assertTrue(targets.contains(board.getCellAt(7, 2)));
		assertTrue(targets.contains(board.getCellAt(7, 6)));
		assertTrue(targets.contains(board.getCellAt(6, 3)));
		assertTrue(targets.contains(board.getCellAt(6, 5)));
		assertFalse(targets.contains(board.getCellAt(7, 3)));
		assertEquals(6, targets.size());
	}

	@Test
	public void testTargetsWithThree() {
		board.startTargets(6, 6, 3);
		Set<BoardCell> targets = board.getTargets();
		Assert.assertTrue(board.getCellAt(board.calcIndex(5, 4)).isDoorway());
		Assert.assertTrue(targets.contains(board.getCellAt(board
				.calcIndex(5, 4))));
	}

	@Test
	public void testTargetsWithFour() {
		board.startTargets(6, 7, 4);
		Set<BoardCell> targets = board.getTargets();
		Assert.assertTrue(board.getCellAt(5, 4).isDoorway());
		Assert.assertTrue(targets.contains(board.getCellAt(board
				.calcIndex(5, 4))));
	}

	@Test
	public void testLeavingRoomA() {
		assertTrue(board.getCellAt(17, 2).isDoorway());
		board.startTargets(17, 2, 1);
		Set<BoardCell> targets = board.getTargets();
		assertTrue(targets.contains(board.getCellAt(16, 2)));
		assertEquals(1, targets.size());
	}

	@Test
	public void testLeavingRoomB() {
		board.startTargets(18, 22, 1);
		Set<BoardCell> targets = board.getTargets();
		assertTrue(targets.contains(board.getCellAt(18, 23)));
		assertEquals(1, targets.size());
	}

	@Test
	public void testAlongWalkwaysNoRooms() {
		board.startTargets(13, 11, 1);
		Set<BoardCell> targets = board.getTargets();
		Iterator<BoardCell> i = targets.iterator();
		while (i.hasNext()) {
			Assert.assertFalse(i.next().isDoorway());
		}
		board.startTargets(13, 11, 2);
		targets = board.getTargets();
		i = targets.iterator();
		while (i.hasNext()) {
			Assert.assertFalse(i.next().isDoorway());
		}
		board.startTargets(3, 11, 3);
		targets = board.getTargets();
		i = targets.iterator();
		while (i.hasNext()) {
			Assert.assertFalse(i.next().isDoorway());
		}
	}
	@Test
	public void testWalkwayCorner() {
		board.startTargets(17, 5, 2);
		Set<BoardCell> targets = board.getTargets();
		assertTrue(targets.contains(board.getCellAt(16,4)));
		assertTrue(targets.contains(board.getCellAt(15,5)));
		assertTrue(targets.contains(board.getCellAt(16,6)));
		assertFalse(targets.contains(board.getCellAt(18,4)));
	}

}
