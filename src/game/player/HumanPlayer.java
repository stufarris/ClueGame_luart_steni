package game.player;

import game.ClueGame;
import game.card.Card;
import gui.GuessDialog;

import java.awt.Color;
import java.util.ArrayList;

public class HumanPlayer extends Player {
	
	private GuessDialog guessDialog;
	
	public HumanPlayer(String id, Color color, int row, int column) {
		super(id, color, row, column);
	}
	
	@Override
	public boolean isHuman() {
		return true;
	}
	
	public void createGuessDialog(){
		GuessDialog gui = new GuessDialog();
		gui.setVisible(true);
		gui.setResizable(false);
	}

}
