package utilz;

import java.awt.geom.Rectangle2D;

import main.Game;

public class HelpMethods {

	public static boolean canMoveHere(float x, float y, float width, float height, int[][] lvlData) {
		if (!isSolid(x, y, lvlData))
			if (!isSolid(x + width, y + height, lvlData))
				if (!isSolid(x + width, y, lvlData))
					if (!isSolid(x, y + height, lvlData))
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

	public static boolean isEntityInWater(Rectangle2D.Float hitbox, int[][] lvlData) {
		//Verifica si la entidad está en contacto con el agua solo en la parte superior. No puede alcanzar la paarte inferior si no toca la parte superior.
		if (getTileValue(hitbox.x, hitbox.y + hitbox.height, lvlData) != 48)
			if (getTileValue(hitbox.x + hitbox.width, hitbox.y + hitbox.height, lvlData) != 48)
				return false;
		return true;
	}

	private static int getTileValue(float xPos, float yPos, int[][] lvlData) {
		int xCord = (int) (xPos / Game.TILES_SIZE); //Calcula la coordenada en X del Tile
		int yCord = (int) (yPos / Game.TILES_SIZE); //Calcula la coordenada en Y del Tile
		return lvlData[yCord][xCord];
	}

	public static boolean isTileSolid(int xTile, int yTile, int[][] lvlData) {
		int value = lvlData[yTile][xTile];

		switch (value) {
		case 11, 48, 49: //Valores en el que el cuadro (tile) no es sólido
			return false;
		default:
			return true;
		}

	}

	//Calcula en qué tile (cuadro) se encuentra la entidad
	public static float getEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		int currentTile = (int) (hitbox.x / Game.TILES_SIZE);
		if (xSpeed > 0) {
			// Derecha
			int tileXPos = currentTile * Game.TILES_SIZE; // Posición del tile actual
			int xOffset = (int) (Game.TILES_SIZE - hitbox.width); //Calcula el espacio entre el borde derecho del hitbox y el borde derecho del tile
			return tileXPos + xOffset - 1; //Nueva posición en x justo al lado del tile
		} else
			// Izquierda
			return currentTile * Game.TILES_SIZE;
	}

	//Ocurre lo mismo que en el constructor anterior, pero en Y con referencia al techo y suelo
	public static float getEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		int currentTile = (int) (hitbox.y / Game.TILES_SIZE);
		if (airSpeed > 0) {
			//Cayendo - tocando suelo
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffset - 1;
		} else
			//Saltando
			return currentTile * Game.TILES_SIZE;

	}

	public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		//Comprueba el píxel debajo de abajo a la izquierda y abajo a la derecha
		if (!isSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
			if (!isSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
				return false;
		return true;
	}

	// Hay dos métodos isFloor (sobrecarga de métodos) para manejar diferentes casos:
	public static boolean isFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
	    // Verifica si hay suelo considerando la velocidad horizontal (en X)
		if (xSpeed > 0)
			return isSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
		else
			return isSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
	}

	public static boolean isFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
	    // Verifica si hay suelo directamente debajo de la hitbox (sin considerar la velocidad)
		if (!isSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
			if (!isSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
				return false;
		return true;
	}

	public static boolean canCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
		int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
		int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

		if (firstXTile > secondXTile)
			return isAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
		else
			return isAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
	}

	//Comprueba si el cuadro es solido o no
	public static boolean isAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData) {
	    // Verifica si todos los tiles en una fila específica y están libres entre xStart y xEnd
		for (int i = 0; i < xEnd - xStart; i++)
		    // Verifica si todos los tiles en una fila específica y están libres
			if (isTileSolid(xStart + i, y, lvlData))
				return false;
		return true;
	}

	public static boolean isAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
	    // Verifica si todos los tiles en una fila específica y están libres
		if (isAllTilesClear(xStart, xEnd, y, lvlData))
	        // Verifica si los tiles en la fila justo debajo son sólidos
			for (int i = 0; i < xEnd - xStart; i++) {
				if (!isTileSolid(xStart + i, y + 1, lvlData))
					return false;
			}
		return true;
	}

	public static boolean isSightClear(int[][] lvlData, Rectangle2D.Float enemyBox, Rectangle2D.Float playerBox, int yTile) {
		int firstXTile = (int) (enemyBox.x / Game.TILES_SIZE);

	    //Cuando el jugador está en un borde y a la vista de un enemigo, verificamos ambos lados del jugador
		int secondXTile;
	    //Si el tile debajo de playerBox.x es sólido, usamos playerBox.x
		if (isSolid(playerBox.x, playerBox.y + playerBox.height + 1, lvlData))
			secondXTile = (int) (playerBox.x / Game.TILES_SIZE);
		else
	        //Si no es sólido, usamos playerBox.x + playerBox.width
			secondXTile = (int) ((playerBox.x + playerBox.width) / Game.TILES_SIZE);

	    // Todos los tiles entre firstXTile y secondXTile son caminables?
		if (firstXTile > secondXTile)
			return isAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
		else
			return isAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);
	}

}