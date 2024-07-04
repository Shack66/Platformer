package Levels;

public class Level {

	private int[][] lvlData;
	
	public Level(int[][] lvlData) {
		this.lvlData = lvlData;
	}
	
	public int getSpriteIndesx(int x, int y) {
		return lvlData[y][x];
	}
	
	public int[][] getLevelData() {
		return lvlData;
	}
}