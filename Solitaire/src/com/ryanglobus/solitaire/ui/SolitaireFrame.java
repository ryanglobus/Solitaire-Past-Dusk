package com.ryanglobus.solitaire.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

import com.ryanglobus.solitaire.SolitaireConstants;
import com.ryanglobus.solitaire.controller.HumanPlayer;
import com.ryanglobus.solitaire.controller.Statistics;
import com.ryanglobus.solitaire.game.Board;
import com.ryanglobus.solitaire.game.Card;
import com.ryanglobus.solitaire.game.Location;
import acm.graphics.GCanvas;
import acm.graphics.GImage;
import acm.graphics.GObject;
import acm.graphics.GPoint;

// TODO prettify control panels
// TODO comments
// TODO put on Github? ryanglobus.com?
// TODO undoAll slow when a lot of timers are going

public class SolitaireFrame extends JFrame {
	private static final long serialVersionUID = -8697882512790344342L;
	private static final int WIDTH = 950;
	private static final int HEIGHT = 700;
	private static final int BORDER = 40;
	private static final int CARD_MARGIN = 5;
	private static final int FREE_CELLS_X = BORDER;
	private static final int FREE_CELLS_Y = BORDER;
	private static final int HOME_CELLS_X =
			FREE_CELLS_X + Board.NUM_FREE_CELLS * (GCard.WIDTH + CARD_MARGIN) + BORDER;
	private static final int HOME_CELLS_Y = BORDER;
	private static final int TABLEAU_X = BORDER;
	private static final int TABLEAU_Y = BORDER + GCard.HEIGHT + CARD_MARGIN;
	private static final int HIGHLIGHT_SHIFT = 2;
	private static final int ANIMATION_TIME_MS = 80; // ms
	private static final int STEP_TIME_MS = 10; // ms
	static final ImageIcon APP_ICON =
			new ImageIcon(SolitaireConstants.ICON_FILENAME);

	
	private GCanvas canvas = new GCanvas();
	private GImage background = new GImage("img/background.jpg");
	private Board board;
	private Statistics stats;
	private JLabel statsLabel;
	private final HumanPlayer player;
	private Map<Card, GCard> cardMap = new HashMap<Card, GCard>();
	private Map<GObject, Location> gobjLocMap = new HashMap<GObject, Location>();
	private Theme theme;
	private boolean isAnimationRunning = false;
	private LinkedList<Runnable> postAnimationEvents = new LinkedList<Runnable>();

	
	private class SolitaireFrameMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			if (isAnimationRunning) return;
			GObject clickedElem = canvas.getElementAt(event.getX(), event.getY());
			if (clickedElem instanceof GCard) {
				player.select(((GCard) clickedElem).getCard());
			} //else {
//				Location clickedLoc = gobjLocMap.get(clickedElem);
//				if (clickedLoc != null) {
//					player.select(clickedLoc);
//				} else {
//					player.select();
//				}
//			}
		}
	}
	
	private class SolitaireMenuBar extends JMenuBar implements ActionListener {
		private static final long serialVersionUID = 6665809359371881500L;
		private final JMenuItem newGameItem, undoItem, redoItem, undoAllItem;
		
		public SolitaireMenuBar() {
			super();
			JMenu fileMenu = new JMenu("File");
			newGameItem = new JMenuItem("New Game");
			newGameItem.addActionListener(this);
			fileMenu.add(newGameItem);
			add(fileMenu);
			
			JMenu editMenu = new JMenu("Edit");
			undoItem = new JMenuItem("Undo");
			undoItem.addActionListener(this);
			editMenu.add(undoItem);
			redoItem = new JMenuItem("Redo");
			redoItem.addActionListener(this);
			editMenu.add(redoItem);
			undoAllItem = new JMenuItem("Undo All");
			undoAllItem.addActionListener(this);
			editMenu.add(undoAllItem);
			add(editMenu);
		}
		
		private void actionPerformed(Object source) {
			if (source == undoItem) {
				player.undo();
			} else if (source == redoItem) {
				player.redo();
			} else if (source == undoAllItem) {
				player.undoAll();
			} else if (source == newGameItem) {
				newGameEvent();
			}
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			final Object source = event.getSource();
			if (isAnimationRunning) {
				postAnimationEvents.add(new Runnable() {
					@Override
					public void run() {
						actionPerformed(source);
					}
				});
			} else {
				actionPerformed(source);
			}
		}
	}
	
	// Source: Exploding Pixels - "Sexy Swing App Ð The Unified Toolbar"
	// Source: http://explodingpixels.wordpress.com/2008/05/02/
	// 		   sexy-swing-app-the-unified-toolbar/
	private static class UnifiedToolbarPanel extends JPanel {
		private static final long serialVersionUID = 5758612278747964260L;
		public static final Color OS_X_UNIFIED_TOOLBAR_FOCUSED_BOTTOM_COLOR =
	            new Color(64, 64, 64);
	    public static final Color OS_X_UNIFIED_TOOLBAR_UNFOCUSED_BORDER_COLOR =
	            new Color(135, 135, 135);    

	    public UnifiedToolbarPanel() {
	        // make the component transparent
	        setOpaque(false);
	        // create an empty border around the panel
	        // note the border below is created using JGoodies Forms
//	        setBorder(Borders.createEmptyBorder("3dlu, 3dlu, 1dlu, 3dlu"));
	    }

	    // Source: StackOverflow, user trashgod
	    // Source: http://stackoverflow.com/questions/12220853/
	    //         how-to-make-the-background-gradient-of-a-jpanel
//	    @Override
//	    public void paintComponent(Graphics g) { TODO gradient
//	    	super.paintComponent(g);
//	    	if (MacUI.isMac()) {
//	    		Graphics2D g2d = (Graphics2D) g;
//	    		Color color1 = getBackground();
//	    		Color color2 = color1.darker();
//	    		int w = getWidth();
//	    		int h = getHeight();
//	    		GradientPaint gp = new GradientPaint(
//	    				0, 0, color1, 0, h, color2);
//	    		g2d.setPaint(gp);
//	    		g2d.fillRect(0, 0, w, h);
//	    	}
//	    }

	    @Override
	    public Border getBorder() {
	        Window window = SwingUtilities.getWindowAncestor(this);
	        return window != null && window.isFocused()
	                ? BorderFactory.createMatteBorder(0,0,1,0,
	                        OS_X_UNIFIED_TOOLBAR_FOCUSED_BOTTOM_COLOR)
	                : BorderFactory.createMatteBorder(0,0,1,0,
	                       OS_X_UNIFIED_TOOLBAR_UNFOCUSED_BORDER_COLOR);
	    }
	}
	
	private class SolitaireBottomPanel extends JPanel {
		private static final long serialVersionUID = 1539551743413166710L;
		private final Color BACKGROUND_COLOR = new Color(220, 220, 220);

		public SolitaireBottomPanel() {
			statsLabel = new JLabel("");
			add(statsLabel);
			setBackground(BACKGROUND_COLOR);
			setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		}
	}
	
	private class SolitaireTopPanel extends UnifiedToolbarPanel implements ActionListener {
		private static final long serialVersionUID = 4475480362217565394L;
		private final JButton newGameButton, undoButton, redoButton, undoAllButton;
		private final JComboBox themeList;
		
		public SolitaireTopPanel() {
			newGameButton = new JButton("New Game"); // TODO make this unselected
			newGameButton.addActionListener(this);
			add(newGameButton);
			undoButton = new JButton("Undo");
			undoButton.addActionListener(this);
			add(undoButton);
			redoButton = new JButton("Redo");
			redoButton.addActionListener(this);
			add(redoButton);
			undoAllButton = new JButton("Undo All");
			undoAllButton.addActionListener(this);
			add(undoAllButton);
			
			add(new JLabel("Theme:"));
			List<Theme> themes = Theme.getThemes();
			String[] themeNames = new String[themes.size()];
			int selectedIndex = -1;
			for (int i = 0; i < themes.size(); i++) {
				Theme theme = themes.get(i);
				if (theme.equals(SolitaireFrame.this.theme)) {
					selectedIndex = i;
				}
				String themeName = theme.getName();
				themeName = themeName.substring(0, 1).toUpperCase() + 
						themeName.substring(1).toLowerCase();
				themeNames[i] = themeName;
			}
			themeList = new JComboBox(themeNames);
			if (selectedIndex >= 0)
				themeList.setSelectedIndex(selectedIndex);
			add(themeList);
			themeList.addActionListener(this);
		}
		
		private void actionPerformed(Object source) {
			if (source == newGameButton) {
				newGameEvent();
			} else if (source == undoButton) {
				player.undo();
			} else if (source == redoButton) {
				player.redo();
			} else if (source == undoAllButton) {
				player.undoAll();
			} else if (source == themeList) {
				Theme theme =
						new Theme(themeList.getSelectedItem().toString().toLowerCase());
				setTheme(theme);
			}
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			final Object source = event.getSource();
			if (isAnimationRunning) {
				postAnimationEvents.add(new Runnable() {
					@Override
					public void run() {
						actionPerformed(source);
					}
				});
			} else {
				actionPerformed(source);
			}
		}
	}
	
	public SolitaireFrame(Board board, Statistics stats, 
			HumanPlayer player) {
		this(board, stats, player, Theme.defaultTheme());
	}
	
	public SolitaireFrame(final Board board, Statistics stats,
			final HumanPlayer player, Theme theme) {
		super(SolitaireConstants.APP_NAME);
		this.board = board;
		this.stats = stats;
		this.player = player;
		this.theme = theme;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (MacUI.isMac()) {
					MacUI.setCurrentWindow(SolitaireFrame.this);
					getRootPane().putClientProperty("apple.awt.brushMetalLook",
							Boolean.TRUE);
				}
				
				setJMenuBar(new SolitaireMenuBar());
				canvas.setSize(WIDTH, HEIGHT);
				canvas.add(background);
				getContentPane().add(canvas, BorderLayout.CENTER);
				getContentPane().add(new SolitaireTopPanel(), BorderLayout.NORTH);
				getContentPane().add(new SolitaireBottomPanel(), BorderLayout.SOUTH);
				
				update();
				canvas.addMouseListener(new SolitaireFrameMouseListener());
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setSize(WIDTH, HEIGHT);
				setVisible(true);
			}
		});
	}

	public void unselect(Card card) {
		if (card == null) return;
		List<Card> selectedCards = board.cardsOnTopOf(card);
		for (Card c : selectedCards) {
			GCard gcard = cardMap.get(c);
			gcard.unhighlight();
			gcard.move(0, -1 * HIGHLIGHT_SHIFT);
		}
	}
	
	public void select(Card card) {
		Stack<Card> stack = board.cardsOnTopOf(card);
		for (Card c : stack) {
			GCard gc = cardMap.get(c);
			if (gc != null) {
				gc.highlight();
				gc.move(0, HIGHLIGHT_SHIFT);
			}
		}
	}
	
	private class AnimationTimer implements ActionListener {
		private int counter = 0;
		private int nsteps;
		private double dx, dy;
		private final Location destination;
		private List<GCard> gcards;
		private final CountDownLatch countDown;
		private final AtomicInteger currTimerNumber;
		private final int timerNumber;
		private boolean hasInitialized = false;
		private final Card card;
		private final List<Card> cardsOnTop;

		public AnimationTimer(Card card, Location destination,
				CountDownLatch countDown, AtomicInteger currTimerNumber,
				int timerNumber, List<Card> cardsOnTop) {
			this.card = card;
			this.destination = destination;
			this.countDown = countDown;
			this.currTimerNumber = currTimerNumber;
			this.timerNumber = timerNumber;
			this.cardsOnTop = cardsOnTop;
			
		}
		private void init() {
			GCard gcard = cardMap.get(card);
			GPoint startpoint = gcard.getLocation();

			// get destination x,y
			GPoint endpoint;
			switch (destination.getType()) {
				case FREE: endpoint = freeCellPoint(destination.getIndex()); break;
				case TABLEAU: endpoint = tableauPoint(destination.getIndex()); break;
				case HOME: endpoint = homeCellPoint(destination.getIndex()); break;
				default: throw new RuntimeException("Should never get here - bad destination type.");
			}
			nsteps = ANIMATION_TIME_MS / STEP_TIME_MS;
			dx = (endpoint.getX() - startpoint.getX()) / nsteps;
			dy = (endpoint.getY() - startpoint.getY()) / nsteps;
			gcards = new ArrayList<GCard>();
			for (Card c : cardsOnTop) {
				GCard gc = cardMap.get(c);
				gcards.add(gc);
			}
			for (int j = gcards.size() - 1; j >= 0; j--) {
				gcards.get(j).sendToFront();
			}
			hasInitialized = true;
		}
		@Override
		public void actionPerformed(ActionEvent event) {
			if (currTimerNumber.get() != timerNumber) return;
			if (!hasInitialized) init();
			for (GCard gc : gcards) {
				gc.move(dx, dy);
			}
			counter++;
			if (counter == nsteps) {
				((Timer) event.getSource()).stop();
				for (GCard gc : gcards) {
					gobjLocMap.put(gc, destination);
				}
				currTimerNumber.incrementAndGet();
				countDown.countDown(); // possibly update
			}
		}
		
	}
	
	/**
	 * Assumes a valid move. Call this after executing the move in the model.
	 * @param card
	 * @param destination
	 */
	public void animateAndUpdate(Card card, Location destination) {
		List<Card> cards = new ArrayList<Card>();
		cards.add(card);
		List<List<Card>> cardsOnTop = new ArrayList<List<Card>>();
		cardsOnTop.add(board.cardsOnTopOf(card));
		List<Location> destinations = new ArrayList<Location>();
		destinations.add(destination);
		animateAndUpdate(cards, cardsOnTop, destinations);
	}
	
	/**
	 * Assumes valid moves. Call this after executing all moves in the model.
	 * @param cards
	 * @param allCardsOnTop
	 * @param destinations
	 */
	public void animateAndUpdate(List<Card> cards, List<List<Card>> allCardsOnTop,
			List<Location> destinations) { // TODO class?
		// TODO if hit redo/undo button fast enough, starts animation while this is running
		if (cards.size() != destinations.size() || cards.size() <= 0) {
			throw new IllegalArgumentException("Invalid list size for animation.");
		}
		int numTimers = cards.size();
		final CountDownLatch countDown = new CountDownLatch(numTimers);
		Thread updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					countDown.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						update();
						isAnimationRunning = false;
						Queue<?> events =
								(Queue<?>) postAnimationEvents.clone();
						postAnimationEvents.clear();
						while (!events.isEmpty()) {
							Object event = events.remove();
							if (event instanceof Runnable) {
								((Runnable) event).run();
							} else {
								System.err.println("Error: Non-Runnable in postAnimationEvents.");
							}
						}
					}
				});
			}
		});
		updateThread.start();
		
		final AtomicInteger currTimerNumber = new AtomicInteger(0);
		for (int i = 0; i < cards.size(); i++) {
			final int timerNumber = i;
			final Card card = cards.get(i);
			final Location destination = destinations.get(i);
			final List<Card> cardsOnTop = allCardsOnTop.get(i);
			
			isAnimationRunning = true;
			Timer timer = new Timer(STEP_TIME_MS,
					new AnimationTimer(card, destination,
							countDown, currTimerNumber, timerNumber,
							cardsOnTop));
			timer.start();
		}
	}

	/**
	 * 
	 * @param board the new board
	 */
	public void update(Board board) {
		this.board = board;
		update();
	}

	// invoked on this thread - this thread should be a Swing thread
	public void update() {
		canvas.removeAll();
		canvas.add(background);
		cardMap.clear();
		gobjLocMap.clear();
		drawFreeCells();
		drawHomeCells();
		drawTableau();
		setTheme(theme);
		setStatsLabel();
	}
	
	public Theme getTheme() {
		return theme;
	}
	
	public void setTheme(Theme theme) {
		this.theme = theme;
		for (GCard gc : cardMap.values()) {
			gc.setTheme(theme);
		}
	}
	
	private void setStatsLabel() {
		String text = stats.getNumWins() + " wins / " + stats.getNumGames() + " games";
		statsLabel.setText(text);
	}
	
	private void drawFreeCells() {
		List<Card> freeCells = board.readFreeCells();
		for (int i = 0; i < freeCells.size(); i++) {
			Location loc = new Location(Location.Type.FREE, i);
			GPoint point = freeCellPoint(i);
			Card card = freeCells.get(i);
			
			GObject emptyCard = GCard.emptyCard();
			gobjLocMap.put(emptyCard, loc);
			canvas.add(emptyCard, point);

			if (card != null) {
				GCard gc = new GCard(card);
				cardMap.put(card, gc);
				gobjLocMap.put(gc, loc);
				canvas.add(gc, point);
			}
		}
	}
	
	private GPoint freeCellPoint(int index) {
		if (index < 0 || index >= Board.NUM_FREE_CELLS) 
			throw new IllegalArgumentException("Invalid free cell index.");
		int x = FREE_CELLS_X + (CARD_MARGIN + GCard.WIDTH) * index;
		int y = FREE_CELLS_Y;
		return new GPoint(x, y);
	}
	
	private GPoint homeCellPoint(int index) {
		if (index < 0 || index >= Board.NUM_HOME_CELLS) 
			throw new IllegalArgumentException("Invalid home cell index.");
		int x = HOME_CELLS_X + (CARD_MARGIN + GCard.WIDTH) * index;
		int y = HOME_CELLS_Y;
		return new GPoint(x, y);
	}
	
	private void drawHomeCells() {
		List<Stack<Card>> homeCells = board.readHomeCells();
		for (int i = 0; i < homeCells.size(); i++) {
			Location loc = new Location(Location.HOME, i);
			GPoint point = homeCellPoint(i);
			Stack<Card> homeStack = homeCells.get(i);
			GObject emptyCard = GCard.emptyCard();
			gobjLocMap.put(emptyCard, loc);
			canvas.add(emptyCard, point);
			if (!homeStack.isEmpty()) {
				for (Card card : homeStack) {
					GCard gcard = new GCard(card);
					cardMap.put(card, gcard);
					gobjLocMap.put(gcard, loc);
					canvas.add(gcard, point);
				}
			}
		}
	}
	
	// returns the point for the next card to place on top
	private GPoint tableauPoint(int index) {
		if (index < 0 || index >= Board.NUM_TABLEAU_COLS)
			throw new IllegalArgumentException("Invalid tableau index.");
		int x = TABLEAU_X + (CARD_MARGIN + GCard.WIDTH) * index;
		int y = TABLEAU_Y;
		GObject prevObj = null;
		boolean oneCardSoFar = false;
		while (true) {
			GObject currObj = canvas.getElementAt(new GPoint(x, y));
			if (!(currObj instanceof GCard)) {
				if (prevObj != null) {
					y -= GCard.HEIGHT;
					y += GCard.MIN_VISIBLE_HEIGHT;
				}
				return new GPoint(x, y);
			}
			if (oneCardSoFar) {
				y += GCard.MIN_VISIBLE_HEIGHT;
			} else {
				y += GCard.HEIGHT;
				oneCardSoFar = true;
			}
			prevObj = currObj;
		}
	}
	
	private void drawTableau() {
		List<Stack<Card>> tableau = board.readTableau();
		for (int i = 0; i < tableau.size(); i++) {
			Stack<Card> column = tableau.get(i);
			GObject emptyCard = GCard.emptyCard();
			gobjLocMap.put(emptyCard, new Location(Location.Type.TABLEAU, i));
			int x = TABLEAU_X + (CARD_MARGIN + GCard.WIDTH) * i;
			canvas.add(emptyCard, x, TABLEAU_Y);
			for (Card card : column) {
				GCard gc = new GCard(card);
				cardMap.put(card, gc);
				canvas.add(gc, tableauPoint(i));
				gobjLocMap.put(gc, new Location(Location.Type.TABLEAU, i));
			}
		}
	}
	
	private void notifyOfPathToWinImpl() {
		String[] options = {"Yes", "No"};
		int response = JOptionPane.showOptionDialog(this,
				"It looks like you're close to winning.\n Would you like me to " +
				"finish the game?",
				"You've Almost Won!",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
		player.finishGame(response == JOptionPane.YES_OPTION);
	}

	public void notifyOfPathToWin() {
		if (isAnimationRunning) {
			postAnimationEvents.add(new Runnable() {
				@Override
				public void run() {
					notifyOfPathToWinImpl();
				}
			});
		} else {
			notifyOfPathToWinImpl();
		}
	}
	
	private void notifyWinImpl() {
		String[] options = {"New Game", "No Thanks"};
		int response = JOptionPane.showOptionDialog(this,
				"Congratulations! You won! Would you like to start a new game?",
				"You Won!",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				APP_ICON,
				options,
				options[0]);
		if (response == JOptionPane.YES_OPTION) {
			player.newGame();
		}
	}
	
	public void notifyWin() {
		if (isAnimationRunning) {
			postAnimationEvents.add(new Runnable() {
				@Override
				public void run() {
					notifyWinImpl();
				}
			});
		} else {
			notifyWinImpl();
		}

	}

	private void newGameEvent() {
		String[] options = {"New Game", "Cancel"};
		int response = JOptionPane.showOptionDialog(SolitaireFrame.this,
				"Are you sure you want to start a new game? \nYou will " +
				"lose the progress of this game.",
				"New Game",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				APP_ICON,
				options,
				options[1]);
		if (response == JOptionPane.YES_OPTION) {
			player.newGame();
		}
	}
	
}
