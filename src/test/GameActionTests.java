package test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;

import game.ClueGame;
import game.board.cell.BoardCell;
import game.card.Card;
import game.player.ComputerPlayer;
import game.player.Player;

import org.junit.BeforeClass;
import org.junit.Test;

import game.Solution;

public class GameActionTests {
	
	private static ClueGame game;
	private static Card white, mustard, knife, rope, study, library;

	@BeforeClass
	public static void setUp() {
		game = new ClueGame();
		game.loadConfigFiles("data/card/weapon/weapons.txt", "data/Players.txt");
		game.dealCards();
		game.getBoard().calcAdjacencies();
		
		
		white = new Card("Mrs. White", Card.CardType.PERSON);
		mustard = new Card("Colonel Mustard", Card.CardType.PERSON);
		knife = new Card("Knife", Card.CardType.WEAPON);
		rope = new Card("Rope", Card.CardType.WEAPON);
		study = new Card("Study", Card.CardType.ROOM);
		library = new Card("Library", Card.CardType.ROOM);
	}
	

	@Test
	// TEST IS GOOD
	//tests that player enters a room from a walkway when that room is in its target list
	public void testUnvisitedRoomEntry() {
		ComputerPlayer testPlayer = new ComputerPlayer("Bob", Color.WHITE, 4, 4, game);
		game.getBoard().startTargets(testPlayer.getRow(), testPlayer.getColumn(), 1);
		assertEquals(testPlayer.pickLocation(game.getBoard().getTargets()), game.getBoard().getCellAt(4,3));		
	}
	
	@Test
	// TEST IS GOOD
	public void testTargetRandomSelectionWithVisitedRoom() {
		ComputerPlayer player = new ComputerPlayer("Bob", Color.WHITE, 4, 4, game);
		player.setLastRoomVisited('C');
		// Pick a location with no rooms in target, just three targets
		game.getBoard().startTargets(player.getRow(), player.getColumn(), 1);
		int loc_4_3Tot = 0;
		int loc_4_5Tot = 0;
		int loc_5_4Tot = 0;
		// Run the test 200 times
		for (int i=0; i<200; i++) {
			BoardCell selected = player.pickLocation(game.getBoard().getTargets());
			if (selected == game.getBoard().getCellAt(4, 3))
				loc_4_3Tot++;
			else if (selected == game.getBoard().getCellAt(4, 5))
				loc_4_5Tot++;
			else if (selected == game.getBoard().getCellAt(5, 4))
				loc_5_4Tot++;
			else
				fail("Invalid target selected");
		}
		// Ensure we have 200 total selections (fail should also ensure)
		assertEquals(200, loc_4_3Tot + loc_4_5Tot + loc_5_4Tot);
		// Ensure each target was selected more than once
		assertTrue(loc_4_3Tot > 10);
		assertTrue(loc_4_5Tot > 10);
		assertTrue(loc_5_4Tot > 10);							
	}
	
	
	
	@Test
	// TEST IS GOOD
	public void testTargetRandomSelection() {
		ComputerPlayer player = new ComputerPlayer("Bob", Color.WHITE, 14, 0, game);
		// Pick a location with no rooms in target, just three targets
		game.getBoard().startTargets(player.getRow(), player.getColumn(), 2);
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
	// TEST IS GOOD
	public void testDisproveOnePlayerOneMatch() {
		
		// Setup
		Card c = null;
		ComputerPlayer testPlayer = new ComputerPlayer("test", Color.WHITE, 0, 0, game);
		testPlayer.giveCard(rope);
		testPlayer.giveCard(knife);
		testPlayer.giveCard(white);
		testPlayer.giveCard(mustard);
		testPlayer.giveCard(study);
		testPlayer.giveCard(library);
		
		// Test a match with a weapon
		c = testPlayer.disproveSuggestion("g", "g", "Rope");
		assertEquals("Rope", c.getTitle());
		
		// Match with person
		c = testPlayer.disproveSuggestion("Mrs. White", "g", "g");
		assertEquals("Mrs. White", c.getTitle());
		
		// Match with room
		c = testPlayer.disproveSuggestion("g", "Study", "g");
		assertEquals("Study", c.getTitle());
		
		// Returns null
		c = testPlayer.disproveSuggestion("g", "g", "g");
		assertEquals(null, c);
	}
	
	@Test
	// TEST IS GOOD
	public void testDisproveOnePlayerMultipleMatches() {
		
		// Setup
		Card c = null;
		ComputerPlayer testPlayer = new ComputerPlayer("test", Color.WHITE, 0, 0, game);
		testPlayer.getCards().add(rope);
		testPlayer.getCards().add(knife);
		testPlayer.getCards().add(white);
		testPlayer.getCards().add(mustard);
		testPlayer.getCards().add(study);
		testPlayer.getCards().add(library);
		
		int whiteTot = 0;
		int studyTot = 0;
		int ropeTot = 0;
		
		// loop to ensure a random card is selected
		for (int i=0; i<200; i++) {
			c = testPlayer.disproveSuggestion("Mrs. White", "Study", "Rope");
			if (c.getTitle().equals("Mrs. White"))
				whiteTot++;
			else if (c.getTitle().equals("Study"))
				studyTot++;
			else if (c.getTitle().equals("Rope"))
				ropeTot++;
			else
				fail("Invalid target selected");
		}
		
		
		assertTrue(whiteTot > 10);
		assertTrue(studyTot > 10);
		assertTrue(ropeTot > 10);
	}
	
	@Test
	// TEST IS GOOD
	public void testAllPlayersQueried(){
		
		Card c = null;
		game.getHumanPlayer().getCards().add(rope);
		game.getComputerPlayers().get(0).getCards().add(knife);
		game.getComputerPlayers().get(1).getCards().add(white);
		game.getComputerPlayers().get(2).getCards().add(mustard);
		game.getComputerPlayers().get(3).getCards().add(study);
		
		// Ensure null
		c = game.handleSuggestion("g", "g", "g", game.getHumanPlayer());
		assertEquals(null, c);
		
		// Test for human
		c = game.handleSuggestion("g", "g", "Rope", game.getComputerPlayers().get(0));
		assertEquals("Rope", c.getTitle());
		
		// Person suggesting is the only one that can disprove it
		c = game.handleSuggestion("g", "g", "Knife", game.getComputerPlayers().get(0));
		assertEquals(null, c);	
		
		// Two possible answers, make sure first is returned
		c = game.handleSuggestion("Mrs. White", "g", "Knife", game.getComputerPlayers().get(3));
		assertEquals("Knife", c.getTitle());
		
		// Make sure all are queried
		c = game.handleSuggestion("g", "Library", "g", game.getHumanPlayer());
		assertEquals("Library", c.getTitle());
		
		

	}
	
	@Test
	// TEST IS GOOD
	public void testCreateSuggestionOnlyOnePossible() {
		ComputerPlayer testPlayer = new ComputerPlayer("test", Color.WHITE, 15, 18, game);
		ArrayList<Card> tempCards = new ArrayList<Card>(game.getPlayerCards());
		
		for (int i = 1; i < tempCards.size(); i++) {
			testPlayer.seeCard(tempCards.get(i));
		}
		
		String suggestionPerson = tempCards.get(0).getTitle();
		
		tempCards.clear();
		tempCards = new ArrayList<Card>(game.getWeaponCards());
		for (int i = 1; i < tempCards.size(); i++) {
			testPlayer.seeCard(tempCards.get(i));
		}
		
		String suggestionWeapon = tempCards.get(0).getTitle();
		
		Solution s = testPlayer.createSuggestion(game.getPlayerCards(), game.getWeaponCards());
		
		assertEquals(suggestionPerson, s.getPerson());
		assertEquals(suggestionWeapon, s.getWeapon());
		assertEquals("Lounge", s.getRoom());
		
	}
	
	@Test
	public void testCreateSuggestionLotsPossible() {
		ComputerPlayer testPlayer = new ComputerPlayer("test", Color.WHITE, 15, 18, game);
		ArrayList<Card> tempCards = new ArrayList<Card>(game.getPlayerCards());
		
		for (int i = 2; i < tempCards.size(); i++) {
			testPlayer.seeCard(tempCards.get(i));
		}
		
		String suggestionPerson1 = tempCards.get(0).getTitle();
		String suggestionPerson2 = tempCards.get(1).getTitle();
		
		tempCards.clear();
		tempCards = new ArrayList<Card>(game.getWeaponCards());
		for (int i = 2; i < tempCards.size(); i++) {
			testPlayer.seeCard(tempCards.get(i));
		}
		
		String suggestionWeapon1 = tempCards.get(0).getTitle();
		String suggestionWeapon2 = tempCards.get(1).getTitle();
		
		int p1Tot = 0;
		int p2Tot = 0;
		int w1Tot = 0;
		int w2Tot = 0;
		Solution s = null;
		
		for (int i = 0; i < 200; i++) {
			s = testPlayer.createSuggestion(game.getPlayerCards(), game.getWeaponCards());	
			if (s.getPerson().equals(suggestionPerson1))
				p1Tot++;
			else if (s.getPerson().equals(suggestionPerson2))
				p2Tot++;
			else
				fail("Invalid suggestion");
			if (s.getWeapon().equals(suggestionWeapon1))
				w1Tot++;
			else if (s.getWeapon().equals(suggestionWeapon2))
				w2Tot++;
			else
				fail("Invalid suggestion");
		}
		
		assertTrue(p1Tot > 10);
		assertTrue(p2Tot > 10);
		assertTrue(w1Tot > 10);
		assertTrue(w2Tot > 10);
	}
}
