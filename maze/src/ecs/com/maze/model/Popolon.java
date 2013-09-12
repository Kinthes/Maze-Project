package ecs.com.maze.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Popolon {
	public enum State{
		IDLE,WALKING,JUMPING,DYING
	}
	
	//static final float SPEED = 2f;
	//static final float JUMP_VELOCITY = 1f;
	static final float SIZE = 0.5f;  //half a unit
	
	Vector2 position = new Vector2();
	Vector2 acceleration = new Vector2();
	Vector2 velocity = new Vector2();
	Rectangle bounds = new Rectangle();
	float stateTime=0;
	State state=State.IDLE;
	boolean facingLeft=true, longJump=false;
	
	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	public Vector2 getAcceleration() {
		return acceleration;
	}

		
	public static float getSize() {
		return SIZE;
	}

		
	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(){
		this.bounds.x = this.position.x;
		this.bounds.y = this.position.y;
	}
	
	public Popolon(Vector2 position){
		this.position=position;
		this.bounds.height=SIZE;
		this.bounds.width=SIZE;
		this.bounds.x = position.x;
		this.bounds.y=position.y;
	}

	public void setFacingLeft(boolean b) {
		this.facingLeft=b;		
	}

	public void setState(State state) {
		this.state = state;		
	}

	public void update(float delta){
		stateTime+=delta;
		position.add(velocity.tmp().mul(delta));
	}

	public boolean isFacingLeft() {
		
		return facingLeft;
	}

	public float getStateTime() {
		// TODO Auto-generated method stub
		return stateTime;
	}

	public State getState() {
		// TODO Auto-generated method stub
		return state;
	}
}
