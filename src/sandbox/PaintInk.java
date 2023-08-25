package sandbox;

import global.UC;
import graphicsLib.G;
import graphicsLib.Window;
import reactions.Ink;
import reactions.Shape;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;

public class PaintInk extends Window {
    public static Ink.List inkList = new Ink.List();
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    public static String recognized = "";
    public PaintInk() {
        super("PaintInk", UC.MAIN_WINDOW_WIDTH, UC.MAIN_WINDOW_HEIGHT);
    }
    public void paintComponent(Graphics g){
        G.clearBack(g);
        g.setColor(Color.BLACK); inkList.show(g);
        g.setColor(Color.RED); Ink.BUFFER.show(g);
        pList.show(g);
        g.drawString("Saw:" + recognized, 700, 40);
        //int n = inkList.size() - 1;
        //if(n > 2){
        //    int d = inkList.get(n).norm.dist(inkList.get(n-1).norm);
        //    g.setColor(d < 1000000? Color.BLACK: Color.RED);
        //    g.drawString("Dist: " + d, 600, 30);
        //}
    }
    public void mousePressed(MouseEvent me){
        Ink.BUFFER.dn(me.getX(), me.getY());
        repaint();
    }
    public void mouseDragged(MouseEvent me){
        Ink.BUFFER.drag(me.getX(), me.getY());
        repaint();
    }
    public void mouseReleased(MouseEvent me){
        Shape.Prototype proto;
        Ink ink = new Ink();
        Shape s = Shape.recognize(ink);
        recognized = s == null ? "UNRECOGNIZED" : s.name;
        inkList.add(ink);
        if(pList.bestDist(ink.norm) < UC.noMatchDist){
            proto = Shape.Prototype.List.bestMatch;
            //Shape.Prototype.List.bestMatch.blend(ink.norm);
            proto.blend(ink.norm);
        }else{
            proto = new Shape.Prototype();
            //pList.add(new Shape.Prototype());
            pList.add(proto); //Norm()
        }
        ink.norm = proto;
        repaint();
    }
    public static void main(String[] args){
        (PANEL = new PaintInk()).launch();
    }
}
