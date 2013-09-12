package ecs.com.maze.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

import ecs.com.maze.model.Block;
import ecs.com.maze.model.Popolon;
import ecs.com.maze.model.Popolon.State;
import ecs.com.maze.model.World;


public class WorldRenderer {
	private static final float CAMERA_WIDTH = 10f;
	private static final float CAMERA_HEIGHT = 7f;
	private static final float RUNNING_FRAME_DURATION = 0.06f;
	
	private World world;
	private OrthographicCamera cam;
	
	ShapeRenderer debugRenderer = new ShapeRenderer();
		
	private SpriteBatch spriteBatch;
	private boolean debug=true;
	private int width, height;
	private float ppuX, ppuY;
	
	//TEXTURES
	private TextureRegion bobIdleLeft;
	private TextureRegion bobIdleRight;
	private TextureRegion blockTexture;
	private TextureRegion bobFrame;
	private TextureRegion bobJumpLeft;
	private TextureRegion bobFallLeft;
	private TextureRegion bobJumpRight;
	private TextureRegion bobFallRight;
	
	//ANIMATIONS
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	
	public void setsize(int w, int h){
		this.width = w;
		this.height= h;
		ppuX = (float)width/CAMERA_WIDTH;
		ppuY = (float)height/CAMERA_HEIGHT;
	}
	
	public WorldRenderer(World world, boolean debug){
		this.world=world;
		this.cam=new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
		this.cam.position.set(CAMERA_WIDTH/2f, CAMERA_HEIGHT/2f, 0);
		this.cam.update();
		
		this.debug=debug;
		spriteBatch = new SpriteBatch();
		loadTextures();
	}
	
	private void loadTextures(){
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/textures.pack"));
		bobIdleLeft=atlas.findRegion("bob-01");
		bobIdleRight=new TextureRegion(bobIdleLeft);
		bobIdleRight.flip(true, false);
		
		blockTexture=atlas.findRegion("block");
		
		TextureRegion[] walkLeftFrames = new TextureRegion[5];
		for (int i=0; i<5; i++)
			walkLeftFrames[i] = atlas.findRegion("bob-0"+(i+2));
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);
		
		TextureRegion[] walkRightFrames = new TextureRegion[5];
		for(int i=0;i<5;i++){
			walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
			walkRightFrames[i].flip(true, false);
		}
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);
		
		bobJumpLeft = atlas.findRegion("bob-up");
		bobJumpRight = new TextureRegion(bobJumpLeft);
		bobJumpRight.flip(true, false);
		bobFallLeft = atlas.findRegion("bob-down");
		bobFallRight = new TextureRegion(bobFallLeft);
		bobFallRight.flip(true, false);
	}
	
	public void render(){
		spriteBatch.begin();
			drawBlocks();
			drawBob();
		spriteBatch.end();
		drawCollisionBlocks();
		if(debug) drawDebug();
	}
	
	private void drawCollisionBlocks(){
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.FilledRectangle);
		debugRenderer.setColor(new Color(1,1,1,1));
		for(Rectangle rect : world.getcollisionRects()){
			debugRenderer.filledRect(rect.x, rect.y, rect.width, rect.height);
		}
		debugRenderer.end();
	}
	
	public void drawBlocks(){
		for(Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)){
			spriteBatch.draw(blockTexture, block.getPosition().x*ppuX, block.getPosition().y*ppuY,Block.getSize()*ppuX, Block.getSize()*ppuY);
		}
	}
	
	public void drawBob(){
		Popolon bob = world.getBob();
		
		if(bob.getState().equals(State.WALKING)){
			bobFrame = bob.isFacingLeft() 
					? walkLeftAnimation.getKeyFrame(bob.getStateTime(), true)
					: walkRightAnimation.getKeyFrame(bob.getStateTime(), true);
		}else if (bob.getState().equals(State.JUMPING)) {
			if (bob.getVelocity().y > 0) {
				bobFrame = bob.isFacingLeft() ? bobJumpLeft : bobJumpRight;
				} else {
				bobFrame = bob.isFacingLeft() ? bobFallLeft : bobFallRight;
				}
		}
		else
			bobFrame=bob.isFacingLeft() ? bobIdleLeft : bobIdleRight;
		spriteBatch.draw(bobFrame, bob.getPosition().x*ppuX, bob.getPosition().y*ppuY,Popolon.getSize()*ppuX, Popolon.getSize()*ppuY);
		
	}
	
	public void drawDebug(){
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Rectangle);
		
		for(Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)){
			Rectangle rect = block.getBounds();			
			debugRenderer.setColor(new Color(1,0,0,1));
			debugRenderer.rect(rect.x,rect.y,rect.width, rect.height);
		}
		//render bob
		Popolon bob = world.getBob();
		Rectangle rect = bob.getBounds();
		debugRenderer.setColor(new Color(0, 1, 0, 1));
		debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		debugRenderer.end();
	}	
}
