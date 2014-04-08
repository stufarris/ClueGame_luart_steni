package gui.panel;

import game.player.Player;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TurnPanel extends JPanel {
	private JTextField nameField;
	
	private JLabel turnLabel;
	
	public TurnPanel(){
		this.setLayout(new BorderLayout());		
		turnLabel = new JLabel("Whose turn?");
		nameField = new JTextField();
		nameField.setColumns(15);
		
		this.add(turnLabel, BorderLayout.NORTH);
		this.add(nameField, BorderLayout.CENTER);
		
	}
	
	public void updateTurn(Player p) {
		nameField.setText(p.getName());
	}
	
}
