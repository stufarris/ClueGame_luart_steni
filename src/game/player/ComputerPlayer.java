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
	
	private Boolean readyToAccuse;
	
	public ComputerPlayer(String id, Color color, int row, int column, ClueGame game) {
		super(id, color, row, column);
		this.game = game;
		readyToAccuse = false;
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
	
	
	public Solution createSuggestion(Set<Card> playerCards, Set<Card> weaponCards) {
		Solution s = null;
		ArrayList<Card> tempPlayers = new ArrayList<Card>(playerCards);
		ArrayList<Card> tempWeapons = new ArrayList<Card>(weaponCards);
		
		Card suggestionPlayer;
		Card suggestionWeapon;
		
		for (Card c : super.getSeenCards()) {
			tempPlayers.remove(c);
			tempWeapons.remove(c);
		}
		
		RoomCell r = game.getBoard().getRoomCellAt(game.getBoard().calcIndex(this.getRow(), this.getColumn()));
		
		if (tempPlayers.isEmpty()) {
			suggestionPlayer = null;
		} else {
			suggestionPlayer = tempPlayers.get(new Random().nextInt(tempPlayers.size()));
		}
		if (tempWeapons.isEmpty()) {
			suggestionWeapon = null;
		} else {
			suggestionWeapon = tempWeapons.get(new Random().nextInt(tempWeapons.size()));
		}
		
		s = new Solution(suggestionPlayer.getTitle(), suggestionWeapon.getTitle(),
				game.getBoard().getRooms().get(r.getInitial()));
		
		return s;
	}
	
	public char getLastRoomVisited() {
		return lastRoomVisited;
	}

	public void setLastRoomVisited(char lastRoomVisited) {
		this.lastRoomVisited = lastRoomVisited;
	}

	public Boolean getReadyToAccuse() {
		return readyToAccuse;
	}

	public void setReadyToAccuse(Boolean readyToAccuse) {
		this.readyToAccuse = readyToAccuse;
	}


}
