package ecs.com.maze;


import com.badlogic.gdx.Game;


import ecs.com.maze.screens.GameScreen;


public class Maze extends Game {
	public static final String LOG = Maze.class.getSimpleName();	
	@Override
	public void create() {		
		setScreen(new GameScreen());
	}
	
	@Override
	public void resize(int width, int height) {
	//do nothing
	}
}
