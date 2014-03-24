package com.ryanglobus.solitaire.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.ryanglobus.solitaire.game.Board;
import com.ryanglobus.solitaire.game.Card;
import com.ryanglobus.solitaire.game.Game;
import com.ryanglobus.solitaire.game.Location;
import com.ryanglobus.solitaire.game.Move;
import com.ryanglobus.solitaire.ui.MacUI;
import com.ryanglobus.solitaire.ui.SolitaireFrame;
import com.ryanglobus.solitaire.ui.Theme;

public class HumanPlayer {
	private Game game;
	private Statistics stats;
	private SolitaireFrame view;
	boolean hasAskedToFinishGame = false;
	private SelectedCard selected = null;
	
	public HumanPlayer() {
		GameState gs = GameState.open();
		game = gs.getGame();
		stats = gs.getStats();
		
		// save on quitting
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				Theme theme;
				if (view == null) theme = null;
				else theme = view.getTheme();
				GameState.save(game, stats, theme);
			}
		}));
		view = new SolitaireFrame(game.getBoard(), stats, this, gs.getTheme());
	}
	
	private static class SelectedCard {
		private final Card card;
		private final List<Location> destinations;
		private int destinationIndex;
		
		public SelectedCard(Card card, List<Location> destinations) {
			if (destinations.isEmpty()) {
				throw new IllegalArgumentException("Destinations cannot be " +
						"empty for SelectedCard.");
			}
			this.card = card;
			this.destinations = destinations;
			this.destinationIndex = 0;
		}
		
		public Card getCard() {
			return card;
		}
		
		public Location nextDestination() {
			int nextIndex = destinationIndex % destinations.size();
			Location nextDestination = destinations.get(nextIndex);
			destinationIndex++;
			return nextDestination;
		}
	}

	public void select(Card card) {
		Location end = null;
		if (selected == null || !selected.getCard().equals(card)) {
			List<Location> legalDestinations = allLegalDestinations(card);
			if (legalDestinations.isEmpty()) {
				selected = null;
				return;
			} else if (legalDestinations.size() == 1) {
				end = legalDestinations.get(0);
			} else {
				selected = new SelectedCard(card, legalDestinations);
			}
		}
		if (end == null) end = selected.nextDestination();
		Move move = new Move(game.getBoard().getLocationOf(card), end,
				game.getBoard().cardsOnTopOf(card).size());
		if (game.isLegal(move)) {
			Stack<Card> movedCards = game.getBoard().movedCards(move);
			game.execute(move);
			view.animateAndUpdate(movedCards.peek(), move.getEnd());
			checkWin();
		} else {
			selected = null;
			throw new RuntimeException("Illegal move in selected card " +
					"destination list.");
		}
	}
	
	// TODO add start location if legal
	private List<Location> allLegalDestinations(Card card) {
		Location start = game.getBoard().getLocationOf(card);
		int numCards = game.getBoard().cardsOnTopOf(card).size();
		List<Location> legalDestinations = new ArrayList<Location>();
		for (int i = 0; i < Board.NUM_HOME_CELLS; i++) {
			Location end = new Location(Location.HOME, i);
			Move move = new Move(start, end, numCards);
			if (game.isLegal(move)) {
				legalDestinations.add(end);
				break;
			}
		}
		for (int i = 0; i < Board.NUM_TABLEAU_COLS; i++) {
			Location end = new Location(Location.TABLEAU, i);
			Move move = new Move(start, end, numCards);
			if (game.isLegal(move)) {
				legalDestinations.add(end);
			}
		}
		for (int i = 0; i < Board.NUM_FREE_CELLS; i++) {
			Location end = new Location(Location.FREE, i);
			Move move = new Move(start, end, numCards);
			if (game.isLegal(move)) {
				legalDestinations.add(end);
				break;
			}
		}
		return legalDestinations;
	}


	
	private void checkWin() {
		if (game.inWinState()) {
			if (!game.getHasWon()) {
				stats.addWin();
				game.setHasWon(true);
			}
			view.notifyWin();
			return;
		}
		if (hasAskedToFinishGame) return;
		List<Move> pathToWin = game.simplePathToWin();
		if (pathToWin != null) {
			view.notifyOfPathToWin();
		}
	}
	
	public void finishGame(boolean finish) {
		if (hasAskedToFinishGame) return;
		List<Move> pathToWin = game.simplePathToWin();
		if (pathToWin != null) {
			if (finish) {
				selected = null;
				List<Card> cards = new ArrayList<Card>();
				List<List<Card>> cardsOnTop = new ArrayList<List<Card>>();
				List<Location> dests = new ArrayList<Location>();
				for (Move move : pathToWin) {
					Stack<Card> movedCards = game.getBoard().movedCards(move);
					cards.add(movedCards.peek());
					cardsOnTop.add(movedCards);
					dests.add(move.getEnd());
					game.execute(move);
				}
				view.animateAndUpdate(cards, cardsOnTop, dests);
				if (!game.getHasWon()) {
					stats.addWin();
					game.setHasWon(true);
				}
				view.notifyWin();
			}
			hasAskedToFinishGame = true;
		}
	}
	
	public void undo() {
		selected = null;
		Move move = game.undoMove();
		if (move == null) return;
		Stack<Card> movedCards = game.getBoard().movedCards(move);
		game.undo();
		view.animateAndUpdate(movedCards.peek(), move.getEnd());
	}
	
	public void redo() {
		selected = null;
		Move move = game.redoMove();
		if (move == null) return;
		Stack<Card> movedCards = game.getBoard().movedCards(move);
		game.redo();
		view.animateAndUpdate(movedCards.peek(), move.getEnd());
	}
	
	public void undoAll() {
		selected = null;
		List<Card> cards = new ArrayList<Card>();
		List<List<Card>> cardsOnTop = new ArrayList<List<Card>>();
		List<Location> dests = new ArrayList<Location>();
		Move currMove;
		while ((currMove = game.undoMove()) != null) {
			Stack<Card> movedCards = game.getBoard().movedCards(currMove);
			cards.add(movedCards.peek());
			cardsOnTop.add(movedCards);
			dests.add(currMove.getEnd());
			game.undo();
		}
		view.animateAndUpdate(cards, cardsOnTop, dests);
	}
	
	public void newGame() {
		if (!game.getHasWon()) stats.addLoss();
		game = new Game();
		view.update(game.getBoard());
		hasAskedToFinishGame = false;
		selected = null;
	}
	
	public static void main(String[] args) {
		try {
			MacUI.setup();
			new HumanPlayer();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
