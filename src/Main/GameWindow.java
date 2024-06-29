package Main;

import javax.swing.JFrame;

public class GameWindow {
	private JFrame jframe;
	
	public GameWindow(GamePanel gamePanel) {
		
		jframe = new JFrame();
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Terminación de la ventana
		jframe.add(gamePanel); //Se agrega el cuadro donde se va a hacer todo
		jframe.setLocationRelativeTo(null); //Mueve el rectángulo
		jframe.setResizable(false);
		jframe.pack();
		jframe.setVisible(true); //Muestra la ventana
	}
 
}

