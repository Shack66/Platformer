package Utils;

import static Utils.Constants.EnemyConstants.CRABBY;
import static Utils.Constants.ObjectConstants.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Entities.Crabby;
import Main.Game;
import Objects.GameContainer;
import Objects.Potion;
import Objects.Spike;

public class HelpMethods {
	
	public static boolean canMoveHere(float x, float y, float width, float height, int[][] lvlData) {
		
		if(!isSolid(x, y, lvlData))
			if(!isSolid(x + width, y + height, lvlData))
				if(!isSolid(x + width, y, lvlData))
					if(!isSolid(x, y + height, lvlData))
						return true;
		return false;
	}
	
	private static boolean isSolid(float x, float y, int[][] lvlData) {
		int maxWidth = lvlData[0].length * Game.TILES_SIZE;
		if (x < 0 || x >= maxWidth)
			return true;
		if (y < 0 || y >= Game.GAME_HEIGHT)
			return true;
		
		float xIndex = x / Game.TILES_SIZE;
		float yIndex = y / Game.TILES_SIZE;
		
		return isTileSolid((int) xIndex, (int) yIndex, lvlData);
	}
		
	public static boolean isTileSolid ( int xTile, int yTile, int[][] lvlData) {
		int value = lvlData[yTile][xTile];
		
		if (value >= 48 || value < 0 || value != 11)
			return true;
		return false;
	}

	public static float getEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		int currentTile = (int)(hitbox.x / Game.TILES_SIZE);
		if (xSpeed > 0) {
			//Derecha
			int tileXPos = currentTile * Game.TILES_SIZE;
			int xOffset = (int)(Game.TILES_SIZE - hitbox.width);
			return tileXPos + xOffset - 1;
		} else 
			//Izquierda
			return currentTile * Game.TILES_SIZE;
	}
		
	public static float getEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		int currentTile = (int)(hitbox.y / Game.TILES_SIZE);
		if (airSpeed > 0) {
			//Cayendo - tocando el suelo
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffset = (int)(Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffset - 1;
		} else 
			//Jumping
			return currentTile * Game.TILES_SIZE;
		
	}
	
	public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		//Comprueba el píxel debajo de abajo a la izquierda y abajo a la derecha
		if (!isSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
			if(!isSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
				return false;
		
		return true;
	}
	
	public static boolean isFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
		if (xSpeed > 0)
			return isSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
		else
			return isSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
	}
	
	public static boolean canCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
		int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);		
		int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

		//Se verifica si no hay obstaculos entre el primer hasta el segundo hitbox (solo de vista, no si se puede caminar)
		if (firstXTile > secondXTile) 
			return isAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
			
		 else 
			return isAllTilesClear(firstXTile, secondXTile, yTile, lvlData);

	}
	
	public static boolean isAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData) {
		for (int i = 0; i < xEnd - xStart; i++) 
			if (isTileSolid(xStart + i, y, lvlData))
				return false;
			
		return true;
	}
	
	public static boolean isAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
		if (isAllTilesClear(xStart, xEnd, y, lvlData))
			for (int i = 0; i < xEnd - xStart; i++) {
				if (!isTileSolid(xStart + i, y + 1, lvlData))
					return false;
		}
		return true;
	}
	
	public static boolean isSightClear(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
		int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);		
		int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

		//Se verifica si no hay obstaculos entre el primer hasta el segundo hitbox
		if (firstXTile > secondXTile) 
			return isAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
			
		 else 
			return isAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);
	}
	
	public static int[][] getLevelDataImg(BufferedImage img) { //Se carga la imagen (sprite) del nivel
		int[][] lvlData = new int[img.getHeight()][img.getWidth()];
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value >= 48)
					value = 0;
				lvlData[j][i] = value;
			}
		return lvlData;
	}
	
	public static ArrayList<Crabby> getCrabsImg(BufferedImage img) {
		ArrayList<Crabby> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == CRABBY)
					list.add(new Crabby(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
			}
		return list;
	}

	public static Point getPlayerSpawn(BufferedImage img) {
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == 100)
					return new Point (i * Game.TILES_SIZE, j * Game.TILES_SIZE);
			}
		return new Point (1 * Game.TILES_SIZE, 1 * Game.TILES_SIZE);
	}
	
	public static ArrayList<Potion> getPotions(BufferedImage img) {
		ArrayList<Potion> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == RED_POTION || value == BLUE_POTION)
					list.add(new Potion(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}
		return list;
	}
	public static ArrayList<GameContainer> getContainers(BufferedImage img) {
		ArrayList<GameContainer> list = new ArrayList<>();
		
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == BOX || value == BARREL)
					list.add(new GameContainer(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}
		return list;
	}
	
	public static ArrayList<Spike> getSpikes(BufferedImage img) {
		ArrayList<Spike> list = new ArrayList<>();
		
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == SPIKE)
					list.add(new Spike(i * Game.TILES_SIZE, j * Game.TILES_SIZE, SPIKE));
			}
		return list;
	}
	
}
	
