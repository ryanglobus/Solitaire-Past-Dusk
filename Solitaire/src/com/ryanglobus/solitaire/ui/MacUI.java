package com.ryanglobus.solitaire.ui;

import java.awt.Image;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationAdapter;

import com.ryanglobus.solitaire.SolitaireConstants;

// TODO maybe this shouldn't all be static??? pretty poor design
public class MacUI {
	
	private static Window currentWindow = null;
	
	public static void setup() {
		if (!isMac()) return;
		setProperties();
		setApplicationListener();
		setDock();
	    try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isMac() {
		String osName = System.getProperty("os.name");
	    return osName.contains("OS X");
	}
	
	private static void setProperties() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				SolitaireConstants.APP_NAME);
	}
	
	private static void setApplicationListener() {
		Application.getApplication().addApplicationListener(new ApplicationAdapter() {
			@Override 
			public void handleAbout(ApplicationEvent event) {
				event.setHandled(true);
				String message = SolitaireConstants.APP_NAME + "\nVersion: " +
						SolitaireConstants.VERSION;
				JOptionPane.showMessageDialog(currentWindow,
						message,
						"About",
						JOptionPane.INFORMATION_MESSAGE,
						SolitaireFrame.APP_ICON);
//				JOptionPane.showMessageDialog(currentWindow, message);
			}
			
			// @Override
			// public void handlePreferences(ApplicationEvent event) {}
			 @Override
			 public void handleQuit(ApplicationEvent event) {
				 System.exit(0);
			 }
		});
	}
	
	private static void setDock() {
		Application.getApplication().setDockIconImage(
				SolitaireFrame.APP_ICON.getImage());
	}
	
	public static void setCurrentWindow(Window currentWindow) {
		MacUI.currentWindow = currentWindow;
	}
	
}
