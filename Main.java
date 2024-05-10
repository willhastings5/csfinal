import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;
        
        JFrame frame = new JFrame("Flappy Bird");
        // frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false); //so no one can resize the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // when you hit X it closes the whole program
        frame.setSize(boardWidth, boardHeight);

        finalProject finalproject = new finalProject();
        frame.add(finalproject);
        frame.pack();
        // finalProject.requestFocus();
        frame.setVisible(true);
    
    }
}
