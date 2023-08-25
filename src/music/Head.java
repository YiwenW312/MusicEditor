package music;

import global.UC;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

import java.awt.*;
import java.util.ArrayList;

public class Head extends Mass implements Comparable<Head> {// Note Head
    public Staff staff;
    public int x, line;
    public Time time;
    public Glyph forcedGlyph = null;
    public Stem stem = null;
    public boolean wrongSide = false;
    public Accidental accidental = null;

    public Head(Staff staff, int x, int y){
        super("NOTE");
        this.staff = staff;
        this.time = staff.sys.getTime(x);
        time.heads.add(this);
        line = staff.lineOfY(y);
        System.out.println("line: " + line);

        addReaction(new Reaction("S-S") {//this will stem or un-stem heads
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                int w = Head.this.W();
                int hy = Head.this.y();
                if(y1 > y || y2 < y){return UC.noBid;}
                int hl = Head.this.time.x, hr = hl + w;
                if (x < hl - w || x > hr + w){return UC.noBid;}
                if(x < hl + w / 2){return hl - x;}
                if(x > hr - w / 2){return x - hr;}
                return UC.noBid;
            }

            @Override
            public void act(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                Staff staff = Head.this.staff;
                Time t = Head.this.time;
                int w = Head.this.W();
                boolean up = x > (t.x + w / 2);
                if (Head.this.stem == null){
                    Stem.getStem(staff, time, y1, y2, up);
//                    t.stemHeads(staff, up, y1, y2);
                }else{
                    t.unStemHeads(y1, y2);
                }
            }
        });

        addReaction(new Reaction("DOT") {//add augmentation dots
            @Override
            public int bid(Gesture g) {
                int xH = Head.this.x(), yH = Head.this.y(), H = Head.this.staff.h(), W = Head.this.W();
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xH || x > xH + 2 * W || y < yH - H || y > yH + H){return UC.noBid;}
                return Math.abs(xH + W - x) + Math.abs(y - yH);
            }

            @Override
            public void act(Gesture g) {
                if (Head.this.stem != null){Head.this.stem.cycleDot();}
            }
        });

        addReaction(new Reaction("NE-SE") {
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                return Math.abs(x - Head.this.x()) + Math.abs(y - Head.this.y());
            }

            @Override
            public void act(Gesture g) {
               if (Head.this.accidental != null){
                   Head.this.incAccidental();
               }else{
                   Head.this.accidental = new Accidental(Head.this);
               }
            }
        });

        addReaction(new Reaction("SE-NE") {
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yL();
                return Math.abs(x - Head.this.x()) + Math.abs(y - Head.this.y());
            }

            @Override
            public void act(Gesture g) {
                if (Head.this.accidental != null){
                    Head.this.decAccidental();
                }else{
                    Head.this.accidental = new Accidental(Head.this);
                }
            }
        });
    }

    private void incAccidental() {accidental.inc();}

    private void decAccidental() {accidental.dec();}

    public void show(Graphics g){
        int H = staff.h();
        g.setColor(stem == null? Color.RED: Color.BLACK);
        (forcedGlyph != null? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
        if (stem != null){
            int off = UC.augDotOffsetRest, sp = UC.augDotSpaceRest;
            for (int i = 0; i < stem.nDot; i++){
                g.fillOval(time.x + off + i * sp, y() - 3 * H / 2, H * 2 / 3, H * 2 / 3);
            }
        }
    }

    public int W(){return 24 * staff.h() / 10;}

    public int y(){return staff.yLine(line);}

    public int x(){
        int res = time.x;
        if (wrongSide){
            res += (stem != null && stem.isUp)? W(): -W();
        }
        return res;
    }
    public Glyph normalGlyph(){
        if (stem == null){return Glyph.HEAD_Q;}
        if (stem.nFlag == -1){return Glyph.HEAD_HALF;}
        if (stem.nFlag == -2){return Glyph.HEAD_W;}
        return Glyph.HEAD_Q;
    }
    public void deleteHead(){time.heads.remove(this);}// this is a stub

    public void unStem(){
        if (stem != null){
            stem.heads.remove(this);
            if (stem.heads.size() == 0){stem.deleteStem();}
            stem = null;
            wrongSide = false;
        }
    }

//    public void joinStem(Stem s){
//        if (stem != null){unStem();}
//        s.heads.add(this);
//        stem = s;
//
//    }

    @Override
    public int compareTo(Head h) {
        return(staff.iStaff != h.staff.iStaff)? staff.iStaff - h.staff.iStaff: line - h.line;
    }

    //------------------------------------List-------------------------------------\\
    public static class List extends ArrayList<Head>{}
}
