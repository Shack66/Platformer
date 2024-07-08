package Levels;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Entities.Crabby;
import Main.Game;
import Utils.LoadSave;
import static Utils.HelpMethods.getLevelDataImg;
import static Utils.HelpMethods.getCrabsImg;
import static Utils.HelpMethods.getPlayerSpawn;

public class Level {

	private BufferedImage img;
	private int[][] lvlData;
	private ArrayList<Crabby> crabs;
	private int lvlTilesWide;
	private int maxTilesOffset;
	private int maxLvlOffsetX;
	private Point playerSpawn;

	
	public Level(BufferedImage img) {
		this.img = img;
		createLevelData();
		createEnemies();
		calcLvlOffsets();
		calcPlayerSpawn();
	}
	
	private void calcPlayerSpawn() {
		playerSpawn = getPlayerSpawn(img);
		
	}

	private void calcLvlOffsets() {
		lvlTilesWide = img.getWidth();
		maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
		maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
		
	}

	private void createEnemies() {
		crabs = getCrabsImg(img);
		
	}

	private void createLevelData() {
		lvlData = getLevelDataImg(img);
		
	}

	public int getSpriteIndesx(int x, int y) {
		return lvlData[y][x];
	}
	
	public int[][] getLevelData() {
		return lvlData;
	}
	
	public int getLvlOffset() {
		return maxLvlOffsetX;
	}
	
	public ArrayList<Crabby> getCrabs() {
		return crabs;	
	}
	
	public Point getSpawnedPlayer() {
		return playerSpawn;
	}
}
