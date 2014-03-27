package gui.panel;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PlayerActionPanel extends JPanel{
	
	JButton nextPlayer;
	JButton accusation;
	
	public PlayerActionPanel(){
		this.setLayout(new GridLayout(1,2));
		this.add(nextPlayer = new JButton("Next Player"));
		this.add(accusation = new JButton("Make an Accusation"));
	}
}
