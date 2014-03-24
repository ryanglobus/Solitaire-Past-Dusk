package com.ryanglobus.solitaire.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.ryanglobus.solitaire.util.FixedSizeList;

public class Board implements Serializable {
	private static final long serialVersionUID = 1927703440431954518L;
	
	public static final int NUM_FREE_CELLS = 4;
	public static final int NUM_HOME_CELLS = 4;
	public static final int NUM_TABLEAU_COLS = 8;
	
	private List<Card> freeCells = new FixedSizeList<Card>(NUM_FREE_CELLS);
	private List<Stack<Card>> homeCells;
	private List<Stack<Card>> tableau;
	private Map<Card, Location> cardLocMap = new HashMap<Card, Location>();
	
	Board() {
		homeCells = new FixedSizeList<Stack<Card>>(NUM_HOME_CELLS);
		for (int i = 0; i < homeCells.size(); i++) {
			homeCells.set(i, new Stack<Card>());
		}
		tableau = new FixedSizeList<Stack<Card>>(NUM_TABLEAU_COLS);
		for (int i = 0; i < tableau.size(); i++) {
			tableau.set(i, new Stack<Card>());
		}
		newGame();
	}
	
	private void newGame() {
		// make deck
		List<Card> deck = new ArrayList<Card>();
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
					deck.add(new Card(suit, rank));
			}
		}
		Collections.shuffle(deck);
		
		int column = 0;
		for (Card card : deck) {
			tableau.get(column).push(card);
			cardLocMap.put(card, new Location(Location.Type.TABLEAU, column));
			column = (column + 1) % NUM_TABLEAU_COLS;
		}
	}
	
	private int numEmptyFreeCells() {
		int numEmptyCells = 0;
		for (Card c : freeCells) {
			if (c == null) numEmptyCells++;
		}
		return numEmptyCells;
	}
	
	private int numEmptyTableauCols() {
		int numEmptyCells = 0;
		for (Stack<Card> stack : tableau) {
			if (stack.isEmpty()) numEmptyCells++;
		}
		return numEmptyCells;
	}
	
	public boolean canSelect(Card card, boolean destIsEmptyTableau) {
		if (getLocationOf(card).isHome()) {
			return cardsOnTopOf(card).size() == 1;
		}
		int numCardsCanSelect = 1 + numEmptyFreeCells();
		int numEmptyTableauCols = numEmptyTableauCols();
		// can't use an empty tableau if it is the final destination
		if (destIsEmptyTableau) numEmptyTableauCols--;
		for (int i = 0; i < numEmptyTableauCols; i++) {
			numCardsCanSelect *= 2;
		}
			
		Stack<Card> stack = cardsOnTopOf(card);
		if (stack.size() > numCardsCanSelect) return false;
		Card prevCard = null;
		while (!stack.isEmpty()) {
			Card currCard = stack.pop();
			if (prevCard != null) {
				if (prevCard.getRank().getValue() != currCard.getRank().getValue() + 1 ||
						prevCard.isSameColor(currCard))
					return false;
			}
			prevCard = currCard;
		}
		return true;
	}
	
	boolean isLegal(Move move) {
		Location start = move.getStart();
		Location end = move.getEnd();
		if (start.equals(end)) return false;
		int numCards = move.getNumCards();
		
		if (start.isHome() && numCards != 1) return false;
		Card selectedCard = getSelectedCard(start, numCards);
		boolean endIsEmptyTableau = end.isTableau() &&
				tableau.get(end.getIndex()).isEmpty();
		if (selectedCard == null ||
			!canSelect(selectedCard, endIsEmptyTableau)) return false;
		
		if (end.isFree()) {
			if (!start.isTableau()) return false;
			Stack<Card> startCards = tableau.get(start.getIndex());
			return startCards.size() > 0 && numCards == 1 &&
					freeCells.get(end.getIndex()) == null;
		} else if (end.isTableau()) {
			Stack<Card> endCol = tableau.get(end.getIndex());
			if (endCol.isEmpty()) return true;
			Card endCard = endCol.peek();
			return endCard.getRank().getValue() == selectedCard.getRank().getValue() + 1 &&
					!endCard.isSameColor(selectedCard);
		} else { // end is home
			if (numCards != 1) return false;
			Stack<Card> homeCol = homeCells.get(end.getIndex());
			if (homeCol.isEmpty()) return selectedCard.getRank().equals(Rank.lowestRank());
			Card topCard = homeCol.peek();
			return topCard.getRank().getValue() == selectedCard.getRank().getValue() - 1 &&
					topCard.getSuit().equals(selectedCard.getSuit());
		}
	}
	
	public Card movedCard(Move move) {
		Stack<Card> movedCards = movedCards(move);
		if (movedCards.isEmpty()) return null;
		else return movedCards(move).peek();
	}
	
	/**
	 * movedCard is on the top - the top card is on the bottom
	 * @param move
	 * @return
	 * @throws
	 */
	public Stack<Card> movedCards(Move move) {
		Location start = move.getStart();
		// remove cards from starting position
		Stack<Card> movedCards = new Stack<Card>();
		if (start.isTableau()) {
			List<Card> col = tableau.get(start.getIndex());
			for (int i = 0; i < move.getNumCards(); i++) {
				Card c = col.get(col.size() - i - 1);
				movedCards.push(c);
			}
		} else if (start.isFree()) {
			Card c = freeCells.get(start.getIndex());
			movedCards.push(c);
		} else if (start.isHome()) {
			List<Card> home = homeCells.get(start.getIndex());
			for (int i = 0; i < move.getNumCards(); i++) {
				Card c = home.get(home.size() - i - 1);
				movedCards.push(c);
			}
		}
		return movedCards;
	}
	
	/**
	 * Does NOT check move's legality
	 * @param move
	 */
	void execute(Move move) {
		Location start = move.getStart();
		Location end = move.getEnd();
		// remove cards from starting position
		// don't use movedCards method - hard to modify after the fact
		Stack<Card> cardsToMove = new Stack<Card>();
		if (start.isTableau()) {
			for (int i = 0; i < move.getNumCards(); i++) {
				Card c = tableau.get(start.getIndex()).pop();
				cardLocMap.remove(c);
				cardsToMove.push(c);
			}
		} else if (start.isFree()) {
			Card c = freeCells.get(start.getIndex());
			freeCells.set(start.getIndex(), null);
			cardLocMap.remove(c);
			cardsToMove.push(c);
		} else if (start.isHome()) {
			for (int i = 0; i < move.getNumCards(); i++) {
				Card c = homeCells.get(start.getIndex()).pop();
				cardLocMap.remove(c);
				cardsToMove.push(c);
			}
		}

		// add cards to end position
		if (end.isFree()) {
			Card c = cardsToMove.pop();
			cardLocMap.put(c, end);
			freeCells.set(end.getIndex(), c); 
		} else if (end.isTableau()) {
			Stack<Card> endCol = tableau.get(end.getIndex());
			while (!cardsToMove.isEmpty()) {
				Card c = cardsToMove.pop();
				cardLocMap.put(c, end);
				endCol.push(c);
			}
		} else { // to home
			Card c = cardsToMove.pop();
			cardLocMap.put(c, end);
			Stack<Card> endCol = homeCells.get(end.getIndex());
			endCol.push(c);
		}
	}
	
	private Card getSelectedCard(Location loc, int numCards) {
		try {
			if (loc.isFree()) {
				return freeCells.get(loc.getIndex());
			} else if (loc.isTableau()) {
				Stack<Card> column = tableau.get(loc.getIndex());
				return column.get(column.size() - numCards);
			} else if (loc.isHome()) {
				Stack<Card> home = homeCells.get(loc.getIndex());
				return home.get(home.size() - numCards);
			} else {
				return null;
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Read-only
	 * @return
	 */
	public Set<Card> getCards() {
		return Collections.unmodifiableSet(cardLocMap.keySet());
	}
	
	public Location getLocationOf(Card card) {
		return cardLocMap.get(card);
	}
	
	/**
	 * card is on top of the stack - the top card is on the bottom
	 * @param card
	 * @return
	 */
	public Stack<Card> cardsOnTopOf(Card card) {
		Stack<Card> cardsOnTop = new Stack<Card>();
		Location loc = getLocationOf(card);
		if (loc.isFree()) {
			cardsOnTop.push(card);
			return cardsOnTop;
		}
		Stack<Card> stackCardIsIn;
		if (loc.isHome()) {
			stackCardIsIn = homeCells.get(loc.getIndex());
		} else {
			stackCardIsIn = tableau.get(loc.getIndex());
		}
		// don't use Stack iterator since it iterates through FIFO (bad design)
		for (int i = stackCardIsIn.size() - 1; i >= 0; i--) {
			Card c = stackCardIsIn.get(i);
			cardsOnTop.push(c);
			if (card.equals(c)) break;
		}
		return cardsOnTop;
	}

	public List<Card> readFreeCells() {
		return Collections.unmodifiableList(freeCells);
	}

	public List<Stack<Card>> readHomeCells() {
		return Collections.unmodifiableList(homeCells);
	}

	public List<Stack<Card>> readTableau() {
		return Collections.unmodifiableList(tableau);
	}
}
