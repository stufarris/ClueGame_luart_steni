package gui;

import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GuessDialog extends JDialog{

	private JTextField name;

	public GuessDialog() {
		setTitle("Make a guess");
		setSize(300, 200);
	}

}
