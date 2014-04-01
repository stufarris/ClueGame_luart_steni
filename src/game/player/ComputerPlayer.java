package game.player;

import java.awt.Color;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;

import game.ClueGame;
import game.board.cell.BoardCell;
import game.board.cell.RoomCell;
import game.card.Card;

public class ComputerPlayer extends Player {
	private ClueGame game;
	
	public ComputerPlayer(){
		super();
		this.seenCharacters = new ArrayList<Card>();
		this.seenWeapons = new ArrayList<Card>();
		this.seenRooms = new ArrayList<Card>();
	}
	
	public ComputerPlayer(String id, Color color, int row, int column, ClueGame game) {
		super(id, color, row, column);
		this.game = game;
		this.seenCharacters = new ArrayList<Card>(game.getCharacters());
		this.seenWeapons = new ArrayList<Card>(game.getWeapons());
		this.seenRooms = new ArrayList<Card>(game.getRooms());
	}
	
	public BoardCell pickLocation(Set<BoardCell> targets) {
		for(BoardCell b : targets) {
			if(b.isDoorway()) {
				RoomCell r = (RoomCell)b;
				if(game.getBoard().getRooms().get(r.getInitial()) != this.getLastRoomVisited()) {
					return r;
				}
			}
		}
		return (new ArrayList<BoardCell>(targets)).get(new Random().nextInt(targets.size()));
	}

	public String[] createSuggestion() {
		String[] s = {getCharacters().get(new Random().nextInt(getCharacters().size())).getTitle(), getLastRoomVisited(),
			getWeapons().get(new Random().nextInt(getWeapons().size())).getTitle()};
		
		return s;

	}
	
	@Override
	public void giveCard(Card c) {
		seeCard(c);
		super.giveCard(c);
	}
}
