package com.ryanglobus.solitaire.game;

import java.io.Serializable;

public enum Suit implements Serializable {
	HEARTS, SPADES, CLUBS, DIAMONDS;
	
	public boolean isRed() {
		return this.equals(HEARTS) || this.equals(DIAMONDS);
	}
	
	public boolean isBlack() {
		return this.equals(SPADES) || this.equals(CLUBS);
	}
}
