package com.ryanglobus.solitaire.game;

import java.io.Serializable;

public class Card implements Serializable {
	private static final long serialVersionUID = -3771171657582564687L;
	private final Suit suit;
	private final Rank rank;
	
	public Card(Suit suit, Rank rank) {
		if (suit == null || rank == null) {
			throw new IllegalArgumentException("Suit and rank must be non-null in Card.");
		}
		this.suit = suit;
		this.rank = rank;
	}

	public Suit getSuit() {
		return suit;
	}

	public Rank getRank() {
		return rank;
	}
	
	public boolean isRed() {
		return suit.isRed();
	}
	
	public boolean isBlack() {
		return suit.isBlack();
	}
	
	public boolean isSameColor(Card other) {
		return (isRed() && other.isRed()) || (isBlack() && other.isBlack());
	}
	
	public int getValue() {
		return rank.getValue();
	}
	
	public Card nextRank() {
		Rank nextRank = rank.nextRank();
		if (nextRank == null) return null;
		else return new Card(suit, nextRank);
	}
	
	@Override
	public String toString() {
		return rank.toString() + " of " + suit.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((suit == null) ? 0 : suit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Card)) {
			return false;
		}
		Card other = (Card) obj;
		if (rank != other.rank) {
			return false;
		}
		if (suit != other.suit) {
			return false;
		}
		return true;
	}

	
}
