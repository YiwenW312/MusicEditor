package sandbox;

import graphicsLib.G;
import graphicsLib.Window;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends Window{
    public static int clicks = 0;
    public static Path thePath;
    public static Path.List paths = new Path.List();

    Paint(){
       super("Paint", 1000, 700);
   }
   @Override
   public void paintComponent(Graphics g){
        G.clearBack(g);
        /*
        g.setColor(G.rndColor());
        g.fillOval(100,100,200,100) ;
        g.setColor(Color.BLACK);
        g.drawLine(100,600,600,200);
        int x = 300, y = 300;
        String str = "Click: " + clicks;
        g.drawString(str,x,y);
        g.setColor(Color.RED);
        g.fillRect(x,y,2,2);
        FontMetrics fm = g.getFontMetrics();
        int a= fm.getAscent(), h = fm.getHeight();
        int w = fm.stringWidth(str);
        g.drawRect(x, y-a, w, h);
        */
       g.setColor(G.rndColor());
       paths.draw(g);
   }
   @Override
   public void mousePressed (MouseEvent me){
        clicks = 0;
        thePath = new Path();
        thePath.add(me.getPoint());
        paths.add(thePath);
        repaint();
    }

    @Override
    public void mouseDragged (MouseEvent me){
        clicks++;
        thePath.add(me.getPoint());
        repaint();
    }
   public static void main(String[] args){
        PANEL=new Paint();
        launch();
   }
   //-----------------nested class--------path----------
   public static class Path extends ArrayList<Point>{
        public void draw(Graphics g){
            for (int i=1; i < size(); i++){
                Point p = get(i-1), n = get(i);
                g.drawLine(p.x, p.y, n.x, n.y);
            }
        }
        //--------------------------list----------------------
       public static class List extends ArrayList<Path>{
            public void draw(Graphics g) {for (Path p: this){p.draw(g);}}
        }
    }
}
