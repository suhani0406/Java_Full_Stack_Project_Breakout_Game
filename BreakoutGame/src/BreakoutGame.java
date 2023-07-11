import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutGame extends JFrame implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BALL_DIAMETER = 20;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 20;
    private static final int BRICK_WIDTH = 60;
    private static final int BRICK_HEIGHT = 20;
    private static final int BRICK_ROWS = 5;
    private static final int BRICK_COLUMNS = 10;
    private static final int INITIAL_X = 350;
    private static final int INITIAL_Y = 500;
    private static final int DELAY = 10;
    private static final int MAX_LIVES = 3;

    private Timer timer;
    private boolean gameRunning;
    private boolean gamePaused;
    private int ballX, ballY, ballDeltaX, ballDeltaY;
    private int paddleX, paddleY;
    private boolean[][] bricks;
    private String playerName;
    private int score;
    private int lives;

    private JButton pauseResumeButton;
    private JLabel scoreLabel;
    private JLabel livesLabel;

    public BreakoutGame() {
        setTitle("Breakout Ball Game");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Player name input
        playerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player";
        }

        // Control panel
        JPanel controlPanel = new JPanel();
        pauseResumeButton = new JButton("Pause");
        pauseResumeButton.addActionListener(this);
        controlPanel.add(pauseResumeButton);
        scoreLabel = new JLabel("Score: 0");
        controlPanel.add(scoreLabel);
        livesLabel = new JLabel("Lives: " + MAX_LIVES);
        controlPanel.add(livesLabel);
        add(controlPanel, BorderLayout.SOUTH);

        // Initialize game objects
        paddleX = INITIAL_X;
        paddleY = INITIAL_Y;
        ballX = INITIAL_X;
        ballY = INITIAL_Y - BALL_DIAMETER;
        ballDeltaX = -1;
        ballDeltaY = -2;
        bricks = new boolean[BRICK_ROWS][BRICK_COLUMNS];
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLUMNS; j++) {
                bricks[i][j] = true;
            }
        }
        lives = MAX_LIVES;
    }
    
    private void resetBall() {
        ballX = INITIAL_X;
        ballY = INITIAL_Y - BALL_DIAMETER;
        ballDeltaX = -1;
        ballDeltaY = -2;
        paddleX = INITIAL_X;
        paddleY = INITIAL_Y;
    }

    private void startGame() {
        // Game panel
        GamePanel gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // Start game loop
        gameRunning = true;
        gamePaused = false;
        timer = new Timer(DELAY, this);
        timer.start();

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pauseResumeButton) {
            if (gamePaused) {
                pauseResumeButton.setText("Pause");
                gamePaused = false;
                timer.start();
            } else {
                pauseResumeButton.setText("Resume");
                gamePaused = true;
                timer.stop();
            }
        } else {
            if (gameRunning && !gamePaused) {
                // Move ball
                ballX += ballDeltaX;
                ballY += ballDeltaY;

                // Check collision with paddle
                if (ballY + BALL_DIAMETER >= paddleY && ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
                    ballDeltaY = -ballDeltaY;
                }

                // Check collision with bricks
                for (int i = 0; i < BRICK_ROWS; i++) {
                    for (int j = 0; j < BRICK_COLUMNS; j++) {
                        if (bricks[i][j] && ballY <= (i + 1) * BRICK_HEIGHT && ballY >= i * BRICK_HEIGHT && ballX >= j * BRICK_WIDTH && ballX <= (j + 1) * BRICK_WIDTH) {
                            bricks[i][j] = false;
                            ballDeltaY = -ballDeltaY;
                            score += 10;
                            scoreLabel.setText("Score: " + score);
                        }
                    }
                }

                // Check collision with walls
                if (ballX <= 0 || ballX >= WIDTH - BALL_DIAMETER) {
                    ballDeltaX = -ballDeltaX;
                }

                if (ballY <= 0) {
                    ballDeltaY = -ballDeltaY;
                }
                
                
             // Check if the ball falls to the ground
                if (ballY >= HEIGHT) {
                    lives--;
                    livesLabel.setText("Lives: " + lives);
                    resetBall();
                }

                // Check if all bricks are destroyed
                boolean allBricksDestroyed = true;
                for (int i = 0; i < BRICK_ROWS; i++) {
                    for (int j = 0; j < BRICK_COLUMNS; j++) {
                        if (bricks[i][j]) {
                            allBricksDestroyed = false;
                            break;
                        }
                    }
                }
                
             // Check if the player loses all lives
                if (lives == 0) {
                    gameRunning = false;
                    timer.stop();
                    JOptionPane.showMessageDialog(this, "Game Over!\nYour score is: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }

                if (allBricksDestroyed) {
                    gameRunning = false;
                    timer.stop();
                    JOptionPane.showMessageDialog(this, "Congratulations, " + playerName + "! You won!\nYour score is: " + score);
                    System.exit(0);
                }

                // Repaint screen
                repaint();
            }
        }
    }

    private class GamePanel extends JPanel {
        public GamePanel() {
            setFocusable(true);
            addKeyListener(new PaddleListener());
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (gameRunning) {
                // Draw ball
                g.setColor(Color.RED);
                g.fillOval(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);

                // Draw paddle
                g.setColor(Color.BLUE);
                g.fillRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

                // Draw bricks
                for (int i = 0; i < BRICK_ROWS; i++) {
                    for (int j = 0; j < BRICK_COLUMNS; j++) {
                        if (bricks[i][j]) {
                            g.setColor(Color.GREEN);
                            g.fillRect(j * BRICK_WIDTH, i * BRICK_HEIGHT, BRICK_WIDTH, BRICK_HEIGHT);
                        }
                    }
                }
            } else {
                // Game over
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2);
            }
        }
    }

    private class PaddleListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_LEFT && paddleX > 0) {
                paddleX -= 10;
            }

            if (keyCode == KeyEvent.VK_RIGHT && paddleX < WIDTH - PADDLE_WIDTH) {
                paddleX += 10;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BreakoutGame game = new BreakoutGame();
                game.startGame();
            }
        });
    }
}
