package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashSet;
import java.util.Set;

import game.ClueGame;
import game.card.Card;
import game.card.Card.CardType;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

// TODO logic for showing "All weapons seen" or "All rooms seen" or "Everyone seen" is known to be incorrect
// All dropdown boxes don't really work right, I believe the need to show NOT of what they do, also can probably
// right some functions, BAD DRY

public class NotesDialog extends JFrame {	
	PeoplePanel peoplePanel;
	RoomsPanel roomsPanel;
	WeaponsPanel weaponsPanel;

	PersonGuessPanel personGuessPanel;
	RoomGuessPanel roomGuessPanel;
	WeaponGuessPanel weaponGuessPanel;

	public NotesDialog(ClueGame g){
		this.setLayout(new GridLayout(3,2));
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setSize(new Dimension(800, 500));
		this.add(peoplePanel = new PeoplePanel(g, personGuessPanel = new PersonGuessPanel(g)));
		this.add(personGuessPanel);
		this.add(roomsPanel = new RoomsPanel(g, roomGuessPanel = new RoomGuessPanel(g)));
		this.add(roomGuessPanel);
		this.add(weaponsPanel = new WeaponsPanel(g, weaponGuessPanel = new WeaponGuessPanel(g)));
		this.add(weaponGuessPanel);
	}

	private class PeoplePanel extends JPanel{
		private ClueGame game;
		private PersonGuessPanel personGuessPanel;

		public PeoplePanel(ClueGame g, PersonGuessPanel pg) {
			this.personGuessPanel = pg;
			this.game = g;
			this.setBorder(new TitledBorder (new EtchedBorder(), "People"));
			this.setLayout(new GridLayout(3,2));
			for(Card c : game.getPlayerCards()){
				JCheckBox cb = new JCheckBox(c.getTitle());
				cb.addActionListener(new PeopleListener());
				if(game.getHumanPlayer().getSeenCards().contains(c)) cb.setSelected(true);
				else cb.setSelected(false);
				this.add(cb);
			}
		}

		private class PeopleListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent a) {
				JCheckBox cb = (JCheckBox)a.getSource();
				if(cb.isSelected()) game.getHumanPlayer().seeCard(new Card(cb.getText(), Card.CardType.PERSON));
				else game.getHumanPlayer().forgetCard(new Card(cb.getText(), Card.CardType.PERSON));
				populateGuesses(personGuessPanel.getBox(), getNotSeenCards(game), CardType.PERSON);
			}
		}
	}

	private class PersonGuessPanel extends JPanel {
		private JComboBox<String> comboBox;
		private ClueGame game;

		public PersonGuessPanel(ClueGame g) {
			this.game = g;
			this.setBorder(new TitledBorder (new EtchedBorder(), "Person Guess"));
			comboBox = new JComboBox<String>();
			this.add(comboBox);
			this.addFocusListener(new pGuessListener());
			populateGuesses(comboBox, getNotSeenCards(game), CardType.PERSON);
		}

		private class pGuessListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent e) {
				populateGuesses(comboBox, getNotSeenCards(game), CardType.PERSON);
			}
			@Override
			public void focusLost(FocusEvent e) {}
		}
		
		public JComboBox<String> getBox() {
			return this.comboBox;
		}
	}

	private class RoomsPanel extends JPanel{
		private ClueGame game;
		private RoomGuessPanel roomGuessPanel;

		public RoomsPanel(ClueGame g, RoomGuessPanel rg) {
			this.roomGuessPanel = rg;
			this.game = g;
			this.setBorder(new TitledBorder (new EtchedBorder(), "Rooms"));
			this.setLayout(new GridLayout(3,2));
			for(Card c : game.getRoomCards()){
				if (!c.getTitle().equalsIgnoreCase("walkway") && !c.getTitle().equalsIgnoreCase("closet")) {
					JCheckBox cb = new JCheckBox(c.getTitle());
					cb.addActionListener(new RoomListener());
					if(game.getHumanPlayer().getSeenCards().contains(c)) cb.setSelected(true);
					else cb.setSelected(false);
					this.add(cb);
				}
			}
		}

		private class RoomListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent a) {
				JCheckBox cb = (JCheckBox)a.getSource();
				if(cb.isSelected()) game.getHumanPlayer().seeCard(new Card(cb.getText(), Card.CardType.ROOM));
				else game.getHumanPlayer().forgetCard(new Card(cb.getText(), Card.CardType.ROOM));
				populateGuesses(roomGuessPanel.getBox(), getNotSeenCards(game), CardType.ROOM);
			}
		}
	}

	private class RoomGuessPanel extends JPanel {
		private JComboBox<String> comboBox;
		private ClueGame game;

		public RoomGuessPanel(ClueGame g) {
			game = g;
			this.setBorder(new TitledBorder (new EtchedBorder(), "Room Guess"));
			comboBox = new JComboBox<String>();
			this.addFocusListener(new rGuessListener());
			this.add(comboBox);
			populateGuesses(comboBox, getNotSeenCards(game), CardType.ROOM);
		}

		private class rGuessListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent e) {
				populateGuesses(comboBox, getNotSeenCards(game), CardType.ROOM);
			}
			@Override
			public void focusLost(FocusEvent e) {}
		}
		
		public JComboBox<String> getBox() {
			return this.comboBox;
		}
	}

	private class WeaponsPanel extends JPanel{
		private ClueGame game;
		private WeaponGuessPanel weaponGuessPanel;

		public WeaponsPanel(ClueGame g, WeaponGuessPanel wg) {
			this.weaponGuessPanel = wg;
			this.game = g;
			this.setBorder(new TitledBorder (new EtchedBorder(), "Weapons"));
			this.setLayout(new GridLayout(3,2));
			for(Card c : game.getWeaponCards()){
				JCheckBox cb = new JCheckBox(c.getTitle());
				cb.addActionListener(new WeaponListener());
				if(game.getHumanPlayer().getSeenCards().contains(c)) cb.setSelected(true);
				else cb.setSelected(false);
				this.add(cb);
			}
		}

		private class WeaponListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent a) {
				JCheckBox cb = (JCheckBox)a.getSource();
				if(cb.isSelected()) game.getHumanPlayer().seeCard(new Card(cb.getText(), Card.CardType.WEAPON));
				else game.getHumanPlayer().forgetCard(new Card(cb.getText(), Card.CardType.WEAPON));
				populateGuesses(weaponGuessPanel.getBox(), getNotSeenCards(game), CardType.WEAPON);
			}
		}
	}

	private class WeaponGuessPanel extends JPanel {
		private JComboBox<String> comboBox;
		private ClueGame game;

		public WeaponGuessPanel(ClueGame g) {
			game = g;
			this.setBorder(new TitledBorder (new EtchedBorder(), "Weapon Guess"));
			comboBox = new JComboBox<String>();
			this.addFocusListener(new wGuessListener());
			this.add(comboBox);
			populateGuesses(comboBox, getNotSeenCards(game), CardType.WEAPON);
		}

		private class wGuessListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent e) {
				populateGuesses(comboBox, getNotSeenCards(game), CardType.WEAPON);
			}
			@Override
			public void focusLost(FocusEvent e) {}
		}
		
		public JComboBox<String> getBox() {
			return this.comboBox;
		}
	}
	
	public void populateGuesses(JComboBox<String> box, Set<Card> cards, CardType type) {
		box.removeAllItems();
		if(cards.isEmpty()) box.addItem("All Seen");
		else {
			for(Card c : cards) {
				if (c.getType() == type) {
					if (!c.getTitle().equalsIgnoreCase("walkway") && !c.getTitle().equalsIgnoreCase("closet")) {
						box.addItem(c.getTitle());
					}
				}
			}
		}
	}
	
	public Set<Card> getNotSeenCards(ClueGame game) {
		Set<Card> notSeenCards = new HashSet<Card>(game.getCards());
		notSeenCards.removeAll(game.getHumanPlayer().getSeenCards());
		return notSeenCards;
	}
}
