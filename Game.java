import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class Game extends JFrame {
    private static final int TILE_SIZE = 64;
    private static final int WIDTH = 15;
    private static final int HEIGHT = 15;
    private Dungeon dungeon;
    private Player player;

    public int level = 1;

    private Image playerImage;
    private Image wallImage;
    private Image floorImage;
    private Image exitImage;

    private String message;
    private Timer messageTimer;

    public Game() {
        loadImages();
        initializeGame();
        showLevelAnnouncement();
        setTitle("Dungeon Crawler - Level " + level);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                int dx = 0, dy = 0;

                switch (key) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP: dy = -1; break;
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT: dx = -1; break;
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN: dy = 1; break;
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT: dx = 1; break;
                }

                int newX = player.getX() + dx;
                int newY = player.getY() + dy;

                if (newX >= 0 && newX < WIDTH && newY >= 0 && newY < HEIGHT &&
                        (dungeon.getTile(newX, newY) == '.' || dungeon.getTile(newX, newY) == 'E')) {
                    dungeon.setTile(player.getX(), player.getY(), '.');
                    player.move(dx, dy);
                    dungeon.setTile(player.getX(), player.getY(), 'P');
                }

                int[] playerPos = player.getPosition();
                if (playerPos[0] == dungeon.exitX && playerPos[1] == dungeon.exitY) {
                    initializeGame();
                    level++;
                    setTitle("Dungeon Crawler - Level " + level);
                    showLevelAnnouncement();
                }

                repaint();
            }
        });

        pack();
        adjustWindowSize();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadImages() {
        try {
            playerImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/player.png")));
            wallImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/wall.png")));
            floorImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/floor.png")));
            exitImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/exit.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeGame() {
        dungeon = new Dungeon(WIDTH, HEIGHT);
        player = new Player(0, 0);
        dungeon.setTile(player.getX(), player.getY(), 'P');
        dungeon.setTile(dungeon.exitX, dungeon.exitY, 'E');
    }

    private void adjustWindowSize() {
        Insets insets = getInsets();
        int frameWidth = WIDTH * TILE_SIZE + insets.left + insets.right;
        int frameHeight = HEIGHT * TILE_SIZE + insets.top + insets.bottom;
        setSize(frameWidth, frameHeight);
    }

    private void showLevelAnnouncement() {
        message = "Welcome to Level " + level;
        if (messageTimer != null) {
            messageTimer.stop();
        }
        messageTimer = new Timer(2000, e -> {
            message = null;
            repaint();
        });
        messageTimer.setRepeats(false);
        messageTimer.start();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                char tile = dungeon.getTile(x, y);
                Image imageToDraw = null;
                switch (tile) {
                    case '#':
                        imageToDraw = wallImage;
                        break;
                    case '.':
                        imageToDraw = floorImage;
                        break;
                    case 'P':
                        imageToDraw = playerImage;
                        break;
                    case 'E':
                        imageToDraw = exitImage;
                        break;
                }
                if (imageToDraw != null) {
                    g.drawImage(imageToDraw, x * TILE_SIZE + getInsets().left, y * TILE_SIZE + getInsets().top, TILE_SIZE, TILE_SIZE, this);
                }
            }
        }
        if (message != null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Times ", Font.BOLD, 56));
            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(message);
            int messageHeight = fm.getHeight();
            int x = (getWidth() - messageWidth) / 2;
            int y = (getHeight() - messageHeight) / 2 + fm.getAscent();
            g.drawString(message, x, y);
        }
    }
}
