package com.ryanglobus.solitaire.game;

import java.io.Serializable;

public class Move implements Serializable {
	private static final long serialVersionUID = -3012854282302495498L;
	private final Location start;
	private final Location end;
	private final int numCards;

	public Move(Location start, Location end, int numCards) {
		if (numCards <= 0)
			throw new IllegalArgumentException("Move must have >0 cards.");
		this.start = start;
		this.end = end;
		this.numCards = numCards;
	}

	public Location getStart() {
		return start;
	}

	public Location getEnd() {
		return end;
	}

	public int getNumCards() {
		return numCards;
	}
	
	public Move reverseMove() {
		return new Move(end, start, numCards);
	}
	
	@Override
	public String toString() {
		return "{start: " + start.toString() + ", " +
				"end: " + end.toString() + ", " +
				"numCards: " + numCards + "}";
	}
}
