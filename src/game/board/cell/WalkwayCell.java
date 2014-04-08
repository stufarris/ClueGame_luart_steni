package game.board.cell;

import java.awt.Color;
import java.awt.Graphics;

public class WalkwayCell extends BoardCell {
	
	private boolean isHighlighted;

	@Override
	public boolean isWalkway() {
		return true;
	}

	@Override
	public void draw(Graphics g, int x, int y, int dimension) {
		this.size = dimension;
		if (isHighlighted) {
			g.setColor(Color.CYAN);
		} else {
			g.setColor(Color.YELLOW);
		}
		g.fillRect(x + dimension * this.column, y + dimension * this.row, dimension, dimension);
		g.setColor(Color.BLACK);
		g.drawRect(x + dimension * this.column, y + dimension * this.row, dimension, dimension);
	}
	
	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

}
