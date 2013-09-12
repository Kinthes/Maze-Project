package ecs.com.maze.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

import ecs.com.maze.controller.WorldController;
import ecs.com.maze.model.World;
import ecs.com.maze.view.WorldRenderer;

public class GameScreen implements Screen, InputProcessor{
	private World world;
	private WorldRenderer renderer;
	private WorldController controller;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f,0.1f,0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		controller.update(delta);
		renderer.render();
	}

	@Override
	public void resize(int width, int height) {
		renderer.setsize(width, height);
		
	}

	@Override
	public void show() {
		world=new World();
		renderer=new WorldRenderer(world, false);	
		controller=new WorldController(world);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.LEFT)
			 controller.leftPressed();
			if (keycode == Keys.RIGHT)
			 controller.rightPressed();
			if (keycode == Keys.UP)
			 controller.jumpPressed();
			if (keycode == Keys.X)
			 controller.firePressed();
			return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.LEFT)
			 controller.leftReleased();
			if (keycode == Keys.RIGHT)
			 controller.rightReleased();
			if (keycode == Keys.UP)
			 controller.jumpReleased();
			if (keycode == Keys.X)
			 controller.fireReleased();
			return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
