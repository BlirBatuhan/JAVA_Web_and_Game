import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;

public class GameMenu extends JFrame {
    private Clip menuMusic;
    
    public GameMenu() {
        setTitle("Korku Oyunları");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        
        // Arka plan müziğini başlat
        loadMenuMusic();
        
        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 10, 10));
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Hafıza Oyunu butonu
        JButton memoryButton = createMenuButton("Hafıza Oyunu");
        memoryButton.addActionListener(e -> {
            stopMenuMusic();
            new HorrorMemoryGame().setVisible(true);
            this.dispose();
        });
        
        // Labirent Oyunu butonu
        JButton mazeButton = createMenuButton("Korku Labirenti");
        mazeButton.addActionListener(e -> {
            stopMenuMusic();
            new HorrorMazeGame().setVisible(true);
            this.dispose();
        });
        
        // Refleks Oyunu butonu
        JButton reactionButton = createMenuButton("Refleks Oyunu");
        reactionButton.addActionListener(e -> {
            stopMenuMusic();
            new ReactionGame().setVisible(true);
            this.dispose();
        });
        
        mainPanel.add(memoryButton);
        mainPanel.add(mazeButton);
        mainPanel.add(reactionButton);
        
        add(mainPanel);
        
        // Pencere kapatıldığında müziği durdur
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopMenuMusic();
            }
        });
    }
    
    private void loadMenuMusic() {
        try {
            File musicFile = new File("menu_background.wav");
            AudioInputStream musicStream = AudioSystem.getAudioInputStream(musicFile);
            menuMusic = AudioSystem.getClip();
            menuMusic.open(musicStream);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println("Menü müziği yüklenemedi: " + e.getMessage());
        }
    }
    
    private void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.close();
        }
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(150, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover efekti
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(200, 0, 0));
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(150, 0, 0));
            }
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameMenu().setVisible(true);
        });
    }
} 