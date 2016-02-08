import java.awt.*;
import java.util.*;

/*
This class represents a individual tetris piece
it is able to display itself on a screen
it also responds to various user input commands
*/

public class Tetris {

//database for all orientations of all the pieces
	private int[][][][] shapes = {
	{
		{
		{1, 1},
		{1, 1},
		}
	},

	{
	{
		{0, 1, 0, 0},
		{0, 1, 0, 0},
		{0, 1, 0, 0},
		{0, 1, 0, 0}
	},
	{
		{0, 0, 0, 0},
		{1, 1, 1, 1},
		{0, 0, 0, 0},
		{0, 0, 0, 0}
	}
	},

	{
	{
		{0, 1, 0},
		{0, 1, 0},
		{0, 1, 1},
	},
	{
		{0, 0, 0},
		{1, 1, 1},
		{1, 0, 0},
	},
	{
		{1, 1, 0},
		{0, 1, 0},
		{0, 1, 0},
	},
	{
		{0, 0, 1},
		{1, 1, 1},
		{0, 0, 0},
	}
	},

	{
	{
		{0, 1, 0},
		{0, 1, 0},
		{1, 1, 0},
	},
	{
		{1, 0, 0},
		{1, 1, 1},
		{0, 0, 0},
	},
	{
		{0, 1, 1},
		{0, 1, 0},
		{0, 1, 0},
	},
	{
		{0, 0, 0},
		{1, 1, 1},
		{0, 0, 1},
	}
	},

	{
	{
		{0, 1, 0},
		{0, 1, 1},
		{0, 0, 1}
	},
	{
		{0, 0, 0},
		{0, 1, 1},
		{1, 1, 0}
	}
	},

	{
	{
		{0, 0, 1},
		{0, 1, 1},
		{0, 1, 0}
	},
	{
		{0, 0, 0},
		{1, 1, 0},
		{0, 1, 1}
	}
	},

	{
	{
		{0, 1, 0},
		{0, 1, 1},
		{0, 1, 0}
	},
	{
		{0, 0, 0},
		{1, 1, 1},
		{0, 1, 0}
	},
	{
		{0, 1, 0},
		{1, 1, 0},
		{0, 1, 0}
	},
	{
		{0, 1, 0},
		{1, 1, 1},
		{0, 0, 0}
	}
	}
	};
//array of colors
	private Color[] colors = {Color.YELLOW, Color.ORANGE, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.GREEN, Color.RED};

	private int type;


	boolean active = false;
	Color color = null;
	Random stream = new Random();
	int[][] shape;
	int phase;
	int x;
	int y;


	public Tetris() {					//initializes the piece as a random shape
		type = stream.nextInt(7);
		color = colors[type];
		shape = shapes[type][0];
		y = 1;
	}

	public void activate() {			//activates the shape and puts it into the field
		active = true;
		phase = 0;
		x = 4;
		y = -2;
	}

	public void tick(Color[][] grid) {		//moves the piece down and checks if it hits anything
		if(active) {
			check(grid, true);
			y++;
		}
	}

	public void check(Color[][] grid, boolean tick) {		//checks below the piece
		active = true;
		if(!tick) {
			y--;
		}
		for(int i = 0; i < shape.length; i++) {
			for(int j = 0; j < shape.length; j++) {
				if(shape[i][j] == 1) {
					if(y+i == 19) {
						active = false;
					}
					if(y+i >= 0) {
						if(grid[x+j][y+i+2] != Color.BLACK) {
							active = false;
						}
					}
				}
			}
		}
		if(!tick) {
			y++;
		}
	}

	public boolean gameOver(Color[][] grid) {			//checks if the piece overlaps with any other blocks, ending the game
		for(int i = 0; i < shape.length; i++) {
			for(int j = 0; j < shape.length; j++) {
				if(shape[i][j] == 1 && grid[j+3][i] != Color.BLACK) {
					return true;
				}
			}
		}
		return false;
	}

	public void convert(Color[][] grid) {				//converts blocks from the piece to the grid
		for(int i = 0; i < shape.length; i++) {
			for(int j = 0; j < shape.length; j++) {
				if(shape[i][j] == 1 && y+i >= 0){
					grid[x+j][y+i] = color;
				}
			}
		}
	}

	public void display(Graphics g, double xScale, double yScale, int xStart, int yStart) {		//displays the piece
	//	if(active) {
			g.setColor(color);
			for(int i = 0; i < shape.length; i++) {
				for(int j = 0; j < shape.length; j++) {
					if(shape[i][j] == 1 && y+i >=0) {
						g.fillRect((int)((x+j)*xScale+xStart), (int)((y+i)*yScale+yStart), (int)xScale, (int)yScale);
					}
				}
			}
	//	}


	}

	public void left (Color[][] grid) {		//checks left, moves left
		//if(active) {
			boolean able = true;
			for(int i = 0; i < shape.length; i++) {
				for(int j = 0; j < shape.length; j++) {
					if(shape[i][j] == 1) {
						if(x+j == 0) {
							able = false;
						}
						if(y+i >= 0) {
							if(grid[x+j-1][y+i] != Color.BLACK) {
								able = false;
							}
						}
					}
				}
			}
			if(able) {
				x--;
			}
		//}
	}

	public void right (Color[][] grid) {	//checks right, moves right
		//if(active) {
			boolean able = true;
			for(int i = 0; i < shape.length; i++) {
				for(int j = 0; j < shape.length; j++) {
					if(shape[i][j] == 1) {
						if(x+j == 9) {

							able = false;
						}
						if(y+i >= 0) {
							if(grid[x+j+1][y+i] != Color.BLACK) {
								able = false;
							}
						}
					}
				}
			}
			if(able) {
				x++;
			}
		//}
	}

	public void rotate (Color[][] grid, int direction) {		//checks, and moves the shape through its different orientations in the database
		phase += direction + shapes[type].length;
		phase = phase%shapes[type].length;
		int[][] nextShape = shapes[type][phase];
		boolean able = true;
		for(int i = 0; i < nextShape.length; i++) {
			for(int j = 0; j < nextShape.length; j++) {
				if(nextShape[i][j] == 1) {
					if(x+j == 10 || x+j == -1 || y == 21) {
						able = false;
					}
					if(y+i >= 0) {
						if(grid[x+j][y+i] != Color.BLACK) {
							able = false;
						}
					}
				}
			}
		}
		if(able){
			shape = nextShape;
		}
		else {
			phase -= direction;
		}
	}

	public void drop(Color[][] grid) {		//moves the piece down as far as it can go
		int count = 0;
		while(active) {
			tick(grid);
			count++;
			if(count > 100) {
				active = false;
			}
		}
		convert(grid);
	}

	public boolean active() { return active; }	//returns if the piece is active	1
	public int size() {		//returns the size of the array to display properly
		if (type == 0) {
			return 2;
		}
		if(type == 1) {
			return 4;
		}
		return 3;
	}
}