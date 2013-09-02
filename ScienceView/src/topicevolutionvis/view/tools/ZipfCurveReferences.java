/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.view.tools;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import topicevolutionvis.preprocessing.Reference;

/**
 *
 * @author Aretha
 */
public class ZipfCurveReferences extends javax.swing.JPanel {

    private int upperLine;
    private int lowerLine;
    private BufferedImage imageBuffer;
    private ArrayList<Reference> references;

    public ZipfCurveReferences() {
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                drawImage();
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.drawImage(this.imageBuffer, 0, 0, this.getWidth(), this.getHeight(), null);
    }

    public int[] setCutLines(int lower, int upper) {
        Reference ref;
        boolean search_upper = true;
        this.lowerLine = -1;
        for (int i = 0; i < references.size(); i++) {
            ref = references.get(i);
            if (search_upper && ref.frequency <= upper) {
                this.upperLine = i;
                search_upper = false;
            }
            if (ref.frequency < lower) {
                this.lowerLine = i - 1;
                break;
            }
        }
        if (lowerLine == -1) {
            lowerLine = references.size() - 1;
        }

        this.drawImage();
        this.repaint();

        int[] freqs = new int[2];
        freqs[0] = this.upperLine;
        freqs[1] = this.lowerLine;

        return freqs;
    }

    private void drawImage() {
        this.imageBuffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics gBuffer = this.imageBuffer.createGraphics();

        java.awt.Dimension size = this.getSize();
        gBuffer.setColor(java.awt.Color.WHITE);
        gBuffer.fillRect(0, 0, size.width, size.height);

        int grid = 40;
        for (int i = 0; i < grid; i++) {
            gBuffer.setColor(java.awt.Color.LIGHT_GRAY);

            int col = (int) ((((float) size.width) / grid) * (i + 1));
            gBuffer.drawLine(col, 0, col, size.height);

            int lin = (int) ((((float) size.height) / grid) * (i + 1));
            gBuffer.drawLine(0, lin, size.width, lin);
        }

        gBuffer.setColor(java.awt.Color.BLACK);
        gBuffer.drawRect(0, 0, size.width - 1, size.height - 1);

        if (references != null) {
            int nelements = references.size();
            float maxf = references.get(0).frequency;
            float minf = references.get(0).frequency;

            for (int i = 1; i < nelements; i++) {
                if (references.get(i).frequency > maxf) {
                    maxf = references.get(i).frequency;
                } else if (references.get(i).frequency < minf) {
                    minf = references.get(i).frequency;
                }
            }

            maxf = (float) Math.log(maxf);
            minf = (float) Math.log(minf);

            for (int i = 0; i < nelements - 1; i++) {
                int posx1 = (int) ((((float) i) / nelements) * (size.width - 40)) + 20;
                int posy1 = (int) ((((Math.log(references.get(i).frequency) - minf)) / (maxf - minf)) * (size.height - 40)) + 20;

                int posx2 = (int) ((((float) (i + 1)) / nelements) * (size.width - 40)) + 20;
                int posy2 = (int) ((((Math.log(references.get(i + 1).frequency) - minf)) / (maxf - minf)) * (size.height - 40)) + 20;

                gBuffer.setColor(java.awt.Color.RED);
                gBuffer.drawLine(posx1, size.height - posy1, posx2, size.height - posy2);
            }

            int posL1 = (int) ((((float) upperLine) / nelements) * (size.width - 40)) + 20;
            int posL2 = (int) ((((float) lowerLine) / nelements) * (size.width - 40)) + 20;

            if ((posL2 - posL1) >= 0) {
                gBuffer.setColor(java.awt.Color.BLUE);
                //gBuffer.drawLine(posL1, 20, posL1, size.height-10);
                //gBuffer.drawString("UPPER", posL1+5, 40);
                //gBuffer.drawLine(posL2, 20, posL2, size.height-10);
                //gBuffer.drawString("LOWER", posL2+5, size.height-20);

                java.awt.Graphics2D g2 = (java.awt.Graphics2D) gBuffer;
                gBuffer.drawRect(posL1, 20, Math.abs(posL2 - posL1), size.height - 30);
                g2.setComposite(AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.2f));
                g2.fill(new Rectangle(posL1, 20, Math.abs(posL2 - posL1), size.height - 30));
            } else {
                gBuffer.drawString("ERROR", size.width / 2, size.height / 2);
            }
        }
    }

    public void setReferences(ArrayList<Reference> references) {
        this.references = references;
        this.drawImage();
        this.repaint();
    }
}
