package game.board.cell;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

public class RoomCell extends BoardCell {
	
	private DoorDirection doorDirection;
	private char letter;
	private boolean printsLabel;
	private String roomName;
	
	
	public enum DoorDirection {
		UP, DOWN, LEFT, RIGHT, NONE
	};

	public RoomCell(char letter) {
		this(letter, DoorDirection.NONE);
	}

	public RoomCell(char letter, DoorDirection dir) {
		this.letter = letter;
		doorDirection = dir;
	}
	
	public RoomCell(char letter, DoorDirection dir, boolean printsLabel, Map<Character, String> room) {
		this.letter = letter;
		doorDirection = dir;
		this.printsLabel = printsLabel;
		this.roomName = room.get(letter);
	}

	public char getInitial() {
		return letter;
	}

	@Override
	public boolean isRoom() {
		return true; // Was !isDoorway(), but didn't make sense to me
	}

	@Override
	public boolean isDoorway() {
		if (doorDirection == DoorDirection.NONE)
			return false;
		return true;
	}

	@Override
	public void draw(Graphics g, int x, int y, int width, int height) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x + width * this.column, y + height * this.row, width, height);
		if(this.isDoorway()) {
			g.setColor(Color.BLUE);
			switch(this.doorDirection) {
			case UP:
				g.fillRect(x + width * this.column, y + height * this.row, width, height / 5);
				break;
			case DOWN:
				g.fillRect(x + width * this.column, y + (height * this.row) + (height * 4) / 5, width, height / 5);
				break;
			case LEFT:
				g.fillRect(x + width * this.column, y + height * this.row, width / 5, height);
				break;
			case RIGHT:
				g.fillRect(x + (width * this.column) + (width * 4) / 5, y + height * this.row, width / 5, height);
				break;
			default:
				break;
			}
		}
		if (printsLabel) {
			g.setColor(Color.BLACK);
			g.drawString(roomName, x + width * this.column, y + height * this.row);
		}
	}

	@Override
	public DoorDirection getDoorDirection() {
		return doorDirection;
	}
}
