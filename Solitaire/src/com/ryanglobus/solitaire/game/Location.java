package com.ryanglobus.solitaire.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Location implements Serializable {
	private static final long serialVersionUID = 2393095793723971911L;
	public static final Type FREE = Type.FREE;
	public static final Type HOME = Type.HOME;
	public static final Type TABLEAU = Type.TABLEAU;
	
	private final Type type;
	private final int index;
	private final static List<Location> allLocations;
	static {
		List<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < Board.NUM_FREE_CELLS; i++) {
			locations.add(new Location(Type.FREE, i));
		}
		for (int i = 0; i < Board.NUM_HOME_CELLS; i++) {
			locations.add(new Location(Type.HOME, i));
		}
		for (int i = 0; i < Board.NUM_TABLEAU_COLS; i++) {
			locations.add(new Location(Type.TABLEAU, i));
		}
		allLocations = Collections.unmodifiableList(locations);
	}
	
	public enum Type implements Serializable {
		FREE, HOME, TABLEAU;
	}
	
	public Location(Type type, int index) {
		if (index < 0 ||
				(type == Type.FREE && index >= Board.NUM_FREE_CELLS) ||
				(type == Type.HOME && index >= Board.NUM_HOME_CELLS) ||
				(type == Type.TABLEAU && index >= Board.NUM_TABLEAU_COLS)) {
			throw new IllegalArgumentException("Illegal location");
		}
		this.type = type;
		this.index = index;
	}
	
	public boolean isHome() {
		return type.equals(HOME);
	}
	
	public boolean isFree() {
		return type.equals(FREE);
	}
	
	public boolean isTableau() {
		return type.equals(TABLEAU);
	}
	
	public Type getType() {
		return type;
	}
	
	public int getIndex() {
		return index;
	}
	
	/**
	 * 
	 * @return an unmodifiable list of all locations on the board
	 */
	public static List<Location> allLocations() {
		return allLocations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (!(obj instanceof Location)) {
			return false;
		}
		Location other = (Location) obj;
		if (index != other.index) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "(" + type.name() + ", " + index + ")";
	}

}
