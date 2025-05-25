import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class HorrorCursor {
    private BufferedImage cursorImage;
    private Cursor customCursor;
    private Timer animationTimer;
    private float glowIntensity = 0.0f;
    private boolean increasing = true;

    public HorrorCursor() {
        // Animasyon zamanlayıcısını başlat
        animationTimer = new Timer(50, e -> {
            updateGlow();
            createHorrorCursor();
        });
        animationTimer.start();

        // Özel imleç oluştur
        createHorrorCursor();
    }

    private void updateGlow() {
        if (increasing) {
            glowIntensity += 0.1f;
            if (glowIntensity >= 1.0f) {
                increasing = false;
            }
        } else {
            glowIntensity -= 0.1f;
            if (glowIntensity <= 0.0f) {
                increasing = true;
            }
        }
    }

    private void createHorrorCursor() {
        // 48x48 boyutunda bir imleç görüntüsü oluştur
        cursorImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = cursorImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Kemik parmak şekli çiz
        g2d.setColor(new Color(200, 200, 200, 200)); // Kemik rengi
        g2d.setStroke(new BasicStroke(3));
        
        // Parmak şekli - boyutları artırıldı
        int[] xPoints = {24, 30, 36, 36, 30, 24, 18, 18};
        int[] yPoints = {6, 6, 12, 36, 42, 42, 36, 12};
        g2d.fillPolygon(xPoints, yPoints, 8);
        
        // Tırnak detayı
        g2d.setColor(new Color(150, 150, 150, 200));
        g2d.fillOval(21, 3, 12, 9);

        // Kan efekti
        g2d.setColor(new Color(150, 0, 0, 150));
        g2d.fillOval(27, 30, 12, 12);

        // Animasyonlu kırmızı parıltı efekti
        int alpha = (int)(100 + (glowIntensity * 155));
        g2d.setColor(new Color(255, 0, 0, alpha));
        g2d.fillOval(24, 24, 18, 18);

        // Ek kan damlaları
        g2d.setColor(new Color(150, 0, 0, 100));
        g2d.fillOval(30, 36, 6, 6);
        g2d.fillOval(33, 39, 4, 4);

        g2d.dispose();

        // İmleç noktasını ayarla
        Point hotSpot = new Point(24, 24);
        customCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImage, hotSpot, "Horror Cursor");
    }

    public Cursor getCustomCursor() {
        return customCursor;
    }
} 