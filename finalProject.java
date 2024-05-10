import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;//stores pipes
import java.util.Random; //places pipes in random spots
import javax.swing.*;

public class finalProject extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360; //size of the "display"
    int boardHeight = 640;

    // Images // going to store our 4 image variables
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird //These lines of code are declaring and initializing integer variables
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird { //stores information about the bird's position, size, and appearance.
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;

        }
    }

    // Pipes //These lines of code are declaring and initializing integer variables
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        // boolean passed = false;
        public boolean passed;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game //These lines of code are declaring and initializing integer variables
    Bird bird;
    int velocityY = 0; // - = moves bird upwards + down
    int velocityX = -4;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();
    
    //These lines of code are declaring and initializing integer variables
    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    finalProject() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        // load Images
        backgroundImg = new ImageIcon(getClass().getResource("./fbBG.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./FB.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./topPipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottomPipe.png")).getImage();
        // var g = (Graphics2D) topPipeImg.getGraphics();
        // g.rotate(Math.toRadians(180));

        // bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipes
        placePipeTimer = new Timer(1500, new ActionListener() { //1.5 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        // timer
        gameLoop = new Timer(1000 / 60, this); // 1000/60 = 16.6 millisecond
        gameLoop.start();

    }
    //method for placing the pipes
    public void placePipes() { 
        // (0-1) * pipeHeight/2. -> 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        double randomPipeY = (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2)); 
        //This line calculates a random y-coordinate for the pipes. It subtracts a quarter of the pipe's height from the starting y-coordinate (pipeY)
        //, then subtracts a random value between zero and half of the pipe's height. 
        //This effectively positions the top pipe at a random vertical position on the screen.
        int openingSpace = boardHeight / 4;
        //This line calculates the space between the top and bottom pipes, 
        //likely representing the gap through which a player-controlled object can pass.

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = (int) randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);

    }

    public void paintComponet(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    //draw method
    public void draw(Graphics g) {
        System.out.println("Drawn");
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        // bird
        g.drawImage(bird.img, bird.x, bird.y, birdWidth, birdHeight, null);

        // pipes
        for (int i = 0; i < pipes.size(); i++) { // This loop iterates over a list of pipe objects stored in the pipes method
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 40));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 45);
        } else {
            g.drawString(String.valueOf((int) score), 10, 45);
        }
    }

    public void move() {
        // bird
        velocityY += gravity; //This line updates the vertical velocity of the bird (velocityY) by adding the value of gravity to it
        bird.y += velocityY; //This effectively moves the bird upwards or downwards based on its velocity.
        bird.y = Math.max(bird.y, 0); // makes it where the bird cant go out of screen

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX; // It increases the x coordinate of the pipe by adding the value of velocityX

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
            if (bird.x > pipe.x + pipe.width) {
                score += 0.5; // 0.5 because there are 2 pipes, so 0.5*2 = 1, 1 for each set of pipes
                pipe.passed = true;
            }

            if (collision(bird, pipe)) { //if the bird hits a pipe the game ends and frezzes until you press space
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) { //if the bird goes out of frame the game ends
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y; // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE); //this code implements the space key as the key used to move the bird 
        velocityY = -9;
        if (gameOver) { //this code restarts the game from the begining
            bird.y = bird.y;
            velocityY = 0;
            pipes.clear();
            score = 0;
            gameOver = false;
            gameLoop.start();
            placePipeTimer.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
