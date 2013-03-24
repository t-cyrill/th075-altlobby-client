package jp.dip.th075altlobby.imo.System.Icon;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class WindowIcon {
    private static final WindowIcon wi = new WindowIcon();

    public void setIcon(JFrame f) {
        f.setIconImage(getImageIcon());
    }

    private WindowIcon() {
    }

    public Image getImageIcon() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader()
                    .getResource("icon.png"));
            return icon.getImage();
        } catch (Exception e) {
            return Toolkit.getDefaultToolkit().getImage("./icon/icon.png");
        }
    }

    public static WindowIcon getInstance() {
        return wi;
    }
}
