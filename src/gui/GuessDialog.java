package gui;

import game.ClueGame;
import game.Solution;
import game.board.Board;
import game.board.cell.BoardCell;
import game.board.cell.RoomCell;
import game.card.Card;
import game.player.ComputerPlayer;
import game.player.HumanPlayer;
import game.player.Player;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GuessDialog extends JDialog{

	private JTextField name;
	private JComboBox<String> personDropdown, weaponDropdown;
	
	private Solution s;

	public GuessDialog(ClueGame game, HumanPlayer humanPlayer) {
		setTitle("Make a guess");
		setSize(300, 200);
		this.setLayout(new GridLayout(4,2));
		
		JLabel roomLabel = new JLabel("Your Room");
		JLabel personLabel = new JLabel("Person");
		JLabel weaponLabel = new JLabel("Weapon");
		JButton submitButton = new JButton("Submit");
		JButton cancelButton = new JButton("Cancel");
		personDropdown = new JComboBox<String>();
		for (Card c : game.getPlayerCards()) {
			personDropdown.addItem(c.getTitle());
		}
		weaponDropdown = new JComboBox<String>();
		for (Card c : game.getWeaponCards()) {
			weaponDropdown.addItem(c.getTitle());
		}
		RoomCell currentCell = (RoomCell)game.getBoard().getCellAt(humanPlayer.getRow(), humanPlayer.getColumn());
		JLabel room = new JLabel(game.getBoard().getRooms().get(currentCell.getInitial()));
		
		add(roomLabel);
		add(room);
		add(personLabel);
		add(personDropdown);
		add(weaponLabel);
		add(weaponDropdown);
		add(submitButton);
		add(cancelButton);
	}
	

}
