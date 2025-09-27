import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PatternDrawing extends JFrame {
   
    enum RectStyle { DOTTED, THICK }
    enum DiamondStyle { SOLID, DASHED }

    private RectStyle rectStyle = RectStyle.DOTTED;
    private DiamondStyle diamondStyle = DiamondStyle.SOLID;

    private DrawPanel drawPanel;

    public PatternDrawing() {
        super("Pattern Drawing: DDA Rectangle + Bresenham Diamond");

        drawPanel = new DrawPanel();
        this.getContentPane().add(drawPanel, BorderLayout.CENTER);

        JMenuBar mb = new JMenuBar();
        JMenu mRect = new JMenu("Rectangle (DDA)");
        JMenu mDiamond = new JMenu("Diamond (Bresenham)");

        JMenuItem miRectDotted = new JMenuItem("Dotted");
        miRectDotted.addActionListener(e -> {
            rectStyle = RectStyle.DOTTED;
            drawPanel.repaint();
        });
        JMenuItem miRectThick = new JMenuItem("Thick");
        miRectThick.addActionListener(e -> {
            rectStyle = RectStyle.THICK;
            drawPanel.repaint();
        });
        mRect.add(miRectDotted);
        mRect.add(miRectThick);

        JMenuItem miDiaSolid = new JMenuItem("Solid");
        miDiaSolid.addActionListener(e -> {
            diamondStyle = DiamondStyle.SOLID;
            drawPanel.repaint();
        });
        JMenuItem miDiaDashed = new JMenuItem("Dashed");
        miDiaDashed.addActionListener(e -> {
            diamondStyle = DiamondStyle.DASHED;
            drawPanel.repaint();
        });
        mDiamond.add(miDiaSolid);
        mDiamond.add(miDiaDashed);

        mb.add(mRect);
        mb.add(mDiamond);
        this.setJMenuBar(mb);

        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;

            int w = getWidth();
            int h = getHeight();

            
            int rectW = 200, rectH = 120;
            int rx = (w - rectW) / 4;
            int ry = (h - rectH) / 2;

            
            int cx = (3 * w) / 4;
            int cy = h / 2;
            int dHalf = 80;

            Point r1 = new Point(rx, ry);
            Point r2 = new Point(rx + rectW, ry);
            Point r3 = new Point(rx + rectW, ry + rectH);
            Point r4 = new Point(rx, ry + rectH);

            drawLineDDA(g, r1.x, r1.y, r2.x, r2.y, rectStyle);
            drawLineDDA(g, r2.x, r2.y, r3.x, r3.y, rectStyle);
            drawLineDDA(g, r3.x, r3.y, r4.x, r4.y, rectStyle);
            drawLineDDA(g, r4.x, r4.y, r1.x, r1.y, rectStyle);

            Point d1 = new Point(cx, cy - dHalf);
            Point d2 = new Point(cx + dHalf, cy);
            Point d3 = new Point(cx, cy + dHalf);
            Point d4 = new Point(cx - dHalf, cy);

            drawLineBresenham(g, d1.x, d1.y, d2.x, d2.y, diamondStyle);
            drawLineBresenham(g, d2.x, d2.y, d3.x, d3.y, diamondStyle);
            drawLineBresenham(g, d3.x, d3.y, d4.x, d4.y, diamondStyle);
            drawLineBresenham(g, d4.x, d4.y, d1.x, d1.y, diamondStyle);
        }
    }

    private void drawLineDDA(Graphics g, int x0, int y0, int x1, int y1, RectStyle style) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        double xInc = dx / (double) steps;
        double yInc = dy / (double) steps;
        double x = x0;
        double y = y0;

        int count = 0;
        int dottedInterval = 5; 
      
        int halfThick = 1;

        for (int i = 0; i <= steps; i++) {
            int xi = (int) Math.round(x);
            int yi = (int) Math.round(y);
            switch (style) {
                case DOTTED:
                    if (count % dottedInterval == 0) {
                        g.drawLine(xi, yi, xi, yi);
                    }
                    break;
                case THICK:
                  
                    for (int tx = -halfThick; tx <= halfThick; tx++) {
                        for (int ty = -halfThick; ty <= halfThick; ty++) {
                            g.drawLine(xi + tx, yi + ty, xi + tx, yi + ty);
                        }
                    }
                    break;
            }
            x += xInc;
            y += yInc;
            count++;
        }
    }

 
    private void drawLineBresenham(Graphics g, int x0, int y0, int x1, int y1, DiamondStyle style) {
      
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;
        int err = dx - dy;

        int count = 0;
        int dashOn = 10, dashOff = 5;
        int dashCycle = dashOn + dashOff;

        while (true) {
           
            if (style == DiamondStyle.SOLID) {
                g.drawLine(x0, y0, x0, y0);
            } else if (style == DiamondStyle.DASHED) {
                int m = count % dashCycle;
                if (m < dashOn) {
                    g.drawLine(x0, y0, x0, y0);
                }
            }

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            count++;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatternDrawing());
    }
}
