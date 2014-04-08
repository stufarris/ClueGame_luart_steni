package game.board;

import game.BadConfigFormatException;
import game.board.cell.BoardCell;
import game.board.cell.RoomCell;
import game.board.cell.WalkwayCell;
import game.board.cell.RoomCell.DoorDirection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Board {

	private String csv;
	private String legend;

	private ArrayList<BoardCell> cells;
	private Map<Character, String> rooms;
	private Map<Integer, ArrayList<Integer>> adjacencyMatrix;
	private boolean[] visited;
	private Set<BoardCell> targets;

	private int numRows;
	private int numColumns;

	private static final int BOARD_CELL_DIMENSION = 25;

	public Board() {
		// default uses CR board and legend
		this("ClueLayout.csv", "ClueLegend.txt");
	}

	public Board(String csv, String legend) {
		rooms = new HashMap<Character, String>();
		cells = new ArrayList<BoardCell>();
		targets = new HashSet<BoardCell>();
		adjacencyMatrix = new HashMap<Integer, ArrayList<Integer>>();
		this.csv = csv;
		this.legend = legend;
		numRows = 0;
		numColumns = 0;
		calcAdjacencies();
	}

	public void loadConfigFiles() {
		try {
			loadRoomConfig();
			loadBoardConfig();
		} catch (BadConfigFormatException e) {
			System.out.println(e.getMessage());
		}
	}

	public void loadRoomConfig() throws BadConfigFormatException {
		try {
			FileReader reader = new FileReader(legend);
			Scanner in = new Scanner(reader);
			if (!in.hasNextLine())
				throw new BadConfigFormatException(
						"Your file contains nothing!");
			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.charAt(1) != ',')
					throw new BadConfigFormatException(
							"You are missing the ',' in your format");
				if (line.charAt(2) != ' ')
					throw new BadConfigFormatException(
							"You are missing a space after the ','");
				char character = line.charAt(0);
				int commaCount = 0;
				for (int i = 0; i < line.length(); ++i) {
					if (line.charAt(i) == ',')
						++commaCount;
				}
				if (commaCount > 1)
					throw new BadConfigFormatException(
							"More than one comma on a line!");
				String roomName = line.substring(3);
				rooms.put(character, roomName);
			}
			reader.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void loadBoardConfig() throws BadConfigFormatException {
		// Reads a file for the layout of the clue board, setting up things such
		// as door directions
		try {
			FileReader fr = new FileReader(csv);
			Scanner s = new Scanner(fr);
			String inLine;
			int numLines = 0;

			// reference the legend for finding which char represents a walkway
			// & closet
			if (rooms.isEmpty()) {
				s.close();
				fr.close();
				throw new RuntimeException(
						"Error! Board legend does not exist yet. Be sure to call loadRoomConfig() first!");
			}
			char walkway = ' ';
			for (Entry<Character, String> e : rooms.entrySet()) {
				if (e.getValue().equalsIgnoreCase("walkway")) {
					walkway = e.getKey();
					continue;
				}
				if (e.getValue().equalsIgnoreCase("closet"))
					e.getKey();
			}
			if (walkway == ' ') {
				s.close();
				fr.close();

				throw new BadConfigFormatException(
						"No walkway entry defined in legend");
			}

			// iterate through the csv and instantiate cells
			while (s.hasNext()) {
				inLine = s.nextLine();
				++numLines;
				String[] lineEntries = inLine.split(",", -1);

				// Set board dimension
				if (numColumns == 0)
					numColumns = lineEntries.length;
				else if (numColumns != lineEntries.length) {
					s.close();
					fr.close();
					throw new BadConfigFormatException(
							"A row in the board layout had a different number of columns than the others.");
				}

				for (String cell : lineEntries) {
					// check to be sure we're reading a character we expect and
					// valid format
					if (rooms.containsKey(cell.charAt(0)) && cell.length() > 0
							&& cell.length() <= 2) {
						// figure out what this entry represents
						if (cell.length() == 1 && cell.charAt(0) == walkway) {
							cells.add(new WalkwayCell());
							continue;
						}

						// Check for door
						if (cell.length() == 2) {
							final char identifier = Character.toUpperCase(cell
									.charAt(1));
							switch (identifier) {
							case ('D'): {
								cells.add(new RoomCell(cell.charAt(0),
										DoorDirection.DOWN));
								continue;
							}
							case ('U'): {
								cells.add(new RoomCell(cell.charAt(0),
										DoorDirection.UP));
								continue;
							}
							// special case to draw label
							case ('N'): {
								cells.add(new RoomCell(cell.charAt(0),
										DoorDirection.NONE, true, rooms));
								continue;
							}
							case ('L'): {
								cells.add(new RoomCell(cell.charAt(0),
										DoorDirection.LEFT));
								continue;
							}
							case ('R'): {
								cells.add(new RoomCell(cell.charAt(0),
										DoorDirection.RIGHT));
								continue;
							}
							default:
								throw new RuntimeException(
										"Invalid door direciton: " + cell);
							}

						}
						// if we've reached here, we have a normal room cell
						cells.add(new RoomCell(cell.charAt(0)));
					} else {
						s.close();
						fr.close();
						throw new BadConfigFormatException(
								"Found cell not in legend! :" + cell);
					}
				}

			}
			if (numLines != 0) {
				numRows = numLines;
				setCellCoordinates();
			} else {
				s.close();
				fr.close();
				throw new BadConfigFormatException(
						"Unable to read number of rows. Is the csv file empty?");
			}
			s.close();
			fr.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		visited = new boolean[numRows * numColumns];
	}

	public void setCellCoordinates() {
		if (cells.isEmpty())
			throw new RuntimeException(
					"Tried to set cell coordinates when there are no cells!");
		for (int r = 0; r < numRows; ++r) {
			for (int c = 0; c < numColumns; ++c) {
				getCellAt(r, c).setCoordinates(r, c);
			}
		}
	}

	public Map<Character, String> getRooms() {
		return rooms;
	}

	public BoardCell getCell(int location) {
		return cells.get(location);
	}

	public BoardCell getCellAt(int row, int column) {
		return getCell(calcIndex(row, column));
	}

	public BoardCell getCellAt(int location) {
		return getCell(location);
	}

	public RoomCell getRoomCellAt(int row, int column) {
		if (getCellAt(row, column).isRoom()) {
			return (RoomCell) getCellAt(row, column);
		}
		else {
			return null;
		}
	}

	public RoomCell getRoomCellAt(int location) {
		if (getCellAt(location).isRoom()) {
			return (RoomCell) getCellAt(location);
		}
		else {
			return null;
		}
	}

	public ArrayList<BoardCell> getBoardCells() {
		return cells;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public Set<BoardCell> getTargets() {
		return targets;
	}

	public void calcAdjacencies() {
		adjacencyMatrix = new HashMap<Integer, ArrayList<Integer>>();
		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				int index = calcIndex(row, column);
				adjacencyMatrix.put(index, new ArrayList<Integer>());
				// Check if it is a room without a door, if it is, can't have any adjacencies
				// Check if doorway
				if (!cells.get(calcIndex(row,column)).isRoom() ||
					(cells.get(calcIndex(row,column)).isRoom() &&
					cells.get(calcIndex(row,column)).isDoorway())) {
					if (cells.get(calcIndex(row,column)).isDoorway()) {
						DoorDirection dir = cells.get(calcIndex(row, column)).getDoorDirection();
						switch (dir) {
						case UP:
							adjacencyMatrix.get(index).add(calcIndex(row - 1, column));
							break;
						case DOWN:
							adjacencyMatrix.get(index).add(calcIndex(row + 1, column));
							break;
						case LEFT:
							adjacencyMatrix.get(index).add(calcIndex(row, column - 1));
							break;
						case RIGHT:
							adjacencyMatrix.get(index).add(calcIndex(row, column + 1));
							break;
						case NONE:
							break;
						}
					} else {
						// ABOVE
						if (row > 0) {
							if (cells.get(calcIndex(row - 1, column)).isWalkway() ||
									(cells.get(calcIndex(row - 1,column)).isDoorway() &&
											(cells.get(calcIndex(row - 1,column)).getDoorDirection() == DoorDirection.DOWN))) {
								adjacencyMatrix.get(index).add(calcIndex(row - 1, column));
							}
						}
						// BELOW
						if (row < (numRows - 1)) {
							if (cells.get(calcIndex(row + 1, column)).isWalkway() ||
									(cells.get(calcIndex(row + 1,column)).isDoorway() &&
											(cells.get(calcIndex(row + 1,column)).getDoorDirection() == DoorDirection.UP))) {
								adjacencyMatrix.get(index).add(calcIndex(row + 1, column));
							}
						}
						// LEFT
						if (column > 0) {
							if (cells.get(calcIndex(row, column - 1)).isWalkway() ||
									(cells.get(calcIndex(row,column - 1)).isDoorway() &&
											(cells.get(calcIndex(row,column - 1)).getDoorDirection() == DoorDirection.RIGHT))) {
								adjacencyMatrix.get(index).add(calcIndex(row, column - 1));
							}
						}
						// RIGHT
						if (column < (numColumns - 1)) {
							if (cells.get(calcIndex(row, column + 1)).isWalkway() ||
									(cells.get(calcIndex(row,column + 1)).isDoorway() &&
											(cells.get(calcIndex(row,column + 1)).getDoorDirection() == DoorDirection.LEFT))) {
								adjacencyMatrix.get(index).add(calcIndex(row, column + 1));
							}
						}
					}
				}
			}
		}
	}
	
	public ArrayList<Integer> getAdjList(int index) {
		return adjacencyMatrix.get(index);
	}
	
	public void startTargets(int row, int column, int move) {
		// Setup
		for (int i = 0; i < visited.length; i++) {
			visited[i] = false;
		}
		if (adjacencyMatrix.isEmpty()) {
			calcAdjacencies();
		}
		targets.clear();
		visited[calcIndex(row, column)] = true;
		calcTargets(calcIndex(row,column), move);
	}
	

	public void calcTargets(int index, int move) {
		ArrayList<Integer> adjacentCells = new ArrayList<Integer>();
		for (Integer cell : getAdjList(index)) {
			if (!visited[cell]) {
				adjacentCells.add(cell);
			}
		}
		for (Integer cell : adjacentCells) {
			visited[cell] = true;
			if (move == 1) {
				targets.add(cells.get(cell));
			} else if (cells.get(cell).isDoorway()) {
				targets.add(cells.get(cell));
			}
			else {
				calcTargets(cell, (move - 1));
			}
			visited[cell] = false;
		}
	}
	
	public void highlightTargets() {
		for (BoardCell b : targets) {
			b.setHighlighted(true);
		}
	}
	
	public void clearHighlights() {
		for (BoardCell b : targets) {
			b.setHighlighted(false);
		}
	}

	public int calcIndex(int row, int column) {
		if (row >= 0 && row < numRows && column >= 0 && column < numColumns) {
			return numColumns * row + column;
		} else {
			throw new RuntimeException(
					"Tried to calcIndex a location not on the board");
		}
	}

	public void drawBoard( int x_offset, int y_offset, Graphics g, JPanel jp) {
		int width = BOARD_CELL_DIMENSION * numColumns;
		int height = BOARD_CELL_DIMENSION * numRows;

		jp.setPreferredSize(new Dimension(width + x_offset, height + y_offset));

		for(BoardCell b : cells) {
			b.draw(g, x_offset, y_offset, BOARD_CELL_DIMENSION);
		}

	}
}
