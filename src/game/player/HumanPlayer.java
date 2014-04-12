package game.player;

import java.awt.Color;

public class HumanPlayer extends Player {
	
	
	public HumanPlayer(String id, Color color, int row, int column) {
		super(id, color, row, column);
	}
	
	@Override
	public boolean isHuman() {
		return true;
	}

}
