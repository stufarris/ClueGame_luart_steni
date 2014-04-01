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
	private Map<Integer, ArrayList<Integer>> adjacencyLists;
	private boolean[] visited;
	private Set<BoardCell> targets;
	
	private int numRows;
	private int numColumns;
	
	private static final int BOARD_CELL_WIDTH = 25;
	private static final int BOARD_CELL_HEIGHT = 25;
	
	public Board() {
		// default uses CR board and legend
		this("ClueLayout.csv", "ClueLegend.txt");
	}
	
	public Board(String csv, String legend) {
		rooms = new HashMap<Character, String>();
		cells = new ArrayList<BoardCell>();
		targets = new HashSet<BoardCell>();
		adjacencyLists = new HashMap<Integer, ArrayList<Integer>>();
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
							// special case because provided board has an
							// Inconsistent door specifier
							case ('N'): {
								break;
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
		// Generate adjacency lists for all board locations
		for (int location = 0; location < numRows * numColumns; ++location) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			// for a given location, check all adjacent cells, provided they
			// exist

			/*
			 * Left. Locations on the left edge have locations that are
			 * multiples of the number of columns
			 */
			if (location % numColumns != 0)
				list.add(location - 1);
			// Right. Locations on the right edge are locations (n*columns)-1
			if ((location + 1) % numColumns != 0)
				list.add(location + 1);
			// Top
			if (location - numColumns >= 0)
				list.add(location - numColumns);
			// Bottom
			if (location + numColumns <= calcIndex(numRows - 1, numColumns - 1))
				list.add(location + numColumns);
			adjacencyLists.put(location, list);
		}
	}

	// Takes the precalculated adjacency list and prunes out any we don't want
	// to pass
	public ArrayList<Integer> getValidAdjacencies(int location) {
		ArrayList<Integer> newList = new ArrayList<Integer>();
		newList.addAll(adjacencyLists.get(location));
		for (Iterator<Integer> it = newList.iterator(); it.hasNext();) {
			int i = it.next();
			// Do not allow pathing through rooms
			if (getCellAt(i).isRoom() && !getCellAt(i).isDoorway()
					|| getCellAt(location).isRoom()) {
				it.remove();
				continue;
			}
			// Do not allow pathing into a door from another door (double doors)
			if (getCellAt(i).isDoorway()) {
				if (getCellAt(location).isDoorway()) {
					it.remove();
					continue;
				}
				DoorDirection d = getRoomCellAt(i).getDoorDirection();
				switch (d) {
				// Check if we're entering the doorway from the right direction
				case UP: {
					if (i != location + numColumns)
						it.remove();
					break;
				}
				case DOWN: {
					if (i != location - numColumns)
						it.remove();

					break;
				}
				case LEFT: {
					if (i != location + 1)
						it.remove();
					break;

				}
				case RIGHT: {
					if (i != location - 1)
						it.remove();
					break;

				}
				case NONE: {
					throw new RuntimeException(
							"Unable to get a door direciton from a door!");
				}
				}

			}
		}
		return newList;
	}

	// This method steals the signature of the one above it because the CR
	// JUnits
	// expect linked lists for some reason
	public LinkedList<Integer> getAdjList(int location) {
		LinkedList<Integer> tempList = new LinkedList<Integer>();
		tempList.addAll(getValidAdjacencies(location));
		return tempList;
	}

	public void calcTargets(int row, int column, int numSteps) {
		calcTargets(calcIndex(row, column), numSteps);
	}

	public void calcTargets(int location, int numSteps) {
		visited = new boolean[numRows * numColumns];
		for (int i = 0; i < numRows * numColumns; i++)
			visited[i] = false;
		// mark start location as visited
		visited[location] = true;
		targets = new HashSet<BoardCell>();
		calcTargetsRecursive(location, numSteps);
	}

	public void calcTargetsRecursive(int row, int column, int numSteps) {
		calcTargetsRecursive(calcIndex(row, column), numSteps);
	}

	public void calcTargetsRecursive(int thisCell, int numSteps) {
		// if we've reached this point, we can go no further down this path
		if (numSteps == 0)
			return;
		// Make a copy of the precalculated adjacency list for the current cell
		ArrayList<Integer> adjListThisCell = new ArrayList<Integer>();
		for (Integer adjCell : getValidAdjacencies(thisCell)) {
			// Only look at cells that haven't been visited
			if (visited[adjCell] == false)
				adjListThisCell.add(adjCell);
		}
		for (Integer adjCell : adjListThisCell) {
			visited[adjCell] = true;
			if (getCellAt(adjCell).isDoorway()) {
				targets.add(getCellAt(adjCell));
			}
			if (numSteps == 1) {
				if (!targets.contains(getCellAt(adjCell)))
					targets.add(getCellAt(adjCell));
			} else {
				calcTargetsRecursive(adjCell, numSteps - 1);
			}
			visited[adjCell] = false;
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
		int width = BOARD_CELL_WIDTH * numColumns;
		int height = BOARD_CELL_HEIGHT * numRows;
		
		jp.setPreferredSize(new Dimension(width + x_offset, height + y_offset));
		
		g.setColor(Color.YELLOW);
		g.fillRect((width + x_offset) / 2 - width / 2, y_offset, width, height);
		
		for(BoardCell b : cells) {
			b.draw(g, x_offset, y_offset, BOARD_CELL_WIDTH, BOARD_CELL_HEIGHT);
		}
		
		g.setColor(Color.BLACK);
		g.drawString("Conservatory", x_offset + 30, y_offset + 60);
		g.drawString("Billiard Room", x_offset + 200, y_offset + 60);
		g.drawString("Library", x_offset + 370, y_offset + 60);
		g.drawString("Study", x_offset + 520, y_offset + 60);
		g.drawString("Ballroom", x_offset + 70, y_offset + 260);
		g.drawString("Hall", x_offset + 490, y_offset + 260);
		g.drawString("Kitchen", x_offset + 50, y_offset + 470);
		g.drawString("Dining Room", x_offset + 250, y_offset + 470);
		g.drawString("Lounge", x_offset + 490, y_offset + 470);
		
	}
}
