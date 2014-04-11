package gui.panel;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class DisplayPanel extends JPanel{
	private int rollCount;
	private String guessText = "", responseText = "";
	private final String rollPrefix = "Roll: ", guessPrefix = "Guess: ", responsePrefix = "Response: ";
	
	private JTextField rollField, guessField, responseField;
	
	public DisplayPanel() {
		this.setLayout(new GridLayout(2, 1));
		JPanel rollPanel = new JPanel(new GridLayout(1, 2));
		rollPanel.add(rollField = new JTextField(rollPrefix));
		rollPanel.add(responseField = new JTextField(responsePrefix), BorderLayout.EAST);
		this.add(rollPanel);
		this.add(guessField = new JTextField(guessPrefix));
		
		rollField.setEditable(false);
		guessField.setEditable(false);
		responseField.setEditable(false);
		
		rollField.setColumns(10);
		guessField.setColumns(19);
		responseField.setColumns(19);
		
		updateDisplay();
	}
	
	private void updateDisplay() {
		rollField.setText(rollPrefix + rollCount);
		guessField.setText(guessPrefix + guessText);
		responseField.setText(responsePrefix + responseText);
	}
	
	public void setRoll(int roll) {
		this.rollCount = roll;
		this.updateDisplay();
	}

	public void setGuess(String guess) {
		this.guessText = guess;
		this.updateDisplay();
	}

	public void setResponse(String response) {
		this.responseText = response;
		this.updateDisplay();
	}
}
