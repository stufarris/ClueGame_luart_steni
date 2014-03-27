package game.player;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import game.card.Card;

public class Player {
	private String id = "";
	private Color color;
	private Set<Card> cards;

	private int row;
	private int column;
	protected String lastRoomVisited = "\0";
	
	protected ArrayList<Card> characters;
	protected ArrayList<Card> weapons;
	protected ArrayList<Card> rooms;
	
	public Player(){
		this.cards = new HashSet<Card>();
	}
	
	public Player(String id, Color color, int row, int column) {
		this.id = id;
		this.color = color;
		this.row = row;
		this.column = column;
		this.cards = new HashSet<Card>();
	}
	
	public Card disproveSuggestion(String character, String room, String weapon) {
		Card p = new Card(character, Card.CardType.PERSON);
		Card r = new Card(room, Card.CardType.ROOM);
		Card w = new Card(weapon, Card.CardType.WEAPON);
		ArrayList<Card> buffer = new ArrayList<Card>();
		if(cards.contains(p)) buffer.add(p);
		if(cards.contains(r)) buffer.add(r);
		if(cards.contains(w)) buffer.add(w);
		return buffer.get(new Random().nextInt(buffer.size()));
	}
	
	public void seeCard(Card c) {
		if(c.getType() == Card.CardType.PERSON) characters.remove(c);
		else if(c.getType() == Card.CardType.WEAPON) weapons.remove(c);
		else if(c.getType() == Card.CardType.ROOM) rooms.remove(c);
	}
	
	public void forgetCard(Card c) {
		if(c.getType() == Card.CardType.PERSON && !characters.contains(c)) characters.add(c);
		else if(c.getType() == Card.CardType.WEAPON && !weapons.contains(c)) weapons.add(c);
		else if(c.getType() == Card.CardType.ROOM && !rooms.contains(c)) rooms.add(c);
	}
	
	public String getLastRoomVisited() {
		return lastRoomVisited;
	}

	public void setLastRoomVisited(String lastRoomVisited) {
		this.lastRoomVisited = lastRoomVisited;
	}
	
	public void giveCard(Card c) {
		cards.add(c);
	}
	
	public boolean hasCard(Card card) {
		return cards.contains(card);
	}
	
	public Set<Card> getCards() {
		return cards;
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
	
	public ArrayList<Card> getCharacters() {
		return characters;
	}

	public ArrayList<Card> getWeapons() {
		return weapons;
	}

	public ArrayList<Card> getRooms() {
		return rooms;
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

	@Override
	public int hashCode() {
		return (this.id + this.color.toString()).hashCode();
	}
	
}
