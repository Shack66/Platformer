package Objects;

import java.util.Random;

public class BackgroundTree {

	private int x, y, type, aniIndex, aniTick;
	
	public BackgroundTree(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;

		//Da un valor cualquierea a aniIndex para dar variaciones a los árboles
		Random r = new Random();
		aniIndex = r.nextInt(4); //hay 4 sprites de árboles
	}
	
	public void update() {
		aniTick++;
		if (aniTick >= 35) { 
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= 4)
				aniIndex = 0;
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAniIndex() {
		return aniIndex;
	}

	public void setAniIndex(int aniIndex) {
		this.aniIndex = aniIndex;
	}
	
	
}
