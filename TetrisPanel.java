import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;


/*
This method contains all elements pertaining to the display of the window, as well as the field where the piece will drop
It also recieves user inputs and transmits them to the tetris module
It scales the display to the size of the window
*/
public class TetrisPanel extends Panel implements KeyListener, MouseListener {

	private Color[][] grid = new Color[10][22];			//a grid where the fallen pieces will be stored

	private BufferedImage osi = null;					//double buffering components
	private Graphics osg = null;
	private Dimension dim = null;						//dimension which will be used to scale the window
	private Timer timer = new Timer();					//timer fow scheduling tasks
	private int tick = 0;								//game tick for adjusting speed
	private int[] levels = {1000, 600, 400, 300, 200, 100};		//delay between drops on respective levels
	private int[] linesNeeded = {2, 4, 8, 16, 32};				//lines needed to clear to get to the next level
	private int level = 0;								//current level this game
	private int lines = 0;								//number of lines cleared this game
	private int restFrame = 0;							//a buffer once the piece hits the ground for the user to continue to input commends
	private double xScale;								//variable used to scale the graphics
	private double yScale;
	private boolean gameOver = false;					//flag for gameover condition
	private boolean paused = false;						//flag for paused condition


	private Tetris piece = new Tetris();				//initializes current tetris piece
	private Tetris next = new Tetris();					//initializes the next tetris piece



	public TetrisPanel() {								//initializing the game
		piece.activate();								//activates the current piece
		for(int i = 0; i < 10; i++) {					//sets all the tiles in the grid to black
			for(int j = 0; j < 22; j++) {
				grid[i][j] = Color.BLACK;
			}
		}
		setBackground(Color.LIGHT_GRAY);				//sets background to light gray
		TimerTask drop = new TimerTask() {				//creates an operation to be run every tick
			public void run() {
				if(!paused && !gameOver){				//tests if the game os paused or over
					tick ++;							//increases the tick
					gameOver = piece.gameOver(grid);	//check if the user has lost
					if(tick%levels[level] == 0) {		//determines if it is time to drop the piece
						piece.tick(grid);				//drops the piece
						if(!piece.active) {				//if the piece hits the ground:
							restFrame ++;				//buffer for additional inputs
							if(restFrame == 2) {
								piece.convert(grid);	//converts the piece onto the grid
								piece = next;			//transfers and activates the piece from storage onto the field
								piece.activate();
								next = new Tetris();	//creates a new storage piece
								restFrame = 0;			//resets the buffer
							}
						}
						lines += clearCheck();			//checks for lines cleared and adjusts the level
						if(level != levels.length-1){
							if(lines > linesNeeded[level] ) {
								level ++;
							}
						}
					}
					repaint();							//repaints the screen

				}
			}
		};
		addKeyListener(this);							//adds listeners and schedules the run task
		addMouseListener(this);
		timer.scheduleAtFixedRate(drop, 0, 1);
	}

	public void paint(Graphics g) {
		dim = getSize();								//generates double scales that will be used to adjust the size of the display
		dim.width = dim.width*2/3;
		xScale = dim.width/12;
		yScale = dim.height/23;
		int xStart = (dim.width%12)/2 + (int)xScale;	//buffer region around the field
		int yStart = (dim.height%23)/2 + (int)yScale;
		for(int i = xStart; i < 10*xScale+xStart; i+=xScale) {	//displays al the pieces in the grid
			for(int j = yStart; j < 22*yScale; j+=yScale) {
				g.setColor(grid[(i-xStart)/(int)xScale][(j-yStart)/(int)yScale]);
				g.fillRect((int)i, (int)j, (int)xScale, (int)yScale);
			}
		}
		piece.display(g, xScale, yScale, xStart, yStart);		//displays the piece
		g.setColor(Color.GRAY);									//adds gridlines
		for(int i = xStart+(int)xScale; i < 10*xScale+xStart; i+=xScale) {
			g.drawLine(i, yStart, i, dim.height-yStart);
		}
		for(int i = yStart+(int)yScale; i < 22*yScale; i+=yScale) {
			g.drawLine(xStart, i, dim.width-xStart, i);
		}

		g.setColor(Color.BLACK);								//adds an area to displat the next piece
		g.fillRect((int)(dim.width), (int)(yStart + yScale), (int)(5*xScale), (int)(5*yScale));
		int size = next.size();
		next.display(g, (int)xScale, (int)yScale, (int)(dim.width+xScale*(5-size)/2), (int)(yStart+yScale*(5-size)/2));
		if(gameOver) {											//diplays gameover text
			Font f = new Font("Dialog", Font.PLAIN, (int)(xScale*7/2));
			g.setFont(f);
			g.setColor(Color.WHITE);
			g.drawString("Game Over", 0 , (int)(yStart + 7*yScale));
		}
		else if(paused) {										//displays paused text
			Font f = new Font("Dialog", Font.PLAIN, (int)(xScale*5));
			g.setFont(f);
			g.setColor(Color.WHITE);
			g.drawString("Paused ", 0 , (int)(yStart + 7*yScale));

		}
	}

	public void update(Graphics g) {			//double buffering method
		dim = getSize();						//creates a image in memory to be drawn on
		osi = new BufferedImage( dim.width, dim.height, BufferedImage.TYPE_INT_RGB );
		osg = osi.getGraphics();
		osg.setColor(Color.LIGHT_GRAY);			//fillsthe background with gray
		osg.fillRect(0, 0, dim.width, dim.height);
		paint(osg);								//paint everythin on the image
		g.drawImage( osi, 0, 0, this );			//display the image on the screen
	}

	public int clearCheck() {					//checks to see if any lines have been cleared, then
		int out = 0;								//moves all the blocks down and returns the number of lines cleared
		for(int i = 0; i < 22; i++) {
			boolean clear = true;
			for(int j = 0; j < 10; j++) {
				if(grid[j][i] == Color.BLACK) {
					clear = false;
				}
			}
			if(clear) {
				out++;
				for(int start = i; start>0; start--) {
					for(int j = 0; j<10; j++) {
						grid[j][start] = grid[j][start-1];
					}
				}
			}
		}
		return out;
	}

	public void pause () {		//pause and unpausing the game
		paused = true;
	}
	public void unpause () {
		paused = false;
	}

	public void keyPressed(KeyEvent e) {		//user inputs:
		if(!gameOver) {
			int code = e.getKeyCode();
			if(!paused) {
				switch(code) {
					case KeyEvent.VK_RIGHT:		//right moves piece right
						piece.right(grid);
						break;
					case KeyEvent.VK_LEFT:		//left moves piece left
						piece.left(grid);
						break;
					case KeyEvent.VK_UP:		//up rotetes piece clockwise
						piece.rotate(grid, 1);
						break;
					case KeyEvent.VK_DOWN:		//down rotates piece counterclockwise
						piece.rotate(grid, -1);
						break;
					case KeyEvent.VK_SPACE:		//space is a hard drop
						piece.drop(grid);
						piece = next;
						piece.activate();
						next = new Tetris();
						lines += clearCheck();
						gameOver = piece.gameOver(grid);
						break;
					case KeyEvent.VK_Z:			//z will cause the piece to move down one unit
						piece.tick(grid);
						break;
				}
			}
			if(code ==  KeyEvent.VK_P) {		//p pauses and unpauses the game
				if(paused){
					unpause();
				}
				else {
					pause();
				}
			}
			piece.check(grid, false);			//updates and repaints
			repaint();
		}
	}



	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {

	}

	public void mousePressed(MouseEvent e) {	//restarts the game
		if(gameOver) {
			System.out.println("lol");
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 22; j++) {
					grid[i][j] = Color.BLACK;
				}
			}
			piece = new Tetris();
			next = new Tetris();
			piece.activate();
			gameOver = false;
			level = 0;
			lines = 0;
		}


	}

	public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
 	}

}