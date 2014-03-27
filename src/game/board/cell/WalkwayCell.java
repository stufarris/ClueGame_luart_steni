package game.board.cell;

import java.awt.Color;
import java.awt.Graphics;

public class WalkwayCell extends BoardCell {

	@Override
	public boolean isWalkway() {
		return true;
	}

	@Override
	public void draw(Graphics g, int x, int y, int width, int height) {
		g.setColor(Color.BLACK);
		g.drawRect(x + width * this.column, y + height * this.row, width, height);
	}

}
