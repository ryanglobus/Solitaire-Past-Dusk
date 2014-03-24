package com.ryanglobus.solitaire.game;

import java.io.Serializable;

public enum Rank implements Serializable {
	ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
	EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13);
	
	private Rank(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	Rank nextRank() {
		switch(this) {
			case ACE: return TWO;
			case TWO: return THREE;
			case THREE: return FOUR;
			case FOUR: return FIVE;
			case FIVE: return SIX;
			case SIX: return SEVEN;
			case SEVEN: return EIGHT;
			case EIGHT: return NINE;
			case NINE: return TEN;
			case TEN: return JACK;
			case JACK: return QUEEN;
			case QUEEN: return KING;
			case KING: return null;
			default: return null;
		}
	}
	
	/**
	 * Ace-low
	 * @return
	 */
	public static Rank lowestRank() {
		return ACE;
	}
	
	/**
	 * King-high
	 * @return
	 */
	public boolean isHighestRank() {
		return this.equals(KING);
	}
	
	private final int value;
}
