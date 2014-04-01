package gui;


import game.ClueGame;
import game.card.Card;
import gui.panel.DisplayPanel;
import gui.panel.PlayerActionPanel;
import gui.panel.TurnPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class ControlFrame extends JFrame {
	private static ClueGame game;
	private JPanel uiPanel, cardsPanel;
	private JMenuBar menu;

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 690;

	public ControlFrame() {
		game = new ClueGame();
		game.loadConfigFiles("data/card/character/characters.txt", "data/card/weapon/weapons.txt", "data/Players.txt");
		game.getPlayers();
		game.dealCards();
		this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		this.setTitle("The Game of Clue");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		uiPanel = new JPanel();
		uiPanel.setLayout(new BorderLayout());
		uiPanel.setSize(new Dimension(800, 30));
		uiPanel.add(new TurnPanel(), BorderLayout.WEST);
		uiPanel.add(new PlayerActionPanel(), BorderLayout.EAST);
		
		createCardsPanel();

		this.add(new DisplayPanel(), BorderLayout.SOUTH);
		this.add(uiPanel, BorderLayout.NORTH);
		this.add(game, BorderLayout.CENTER);
		this.add(cardsPanel, BorderLayout.EAST);
		menu = new JMenuBar();
		this.setJMenuBar(menu);
		menu.add(createFileMenu());
		
		//splash screen
		JOptionPane.showMessageDialog(null, "You are " + game.getHumanPlayer().getName() + ", press Next Player to begin play!");

	}
	
	private void createCardsPanel() {
		cardsPanel = new JPanel();
		cardsPanel.setLayout(new GridLayout(0, 1));
		cardsPanel.setSize(new Dimension(50, 400));
		cardsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Cards:"));
		Vector<String> cardNames = new Vector<String>();
		for (Card c : game.getHumanPlayer().getCards()) {
			cardNames.add(c.getTitle());
		}
		JList<String> cardList = new JList<String>(cardNames);
		cardsPanel.add(cardList);
	}

	private JMenu createFileMenu()
	{
		JMenu menu = new JMenu("File"); 
		menu.add(createNotesDialogItem());
		menu.add(createExitItem());
		return menu;
	}

	private JMenuItem createNotesDialogItem()
	{
		JMenuItem item = new JMenuItem("Show Detective Notes");
		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e)
			{
				NotesDialog gui = new NotesDialog(game);
				gui.setVisible(true);
				gui.setVisible(true);
				gui.setResizable(false);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
	
	private JMenuItem createExitItem(){
		JMenuItem item = new JMenuItem("Exit");
		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
		
	}
	
	public static void main(String[] args) {
		ControlFrame gui = new ControlFrame();
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
		gui.setResizable(false);
	}
}
