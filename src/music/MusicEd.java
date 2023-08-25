package music;

import global.UC;
import graphicsLib.G;
import graphicsLib.Window;
import reactions.*;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MusicEd extends Window {

    public static Page PAGE;
    static {
        Layer.createAll("BACK NOTE FORE".split(" "));
    }

    public MusicEd(){
        super("Music Editor", UC.MAIN_WINDOW_WIDTH, UC.MAIN_WINDOW_HEIGHT);
        Reaction.initialReactions.addReaction(new Reaction("E-E") {
            public int bid(Gesture g){return 10;}
            public void act(Gesture g){
                int y = g.vs.yM();
                Sys.Fmt sf = new Sys.Fmt();
                PAGE = new Page(sf);
                PAGE.margin.top = y;
                PAGE.addNewSys();
                PAGE.addNewStaff(0);
                this.disable();
            }
        });
    }

    public void paintComponent(Graphics g){
        G.clearBack(g);
        g.setColor(Color.GREEN);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
//        if(PAGE != null){
//            Glyph.CLEF_G.showAt(g, 8, 100, PAGE.margin.top + 4 * 8);
//            int H = 8;
//            Glyph.HEAD_Q.showAt(g, H, 200, PAGE.margin.top + 4 * H);
//            g.setColor(Color.RED);
//            g.drawRect(200, PAGE.margin.top + 3 * H, 24 * H / 10, 2 * H);
//        }
//        g.setColor(Color.BLACK);
//        int h = 8, x1 = 100, x2 = 200;
//        Beam.setMasterBeam(x1, 100 + G.rnd(100), x2, 100 + G. rnd(100));
//        Beam.drawBeamStack(g, 0, 1, x1, x2, -h);
//        g.setColor(Color.ORANGE);
//        Beam.drawBeamStack(g, 1, 3, x1 + 10, x2 - 10, -h);
//        Beam.setPoly(100, 100 + G.rnd(100), 200, 100 + G.rnd(100), 8);
//        g.fillPolygon(Beam.poly);
    }

    public void mousePressed(MouseEvent me){
        Gesture.AREA.dn(me.getX(), me.getY());
        repaint();
    }
    public void mouseDragged(MouseEvent me){
        Gesture.AREA.drag(me.getX(), me.getY());
        repaint();
    }
    public void mouseReleased(MouseEvent me){
        Gesture.AREA.up(me.getX(), me.getY());
        repaint();
    }

    public static void main(String[] args) {
        (PANEL = new MusicEd()).launch();
    }

}
