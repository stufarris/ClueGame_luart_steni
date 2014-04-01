package test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;

import game.ClueGame;
import game.board.cell.BoardCell;
import game.card.Card;
import game.player.ComputerPlayer;
import game.player.Player;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameActionTests {
	
	private static ClueGame game;

	@Before
	public void setUp() {
		game = new ClueGame();
		game.loadConfigFiles("data/card/character/characters.txt", "data/card/weapon/weapons.txt", "data/Players.txt");
		game.dealCards();
		game.getBoard().calcAdjacencies();
	}
	

	@Test
	//tests that player enters a room from a walkway when that room is in its target list
	public void testUnvisitedRoomEntry() {
		ComputerPlayer testPlayer = new ComputerPlayer("Bob",Color.WHITE,4,4, game);
		game.getBoard().calcTargets(testPlayer.getRow(), testPlayer.getColumn(), 1);
		assertEquals(testPlayer.pickLocation(game.getBoard().getTargets()), game.getBoard().getCellAt(4,3));		
	}
	
	@Test
	//tests that a player will not enter the room it last visited
	public void testIgnoreVisitedRoom(){
		ComputerPlayer testPlayer = new ComputerPlayer("Bob",Color.WHITE,4,4, game);
		testPlayer.setLastRoomVisited("Conservatory");
		game.getBoard().calcTargets(testPlayer.getRow(), testPlayer.getColumn(), 1);
		assertTrue(testPlayer.pickLocation(game.getBoard().getTargets()) == game.getBoard().getCellAt(4, 3)
				|| testPlayer.pickLocation(game.getBoard().getTargets()) == game.getBoard().getCellAt(4, 5)
				|| testPlayer.pickLocation(game.getBoard().getTargets()) == game.getBoard().getCellAt(5, 4));
		
	}
	
	@Test
	public void testTargetRandomSelection() {
		ComputerPlayer player = new ComputerPlayer("Bob",Color.WHITE,14,0, game);
		// Pick a location with no rooms in target, just three targets
		game.getBoard().calcTargets(14, 0, 2);
		int loc_12_0Tot = 0;
		int loc_14_2Tot = 0;
		int loc_15_1Tot = 0;
		// Run the test 100 times
		for (int i=0; i<100; i++) {
			BoardCell selected = player.pickLocation(game.getBoard().getTargets());
			if (selected == game.getBoard().getCellAt(12, 0))
				loc_12_0Tot++;
			else if (selected == game.getBoard().getCellAt(14, 2))
				loc_14_2Tot++;
			else if (selected == game.getBoard().getCellAt(15, 1))
				loc_15_1Tot++;
			else
				fail("Invalid target selected");
		}
		// Ensure we have 100 total selections (fail should also ensure)
		assertEquals(100, loc_12_0Tot + loc_14_2Tot + loc_15_1Tot);
		// Ensure each target was selected more than once
		assertTrue(loc_12_0Tot > 10);
		assertTrue(loc_14_2Tot > 10);
		assertTrue(loc_15_1Tot > 10);							
	}
	
	@Test
	//player has one card in card to disprove a suggestion
	public void testOneCardDisprove(){
		Player p = new Player();
		p.giveCard(new Card("gun",Card.CardType.WEAPON));
		assertEquals(p.disproveSuggestion("joe", "bedroom", "gun"), new Card("gun",Card.CardType.WEAPON));
	}
	
	@Test
	//player has two cards to disprove a suggestion but picks randomly
	public void testTwoCardRandomDisprove(){
		Player p = new Player();
		p.giveCard(new Card("gun",Card.CardType.WEAPON));
		p.giveCard(new Card("gunroom",Card.CardType.ROOM));
		int gunCount = 0;
		int gunRoomCount = 0;
		for (int i=0; i<100; i++) {
			Card selected = p.disproveSuggestion("joe", "gunroom", "gun");
			if (selected.equals(new Card("gun",Card.CardType.WEAPON)))
				gunCount++;
			else if (selected.equals(new Card("gunroom",Card.CardType.ROOM)))
				gunRoomCount++;
		}
		// Ensure we have 100 total selections (fail should also ensure)
		assertEquals(100, gunCount + gunRoomCount);
		// Ensure each target was selected more than once
		assertTrue(gunCount > 10);
		assertTrue(gunRoomCount > 10);
	}
	
	@Test
	// There are 3 players. Each player has a unique Card. 
	// I will query each player in a certain order to prove the query stops
	public void testQueryOrder() {
		ArrayList<Player> players = game.getPlayers();
		Card gunroom = new Card("gunroom", Card.CardType.ROOM);
		Card gun = new Card("gun", Card.CardType.WEAPON);
		Card gunner = new Card("gunner", Card.CardType.PERSON);
		players.get(1).giveCard(gunroom);
		players.get(2).giveCard(gun);
		players.get(3).giveCard(gunner);
		
		// Tests that players are queried in a certain order
		assertEquals(gunroom, game.handleSuggestion("gunner", "gunroom", "gun", players.get(0)));
		assertEquals(gun, game.handleSuggestion("gunner", "null", "gun", players.get(0)));
		assertEquals(gunner, game.handleSuggestion("gunner", "null", "null", players.get(0)));
		
	}
	
	@Test
	// Test that a player cannot query himself
	public void testSelfQuery() {
		ArrayList<Player> players = game.getPlayers();
		Card gunroom = new Card("gunroom", Card.CardType.ROOM);
		players.get(0).giveCard(gunroom);
		
		// Tests that players are queried in a certain order
		assertTrue(null == game.handleSuggestion("gunner", "gunroom", "gun", players.get(0)));
		
	}
	
	@Test
	// Test that a computer player makes a correct suggestion
	public void testComputerCorrectSuggestion() {
		ArrayList<ComputerPlayer> players = game.getComputerPlayers();
		
		// Remove all possible weapons
		for(Card c : game.getWeapons()) {
			players.get(0).seeCard(c);
		}
		players.get(0).getWeapons().add(new ArrayList<Card>(game.getWeapons()).get(0));
		for(Card c : game.getCharacters()) {
			players.get(0).seeCard(c);
		}
		players.get(0).getCharacters().add(new ArrayList<Card>(game.getCharacters()).get(0));
		
		String[] sug = players.get(0).createSuggestion();
		assertEquals(sug[0], new ArrayList<Card>(game.getCharacters()).get(0).getTitle());
		assertEquals(sug[1], players.get(0).getLastRoomVisited());
		assertEquals(sug[2], new ArrayList<Card>(game.getWeapons()).get(0).getTitle());
	}
	
	@Test
	// Test that a computer player makes a suggestion randomly
	public void testComputerRandomSuggestion() {
		ArrayList<ComputerPlayer> players = game.getComputerPlayers();
		
		for(Card c : game.getWeapons()) {
			players.get(0).seeCard(c);
		}
		players.get(0).getWeapons().add(new ArrayList<Card>(game.getWeapons()).get(0));
		players.get(0).getWeapons().add(new ArrayList<Card>(game.getWeapons()).get(1));
		for(Card c : game.getCharacters()) {
			players.get(0).seeCard(c);
		}
		players.get(0).getCharacters().add(new ArrayList<Card>(game.getCharacters()).get(0));
		players.get(0).getCharacters().add(new ArrayList<Card>(game.getCharacters()).get(1));

		int g1 = 0;
		int g2 = 0;
		for (int i=0; i<100; i++) {
			String[] sug = ((ComputerPlayer)(players.get(0))).createSuggestion();
			if (sug[0] == new ArrayList<Card>(game.getCharacters()).get(0).getTitle())
				g1++;
			else if (sug[0] == new ArrayList<Card>(game.getCharacters()).get(1).getTitle())
				g2++;
		}
		// Ensure we have 100 total selections (fail should also ensure)
		assertEquals(100, g1 + g2);
		// Ensure each target was selected more than once
		assertTrue(g1 > 10);
		assertTrue(g2 > 10);
	}
}
