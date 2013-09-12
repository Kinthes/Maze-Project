package ecs.com.maze.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block {
	static final float SIZE=1f;
	
	Vector2 position = new Vector2();
	Rectangle bounds = new Rectangle();
	
	public static float getSize() {
		return SIZE;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public Block(Vector2 pos){
		this.position=pos;
		this.bounds.width=SIZE;
		this.bounds.height=SIZE;
		this.bounds.setX(pos.x);
		this.bounds.setY(pos.y);
	}
}
