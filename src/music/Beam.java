package music;

import reactions.Mass;

import java.awt.*;

public class Beam extends Mass {
    public Stem.List stems = new Stem.List();

    public Beam(Stem first, Stem last){
        super("NOTE");
        stems.addStem(first);
        stems.addStem(last);
        first.nFlag = 1;
        last.nFlag = 1;
        first.beam = this;
        last.beam = this;
        stems.sort();
    }

    public Stem first(){return stems.get(0);}

    public Stem last(){return stems.get(stems.size() - 1);}

    public void deleteBeam(){
        for(Stem s: stems){s.beam = null;}
        deleteMass();
    }

    public void addStem(Stem s){
        if(s.beam == null){stems.addStem(s); s.beam = this; stems.sort();}
    }

    @Override
    public void show(Graphics g) {
        g.setColor(Color.BLACK);
        drawBeamGroup(g);
    }

    public static int mX1, mY1, mX2, mY2;//the coordinates for master beam

    public static int yOfx(int x, int x1, int y1, int x2, int y2){
        int dY = y2 - y1, dX = x2 - x1;
        return (x - x1) * dY / dX + y1;
    }

    public static int yOfx(int x){
        int dY = mY2 - mY1, dX = mX2 - mX1;
        return (x - mX1) * dY / dX + mY1;
    }

    public static boolean verticalLineCrossesSegment(int x, int y1, int y2, int bX, int bY, int eX, int eY) {
        if (x < bX || x > eX){return false;}
        int y = yOfx(x, bX, bY, eX, eY);
        if (y1 < y2){return y1 < y && y < y2;}else{return y2 < y && y < y1;}
    }
    public static void setMasterBeam(int x1, int y1, int x2, int y2){
        mX1 = x1;
        mY1 = y1;
        mX2 = x2;
        mY2 = y2;
    }

    public void removeStem(Stem s){
        if (s.isInternalStem()){
            stems.remove(s); stems.sort();
        }else{
            deleteBeam();
        }

    }
    public void setMasterBeam(){
        mX1 = first().x();
        mY1 = first().yBeamEnd();
        mX2 = last().x();
        mY2 = last().yBeamEnd();
    }

    public static Polygon poly;
    static {int[] foo = {0, 0, 0, 0};poly = new Polygon(foo, foo, 4);}

    public static void setPoly(int x1, int y1, int x2, int y2, int h){
        int[] a = poly.xpoints;
        a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
        a = poly.ypoints; a[0] = y1; a[1] = y2; a[2] = y2 + h; a[3] = y1 + h;
    }

    public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h){
        int y1 = yOfx(x1), y2 = yOfx(x2);
        for(int i = n1; i < n2; i++){
            setPoly(x1, y1 + i * 2 * h, x2, y2 + i * 2 * h, h);
            g.fillPolygon(poly);
        }
    }

    public void drawBeamGroup(Graphics g){
        setMasterBeam();
        Stem firstStem = first();
        int h = firstStem.staff.h();
        int sH = firstStem.isUp? h: -h;
        int nPrev = 0, nCurr = firstStem.nFlag, nNext = stems.get(1).nFlag;
        int pX;
        int cX = firstStem.x();
        int bX = cX + 3 * h;
        if (nCurr > nNext){drawBeamStack(g, nNext, nCurr, cX, bX, sH);}
        for (int curr = 1; curr < stems.size(); curr++){
            Stem sCurr = stems.get(curr);
            pX = cX;
            cX = sCurr.x();
            nPrev = nCurr;
            nCurr = nNext;
            nNext = (curr < (stems.size() - 1))? stems.get(curr + 1).nFlag: 0;
            int nBack = Math.min(nPrev, nCurr);
            drawBeamStack(g, 0, nBack, pX, cX, sH);
            if(nCurr > nPrev && nCurr > nNext){//BeamLETS is required
                if(nPrev < nNext){//BeamLETS lean towards side
                    bX = cX + 3 * h;
                    drawBeamStack(g, nNext, nCurr, cX, bX, sH);
                }else{
                    bX = cX - 3 * h;
                    drawBeamStack(g, nPrev, nCurr, bX, cX, sH);
                }
            }
        }
    }
}
