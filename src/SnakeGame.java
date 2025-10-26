import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x, y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // board
    int boardHeight;
    int boardWidth;
    int tileSize = 25;
    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;
    // food
    Tile food;
    Random random;

    // Game logic
    Timer gameLoop;
    int velocityX, velocityY;
    boolean gameOver = false;

    // score
    int score = 0;

    // constructor
    SnakeGame(int boardHeight, int boardWidth) {
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;

        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        // Snake
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        // food
        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        // Movement
        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        // Grid
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
            super.paintComponent(g);

            if (gameOver) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("Game Over!", boardWidth / 2 - 100, boardHeight / 2);
            }

        }
        // Snake
        g.setColor(Color.red);
        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }

        // Food
        g.setColor(Color.white);
        g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);
    }

    public void placeFood() {

        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        // Store tail position for growth
        Tile tail = null;
        if (!snakeBody.isEmpty()) {
            Tile last = snakeBody.get(snakeBody.size() - 1);
            tail = new Tile(last.x, last.y);
        }

        // Move body
        for (int i = snakeBody.size() - 1; i >= 1; i--) {
            Tile prev = snakeBody.get(i - 1);
            snakeBody.get(i).x = prev.x;
            snakeBody.get(i).y = prev.y;
        }
        if (!snakeBody.isEmpty()) {
            snakeBody.get(0).x = snakeHead.x;
            snakeBody.get(0).y = snakeHead.y;
        }

        // Move head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Check food collision after moving
        if (collision(snakeHead, food)) {
            score++;
            if (tail != null) {
                snakeBody.add(tail); // grow at tail
            } else {
                // first growth
                int newX = snakeHead.x - velocityX;
                int newY = snakeHead.y - velocityY;
                snakeBody.add(new Tile(newX, newY));
            }
            placeFood();
        }

        // Game over
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            if (collision(snakeHead, snakePart)) {
                gameOver = true;

            }
        }

        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardWidth || snakeHead.y * tileSize < 0
                || snakeHead.y * tileSize > boardHeight) {
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }

        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            resetGame();
        }
    }

    public void resetGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        velocityX = 0;
        velocityY = 0;
        score = 0;
        placeFood();
        gameOver = false;
        gameLoop.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}