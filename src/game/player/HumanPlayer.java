package game.player;

import game.ClueGame;
import game.card.Card;

import java.awt.Color;
import java.util.ArrayList;

public class HumanPlayer extends Player {
	private ClueGame game;
	
	public HumanPlayer(String id, Color color, int row, int column, ClueGame game) {
		super(id, color, row, column);
		this.game = game;
		this.characters = new ArrayList<Card>(game.getCharacters());
		this.weapons = new ArrayList<Card>(game.getWeapons());
		this.rooms = new ArrayList<Card>(game.getRooms());
	}

}
