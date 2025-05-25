import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.util.Random;

public class HorrorMazeGame extends JFrame {
    private static final int CELL_SIZE = 20;
    private static final int MAZE_WIDTH = 25;
    private static final int MAZE_HEIGHT = 25;
    
    private JPanel mazePanel;
    private boolean[][] walls;
    private int playerX, playerY;
    private int exitX, exitY;
    private Timer gameTimer;
    private int timeLeft;
    private JLabel timeLabel;
    private JLabel levelLabel;
    private Clip backgroundMusic;
    private Clip screamSound;
    private Clip loseSound;
    private Random random;
    private int currentLevel = 1;
    private static final int TOTAL_LEVELS = 5;
    private HorrorCursor horrorCursor;
    
    public HorrorMazeGame() {
        setTitle("Korku Labirenti");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        
        // Özel imleci başlat
        horrorCursor = new HorrorCursor();
        setCursor(horrorCursor.getCustomCursor());
        
        random = new Random();
        setupUI();
        initializeGame();
        loadSounds();
        startGame();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Üst panel
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(Color.BLACK);
        topPanel.setCursor(horrorCursor.getCustomCursor());
        
        // Seviye göstergesi
        levelLabel = new JLabel("Bölüm: 1/" + TOTAL_LEVELS);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelLabel.setForeground(Color.GREEN);
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(levelLabel);
        
        // Zaman göstergesi
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timeLabel.setForeground(Color.RED);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(timeLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Labirent paneli
        mazePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMaze(g);
            }
        };
        mazePanel.setPreferredSize(new Dimension(MAZE_WIDTH * CELL_SIZE, MAZE_HEIGHT * CELL_SIZE));
        mazePanel.setBackground(Color.BLACK);
        mazePanel.setCursor(horrorCursor.getCustomCursor());
        add(mazePanel, BorderLayout.CENTER);
        
        // Klavye kontrolü
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int newX = playerX;
                int newY = playerY;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP: newY--; break;
                    case KeyEvent.VK_DOWN: newY++; break;
                    case KeyEvent.VK_LEFT: newX--; break;
                    case KeyEvent.VK_RIGHT: newX++; break;
                }
                
                if (isValidMove(newX, newY)) {
                    playerX = newX;
                    playerY = newY;
                    mazePanel.repaint();
                    
                    if (playerX == exitX && playerY == exitY) {
                        levelCompleted();
                    }
                }
            }
        });
        
        setFocusable(true);
    }
    
    private void initializeGame() {
        // Labirenti oluştur
        walls = new boolean[MAZE_WIDTH][MAZE_HEIGHT];
        generateMaze();
        
        // Oyuncuyu başlangıç noktasına yerleştir
        playerX = 1;
        playerY = 1;
        
        // Çıkış noktasını belirle
        exitX = MAZE_WIDTH - 2;
        exitY = MAZE_HEIGHT - 2;
        
        // Seviyeye göre süreyi ayarla
        setTimeForLevel();
    }
    
    private void setTimeForLevel() {
        switch (currentLevel) {
            case 1: timeLeft = 30; break;  // 30 saniye
            case 2: timeLeft = 25; break;  // 25 saniye
            case 3: timeLeft = 20; break;  // 20 saniye
            case 4: timeLeft = 15; break;  // 15 saniye
            case 5: timeLeft = 7; break;   // 7 saniye
        }
        updateTimeLabel();
    }
    
    private void generateMaze() {
        // Başlangıçta tüm duvarları boş yap
        for (int x = 0; x < MAZE_WIDTH; x++) {
            for (int y = 0; y < MAZE_HEIGHT; y++) {
                walls[x][y] = false;
            }
        }
        
        // Dış duvarları oluştur
        for (int x = 0; x < MAZE_WIDTH; x++) {
            walls[x][0] = true;
            walls[x][MAZE_HEIGHT - 1] = true;
        }
        for (int y = 0; y < MAZE_HEIGHT; y++) {
            walls[0][y] = true;
            walls[MAZE_WIDTH - 1][y] = true;
        }
        
        // Önce çıkış yolunu oluştur
        createPathToExit();
        
        // Sonra rastgele duvarlar ekle
        addRandomWalls();
    }
    
    private void createPathToExit() {
        int currentX = playerX;
        int currentY = playerY;
        
        // Yatay hareket
        while (currentX != exitX) {
            if (currentX < exitX) {
                currentX++;
            } else {
                currentX--;
            }
            // Yolun etrafına daha fazla duvar ekleme
            if (currentY > 1) {
                walls[currentX][currentY - 1] = true;
                if (currentY > 2) walls[currentX][currentY - 2] = true;
            }
            if (currentY < MAZE_HEIGHT - 2) {
                walls[currentX][currentY + 1] = true;
                if (currentY < MAZE_HEIGHT - 3) walls[currentX][currentY + 2] = true;
            }
        }
        
        // Dikey hareket
        while (currentY != exitY) {
            if (currentY < exitY) {
                currentY++;
            } else {
                currentY--;
            }
            // Yolun etrafına daha fazla duvar ekleme
            if (currentX > 1) {
                walls[currentX - 1][currentY] = true;
                if (currentX > 2) walls[currentX - 2][currentY] = true;
            }
            if (currentX < MAZE_WIDTH - 2) {
                walls[currentX + 1][currentY] = true;
                if (currentX < MAZE_WIDTH - 3) walls[currentX + 2][currentY] = true;
            }
        }
    }
    
    private void addRandomWalls() {
        // Rastgele duvarlar ekle (duvar sayısını artırdık)
        for (int i = 0; i < MAZE_WIDTH * MAZE_HEIGHT / 4; i++) { // 6 yerine 4'e böldük
            int x = random.nextInt(MAZE_WIDTH - 2) + 1;
            int y = random.nextInt(MAZE_HEIGHT - 2) + 1;
            
            // Başlangıç ve bitiş noktalarına yakın duvar ekleme
            if (Math.abs(x - playerX) > 4 || Math.abs(y - playerY) > 4) { // Güvenli mesafeyi artırdık
                if (Math.abs(x - exitX) > 4 || Math.abs(y - exitY) > 4) {
                    // Duvar ekle
                    walls[x][y] = true;
                    
                    // Eğer bu duvar yolu tamamen kapatıyorsa, geri al
                    if (!isPathPossible()) {
                        walls[x][y] = false;
                    }
                }
            }
        }
    }
    
    private boolean isPathPossible() {
        boolean[][] visited = new boolean[MAZE_WIDTH][MAZE_HEIGHT];
        return findPath(playerX, playerY, visited);
    }
    
    private boolean findPath(int x, int y, boolean[][] visited) {
        if (x == exitX && y == exitY) {
            return true;
        }
        
        if (x < 0 || x >= MAZE_WIDTH || y < 0 || y >= MAZE_HEIGHT || walls[x][y] || visited[x][y]) {
            return false;
        }
        
        visited[x][y] = true;
        
        // Dört yöne git
        return findPath(x + 1, y, visited) || // Sağ
               findPath(x - 1, y, visited) || // Sol
               findPath(x, y + 1, visited) || // Aşağı
               findPath(x, y - 1, visited);   // Yukarı
    }
    
    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < MAZE_WIDTH && y >= 0 && y < MAZE_HEIGHT && !walls[x][y];
    }
    
    private void drawMaze(Graphics g) {
        // Duvarları çiz
        g.setColor(new Color(100, 0, 0));
        for (int x = 0; x < MAZE_WIDTH; x++) {
            for (int y = 0; y < MAZE_HEIGHT; y++) {
                if (walls[x][y]) {
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        
        // Oyuncuyu çiz
        g.setColor(Color.GREEN);
        g.fillOval(playerX * CELL_SIZE + 3, playerY * CELL_SIZE + 3, 
                   CELL_SIZE - 6, CELL_SIZE - 6);
        
        // Çıkışı çiz (daha belirgin)
        g.setColor(Color.RED);
        g.fillRect(exitX * CELL_SIZE - 3, exitY * CELL_SIZE - 3, 
                   CELL_SIZE + 6, CELL_SIZE + 6);
        
        // Çıkış etrafına parlak çerçeve ekle
        g.setColor(Color.YELLOW);
        g.drawRect(exitX * CELL_SIZE - 5, exitY * CELL_SIZE - 5, 
                   CELL_SIZE + 10, CELL_SIZE + 10);
    }
    
    private void loadSounds() {
        try {
            // Arka plan müziği
            File musicFile = new File("menu_background.wav");
            AudioInputStream musicStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(musicStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            
            // Çığlık sesi
            File screamFile = new File("scream.wav");
            AudioInputStream screamStream = AudioSystem.getAudioInputStream(screamFile);
            screamSound = AudioSystem.getClip();
            screamSound.open(screamStream);
            
            // Lose sesi
            File loseFile = new File("lose.wav");
            AudioInputStream loseStream = AudioSystem.getAudioInputStream(loseFile);
            loseSound = AudioSystem.getClip();
            loseSound.open(loseStream);
        } catch (Exception e) {
            System.out.println("Ses yükleme hatası: " + e.getMessage());
        }
    }
    
    private void startGame() {
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            updateTimeLabel();
            
            if (timeLeft <= 0) {
                gameOver();
            }
        });
        gameTimer.start();
    }
    
    private void updateTimeLabel() {
        timeLabel.setText(String.format("Kalan Süre: %d saniye", timeLeft));
        levelLabel.setText(String.format("Bölüm: %d/%d", currentLevel, TOTAL_LEVELS));
    }
    
    private void levelCompleted() {
        gameTimer.stop();
        
        if (currentLevel < TOTAL_LEVELS) {
            // Sonraki seviyeye geç
            currentLevel++;
            JOptionPane.showMessageDialog(this,
                "Tebrikler! " + (currentLevel - 1) + ". bölümü tamamladınız!\n" +
                "Sıradaki bölüme geçiliyor...",
                "Bölüm Tamamlandı",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Yeni seviye için oyunu sıfırla
            initializeGame();
            startGame();
        } else {
            // Oyunu kazan
            gameWon();
        }
    }
    
    private void gameOver() {
        gameTimer.stop();
        playLoseSound();
        JOptionPane.showMessageDialog(this,
            "Süre doldu! Kaybettiniz!\n" +
            "Tamamlanan Bölüm: " + (currentLevel - 1) + "/" + TOTAL_LEVELS,
            "Oyun Bitti",
            JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
    
    private void gameWon() {
        gameTimer.stop();
        JOptionPane.showMessageDialog(this,
            "Tebrikler! Tüm bölümleri başarıyla tamamladınız!",
            "Oyun Bitti",
            JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
    
    private void playScreamSound() {
        if (screamSound != null) {
            screamSound.setFramePosition(0);
            screamSound.start();
        }
    }
    
    private void playLoseSound() {
        if (loseSound != null) {
            loseSound.setFramePosition(0);
            loseSound.start();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HorrorMazeGame().setVisible(true);
        });
    }
} 