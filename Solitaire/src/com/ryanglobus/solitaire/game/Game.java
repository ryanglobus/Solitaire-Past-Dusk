package com.ryanglobus.solitaire.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

public class Game implements Serializable {
	private static final long serialVersionUID = 406966463644307683L;
	private Board board = new Board();
	// TODO if move card more than once, only add one move to moves stack
	private Stack<Move> moves = new Stack<Move>();
	private Stack<Move> undoneMoves = new Stack<Move>();
	private boolean hasWon = false;
	
	public Game() {
		// do nothing
	}
	
	public boolean isLegal(Move move) {
		return board.isLegal(move);
	}
	
	private void execute(Move move, boolean clearUndoneMoves) {
		if (!isLegal(move))
			throw new IllegalArgumentException("Move is illegal.");
		board.execute(move);
		moves.push(move);
		if (clearUndoneMoves) undoneMoves.clear();
	}
	
	public void execute(Move move) {
		execute(move, true);
	}
	
	// returns the move execute (the reverse of the move undone), or null
	// if no move is undone
	public boolean undo() {
		Move move = undoMove();
		if (move == null) return false;
		board.execute(move);
		moves.pop();
		undoneMoves.push(move.reverseMove());
		return true;
	}
	
	public Move undoMove() {
		if (moves.isEmpty()) return null;
		Move move = moves.peek();
		return move.reverseMove();
	}
	
	public boolean redo() {
		Move move = redoMove();
		if (move == null) return false;
		execute(redoMove(), false);
		undoneMoves.pop();
		return true;
	}
	
	public Move redoMove() {
		if (undoneMoves.isEmpty()) return null;
		return undoneMoves.peek();
	}
	
	public boolean inWinState() {
		for (Stack<Card> homeStack : board.readHomeCells()) {
			if (homeStack.isEmpty() || !homeStack.peek().getRank().isHighestRank()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean simplePathToWinExists() {
		for (Stack<Card> col : board.readTableau()) {
			Card prev = null;
			for (Card curr : col) {
				if (prev != null &&
						prev.getRank().getValue() <= curr.getRank().getValue()) {
					// rank strictly greater than is limiting, but makes
					// finding path easier
					return false;
				}
				prev = curr;
			}
		}
		return true;
	}
	
	public List<Move> simplePathToWin() {
		if (!simplePathToWinExists()) return null;
		Map<Suit, Location> suitHomeMap = new HashMap<Suit, Location>();
		PriorityQueue<Card> nextCardQueue = new PriorityQueue<Card>(4, new Comparator<Card>() {
			@Override
			public int compare(Card lhs, Card rhs) {
				if (lhs.getRank().getValue() < rhs.getRank().getValue())
						return -1;
				else if (lhs.getRank().getValue() > rhs.getRank().getValue())
					return +1;
				else return lhs.getSuit().compareTo(rhs.getSuit());
			}
		});
		List<Integer> freeHomeIndices = new ArrayList<Integer>();
		List<Stack<Card>> homeCells = board.readHomeCells();
		// add suits in home
		for (int i = 0; i < homeCells.size(); i++) {
			Stack<Card> home = homeCells.get(i);
			if (!home.isEmpty()) {
				Card topCard = home.peek();
				suitHomeMap.put(topCard.getSuit(), board.getLocationOf(topCard));
				Card nextCard = topCard.nextRank();
				if (nextCard != null) nextCardQueue.add(nextCard);
			} else {
				freeHomeIndices.add(i);
			}
		}
		// add suits not in home
		for (Suit suit : Suit.values()) {
			if (!suitHomeMap.containsKey(suit)) {
				int homeIndex = freeHomeIndices.remove(0);
				suitHomeMap.put(suit, new Location(Location.HOME, homeIndex));
				nextCardQueue.add(new Card(suit, Rank.lowestRank()));
			}
		}
		// form path
		List<Move> pathToWin = new ArrayList<Move>();
		while (!nextCardQueue.isEmpty()) {
			Card card = nextCardQueue.remove();
			Location start = board.getLocationOf(card);
			Location end = suitHomeMap.get(card.getSuit());
			pathToWin.add(new Move(start, end, 1));
			Card nextCard = card.nextRank();
			if (nextCard != null) nextCardQueue.add(nextCard);
		}
		return pathToWin;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void setHasWon(boolean hasWon) {
		this.hasWon = hasWon;
	}
	
	public boolean getHasWon() {
		return hasWon;
	}
}
