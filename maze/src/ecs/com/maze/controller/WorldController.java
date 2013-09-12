package ecs.com.maze.controller;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import ecs.com.maze.Maze;
import ecs.com.maze.model.Popolon;
import ecs.com.maze.model.Popolon.State;
import ecs.com.maze.model.World;
import ecs.com.maze.model.Block;


public class WorldController {
	enum Keys{
		LEFT,RIGHT,JUMP,FIRE
	}
	
	private static final long LONG_JUMP_PRESS  	= 150l;
	private static final float ACCELERATION		= 15f;
	private static final float GRAVITY			= -20f;
	private static final float MAX_JUMP_SPEED	= 7f;
	private static final float DAMP				= 0.90f;
	private static final float MAX_VEL			= 4f;
	private static final float MAX_FALL_SPEED	= 4f;
	
	private World world;
	private Popolon bob;
	private long jumpPressedTime;
	private boolean jumpingPressed, grounded=false,pause=false;
	public Array<Block> collidable = new Array<Block>();
	
	private Pool<Rectangle> rectPool = new Pool<Rectangle>(){
		@Override
		protected Rectangle newObject(){
			return new Rectangle();
		}
	};
	
	static Map<Keys, Boolean> keys= new HashMap<WorldController.Keys, Boolean>();
	static{
		keys.put(Keys.LEFT,false);
		keys.put(Keys.RIGHT,false);
		keys.put(Keys.JUMP,false);
		keys.put(Keys.FIRE,false);
	};
	
	public WorldController(World world){
		this.world=world;
		this.bob=world.getBob();
	}
	
	public void leftPressed() {
		 keys.get(keys.put(Keys.LEFT, true));
		}

		public void rightPressed() {
		 keys.get(keys.put(Keys.RIGHT, true));
		}

		public void jumpPressed() {
		 keys.get(keys.put(Keys.JUMP, true));
		}

		public void firePressed() {
		 keys.get(keys.put(Keys.FIRE, true));
		 Gdx.app.exit();
		}

		public void leftReleased() {
		 keys.get(keys.put(Keys.LEFT, false));
		}

		public void rightReleased() {
		 keys.get(keys.put(Keys.RIGHT, false));
		}

		public void jumpReleased() {
		 keys.get(keys.put(Keys.JUMP, false));
		 jumpingPressed = false;
		}

		public void fireReleased() {
		 keys.get(keys.put(Keys.FIRE, false));
		}

		/** The main update method **/
		public void update(float delta) {
		 processInput();
		 if(grounded && bob.getState().equals(State.JUMPING))
			 bob.setState(State.IDLE);
		 bob.getAcceleration().y=GRAVITY;
		 bob.getAcceleration().mul(delta);
		 bob.getVelocity().add(bob.getAcceleration().x, bob.getAcceleration().y);
		 
		 checkCollisionWithBlocks(delta);
		 bob.getVelocity().x *= DAMP;
		 
		 if(bob.getVelocity().x > MAX_VEL)
			 bob.getVelocity().x = MAX_VEL;
		 if(bob.getVelocity().x < -MAX_VEL)
			 bob.getVelocity().x = -MAX_VEL;
		 
		 if(bob.getVelocity().y > MAX_FALL_SPEED)
			 bob.getVelocity().y = MAX_FALL_SPEED;
		 if(bob.getVelocity().y < -MAX_FALL_SPEED)
			 bob.getVelocity().y = -MAX_FALL_SPEED;
		 
		 bob.update(delta);			 
		}

		private void checkCollisionWithBlocks(float delta){
			bob.getVelocity().mul(delta);
			Rectangle bobRect=rectPool.obtain(); //meme chose que new Rectangle(); mais évite garbage collector grace à la classe Pool
													//nécessite donc de libérer la ressource manuellement
			bobRect.set(bob.getBounds().x, bob.getBounds().y, bob.getBounds().width, bob.getBounds().height);
			
			 
			float alignXRight=(float) (Math.floor(bob.getPosition().x)+bobRect.width - 0.01);
			float alignXLeft=(float) Math.floor(bob.getPosition().x + 0.1);
			
			bobRect.y+=bob.getVelocity().y;
			if(bob.getVelocity().y < 0){  //donc tombe
				populateCollidableBlocks((int)bobRect.x, (int)bobRect.y, (int)(bobRect.x+bobRect.width), (int)bobRect.y);
			} else {
				populateCollidableBlocks((int)bobRect.x, (int)(bobRect.y+bobRect.height), (int)(bobRect.x+bobRect.width), (int)(bobRect.y+bobRect.height));
			}
			world.getcollisionRects().clear();
			
			if(bob.getVelocity().x > 0){
				//bobRect.x = alignXRight;
				bobRect.x -= 0.1;
			}else if(bob.getVelocity().x < 0){
				//bobRect.x = alignXLeft;
				bobRect.x+= 0.1;
			}
			
			for(Block block : collidable){
				if(block==null)continue;
				if(bobRect.overlaps(block.getBounds())){
					
					if(bob.getVelocity().y<0){
						grounded=true;
						bob.getPosition().y=(float) Math.floor(block.getPosition().y)+1;
						if(bob.getState().equals(State.JUMPING))bob.setState(State.IDLE);
					}else if(bob.getVelocity().y>0)
						bob.getPosition().y=(float) Math.floor(block.getPosition().y)-1;
					bob.getVelocity().y=0;
					
					world.getcollisionRects().add(block.getBounds());
					break;
				}					
			}
			
			bobRect.y = bob.getPosition().y;
			bobRect.x = bob.getPosition().x;
			
			bobRect.x+=bob.getVelocity().x;
			if(bob.getVelocity().x <0){
				populateCollidableBlocks((int)(bobRect.x), (int)(bobRect.y), (int)bobRect.x, (int)(bobRect.y + bobRect.height));
			}
			else {
				populateCollidableBlocks((int)(bobRect.x+bobRect.width), (int)(bobRect.y), (int)(bobRect.x+bobRect.width), (int)(bobRect.y + bobRect.height));
			}
			for(Block block : collidable){
				if(block==null)continue;
				if(bobRect.overlaps(block.getBounds())){
					//Gdx.app.log(Maze.LOG, "contact Xtest : "+bobRect.x+";"+bobRect.y);
					if(bob.getVelocity().x<0){
						bob.getPosition().x = alignXLeft;
					}else if(bob.getVelocity().x>0){
						bob.getPosition().x = alignXRight;
					}
					//Gdx.app.log(Maze.LOG, "new position : "+bob.getPosition().x+";"+bob.getPosition().y);
					bob.getVelocity().x=0;
					world.getcollisionRects().add(block.getBounds());
				}
			}
			//le test se fait si en sautant sur place le coin x testé entre dans la case de contact en dessous
			
			bob.getPosition().add(bob.getVelocity());
			bob.setBounds();
			bob.getVelocity().mul(1 / delta);
			
			rectPool.free(bobRect);		
		}
		
		
		private void populateCollidableBlocks(int startX, int startY, int endX, int endY){
			collidable.clear();
			for(int x=startX;x<=endX;x++)
				for(int y=startY;y<=endY;y++){
					//Gdx.app.log(Maze.LOG, x+" ; "+y);
					if(x>=0 && x<world.getLevel().getWidth() && y>=0 && y<world.getLevel().getHeight())
						collidable.add(world.getLevel().get(x,y));
				}
		}
		
		
		
		// Change Bob's state and parameters based on input controls 
		private boolean processInput() {
			 if(keys.get(Keys.JUMP)){
				 if(!bob.getState().equals(State.JUMPING)){
					 jumpingPressed=true;
					 grounded=false;
					 jumpPressedTime=System.currentTimeMillis();
					 bob.setState(State.JUMPING);
					 bob.getVelocity().y = MAX_JUMP_SPEED;
				 } else {
					 if(jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS))
						 	jumpingPressed=false;
					 else if (jumpingPressed)
						 	bob.getVelocity().y = MAX_JUMP_SPEED;
				 }
			 }
			
			 if (keys.get(Keys.LEFT)) {
			  // left is pressed
			  bob.setFacingLeft(true);
			  if(!bob.getState().equals(State.JUMPING))bob.setState(State.WALKING);
			  bob.getAcceleration().x = -ACCELERATION;
			 }
			 else if (keys.get(Keys.RIGHT)) {
			  // left is pressed
			  bob.setFacingLeft(false);
			  if(!bob.getState().equals(State.JUMPING))bob.setState(State.WALKING);
			  bob.getAcceleration().x = ACCELERATION;
			 }
			 // need to check if both or none direction are pressed, then Bob is idle
			 else{
				 if(!bob.getState().equals(State.JUMPING))
					 bob.setState(State.IDLE);
				 bob.getAcceleration().x=0;
			 }
			 return false;
		}
}
