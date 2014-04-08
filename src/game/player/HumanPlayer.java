package game.player;

import game.ClueGame;
import game.card.Card;

import java.awt.Color;
import java.util.ArrayList;

public class HumanPlayer extends Player {
	
	public HumanPlayer(String id, Color color, int row, int column) {
		super(id, color, row, column);
	}
	
	@Override
	public boolean isHuman() {
		return true;
	}

}
