package game.player;

import java.awt.Color;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;

import game.Solution;

import game.board.Board;
import game.board.cell.WalkwayCell;

import game.ClueGame;
import game.board.cell.BoardCell;
import game.board.cell.RoomCell;
import game.card.Card;

public class ComputerPlayer extends Player {
	
	private char lastRoomVisited;
	private ClueGame game;
	
	public ComputerPlayer(String id, Color color, int row, int column, ClueGame game) {
		super(id, color, row, column);
		this.game = game;
	}
	
	public BoardCell pickLocation(Set<BoardCell> targets) {
		for (BoardCell c : targets) {
			if (c.isDoorway()) {
				RoomCell r = (RoomCell)c;
				if (r.getInitial() != lastRoomVisited) {
					return c;
				}
			}
		}
		
		int i = 0;
		int index = new Random().nextInt(targets.size());
		for (BoardCell c : targets) {
			if (i == index) {
				return c;
			}
			i++;
		}
		
		
		return new WalkwayCell();
	}
	
	
	public Solution createSuggestion(ArrayList<Card> playerCards, ArrayList<Card> weaponCards) {
		Solution s = null;
		ArrayList<Card> tempPlayers = new ArrayList<Card>(playerCards);
		ArrayList<Card> tempWeapons = new ArrayList<Card>(weaponCards);
		
		for (Card c : super.getSeenCards()) {
			tempPlayers.remove(c);
			tempWeapons.remove(c);
		}
		
		RoomCell r = game.getBoard().getRoomCellAt(game.getBoard().calcIndex(this.getRow(), this.getColumn()));
		
		s = new Solution(tempPlayers.get(new Random().nextInt(tempPlayers.size())).getTitle(),
				tempWeapons.get(new Random().nextInt(tempWeapons.size())).getTitle(),
				game.getBoard().getRooms().get(r.getInitial()));
		
		return s;
	}
	
	public char getLastRoomVisited() {
		return lastRoomVisited;
	}

	public void setLastRoomVisited(char lastRoomVisited) {
		this.lastRoomVisited = lastRoomVisited;
	}

}
