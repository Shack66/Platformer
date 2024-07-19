package Gamestates;

import java.awt.event.MouseEvent;

import Audio.AudioPlayer;
import Main.Game;
import Ui.MenuButton;

public class State {

	protected Game game;

	public State(Game game) {
		this.game = game;
	}

	public boolean isIn(MouseEvent e, MenuButton mb) {
		return mb.getBounds().contains(e.getX(), e.getY());
	}

	public Game getGame() {
		return game;
	}

	@SuppressWarnings("incomplete-switch")
	public void setGamestate(Gamestate state) {
		switch (state) {
		case MENU -> game.getAudioPlayer().playSong(AudioPlayer.MENU_1); //Si el valor es menu entonces llama al metodo playSong del objeto AudioPlayer y reproduce la cancion del menu
		case PLAYING -> game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLevelIndex());
		}

		Gamestate.state = state;
	}

}