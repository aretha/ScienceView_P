package topicevolutionvis.view.color;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import topicevolutionvis.view.Viewer;

/**
 *
 * @author Fernando Vieira Paulovich
 */
public class ColorScalePanel extends javax.swing.JPanel {

    public ColorScalePanel(Viewer gv) {
        this.gv = gv;

        this.scale = new ColorScalePanel.ColorScale();

        this.maxLabel.setForeground(Color.GRAY);
        this.maxLabel.setFont(new Font("Verdana", Font.BOLD, 10));

        this.minLabel.setForeground(Color.GRAY);
        this.minLabel.setFont(new Font("Verdana", Font.BOLD, 10));

//        this.scale.setBorder(new javax.swing.border.LineBorder(Color.GRAY, 1, true));
//        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        this.setToolTipText("Click to change the color scale");

        this.setLayout(new BorderLayout(5, 5));
        this.add(this.scale, BorderLayout.CENTER);
        this.add(this.maxLabel, BorderLayout.EAST);
        this.add(this.minLabel, BorderLayout.WEST);

        this.addMouseListener(new MouseClickedListener());
    }

    public void setColorTable(ColorTable colorTable) {
        this.colorTable = colorTable;
        this.scale.repaint();
    }

    class MouseClickedListener extends MouseAdapter {

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            super.mouseClicked(evt);
            if (ColorScalePanel.this.gv != null) {
                ColorScaleChange.getInstance(gv.getTopicEvolutionView(), gv).display();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            ColorScalePanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            ColorScalePanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public class ColorScale extends javax.swing.JPanel {

        @Override
        public void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            drawScale(g);
        }
    }

    public void drawScale(Graphics g) {
        if (colorTable != null) {
            Dimension size = scale.getSize();
            int height = size.height;
            int width = size.width;

            for (int i = 0; i < width; i++) {
                float index = ((float) i) / ((float) width);
                g.setColor(colorTable.getColor(index));
                g.drawRect(i, 0, i, height);
                g.fillRect(i, 0, i, height);
            }
        }
    }
    private JLabel maxLabel = new JLabel("Max");
    private JLabel minLabel = new JLabel("Min");
    private ColorScalePanel.ColorScale scale;
    private ColorTable colorTable;
    private Viewer gv;
}
