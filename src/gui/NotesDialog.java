package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import game.ClueGame;
import game.card.Card;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

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
			for(Card c : game.getCharacters()){
				JCheckBox cb = new JCheckBox(c.getTitle());
				cb.addActionListener(new PeopleListener());
				if(game.getHumanPlayer().getCharacters().contains(c)) cb.setSelected(false);
				else cb.setSelected(true);
				this.add(cb);
			}
		}

		private class PeopleListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent a) {
				JCheckBox cb = (JCheckBox)a.getSource();
				if(cb.isSelected()) game.getHumanPlayer().seeCard(new Card(cb.getText(), Card.CardType.PERSON));
				else game.getHumanPlayer().forgetCard(new Card(cb.getText(), Card.CardType.PERSON));
				personGuessPanel.populateGuesses();
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
			populateGuesses();
		}

		private void populateGuesses() {
			comboBox.removeAllItems();
			if(game.getHumanPlayer().getCharacters().isEmpty()) comboBox.addItem("Everyone Seen");
			else for(Card c : game.getHumanPlayer().getCharacters()) comboBox.addItem(c.getTitle());
		}

		private class pGuessListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent e) {
				populateGuesses();
			}
			@Override
			public void focusLost(FocusEvent e) {}
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
			for(Card c : game.getRooms()){
				JCheckBox cb = new JCheckBox(c.getTitle());
				cb.addActionListener(new RoomListener());
				if(game.getHumanPlayer().getRooms().contains(c)) cb.setSelected(false);
				else cb.setSelected(true);
				this.add(cb);
			}
		}

		private class RoomListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent a) {
				JCheckBox cb = (JCheckBox)a.getSource();
				if(cb.isSelected()) game.getHumanPlayer().seeCard(new Card(cb.getText(), Card.CardType.ROOM));
				else game.getHumanPlayer().forgetCard(new Card(cb.getText(), Card.CardType.ROOM));
				roomGuessPanel.populateGuesses();
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
			populateGuesses();
		}

		public void populateGuesses() {
			comboBox.removeAllItems();
			if(game.getHumanPlayer().getRooms().isEmpty()) comboBox.addItem("All Rooms Seen");
			else for(Card c : game.getHumanPlayer().getRooms()) comboBox.addItem(c.getTitle());
		}

		private class rGuessListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent e) {
				populateGuesses();
			}
			@Override
			public void focusLost(FocusEvent e) {}
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
			for(Card c : game.getWeapons()){
				JCheckBox cb = new JCheckBox(c.getTitle());
				cb.addActionListener(new WeaponListener());
				if(game.getHumanPlayer().getWeapons().contains(c)) cb.setSelected(false);
				else cb.setSelected(true);
				this.add(cb);
			}
		}

		private class WeaponListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent a) {
				JCheckBox cb = (JCheckBox)a.getSource();
				if(cb.isSelected()) game.getHumanPlayer().seeCard(new Card(cb.getText(), Card.CardType.WEAPON));
				else game.getHumanPlayer().forgetCard(new Card(cb.getText(), Card.CardType.WEAPON));
				weaponGuessPanel.populateGuesses();
			}
		}
	}

	private class WeaponGuessPanel extends JPanel {
		private JComboBox<String> comboBox;
		private ClueGame game;

		public WeaponGuessPanel(ClueGame g) {
			game = g;
			this.setBorder(new TitledBorder (new EtchedBorder(), "Room Guess"));
			comboBox = new JComboBox<String>();
			this.addFocusListener(new wGuessListener());
			this.add(comboBox);
			populateGuesses();
		}

		private void populateGuesses() {
			comboBox.removeAllItems();
			if(game.getHumanPlayer().getWeapons().isEmpty()) comboBox.addItem("All Weapons Seen");
			else for(Card c : game.getHumanPlayer().getWeapons()) comboBox.addItem(c.getTitle());
		}

		private class wGuessListener implements FocusListener {
			@Override
			public void focusGained(FocusEvent e) {
				populateGuesses();
			}
			@Override
			public void focusLost(FocusEvent e) {}
		}
	}
}
