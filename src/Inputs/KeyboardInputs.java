package Inputs;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import Gamestates.Gamestate;
import Main.GamePanel;

public class KeyboardInputs implements KeyListener {

	private GamePanel gamePanel;
	
	public KeyboardInputs(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void keyReleased(KeyEvent e) { 
		switch (Gamestate.state) {
		case MENU -> gamePanel.getGame().getMenu().keyReleased(e);
		case PLAYING  -> gamePanel.getGame().getPlaying().keyReleased(e);
		}
	}
		
	@Override
	public void keyPressed(KeyEvent e) {
		switch (Gamestate.state) {
		case MENU:
			gamePanel.getGame().getMenu().keyPressed(e);
			break;
		case PLAYING:
			gamePanel.getGame().getPlaying().keyPressed(e);
			break;
		case OPTIONS:
			gamePanel.getGame().getGameOptions().keyPressed(e);
		default:
			break;
		
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// No se usa
	}
}