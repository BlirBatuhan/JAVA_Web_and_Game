import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.net.URL;
import java.io.InputStream;

public class HorrorMemoryGame extends JFrame {
    private JPanel gamePanel;
    private ArrayList<Card> cards;
    private Card firstCard = null;
    private Card secondCard = null;
    private Timer timer;
    private Clip clickSound;
    private Clip screamSound;
    private Clip trueSound;
    private Clip winSound;
    private ImageIcon[] cardImages;
    private ImageIcon backImage;
    private int currentLevel = 1;
    private JPanel menuPanel;
    private HorrorCursor horrorCursor;

    private class Card extends JButton {
        private int imageIndex;
        private boolean isFlipped = false;
        private boolean isMatched = false;

        public Card(int imageIndex) {
            this.imageIndex = imageIndex;
            setPreferredSize(new Dimension(100, 100));
            setBackground(new Color(51, 51, 51));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setIcon(backImage);
            setCursor(horrorCursor.getCustomCursor());
            addActionListener(e -> flipCard());
        }

        public void flipCard() {
            if (!isFlipped && !isMatched && firstCard != this) {
                playClickSound();
                isFlipped = true;
                setIcon(cardImages[imageIndex]);

                if (firstCard == null) {
                    firstCard = this;
                } else {
                    secondCard = this;
                    checkMatch();
                }
            }
        }

        public void reset() {
            isFlipped = false;
            setIcon(backImage);
        }

        public void setMatched() {
            isMatched = true;
            setBackground(new Color(0, 100, 0));
        }
    }

    public HorrorMemoryGame() {
        setTitle("Korku Temalı Eşleştirme Oyunu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Özel imleci başlat
        horrorCursor = new HorrorCursor();
        setCursor(horrorCursor.getCustomCursor());

        createMenuPanel();
        loadImages();
        loadSounds();

        gamePanel = new JPanel();
        gamePanel.setBackground(Color.BLACK);

        createCards();

        timer = new Timer(1000, e -> {
            if (firstCard != null && secondCard != null) {
                if (firstCard.imageIndex != secondCard.imageIndex) {
                    firstCard.reset();
                    secondCard.reset();
                    playScreamSound();
                } else {
                    firstCard.setMatched();
                    secondCard.setMatched();
                    playTrueSound();
                    boolean allMatched = true;
                    for (Card card : cards) {
                        if (!card.isMatched) {
                            allMatched = false;
                            break;
                        }
                    }
                    if (allMatched) {
                        playWinSound();
                        showLevelCompleteDialog();
                    }
                }
                firstCard = null;
                secondCard = null;
            }
        });
        timer.setRepeats(false);
    }

    private void createMenuPanel() {
        menuPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        menuPanel.setBackground(Color.BLACK);
        JButton level1Button = createMenuButton("Bölüm 1 (4x4)");
        JButton level2Button = createMenuButton("Bölüm 2 (6x6)");
        level1Button.addActionListener(e -> startLevel(1));
        level2Button.addActionListener(e -> startLevel(2));
        menuPanel.add(level1Button);
        menuPanel.add(level2Button);
        add(menuPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(150, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setCursor(horrorCursor.getCustomCursor());
        return button;
    }

    private void startLevel(int level) {
        currentLevel = level;
        menuPanel.setVisible(false);
        if (gamePanel.getParent() == null) {
            add(gamePanel, BorderLayout.CENTER);
        }
        gamePanel.setVisible(true);
        startGame();
    }

    private void showLevelCompleteDialog() {
        JOptionPane.showMessageDialog(this,
            "Tebrikler! Bölümü tamamladınız!",
            "Bölüm Tamamlandı",
            JOptionPane.INFORMATION_MESSAGE);
        menuPanel.setVisible(true);
        gamePanel.setVisible(false);
    }

    private void loadImages() {
        try {
            backImage = new ImageIcon("back.jpeg");
            Image backImg = backImage.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            backImage = new ImageIcon(backImg);

            int imageCount = currentLevel == 1 ? 8 : 18;
            cardImages = new ImageIcon[imageCount];
            
            String[] imageNames = {
                "Ghost.png", "skull.png", "bat.png", "spider.png",
                "zombie.jpeg", "brain.jpeg", "blood.jpeg", "coffin.jpeg",
                "witch.png", "vampire.png", "mummy.png", "frankenstein.png",
                "ghost2.png", "skull2.png", "bat2.png", "spider2.png",
                "zombie2.jpeg", "brain2.jpeg"
            };
            
            for (int i = 0; i < imageCount; i++) {
                ImageIcon original = new ImageIcon(imageNames[i]);
                Image img = original.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                cardImages[i] = new ImageIcon(img);
            }
        } catch (Exception e) {
            System.out.println("Resim yükleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSounds() {
        try {
            File clickFile = new File("click.wav");
            AudioInputStream clickStream = AudioSystem.getAudioInputStream(clickFile);
            clickSound = AudioSystem.getClip();
            clickSound.open(clickStream);
            System.out.println("Tıklama sesi yüklendi");

            File screamFile = new File("scream.wav");
            AudioInputStream screamStream = AudioSystem.getAudioInputStream(screamFile);
            screamSound = AudioSystem.getClip();
            screamSound.open(screamStream);
            System.out.println("Korku sesi yüklendi");

            File trueFile = new File("true.wav");
            AudioInputStream trueStream = AudioSystem.getAudioInputStream(trueFile);
            trueSound = AudioSystem.getClip();
            trueSound.open(trueStream);
            System.out.println("Doğru eşleştirme sesi yüklendi");

            File winFile = new File("win.wav");
            AudioInputStream winStream = AudioSystem.getAudioInputStream(winFile);
            winSound = AudioSystem.getClip();
            winSound.open(winStream);
            System.out.println("Kazanma sesi yüklendi");

        } catch (Exception e) {
            System.out.println("Ses yükleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            try {
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                System.out.println("Ses çalma hatası: " + e.getMessage());
            }
        }
    }

    private void playClickSound() {
        playSound(clickSound);
    }

    private void playScreamSound() {
        playSound(screamSound);
    }

    private void playTrueSound() {
        playSound(trueSound);
    }

    private void playWinSound() {
        playSound(winSound);
    }

    private void createCards() {
        cards = new ArrayList<>();
        int cardCount = currentLevel == 1 ? 16 : 36;
        int imageCount = currentLevel == 1 ? 8 : 18;
        
        for (int i = 0; i < imageCount; i++) {
            cards.add(new Card(i));
            cards.add(new Card(i));
        }
    }

    private void startGame() {
        Collections.shuffle(cards);
        
        gamePanel.removeAll();
        int gridSize = currentLevel == 1 ? 4 : 6;
        gamePanel.setLayout(new GridLayout(gridSize, gridSize, 5, 5));
        
        for (Card card : cards) {
            card.reset();
            gamePanel.add(card);
        }
        
        gamePanel.setVisible(true);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    private void checkMatch() {
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HorrorMemoryGame().setVisible(true);
        });
    }
} 