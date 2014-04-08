package test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;

import game.ClueGame;
import game.Solution;
import game.card.Card;
import game.player.*;


public class ClueGameTest {
	
	private static ClueGame game;
	
	// Sets up the GlueGame object and deals the cards to the players in the ClueGame player list
	@BeforeClass
	public static void setUp() {
		game = new ClueGame();
		game.loadConfigFiles("data/card/character/characters.txt", "data/card/weapon/weapons.txt", "data/Players.txt");
		game.dealCards();
	}
	
	// Tests to insure all the players are loaded and have the correct properties
	@Test
	public void testLoadingPlayers() {
		ArrayList<Player> players = game.getPlayers();
	
		assertTrue(players.contains(new ComputerPlayer("Darth Vader", Color.BLUE, 1, 5, game)));
		assertTrue(players.contains(new ComputerPlayer("Storm Trooper", Color.GREEN, 15, 1, game)));
		assertTrue(players.contains(new ComputerPlayer("Obi-Wan Kenobi", Color.CYAN, 19, 7, game)));
		assertTrue(players.contains(new ComputerPlayer("Hans Solo", Color.ORANGE, 6, 5, game)));
		assertTrue(players.contains(new HumanPlayer("Luke Skywalker", Color.RED, 6, 3, game)));
	}
	
	// Tests loading characters from the character file to insure they have the correct names and are PERSON cards
	@Test
	public void testLoadingPlayerCards() {
		Set<Card> cards = game.getCards();
		assertTrue(cards.contains(new Card("Luke Skywalker", Card.CardType.PERSON)));
		assertTrue(cards.contains(new Card("Darth Vader", Card.CardType.PERSON)));
		assertTrue(cards.contains(new Card("Storm Trooper", Card.CardType.PERSON)));
		assertTrue(cards.contains(new Card("Obi-Wan Kenobi", Card.CardType.PERSON)));
		assertTrue(cards.contains(new Card("Hans Solo", Card.CardType.PERSON)));
	}
	
	// Tests loading weapons from the weapon file to insure they have the correct names and are WEAPON cards
	@Test
	public void testLoadingWeapons() {
		Set<Card> cards = game.getCards();
		assertTrue(cards.contains(new Card("Green Lightsaber", Card.CardType.WEAPON)));
		assertTrue(cards.contains(new Card("Red Lightsaber", Card.CardType.WEAPON)));
		assertTrue(cards.contains(new Card("BlasTech E-11 Blaster Rifle", Card.CardType.WEAPON)));
		assertTrue(cards.contains(new Card("The Force", Card.CardType.WEAPON)));
		assertTrue(cards.contains(new Card("DL-44 Blaster Pistol", Card.CardType.WEAPON)));
		assertTrue(cards.contains(new Card("Bowcaster", Card.CardType.WEAPON)));
	}
	
	// Tests loading rooms from the loaded Board instance variable. It compares the cards in the list to the room list stored by the board
	public void testLoadingRooms() {
		Set<Card> cards = game.getCards();
		String[] rooms = (String[])game.getBoard().getRooms().values().toArray();
		
		// This loop iterates through the room list provided by the game board and compares it to the set of cards in the ClueGame class
		for(String id : rooms) {
			assertTrue(cards.contains(new Card(id, Card.CardType.ROOM)));
		}
	}
	
	// This test checks to make sure the deck of cards is the correct size
	@Test
	public void testCompleteDeck() {
		Assert.assertEquals(game.getCards().size(), 22);
	}
	
	// This test iterates through the deck of cards and checks that only one player has the card, If the card has not been dealt or been redealt the test fails
	@Test
	public void testDealingCards() {
		int count = 0;
		for(Card card : game.getCards()) {
			for(Player player : game.getPlayers()) {
				if(player.hasCard(card)) count++;
			}
			Assert.assertEquals(count, 1);
			count = 0;
		}
	}
	
	// This test uses integer division to approximate the lower value for how many cards a player could have.
	// Since the only other possibility is one greater that than the low value it also allows one above the low value.
	// Therefore if a player doesnot have the low value or one greater than the low value, the test fails
	@Test
	public void testPlayerDealtCardAmount() {
		int low = game.getCards().size() / game.getPlayers().size();
		int totalCards = 0;
		
		for(Player player : game.getPlayers()) {
			totalCards += player.getCards().size();
			assertTrue(player.getCards().size() == low || player.getCards().size() == (low + 1));
		}
		assertEquals(totalCards, game.getCards().size());
	}
	
	// This test checks that a correct Accusation matches the Solution
	@Test
	public void testCorrectAccusation() {
		game.setSolution("Darth Vader", "Green Lightsaber", "Library");
		assertTrue(game.checkAccusation(new Solution("Darth Vader", "Green Lightsaber", "Library")));
	}
	
	// This test checks that and incorrect accusation for the PERSON does not match the solution
	@Test
	public void testWrongPersonAccusation() {
		game.setSolution("Darth Vader", "Green Lightsaber", "Library");
		assertFalse(game.checkAccusation(new Solution("Luke Skywalker", "Green Lightsaber", "Library")));
	}
	
	// This test checks that and incorrect accusation for the WEAPON does not match the solution
	@Test
	public void testWrongWeaponAccusation() {
		game.setSolution("Darth Vader", "Green Lightsaber", "Library");
		assertFalse(game.checkAccusation(new Solution("Darth Vader", "Red Lightsaber", "Library")));
	}
	
	// This test checks that and incorrect accusation for the ROOM does not match the solution
	@Test
	public void testWrongRoomAccusation() {
		game.setSolution("Darth Vader", "Green Lightsaber", "Library");
		assertFalse(game.checkAccusation(new Solution("Darth Vader", "Green Lightsaber", "Dining Room")));
	}
}
