package Main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class GameWindow {
	private JFrame jframe;
	
	public GameWindow(GamePanel gamePanel) {
		
		jframe = new JFrame();
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Terminación de la ventana
		jframe.add(gamePanel); //Se agrega el cuadro donde se va a hacer todo
		jframe.setLocationRelativeTo(null); //Mueve el sprite
		jframe.setResizable(false);
		jframe.pack();
		jframe.setVisible(true); //Muestra la ventana
		jframe.addWindowFocusListener(new WindowFocusListener() { //Para detectar si se pierde el "enfoque" y volver todos los booleanos falsos para así detener los botones


			@Override
			public void windowGainedFocus(WindowEvent e) {
				gamePanel.getGame().windowFocusLost();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				
			}
			
			
		});
	}
}

