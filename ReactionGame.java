import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class ReactionGame extends JFrame {
    private JButton targetButton;
    private JLabel reactionLabel;
    private JLabel timeLabel;
    private Timer waitTimer;
    private long startTime;
    private Random random = new Random();
    private boolean isWaiting = true;
    private int attemptCount = 0;
    private Clip winSound, loseSound, retrySound;
    private double lastReactionTime = 0;
    private HorrorCursor horrorCursor;
    
    public ReactionGame() {
        setTitle("Refleks Oyunu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // Özel imleci başlat
        horrorCursor = new HorrorCursor();
        setCursor(horrorCursor.getCustomCursor());
        
        // Sesleri yükle
        loadSounds();
        
        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setCursor(horrorCursor.getCustomCursor());
        
        // Skor paneli
        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(Color.BLACK);
        scorePanel.setCursor(horrorCursor.getCustomCursor());
        reactionLabel = new JLabel("Reaksiyon Süresi: --");
        timeLabel = new JLabel("Durum: Hazırlan... (Deneme: 1/5)");
        reactionLabel.setForeground(Color.WHITE);
        timeLabel.setForeground(Color.WHITE);
        reactionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scorePanel.add(reactionLabel);
        scorePanel.add(timeLabel);
        
        // Hedef buton
        targetButton = new JButton("TIKLA!");
        targetButton.setFont(new Font("Arial", Font.BOLD, 24));
        targetButton.setEnabled(false);
        targetButton.setBackground(Color.RED);
        targetButton.setForeground(Color.WHITE);
        targetButton.setFocusPainted(false);
        targetButton.setBorderPainted(false);
        targetButton.setCursor(horrorCursor.getCustomCursor());
        
        // Oyun alanı
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridBagLayout());
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setCursor(horrorCursor.getCustomCursor());
        gamePanel.add(targetButton);
        
        // Buton tıklama olayı
        targetButton.addActionListener(e -> {
            if (!isWaiting) {
                long reactionTime = System.currentTimeMillis() - startTime;
                double reactionTimeInSeconds = reactionTime / 1000.0;
                lastReactionTime = reactionTimeInSeconds;
                reactionLabel.setText(String.format("Reaksiyon Süresi: %.3f saniye", reactionTimeInSeconds));
                
                if (reactionTimeInSeconds < 0.2) {
                    // Başarılı
                    playSound(winSound);
                    JOptionPane.showMessageDialog(this, "Tebrikler! Kazandınız!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new GameMenu().setVisible(true);
                } else {
                    // Başarısız
                    attemptCount++;
                    if (attemptCount >= 5) {
                        playSound(loseSound);
                        JOptionPane.showMessageDialog(this, "Başarısız! 5 deneme hakkınız bitti.", "Oyun Bitti", JOptionPane.ERROR_MESSAGE);
                        dispose();
                        new GameMenu().setVisible(true);
                    } else {
                        playSound(retrySound);
                        timeLabel.setText(String.format("Durum: Tekrar Dene! (Deneme: %d/5)", attemptCount + 1));
                        resetButton();
                        startNewRound();
                    }
                }
            }
        });
        
        // Geri dön butonu
        JButton backButton = new JButton("Ana Menüye Dön");
        backButton.setBackground(new Color(150, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setCursor(horrorCursor.getCustomCursor());
        backButton.addActionListener(e -> {
            dispose();
            new GameMenu().setVisible(true);
        });
        
        // Panelleri ekle
        mainPanel.add(scorePanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Timer'ları başlat
        setupTimers();
        startNewRound();
    }
    
    private void loadSounds() {
        try {
            // Kazanma sesi
            AudioInputStream winStream = AudioSystem.getAudioInputStream(new File("win.wav"));
            winSound = AudioSystem.getClip();
            winSound.open(winStream);
            
            // Kaybetme sesi
            AudioInputStream loseStream = AudioSystem.getAudioInputStream(new File("lose.wav"));
            loseSound = AudioSystem.getClip();
            loseSound.open(loseStream);
            
            // Tekrar deneme sesi
            AudioInputStream retryStream = AudioSystem.getAudioInputStream(new File("tekrar.wav"));
            retrySound = AudioSystem.getClip();
            retrySound.open(retryStream);
        } catch (Exception e) {
            System.out.println("Ses dosyaları yüklenemedi: " + e.getMessage());
        }
    }
    
    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    private void resetButton() {
        targetButton.setEnabled(false);
        targetButton.setBackground(Color.RED);
        isWaiting = true;
        waitTimer.stop();
    }
    
    private void setupTimers() {
        waitTimer = new Timer(1000, e -> {
            if (isWaiting) {
                isWaiting = false;
                targetButton.setEnabled(true);
                targetButton.setBackground(Color.GREEN);
                startTime = System.currentTimeMillis();
                timeLabel.setText(String.format("Durum: TIKLA! (Deneme: %d/5) - Son Süre: %.3f saniye", 
                    attemptCount + 1, lastReactionTime));
            }
        });
    }
    
    private void startNewRound() {
        resetButton();
        timeLabel.setText(String.format("Durum: Hazırlan... (Deneme: %d/5) - Son Süre: %.3f saniye", 
            attemptCount + 1, lastReactionTime));
        
        // Rastgele konum
        int x = random.nextInt(400);
        int y = random.nextInt(200);
        targetButton.setLocation(x, y);
        
        // Rastgele bekleme süresi (1-3 saniye)
        int waitTime = 1000 + random.nextInt(2000);
        waitTimer.setInitialDelay(waitTime);
        waitTimer.restart();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReactionGame().setVisible(true);
        });
    }
} 