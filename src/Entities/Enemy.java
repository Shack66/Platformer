package Entities;

import static Utils.Constants.EnemyConstants.*;
import static Utils.HelpMethods.*;

import java.awt.geom.Rectangle2D;

import Gamestates.Playing;

import static Utils.Constants.Directions.*;
import static Utils.Constants.*;

import Main.Game;

public abstract class Enemy extends Entity {
	protected int enemyType;
	protected boolean firstUpdate = true;
	protected float walkDir = LEFT;
	protected int tileY;
	protected float attackDistance = Game.TILES_SIZE;
	protected boolean active = true;
	protected boolean attackChecked;
	protected int attackBoxOffsetX;
	
	public Enemy(float x, float y, int width, int height, int enemyType) {
		super(x, y, width, height);
		this.enemyType = enemyType;
		
		maxHealth = getMaxHealth(enemyType);
		currentHealth = maxHealth;
		walkSpeed = Game.SCALE * 0.35f;
	}
	
	protected void updateAttackBox() {
		attackBox.x = hitbox.x - attackBoxOffsetX;
		attackBox.y = hitbox.y;
	}
	
	protected void updateAttackBoxFlip() {
		if (walkDir == RIGHT)
			attackBox.x = hitbox.x + hitbox.width;
		else 
			attackBox.y = hitbox.x - attackBoxOffsetX;
		
		attackBox.y = hitbox.y;
	}
	
	protected void initAttackBox(int w, int h, int attackBoxOffsetX) {
		attackBox = new Rectangle2D.Float(x, y, (int) (w * Game.SCALE), (int) (h * Game.SCALE));
		this.attackBoxOffsetX = (int) (Game.SCALE * attackBoxOffsetX);		
	}
	
	protected void firstUpdateCheck(int[][] lvlData) {
		if (!isEntityOnFloor(hitbox, lvlData))
			inAir = true;
		firstUpdate = false;
	}
	
	protected void inAirChecks(int[][] lvlData, Playing playing) {
		if (state != HIT && state != DEAD) {
			updateInAir(lvlData);
			playing.getObjectManager().checkSpikesTouched(this);
			if(isEntityInWater(hitbox, lvlData))
				hurt(maxHealth);
		}
	}
		
	protected void updateInAir(int[][] lvlData) {
		if (canMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
			hitbox.y += airSpeed;
			airSpeed += GRAVITY;
		} else {
			inAir = false;
			hitbox.y = getEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
			tileY = (int) (hitbox.y / Game.TILES_SIZE);
		}
	}
	
	protected void move(int[][] lvlData) {
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
		
	}
	
	protected void turnTowardsPlayer(Player player) { //mueve al enemigo hacia donde esta el jugador
		if (player.hitbox.x > hitbox.x)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
	}
	
	protected boolean canSeePlayer(int[][] lvlData, Player player) { //Para detectar si el jugador es visible para el enemigo
		int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
		if (playerTileY == tileY) 
			if (isPlayerInRange(player)) {
				if(isSightClear(lvlData, hitbox, player.hitbox, tileY)) //Para comprobar si el jugador no tiene ningun obstaculo (como un precipicio o un objeto) en la visibilidad del enemigo
					return true;
			}
		return false;
	}
	
	protected boolean isPlayerInRange(Player player) { //Dos tipos de rango: visibilidad y ataque
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x); //Valor absoluto
		return absValue <= attackDistance * 5;
	}
	
	protected boolean isPlayerCloseForAttack(Player player) {
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x); //Valor absoluto
		switch (enemyType) {
		case CRABBY -> {
			return absValue <= attackDistance;
		}
		case SHARK -> {
			return absValue <= attackDistance * 2;
		}
		}
		return false;
	}
	
	public void hurt(int amount) {
		currentHealth -= amount;
		if (currentHealth <= 0)
			newState(DEAD);
		else {
			newState(HIT);
			if (walkDir == LEFT)
				pushBackDir = RIGHT;
			else
				pushBackDir = LEFT;
			pushBackOffsetDir = UP;
			pushDrawOffset = 0;
		}
	}
	
	protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player) {
		if (attackBox.intersects(player.hitbox))
			player.changeHealth(-getEnemyDmg(enemyType), this);
		else {
			if (enemyType == SHARK)
				return;
		}
		attackChecked = true;
	}
		
	protected void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= getSpriteAmount(enemyType, state)) {
				if (enemyType == CRABBY || enemyType == SHARK) {
					aniIndex = 0;
				
					switch (state) {
					case ATTACK, HIT -> state = IDLE;
					case DEAD -> active = false;
					}				
				}
			}
		}
	}
	
	protected void changeWalkDir() {
		if (walkDir == LEFT)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
	}
	
	public void resetEnemy() {
		hitbox.x = x;
		hitbox.y = y;
		firstUpdate = true;
		currentHealth = maxHealth;
		newState(IDLE);
		active = true;
		airSpeed = 0;
		
		pushDrawOffset = 0;
	}
	
	public int flipX() {
		if (walkDir == RIGHT)
			return width;
		else 
			return 0;
	}
		
	public int flipW() {
		if (walkDir == RIGHT)
			return -1;
		else 
			return 1;
	}

	public boolean isActive() {
		return active;
	}
	
	public float getPushDrawOffset() {
		return pushDrawOffset;
	}
	
}
