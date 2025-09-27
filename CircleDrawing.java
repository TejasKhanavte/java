import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CircleDrawing extends JFrame {
    enum Alg { DDA, BRESENHAM, MIDPOINT }
    enum Style { SOLID, DOTTED, DASHED }

    private Alg currentAlg = Alg.MIDPOINT;
    private Style currentStyle = Style.SOLID;

    private int xc = 200, yc = 200;
    private int radius = 100;

    private CirclePanel drawPanel;

    public CircleDrawing() {
        super("Circle Drawing – DDA / Bresenham / Midpoint");

        drawPanel = new CirclePanel();
        this.getContentPane().add(drawPanel, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu algMenu = new JMenu("Algorithm");
        JMenu styleMenu = new JMenu("Style");

        for (Alg a : Alg.values()) {
            JMenuItem mi = new JMenuItem(a.toString());
            mi.addActionListener(e -> {
                currentAlg = a;
                drawPanel.repaint();
            });
            algMenu.add(mi);
        }
        for (Style s : Style.values()) {
            JMenuItem mi = new JMenuItem(s.toString());
            mi.addActionListener(e -> {
                currentStyle = s;
                drawPanel.repaint();
            });
            styleMenu.add(mi);
        }

        menuBar.add(algMenu);
        menuBar.add(styleMenu);
        this.setJMenuBar(menuBar);

        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    class CirclePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
           
            xc = getWidth() / 2;
            yc = getHeight() / 2;

            List<Point> pts = new ArrayList<>();
            switch (currentAlg) {
                case DDA:
                    pts = computeCircleDDA(xc, yc, radius);
                    break;
                case BRESENHAM:
                    pts = computeCircleBres(xc, yc, radius);
                    break;
                case MIDPOINT:
                    pts = computeCircleMidpoint(xc, yc, radius);
                    break;
            }

            drawPoints(g, pts, currentStyle);
        }
    }

  
    private List<Point> computeCircleDDA(int xc, int yc, int r) {
        List<Point> list = new ArrayList<>();
        double thetaStep = 1.0 / r;  // approximate small angle step
        for (double theta = 0; theta < 2 * Math.PI; theta += thetaStep) {
            int x = (int) Math.round(r * Math.cos(theta));
            int y = (int) Math.round(r * Math.sin(theta));
            list.add(new Point(xc + x, yc + y));
        }
        return list;
    }

   
    private List<Point> computeCircleBres(int xc, int yc, int r) {
        List<Point> list = new ArrayList<>();
        int x = 0;
        int y = r;
        int d = 3 - 2 * r;

        while (x <= y) {
            plot8(list, xc, yc, x, y);
            if (d < 0) {
                d = d + 4 * x + 6;
            } else {
                d = d + 4 * (x - y) + 10;
                y--;
            }
            x++;
        }
        return list;
    }

   
    private List<Point> computeCircleMidpoint(int xc, int yc, int r) {
        List<Point> list = new ArrayList<>();
        int x = r;
        int y = 0;
        int p = 1 - r;
       
        plot8(list, xc, yc, x, y);

        while (x > y) {
            y++;
            if (p <= 0) {
                p = p + 2 * y + 1;
            } else {
                x--;
                p = p + 2 * (y - x) + 1;
            }
            if (x < y) break;
            plot8(list, xc, yc, x, y);
        }
        return list;
    }

    private void plot8(List<Point> list, int xc, int yc, int x, int y) {
        list.add(new Point(xc + x, yc + y));
        list.add(new Point(xc - x, yc + y));
        list.add(new Point(xc + x, yc - y));
        list.add(new Point(xc - x, yc - y));
        list.add(new Point(xc + y, yc + x));
        list.add(new Point(xc - y, yc + x));
        list.add(new Point(xc + y, yc - x));
        list.add(new Point(xc - y, yc - x));
    }

   
    private void drawPoints(Graphics g, List<Point> pts, Style style) {
        int counter = 0;
        int dottedInterval = 5;   
        int dashOn = 10, dashOff = 5; 
        int dashCycle = dashOn + dashOff;
        for (Point p : pts) {
            switch (style) {
                case SOLID:
                    g.drawLine(p.x, p.y, p.x, p.y);
                    break;
                case DOTTED:
                    if (counter % dottedInterval == 0) {
                        g.drawLine(p.x, p.y, p.x, p.y);
                    }
                    break;
                case DASHED:
                    int mod = counter % dashCycle;
                    if (mod < dashOn) {
                        g.drawLine(p.x, p.y, p.x, p.y);
                    }
                    break;
            }
            counter++;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CircleDrawing());
    }
}
