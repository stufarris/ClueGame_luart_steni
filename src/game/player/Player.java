package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import game.ClueGame;
import game.board.cell.BoardCell;
import game.card.Card;

public class Player {
	private String id;
	private Color color;
	private Set<Card> handOfCards = new HashSet<Card>();
	private Set<Card> seenCards = new HashSet<Card>();

	private int row;
	private int column;
	
	public Player(String id, Color color, int row, int column) {
		this.id = id;
		this.color = color;
		this.row = row;
		this.column = column;
	}
	
	public Card disproveSuggestion(String character, String room, String weapon) {
		Card p = new Card(character, Card.CardType.PERSON);
		Card r = new Card(room, Card.CardType.ROOM);
		Card w = new Card(weapon, Card.CardType.WEAPON);
		ArrayList<Card> buffer = new ArrayList<Card>();
		if(handOfCards.contains(p)) buffer.add(p);
		if(handOfCards.contains(r)) buffer.add(r);
		if(handOfCards.contains(w)) buffer.add(w);
		return buffer.get(new Random().nextInt(buffer.size()));
	}
	
	public void forgetCard(Card c) {
		seenCards.remove(c);
	}
	
	public void seeCard(Card c) {
		seenCards.add(c);
	}
	
	public void giveCard(Card c) {
		handOfCards.add(c);
		seeCard(c);
	}
	
	public boolean hasCard(Card card) {
		return handOfCards.contains(card);
	}
	
	public Set<Card> getCards() {
		return handOfCards;
	}
	
	public String getName() {
		return id;
	}

	public Color getColor() {
		return color;
	}
	//temporary setter for testing
	public void setRow(int row) {
		this.row = row;
	}
	//temporary setter for testing
	public void setColumn(int column) {
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
	public Set<Card> getSeenCards() {
		return seenCards;
	}
	
	public void draw(Graphics g, int x, int y, int width, int height){
		g.setColor(color);
		System.out.println();
		g.fillOval(x + width * this.column, y + height * this.row, width, height);
	}

	@Override
	public boolean equals(Object other) {
		 if (other == null) return false;
		 if (this.getClass() != other.getClass()) return false;
		 if (this.getColor() != ((Player)other).getColor()) return false;
		 if (!this.getName().equals(((Player)other).getName())) return false;
		 return true;
	}
	
	public boolean isHuman() {
		return false;
	}
	
	public void updateLocation(BoardCell c) {
		this.row = c.getRow();
		this.column = c.getColumn();
	}
}
