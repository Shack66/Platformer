package Entities;

import static Utils.Constants.EnemyConstants.*;
import static Utils.Constants.Directions.LEFT;
import static Utils.HelpMethods.canMoveHere;
import static Utils.HelpMethods.isFloor;

import Gamestates.Playing;

public class Shark extends Enemy {

	public Shark(float x, float y) {
		super(x, y, SHARK_WIDTH, SHARK_HEIGHT, SHARK);
		initHitbox(18, 22);
		initAttackBox(20, 20, 20);
	}
	
	public void update(int[][] lvlData, Playing playing) {
		updateBehavior(lvlData, playing);
		updateAnimationTick();
		updateAttackBoxFlip();
	}

	private void updateBehavior(int[][] lvlData, Playing playing) {
		if (firstUpdate)
			firstUpdateCheck(lvlData);
		
		if(inAir)
			inAirChecks(lvlData, playing);
		else {
			switch (state) {
			case IDLE:
				if (isFloor(hitbox, lvlData))
					newState(RUNNING);
				else
					inAir = true;
				break;
			case RUNNING:
				if (canSeePlayer(lvlData, playing.getPlayer())) {
					turnTowardsPlayer(playing.getPlayer());
					if (isPlayerCloseForAttack(playing.getPlayer()))
						newState(ATTACK);
				}
				
				move(lvlData);
				break;
			case ATTACK:
				if (aniIndex == 0)
					attackChecked = false;
				else if (aniIndex == 3) {
					if (!attackChecked)
						checkPlayerHit(attackBox, playing.getPlayer());
					attackMove(lvlData, playing);
				}
			}
		}
		
	}

	private void attackMove(int[][] lvlData, Playing playing) {
		float xSpeed = 0;
		
		if (walkDir == LEFT)
			xSpeed = -walkSpeed;
		else
			xSpeed = walkSpeed;
		
		if (canMoveHere(hitbox.x + xSpeed * 4, hitbox.y, hitbox.width, hitbox.height, lvlData))
			if (isFloor(hitbox, xSpeed *4, lvlData)) {
				hitbox.x += xSpeed * 4;
				return;
			}
		newState(IDLE);
	}
}
