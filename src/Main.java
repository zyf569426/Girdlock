import javax.swing.JFrame;

public class Main {
        public static void main(String[] args){
                JFrame obj = new JFrame();
                Gameplay gamePlay = new Gameplay(); 
                // window width is 300 and height is 330
                obj.setBounds(10,10,300,330); 
                obj.setTitle("GridLock");
                obj.setResizable(false);
                obj.setVisible(true);
                obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                obj.add(gamePlay);
        }
}
