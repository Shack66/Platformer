package Gamestates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Main.Game;
import Utilities.LoadSave;

public class Credits extends State implements Statemethods {
	private BufferedImage backgroundImg, creditsImg;
	private int bgX, bgY, bgW, bgH;

	private ArrayList<ShowEntity> entitiesList;

	public Credits(Game game) {
		super(game);
		backgroundImg = LoadSave.getSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG);
		creditsImg = LoadSave.getSpriteAtlas(LoadSave.CREDITS);

        float scale = Math.min(Game.GAME_WIDTH / (float) creditsImg.getWidth(), Game.GAME_HEIGHT / (float) creditsImg.getHeight());
        bgW = (int) (creditsImg.getWidth() * scale);
        bgH = (int) (creditsImg.getHeight() * scale);
        bgX = (Game.GAME_WIDTH - bgW) / 2;
        bgY = 0;

        loadEntities();
    }
	private void loadEntities() {
		entitiesList = new ArrayList<>();
		entitiesList.add(new ShowEntity(getIdleAni(LoadSave.getSpriteAtlas(LoadSave.PLAYER_ATLAS), 5, 64, 40), (int) (Game.GAME_WIDTH * 0.05), (int) (Game.GAME_HEIGHT * 0.8)));
		entitiesList.add(new ShowEntity(getIdleAni(LoadSave.getSpriteAtlas(LoadSave.CRABBY_SPRITE), 9, 72, 32), (int) (Game.GAME_WIDTH * 0.15), (int) (Game.GAME_HEIGHT * 0.75)));
		entitiesList.add(new ShowEntity(getIdleAni(LoadSave.getSpriteAtlas(LoadSave.SHARK_ATLAS), 8, 34, 30), (int) (Game.GAME_WIDTH * 0.8), (int) (Game.GAME_HEIGHT * 0.8)));
	}

	private BufferedImage[] getIdleAni(BufferedImage atlas, int spritesAmount, int width, int height) {
		BufferedImage[] arr = new BufferedImage[spritesAmount];
		for (int i = 0; i < spritesAmount; i++)
			arr[i] = atlas.getSubimage(width * i, 0, width, height);
		return arr;
	}

	@Override
	public void update() {
		for (ShowEntity se : entitiesList)
			se.update();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(creditsImg, bgX, bgY, bgW, bgH, null);

		for (ShowEntity se : entitiesList)
			se.draw(g);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			setGamestate(Gamestate.MENU);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// No se usa
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// No se usa
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// No se usa
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// No se usa
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// No se usa
	}

	private class ShowEntity {
		private BufferedImage[] idleAnimation;
		private int x, y, aniIndex, aniTick;

		public ShowEntity(BufferedImage[] idleAnimation, int x, int y) {
			this.idleAnimation = idleAnimation;
			this.x = x;
			this.y = y;
		}

		public void draw(Graphics g) {
			g.drawImage(idleAnimation[aniIndex], x, y, (int) (idleAnimation[aniIndex].getWidth() * 4), (int) (idleAnimation[aniIndex].getHeight() * 4), null);
		}

		public void update() {
			aniTick++;
			if (aniTick >= 25) {
				aniTick = 0;
				aniIndex++;
				if (aniIndex >= idleAnimation.length)
					aniIndex = 0;
			}

		}
	}

}