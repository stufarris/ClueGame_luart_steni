package game.board.cell;

import game.board.cell.RoomCell.DoorDirection;

import java.awt.Graphics;

public abstract class BoardCell {

	// location of this piece
	protected int column;
	protected int row;
	
	public BoardCell() {
		
	}
	
	public BoardCell(int row, int column) {
		setCoordinates(row, column);
	}
	public void setCoordinates(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public abstract void draw(Graphics g, int x, int y, int width, int height);

	public boolean isWalkway() {
		return false;
	}

	public boolean isDoorway() {
		return false;
	}

	public boolean isRoom() {
		return false;
	}

	@Override
	public String toString() {
		return "(" + row + ", " + column + ")";
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	
	public DoorDirection getDoorDirection() {
		return DoorDirection.NONE;
	}

	public abstract void setHighlighted(boolean isHighlighted);
}
