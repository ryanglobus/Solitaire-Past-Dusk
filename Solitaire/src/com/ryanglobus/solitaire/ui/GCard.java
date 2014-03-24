package com.ryanglobus.solitaire.ui;

import java.awt.Color;
import java.awt.Font;

import com.ryanglobus.solitaire.game.Card;

import acm.graphics.GCompound;
import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;

public class GCard extends GCompound {
	private static final long serialVersionUID = -8966640165591508073L;
	static final int WIDTH = 100;
	static final int HEIGHT = 135;
	private static final int SUIT_WIDTH = 18;
	private static final int SUIT_HEIGHT = 18;
	private static final int MARGIN_X = 5;
	private static final int MARGIN_Y = 2;
	private static final int IMAGE_MARGIN = 10;
	private static final int SUIT_MARGIN = 5;
	private static final Color RED = new Color(199, 0, 7);
	private static final Color BLACK = Color.BLACK;
	/**
	 * The minimum height from the top of the card necessary to see the card's
	 * rank and suit.
	 */
	static final int MIN_VISIBLE_HEIGHT = 22;
	
	private final GRect rect;
	private final Card card;
	private GImage image = null;
	
	public GCard(Card card) {
		this.card = card;
		rect = new GRect(WIDTH, HEIGHT);
		rect.setColor(Color.BLACK);
		rect.setFillColor(Color.WHITE);
		rect.setFilled(true);
		String text = "";
		switch (card.getRank()) {
			case ACE: text += 'A'; break;
			case KING: text += 'K'; break;
			case QUEEN: text += 'Q'; break;
			case JACK: text += 'J'; break;
			default: text += card.getRank().getValue(); break;
		}
		GLabel label = new GLabel(text);
		Font font = new Font("", Font.PLAIN, 18);
		label.setFont(font);
		if (card.isRed()) label.setColor(RED);
		else label.setColor(BLACK);
		
		add(rect);
		add(label, MARGIN_X, label.getAscent());
		GLabel suitMarginLabel = new GLabel("10");
		suitMarginLabel.setFont(font);
		double suitX = suitMarginLabel.getWidth();
//		add(newSuitImage(), rect.getWidth() - SUIT_WIDTH - MARGIN_X, MARGIN_Y);
		add(newSuitImage(), label.getWidth() + 7, MARGIN_Y);
//		add(newSuitImage(), suitX + SUIT_MARGIN, MARGIN_Y);
	}
	
	public void setTheme(Theme theme) {
		if (!hasImage() || theme == null) return;
		if (image != null) remove(image);
		String filepath = theme.getFilepath(card.getRank());
		if (filepath == null) return;
		else image = new GImage(filepath);
		image.setBounds(IMAGE_MARGIN, MIN_VISIBLE_HEIGHT,
				rect.getWidth() - 2 * IMAGE_MARGIN,
				rect.getHeight() - MIN_VISIBLE_HEIGHT - IMAGE_MARGIN);
		
		add(image);
	}
	
	private boolean hasImage() {
		switch(card.getRank()) {
			case ACE: case KING: case QUEEN: case JACK: return true;
			default: return false;
		}
	}
	
	public Card getCard() {
		return card;
	}
	
	public void highlight() {
		rect.setColor(Color.BLUE);
	}
	
	public void unhighlight() {
		rect.setColor(Color.BLACK);
	}
	
	public static GObject emptyCard() {
		GRect ec = new GRect(WIDTH, HEIGHT);
		ec.setColor(Color.BLACK);
		return ec;
	}
	
	private GImage newSuitImage() {
		GImage suit = null;
		switch (card.getSuit()) {
			case DIAMONDS: suit = new GImage("img/diamonds.png"); break;
			case HEARTS: suit = new GImage("img/hearts.png"); break;
			case CLUBS: suit = new GImage("img/clubs.png"); break;
			case SPADES: suit = new GImage("img/spades.png"); break;
		}
		suit.setBounds(0, 0, SUIT_WIDTH, SUIT_HEIGHT);
		return suit;
	}
	
}
