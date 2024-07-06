package Entities;

import static Utils.Constants.EnemyConstants.*;
import static Utils.HelpMethods.*;
import static Utils.Constants.Directions.*;

import Main.Game;

public abstract class Enemy extends Entity {
	private int aniIndex, enemyState, enemyType;
	private int aniTick, aniSpeed = 25;
	private boolean firstUpdate = true;
	private boolean inAir;
	private float fallSpeed;
	private float gravity = 0.04f * Game.SCALE;
	private float walkSpeed = 0.35f * Game.SCALE;
	private float walkDir = LEFT;
	
	public Enemy(float x, float y, int width, int height, int enemyType) {
		super(x, y, width, height);
		this.enemyType = enemyType;
		initHitbox(x, y, width, height);
		
	}
	
	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= getSpriteAmount(enemyType, enemyState)) {
				aniIndex = 0;
			}
		}
	}

	public void update(int[][] lvlData) {
		updateMove(lvlData);
		updateAnimationTick();
	}
	
	private void updateMove(int[][] lvlData) {
		if (firstUpdate) {
			if (!isEntityOnFloor(hitbox, lvlData)) 
				inAir = true;
			firstUpdate = false;
		}
		
		if (inAir) {
			if (canMoveHere(hitbox.x, hitbox.y + fallSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += fallSpeed;
				fallSpeed += gravity;
			} else {
				inAir = false;
				hitbox.y = getEntityYPosUnderRoofOrAboveFloor(hitbox, fallSpeed);
			}
			
		}  else {
			switch (enemyState) {
			case IDLE:
				enemyState = RUNNING;
				break;
			case RUNNING:
				float xSpeed = 0;
				
				if (walkDir == LEFT)
					xSpeed = -walkSpeed;
				else 
					xSpeed = walkSpeed;
				
				if (canMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
					if (isFloor(hitbox, xSpeed, lvlData)) {
						hitbox.x += xSpeed;
						return;
					}
				
				changeWalkDir();
				
				break;
			}
		}
		
	}

	
	private void changeWalkDir() {
		if (walkDir == LEFT)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
		
	}

	public int getAniIndex() {
		return aniIndex;
	}
	
	public int getEnemyState() {
		return enemyState;
	}
	
}