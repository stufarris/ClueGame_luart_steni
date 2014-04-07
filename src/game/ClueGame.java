package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import game.board.Board;
import game.card.Card;
import game.player.ComputerPlayer;
import game.player.HumanPlayer;
import game.player.Player;
import gui.panel.DisplayPanel;

public class ClueGame extends JPanel{

	private static final long serialVersionUID = 7898999844979750592L;
	private Set<Card> cards;
	private Set<Card> weapons;
	private Set<Card> rooms;
	private Set<Card> characters;
	
	private ArrayList<ComputerPlayer> computerPlayers;
	private HumanPlayer humanPlayer;
	private ArrayList<Player> players;
	private Player currentPlayer;
	
	private Solution solution;
	private Board board;
	
	private int dieRoll;
	
	private static final int BOARD_DIMENSION = 650;
	
	private static final int HUMAN_START_ROW = 19;
	private static final int HUMAN_START_COLUMN = 16;
	private static final int PLAYER_DIMENSION = 25;
	private static final int X_OFFSET = 5;
	private static final int Y_OFFSET = 15;
	
	public ClueGame() {
		this.setPreferredSize(new Dimension(BOARD_DIMENSION, BOARD_DIMENSION));
		this.setSize(new Dimension(BOARD_DIMENSION, BOARD_DIMENSION));
		this.setBorder(new TitledBorder (new EtchedBorder(), "Game Board"));
		
		cards = new HashSet<Card>();
		computerPlayers = new ArrayList<ComputerPlayer>();
		players = new ArrayList<Player>();
		weapons = new HashSet<Card>();
		rooms = new HashSet<Card>();
		characters = new HashSet<Card>();
		currentPlayer = null;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void loadConfigFiles(String characterFilename, String weaponFilename, String playerFilename) {
		board = new Board("data/board/ClueLayout.csv", "data/board/ClueLegend.txt");
		board.loadConfigFiles();
		try {
			loadRoomsFromBoard();
			loadWeaponFile(weaponFilename);
			loadCharacterFile(characterFilename);
			loadPlayerFile(playerFilename);
			humanPlayer = (new HumanPlayer("Human", Color.BLACK, HUMAN_START_ROW, HUMAN_START_COLUMN, this));
		} catch(BadConfigFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addCard(Card c) {
		if(c.getType() == Card.CardType.WEAPON) weapons.add(c);
		else if(c.getType() == Card.CardType.PERSON) characters.add(c);
		else if(c.getType() == Card.CardType.ROOM) rooms.add(c);
		cards.add(c);
	}
	
	private void loadWeaponFile(String filename) throws BadConfigFormatException, FileNotFoundException {
		Scanner in = new Scanner(new FileReader(filename));
		String line = "";
		
		while(in.hasNextLine()) {
			line = in.nextLine();
			if(line == null) throw new BadConfigFormatException("Invalid Weapon in file " + filename);
			else addCard(new Card(line, Card.CardType.WEAPON));
		}
	}
	
	private void loadCharacterFile(String filename) throws BadConfigFormatException, FileNotFoundException{
		Scanner in = new Scanner(new FileReader(filename));
		String line = "";
		
		while(in.hasNextLine()) {
			line = in.nextLine();
			if(line == null) throw new BadConfigFormatException("Invalid Character in file " + filename);
			else addCard(new Card(line, Card.CardType.PERSON));
		}
	}

	private void loadRoomsFromBoard() {
		Collection<String> in = board.getRooms().values();
		for(String id : in) {
			addCard(new Card(id, Card.CardType.ROOM));
		}
	}
	
	private void loadPlayerFile(String filename) throws NumberFormatException, IOException, BadConfigFormatException {
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line = "";
				
		while((line  = input.readLine()) != null) {
			String[] info = line.split(", *");
			if(
					info[0] == null || 
					info[1] == null || 
					info[2] == null || 
					info[3] == null || 
					info.length != 4) 
				throw new BadConfigFormatException("Layout Legend File Invalid.");
			Color color;
			try {
			    Field field = Color.class.getField(info[3]);
			    color = (Color)field.get(null);
			} catch (Exception e) {
			    color = null; // Not defined
			}
			computerPlayers.add(new ComputerPlayer(info[0], color, Integer.parseInt(info[1]), Integer.parseInt(info[2]), this));
		}
	}
	
	public void dealCards() {
		ArrayList<Card> buffer = new ArrayList<Card>(cards);
		Random rand = new Random();
		while(buffer.size() > 0) {
			for(ComputerPlayer p : computerPlayers) {
				if(buffer.size() == 0) break;
				int r = rand.nextInt(buffer.size());
				p.giveCard(buffer.get(r));
				buffer.remove(r);
			}
			if(buffer.size() == 0) break;
			int r = rand.nextInt(buffer.size());
			humanPlayer.giveCard(buffer.get(r));
			buffer.remove(r);
		}
	}
	
	public void setSolution(String person, String weapon, String room) {
		this.solution = new Solution(person, weapon, room);
	}
	
	public Card handleSuggestion(String person, String room, String weapon, Player accusingPerson) {
		Card c1 = new Card(person, Card.CardType.PERSON);
		Card c2 = new Card(room, Card.CardType.ROOM);
		Card c3 = new Card(weapon, Card.CardType.WEAPON);
		ArrayList<Player> players = this.getPlayers();
		ArrayList<Card> possibleResponse = new ArrayList<Card>();
		int accuser = players.indexOf(accusingPerson);
		for(int i = (accuser + 1) % players.size(); i != accuser; i = (i + 1) % players.size()) {
			if(players.get(i).getCards().contains(c1)) {
				accusingPerson.seeCard(c1);
				possibleResponse.add(c1);
			}
			if(players.get(i).getCards().contains(c2)) {
				accusingPerson.seeCard(c2);
				possibleResponse.add(c2);
			}
			if(players.get(i).getCards().contains(c3)) {
				accusingPerson.seeCard(c3);
				possibleResponse.add(c3);
			}
			if(possibleResponse.size() > 0) return(possibleResponse.get(new Random().nextInt(possibleResponse.size())));
		}
		return null;
		
	}
	
	public boolean checkAccusation(Solution solution) {
		return solution.equals(this.getSolution());
	}

	public Set<Card> getCards() {
		return cards;
	}

	public ArrayList<Player> getPlayers() {
		players.clear();
		players.add(humanPlayer);
		players.addAll(computerPlayers);
		return players;
	}
	
	public ArrayList<ComputerPlayer> getComputerPlayers() {
		return computerPlayers;
	}
	
	public HumanPlayer getHumanPlayer() {
		return(humanPlayer);
	}
	
	public Solution getSolution() {
		return solution;
	}

	public Board getBoard() {
		return board;
	}
	
	public Set<Card> getWeapons() {
		return weapons;
	}

	public Set<Card> getRooms() {
		return rooms;
	}

	public Set<Card> getCharacters() {
		return characters;
	}
	
	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		this.getBoard().drawBoard(X_OFFSET, Y_OFFSET, g, this);
		
		g.setColor(Color.BLACK);
		for(Player p : players){
			p.draw(g, X_OFFSET, Y_OFFSET, PLAYER_DIMENSION, PLAYER_DIMENSION);
		}
	}
	
	public void nextPlayerPressed(DisplayPanel p) {
		
		if (currentPlayer == null) {
			currentPlayer = humanPlayer;
		}
		else {
			int nextIndex = players.indexOf(currentPlayer) + 1;
			if (nextIndex > (players.size() - 1)) {
				nextIndex = 0;
			}
		}
		rollDice();
		p.setRoll(dieRoll);
		board.startTargets(currentPlayer.getRow(), currentPlayer.getColumn(), dieRoll);
		if (currentPlayer.isHuman()) {
			board.highlightTargets();
			repaint();
			
		}
		else {
			// do computer things
			ComputerPlayer currentComputer = (ComputerPlayer)currentPlayer;
			currentComputer.updateLocation(currentComputer.pickLocation(board.getTargets()));
			// is the player in a room? do suggestion things
		}
		repaint();
		
	}
	
	private void rollDice() {
		Random rand = new Random();
		dieRoll = rand.nextInt(5) + 1;
		
	}
}
