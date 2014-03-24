package com.ryanglobus.solitaire.controller;

import java.io.Serializable;

public class Statistics implements Serializable {
	private static final long serialVersionUID = -4747737921247911148L;
	private int numWins;
	private int numGames;
	
	public Statistics() {
		this(0, 0);
	}
	
	public Statistics(int numWins, int numGames) {
		if (numWins < 0 || numGames < 0 || numGames < numWins) {
			throw new IllegalArgumentException("Invalid statistics values");
		}
		this.numWins = numWins;
		this.numGames = numGames;
	}
	
	void addWin() {
		numGames++;
		numWins++;
	}
	
	void addLoss() {
		numGames++;
	}

	public int getNumWins() {
		return numWins;
	}

	public int getNumGames() {
		return numGames;
	}
}
