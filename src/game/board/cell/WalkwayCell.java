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
	public void draw(Graphics g, int x, int y, int width, int height) {
		if (isHighlighted) {
			g.setColor(Color.CYAN);
		} else {
			g.setColor(Color.YELLOW);
		}
		g.fillRect(x + width * this.column, y + height * this.row, width, height);
		g.setColor(Color.BLACK);
		g.drawRect(x + width * this.column, y + height * this.row, width, height);
	}
	
	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

}
