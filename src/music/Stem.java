package music;

import global.UC;
import reactions.Gesture;
import reactions.Reaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Stem extends Duration implements Comparable<Stem>{
    public Staff staff;
    public Head.List heads = new Head.List();
    public boolean isUp = true;
    public Beam beam = null;

    public Stem(Staff staff, Head.List heads, boolean up){
        this.staff = staff;
        isUp = up;
        for (Head h: heads){
            h.unStem();
            h.stem = this;
        }
        this.heads = heads;
        staff.sys.stems.addStem(this);
        setWrongSides();

        addReaction(new Reaction("E-E") {
            @Override
            public int bid(Gesture g) {
                return bidLineCrossStem(g.vs.yM(), g.vs.xL(), g.vs.xH(), Stem.this);
            }

            @Override
            public void act(Gesture g) {
                Stem.this.incFlag();
            }
        });

        addReaction(new Reaction("W-W") {
            @Override
            public int bid(Gesture g) {
                return bidLineCrossStem(g.vs.yM(), g.vs.xL(), g.vs.xH(), Stem.this);
            }

            @Override
            public void act(Gesture g) {
                Stem.this.decFlag();
            }
        });
    }

    public static int bidLineCrossStem(int y, int x1, int x2, Stem stem){
        int xS = stem.x();
        if (x1 > xS || x2 < xS){return UC.noBid;}
        int y1 = stem.yL(), y2 = stem.yH();
        if (y < y1 || y > y2){return UC.noBid;}
        return Math.abs(y - (y1 + y2) / 2) + 60; // see "E-E" reaction in sys class
    }

    //factory methods gets stems if there is a head available
    public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up){
        Head.List heads = new Head.List();
        for (Head h: time.heads){
            int yH = h.y();
            if (yH > y1 && yH < y2){heads.add(h);}
        }
        if (heads.size() == 0){return null;}
        Beam b = internalStem(staff.sys, time.x, y1, y2);
        Stem res = new Stem(staff, heads, up);
        if (b != null){b.addStem(res); res.nFlag = 1;}
        return res;
    }

    private static Beam internalStem(Sys sys, int x, int y1, int y2) {
        for (Stem s: sys.stems){
            if (s.beam != null){
                int bX = s.beam.first().x(), bY = s.beam.first().yBeamEnd();
                int eX = s.beam.last().x(), eY = s.beam.last().yBeamEnd();
                System.out.println("Found Beam" + "" + bX + "" + bY + "" + eX + ""+ eY);
                if (Beam.verticalLineCrossesSegment(x, y1, y2, bX, bY, eX, eY)){return s.beam;}
            }
        }
        return null;
    }

    @Override
    public void show(Graphics g) {
     if (nFlag >= -1 && heads.size() > 0){
         int h = staff.h(), yH = yFirstHead(), yB = yBeamEnd();
         g.drawLine(x(), yH, x(), yB);
         if (nFlag > 0 && beam == null){
             if (nFlag == 1){(isUp? Glyph.FLAG1D: Glyph.FLAG1U).showAt(g, h, x(),yB);}
             if (nFlag == 2){(isUp? Glyph.FLAG2D: Glyph.FLAG2U).showAt(g, h, x(),yB);}
             if (nFlag == 3){(isUp? Glyph.FLAG3D: Glyph.FLAG3U).showAt(g, h, x(),yB);}
             if (nFlag == 4){(isUp? Glyph.FLAG4D: Glyph.FLAG4U).showAt(g, h, x(),yB);}
         }
     }
    }
    public Head firstHead(){return heads.get(isUp ? heads.size() - 1 : 0);}

    public Head lastHead(){return heads.get(isUp ? 0 : heads.size() - 1);}

    public int yL(){return isUp? yBeamEnd(): yFirstHead();}

    public int yH(){return isUp? yFirstHead(): yBeamEnd();}

    public int yFirstHead(){
        if (heads.size() ==0){return 200;} //put fake location
        return firstHead().y();
    }

    public int x(){
        if (heads.size() ==0){return 100;} //put fake x location for malformed
        Head h = firstHead(); return h.time.x + (isUp ? h.W(): 0);
    }

    public int yBeamEnd(){
        if (heads.size() ==0){return 100;} //put fake location
        if (isInternalStem()){beam.setMasterBeam();return beam.yOfx(x());}
        Head h = lastHead();
        int line = h.line;
        line += isUp? - 7 : 7; // default is one octave from last head on stem
        int flagInc = nFlag > 2? 2 * (nFlag - 2) : 0;// if more than 2 flags, adjust beamEnd.
        line += isUp? - flagInc : flagInc;
        if(isUp && line > 4 || (!isUp && line < 4)){line = 4;} // meet center line
        return h.staff.yLine(line);
    }

    public boolean isInternalStem(){
        if (beam == null){return false;}
        return beam.first() != this && beam.last() != this;
    }

    public void deleteStem(){
        if (heads.size() !=0){System.out.println("Deleting stem that had heads on it.");}
        staff.sys.stems.remove(this);
        if (beam != null){beam.removeStem(this);}
        deleteMass();
    }

    public void setWrongSides(){//call by time.stemHeads
        Collections.sort(heads);
        int i, last, next;
        if (isUp){
            i = heads.size() - 1; last = 0; next = -1;
        }else{
            i = 0; last = heads.size() - 1; next = 1;
        }
        Head ph = heads.get(i);
        ph.wrongSide = false;
        while (i != last){
            i += next;
            Head nh = heads.get(i);
            nh.wrongSide = ph.staff == nh.staff && Math.abs(nh.line - ph.line) <= 1 && !ph.wrongSide;
            ph = nh;
        }
    }

    @Override
    public int compareTo(Stem s) {return x() - s.x();}

    //------------------------------------------------List--------------------------------------------------\\
    public static class List extends ArrayList<Stem>{
        public int yMin = 10000000, yMax = -10000000;
        public void addStem(Stem s){
            add(s);
            if (s.yL() < yMin){yMin = s.yL();}
            if (s.yH() > yMax){yMax = s.yH();}
        }
        public void sort(){Collections.sort(this);}

        public boolean fastReject(int y) {
            return y < yMin || y > yMax;
        }
        public ArrayList<Stem> allIntersectors(int x1, int y1, int x2, int y2){
            ArrayList<Stem> res = new ArrayList<>();
            for (Stem s: this){
                if (Beam.verticalLineCrossesSegment(s.x(), s.yL(), s.yH(), x1, y1, x2, y2)){res.add(s);}
            }
            return res;
        }
    }
}

