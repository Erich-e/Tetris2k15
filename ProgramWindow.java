import java.awt.*;
import java.awt.event.*;

/*
This class represents the window where tetris takes place
*/

public class ProgramWindow extends Frame implements WindowListener
{
	private TetrisPanel panel = new TetrisPanel() ;		// create a panel where the grid will be stored and painting will be done

	public ProgramWindow( )								//initializes aspects of the wintow
  	{
		setTitle ("Tetris") ;
  		setSize	(325, 420) ;
		setResizable( true ) ;
    	add( panel ) ;
		setVisible (true) ;
		addWindowListener(this);
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {			//closes the window
		System.exit(0);
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {		//unpauses the game if the user re-activates the window
		panel.unpause();
		repaint();
	}

	public void windowDeactivated(WindowEvent e) {		//pauses the gae if the user de-activates the window
		panel.pause();
		panel.repaint();
	}

}