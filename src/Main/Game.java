package Main;

import java.awt.Graphics;
import java.awt.event.KeyListener;

import Audio.AudioPlayer;
import Gamestates.GameOptions;
import Gamestates.Gamestate;
import Gamestates.Menu;
import Gamestates.Playing;
import Ui.AudioOptions;
import Utils.LoadSave;

public class Game implements Runnable {
	
	private GameWindow gameWindow;
	private GamePanel gamePanel;
	private Thread gameThread;
	private final int FPS_SET = 120; //Se encarga del renderizado
	private final int UPS_SET = 200; //Se encarga de la logica
	
	private Playing playing;
	private Menu menu;
	private GameOptions gameOptions;
	private AudioOptions audioOptions;
	private AudioPlayer audioPlayer;
	
	public final static int TILE_DEFAULT_SIZE = 32;
	public final static float SCALE = 1.5f;
	public final static int TILES_IN_WIDTH = 26;
	public final static int TILES_IN_HEIGHT = 14;
	public final static int TILES_SIZE = (int) (TILE_DEFAULT_SIZE * SCALE);
	public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
	
	
	public Game() {
		initClasses();
		
		gamePanel = new GamePanel(this);
		gameWindow = new GameWindow(gamePanel);
		gamePanel.setFocusable(true);
		gamePanel.requestFocus();
		
		startGameLoop();
	}
	
	private void initClasses() {
		audioOptions = new AudioOptions(this);
		menu = new Menu(this);
		playing = new Playing(this);
		gameOptions = new GameOptions(this);
		audioPlayer= new AudioPlayer();
	}

	private void startGameLoop() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void update() {
		switch (Gamestate.state) {
		case MENU:
			menu.update();
			break;
		case PLAYING:
			playing.update();
			break;
		case OPTIONS:
			gameOptions.update();
			break;
		case QUIT:
		default:
			System.exit(0);
			break;
		}

	}
	
	public void render(Graphics g) {
		switch (Gamestate.state) {
		case MENU:
			menu.draw(g);
			break;
		case PLAYING:
			playing.draw(g);
			break;
		case OPTIONS:
			gameOptions.draw(g);
			break;
		default:
			break;
		}	
	}
	
	@Override
	public void run() {
		
		double timePerFrame = 1000000000.0/ FPS_SET; //Nanosegundos
		double timePerUpdate = 1000000000.0/ UPS_SET; 
				
		long previousTime = System.nanoTime(); 
		
		int frames = 0;
		int updates = 0; //para revisar las actualizaciones por segundo (UPS) cuando se impriman
		long lastCheck = System.currentTimeMillis();
		
		double deltaU = 0;
		double deltaF = 0;
		
		while(true) {
			long currentTime = System.nanoTime();
			
			deltaU += (currentTime - previousTime) / timePerUpdate;
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;
			
			if (deltaU >= 1) {
				update();
				updates++;
				deltaU--;
			}
			
			if (deltaF >= 1) {
				gamePanel.repaint();
				frames++;
				deltaF--;
			}
			
			if(System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames + "| UPS" + updates);
				frames = 0;
				updates = 0;
			}
		}
		
	}
	
	public void windowFocusLost() {
		if (Gamestate.state == Gamestate.PLAYING)
			playing.getPlayer().resetDirBooleans();
	}
	
	public Menu getMenu() {
		return menu;
	}
	
	public Playing getPlaying() {
		return playing;
	}
	public GameOptions getGameOptions() {
		return gameOptions;
	}
	
	public AudioOptions getAudioOptions() {
		return audioOptions;
	}
	
	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

}
