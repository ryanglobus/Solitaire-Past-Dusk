package com.ryanglobus.solitaire.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.ryanglobus.solitaire.SolitaireConstants;
import com.ryanglobus.solitaire.game.Game;
import com.ryanglobus.solitaire.ui.MacUI;
import com.ryanglobus.solitaire.ui.Theme;

public class GameState implements Serializable {
	private static final long serialVersionUID = -3045201507621931045L;
	private Game game;
	private Statistics stats;
	private Theme theme;
	
	private GameState(Game game, Statistics stats, Theme theme) {
		this.game = game;
		this.stats = stats;
		this.theme = theme;
	}
	
	private static String getFilepath() {
		String fileName = "game-state.ser";
		if (MacUI.isMac()) {
			String home = System.getProperty("user.home");
			if (home == null || home.isEmpty()) return fileName;
			String dirPath = home + "/Library/Application Support/" +
					SolitaireConstants.BUNDLE_NAME;
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (dir.exists() && dir.isDirectory() &&
					dir.canRead() && dir.canWrite()) {
				return dirPath + "/" + fileName;
			}
		}
		return fileName;
	}
	
	public static GameState open() {
		return open(getFilepath());
	}
	
	public static GameState open(String filepath) {
		ObjectInputStream in = null;
		GameState gs = new GameState(null, null, null);
		try {
			in = new ObjectInputStream(new FileInputStream(filepath));
			gs = (GameState) in.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (gs.game == null) gs.game = new Game();
		if (gs.stats == null) gs.stats = new Statistics();
		if (gs.theme == null) gs.theme = Theme.defaultTheme();
		return gs;
	}
	
	public static void save(Game game, Statistics stats, Theme theme) {
		save(game, stats, theme, getFilepath());
	}
	
	public static void save(Game game, Statistics stats, Theme theme, String filepath) {
		GameState gs = new GameState(game, stats, theme);
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(filepath, false));
			out.writeObject(gs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Game getGame() {
		return game;
	}

	public Statistics getStats() {
		return stats;
	}

	public Theme getTheme() {
		return theme;
	}
}
