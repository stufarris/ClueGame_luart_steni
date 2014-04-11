package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import game.board.Board;
import game.board.cell.BoardCell;
import game.board.cell.RoomCell;
import game.card.Card;
import game.card.Card.CardType;
import game.player.ComputerPlayer;
import game.player.HumanPlayer;
import game.player.Player;
import gui.ControlFrame;
import gui.GuessDialog;
import gui.panel.DisplayPanel;

public class ClueGame extends JPanel{

	private static final long serialVersionUID = 7898999844979750592L;
	private Set<Card> cards;
	private Set<Card> weaponCards;
	private Set<Card> roomCards;
	private Set<Card> playerCards;
	
	private ArrayList<ComputerPlayer> computerPlayers;
	private HumanPlayer humanPlayer;
	private ArrayList<Player> players;
	private Player currentPlayer;
	
	private Solution solution;
	private Board board;
	
	private int dieRoll;
	private boolean humanMustFinish;
	
	private DisplayPanel displayPanel;
	
	private static final int BOARD_DIMENSION = 650;
	private static final int CLICK_Y_OFFSET = 15;
	
	private static final int HUMAN_START_ROW = 19;
	private static final int HUMAN_START_COLUMN = 16;
	private static final int PLAYER_DIMENSION = 25;
	private static final int X_OFFSET = 5;
	private static final int Y_OFFSET = 15;
	
	public ClueGame(DisplayPanel p) {
		this.setPreferredSize(new Dimension(BOARD_DIMENSION, BOARD_DIMENSION));
		this.setSize(new Dimension(BOARD_DIMENSION, BOARD_DIMENSION));
		this.setBorder(new TitledBorder (new EtchedBorder(), "Game Board"));
		this.addMouseListener(new HumanMoveListener(this));
		
		displayPanel = p;
		cards = new HashSet<Card>();
		computerPlayers = new ArrayList<ComputerPlayer>();
		players = new ArrayList<Player>();
		weaponCards = new HashSet<Card>();
		roomCards = new HashSet<Card>();
		playerCards = new HashSet<Card>();
		currentPlayer = null;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void loadConfigFiles(String weaponFilename, String playerFilename) {
		board = new Board("data/board/ClueLayout.csv", "data/board/ClueLegend.txt");
		board.loadConfigFiles();
		try {
			loadRoomsFromBoard();
			loadWeaponFile(weaponFilename);
			loadPlayerFile(playerFilename);
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
		if(c.getType() == Card.CardType.WEAPON) weaponCards.add(c);
		else if(c.getType() == Card.CardType.PERSON) playerCards.add(c);
		else if(c.getType() == Card.CardType.ROOM) roomCards.add(c);
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

	private void loadRoomsFromBoard() {
		Collection<String> in = board.getRooms().values();
		for(String id : in) {
			addCard(new Card(id, Card.CardType.ROOM));
		}
	}
	
	private void loadPlayerFile(String filename) throws NumberFormatException, IOException, BadConfigFormatException {
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line = "";
		int count = 0;
				
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
			addCard(new Card(info[0], Card.CardType.PERSON));
			if(count == 0){
				humanPlayer = new HumanPlayer(info[0], color, Integer.parseInt(info[1]), Integer.parseInt(info[2]));
			}
			else{
				computerPlayers.add(new ComputerPlayer(info[0], color, Integer.parseInt(info[1]), Integer.parseInt(info[2]), this));
			}
			count++;
		}
	}
	
	public void dealCards() {
		createSolution();
		ArrayList<Card> buffer = new ArrayList<Card>(cards);
		buffer.remove(new Card(solution.getPerson(), CardType.PERSON));
		buffer.remove(new Card(solution.getWeapon(), CardType.WEAPON));
		buffer.remove(new Card(solution.getRoom(), CardType.ROOM));
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
	
	public void createSolution() {
		ArrayList<Card> weapons = new ArrayList<Card>(weaponCards);
		ArrayList<Card> players = new ArrayList<Card>(playerCards);
		ArrayList<Card> rooms = new ArrayList<Card>(roomCards);
		setSolution(players.get(new Random().nextInt(players.size())).getTitle(),
				weapons.get(new Random().nextInt(weapons.size())).getTitle(),
				rooms.get(new Random().nextInt(rooms.size())).getTitle());
		System.out.println("Solution person is: " + solution.getPerson());
		System.out.println("Solution weapon is: " + solution.getWeapon());
		System.out.println("Solution room is: " + solution.getRoom());
	}
	
	public void setSolution(String person, String weapon, String room) {
		this.solution = new Solution(person, weapon, room);
	}
	
	public Card handleSuggestion(String person, String room, String weapon, Player accusingPerson) {
		Card c = null;
		ArrayList<Player> players = this.getPlayers();
		int accuserIndex = players.indexOf(accusingPerson);
		int currentIndex = accuserIndex + 1;
		if (currentIndex > players.size() - 1) currentIndex = 0;
		while (currentIndex != accuserIndex) {
			c = players.get(currentIndex).disproveSuggestion(person, room, weapon);
			if (c != null) {
				return c;
			}
			currentIndex++;
			if (currentIndex > players.size() - 1) currentIndex = 0;
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
	
	public Set<Card> getWeaponCards() {
		return weaponCards;
	}

	public Set<Card> getRoomCards() {
		return roomCards;
	}

	public Set<Card> getPlayerCards() {
		return playerCards;
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
	
	public void nextPlayerPressed() {

		if (!humanMustFinish) {

			if (currentPlayer == null) {
				currentPlayer = humanPlayer;
			}
			else {
				int nextIndex = players.indexOf(currentPlayer) + 1;
				if (nextIndex > (players.size() - 1)) {
					nextIndex = 0;
				}
				currentPlayer = players.get(nextIndex);
			}
			rollDice();
			displayPanel.setRoll(dieRoll);
			board.startTargets(currentPlayer.getRow(), currentPlayer.getColumn(), dieRoll);
			if (currentPlayer.isHuman()) {
				board.highlightTargets();
				humanMustFinish = true;
				repaint();
			}
			else {
				// do computer things
				ComputerPlayer currentComputer = (ComputerPlayer)currentPlayer;

				//ready to make accusation?
				if(currentComputer.getReadyToAccuse() && currentComputer.getLastGuess() == this.getSolution()){
					//game over computer player wins
					gameWon(currentComputer);
				}
				//move computer player
				currentComputer.updateLocation(currentComputer.pickLocation(board.getTargets()));
				// is the player in a room? do suggestion things
				if(board.getCellAt(currentComputer.getRow(), currentComputer.getColumn()).isDoorway()){
					Solution s = currentComputer.createSuggestion(playerCards, weaponCards);
					Card c = handleSuggestion(s.getPerson(), s.getRoom(), s.getWeapon(), currentComputer);
					if(c == null){
						displayPanel.setResponse("");
						currentComputer.setReadyToAccuse(true);
					}
					else{
						for(ComputerPlayer player : computerPlayers){
							player.seeCard(c);
						}
						displayPanel.setGuess("Was it " + s.getPerson() + " with the " + s.getWeapon() + " in the " + s.getRoom() + "?");
						displayPanel.setResponse(c.getTitle());
					}
					currentComputer.setLastGuess(s);
				}
			}
		}

		repaint();

	}
	
	private void rollDice() {
		Random rand = new Random();
		dieRoll = rand.nextInt(5) + 1;
		
	}
	
	private void gameWon(Player p) {
		JOptionPane.showInternalMessageDialog(null, p.getName() + " has won the game.");
	}
	
	private class GuessDialog extends JDialog {

		private JTextField name;
		private JComboBox<String> personDropdown, weaponDropdown;

		public GuessDialog() {
			setTitle("Make a guess");
			setSize(300, 200);
			this.setLayout(new GridLayout(4,2));
			
			JLabel roomLabel = new JLabel("Your Room");
			JLabel personLabel = new JLabel("Person");
			JLabel weaponLabel = new JLabel("Weapon");
			JButton submitButton = new JButton("Submit");
			submitButton.addActionListener(new SubmitButtonListener());
			JButton cancelButton = new JButton("Cancel");
			personDropdown = new JComboBox<String>();
			for (Card c : getPlayerCards()) {
				if (c.getTitle() != getHumanPlayer().getName()) {
					personDropdown.addItem(c.getTitle());
				}
			}
			weaponDropdown = new JComboBox<String>();
			for (Card c : getWeaponCards()) {
				weaponDropdown.addItem(c.getTitle());
			}
			RoomCell currentCell = (RoomCell)getBoard().getCellAt(humanPlayer.getRow(), humanPlayer.getColumn());
			JLabel room = new JLabel(getBoard().getRooms().get(currentCell.getInitial()));
			
			add(roomLabel);
			add(room);
			add(personLabel);
			add(personDropdown);
			add(weaponLabel);
			add(weaponDropdown);
			add(submitButton);
			add(cancelButton);
		}
		
		private class SubmitButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				RoomCell r = (RoomCell)board.getCellAt(humanPlayer.getRow(), humanPlayer.getColumn());
				String roomName = board.getRooms().get(r.getInitial());
				String weaponName = (String)weaponDropdown.getSelectedItem();
				String personName = (String)personDropdown.getSelectedItem();
				Card c = handleSuggestion(personName, roomName, weaponName, humanPlayer);
				displayPanel.setGuess("Was it " + personName + " with the " + weaponName + " in the " + roomName + "?");
				if (c != null) {
					displayPanel.setResponse(c.getTitle());
				} else {
					displayPanel.setResponse("");
				}
				setVisible(false);
				dispose();
			}
		}
		

	}
	
	private class HumanMoveListener implements MouseListener {
		
		private ClueGame game;
		
		public HumanMoveListener(ClueGame game) {
			this.game = game;
		}

		@Override
		public void mouseClicked(MouseEvent event) {
			int clickX = event.getX();
			int clickY = event.getY() - CLICK_Y_OFFSET;
			boolean validClick = false;
			for (BoardCell b : board.getTargets()) {
				if (b.isClicked(clickX, clickY)) {
					currentPlayer.updateLocation(b);
					humanMustFinish = false;
					board.clearHighlights();
					repaint();
					validClick = true;
					//make a suggestion
					if(board.getCellAt(humanPlayer.getRow(), humanPlayer.getColumn()).isDoorway()){
						GuessDialog gDiag = new GuessDialog();
						gDiag.setVisible(true);
						gDiag.setResizable(false);
					}
				}
			}
			if (!validClick) {
				JOptionPane.showMessageDialog(null, "Invalid location clicked!");
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {	
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}
		
}
