package graphicsLib;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class G {
    public static final Random RANDOM = new Random();
    public static int rnd(int max){return RANDOM.nextInt(max);}
    public static Color rndColor(){
        return new Color(rnd(256), rnd(256), rnd(256) );
    }
    public static void clearBack (Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect (0,0,5000,5000);
    }

    //------------------------------------------V------------------------------------------\\
    public static class V implements Serializable{
        public static Transform T = new Transform();
        public int x, y;
        public V(int x, int y) {set(x, y);}

        public void set(int x, int y) {this.x = x; this.y = y;}

        public void set(V v) {x = v.x; y = v.y;}

        public void add(V v) {x += v.x; y += v.y;}
        //------------------------Transforming methods-----------------------------\\
        public void blend(V v, int k){set((k * x + v.x)/(k + 1), (k * y + v.y)/(k + 1));}
        public void setT(V v){set(v.tx(), v.ty());}
        public int tx(){return x * T.n / T.d + T.dx;}
        public int ty(){return y * T.n / T.d + T.dy;}
        //--------------------------------Transform---------------------------------\\
        public static class Transform implements Serializable{
            int n, d, dx, dy;
            public void set(VS oVS, VS nVS){
                setScale(oVS.size.x, oVS.size.y, nVS.size.x, nVS.size.y);
                dx = setOff(oVS.loc.x, oVS.size.x, nVS.loc.x, nVS.size.x);
                dy = setOff(oVS.loc.y, oVS.size.y, nVS.loc.y, nVS.size.y);
            }
            public void set(BBox bBox, VS nVS){
                setScale(bBox.h.size(), bBox.v.size(), nVS.size.x, nVS.size.y);
                dx = setOff(bBox.h.lo, bBox.h.size(), nVS.loc.x, nVS.size.x);
                dy = setOff(bBox.v.lo, bBox.v.size(), nVS.loc.y, nVS.size.y);
            }
            public void setScale(int oW, int oH, int nW, int nH){
                n = (nW > nH)? nW:nH;
                d = (oW > oH)? oW:oH;
            }
            public int setOff(int oX, int oW, int nX, int nW){
                return (-oX -oW / 2) * n / d + nX + nW / 2;
            }
        }
    }

    //------------------------------------------VS-----------------------------------------\\
    public static class VS implements Serializable{
        public V loc, size;
        public VS (int x, int y, int w, int h){loc = new V(x,y); size = new V(w, h);}
        public void fill(Graphics g, Color c){g.setColor(c); g.fillRect(loc.x, loc.y, size.x, size.y);}
        public boolean hit(int x, int y){return loc.x <= x && loc.y <= y && x<=(loc.x + size.x) && y<= (loc.y +size.y);}
        public int xL(){return loc.x;}
        public int xH(){return loc.x + size.x;}
        public int xM(){return loc.x + size.x / 2;}
        public int yL(){return loc.y;}
        public int yH(){return loc.y + size.y;}
        public int yM(){return loc.y + size.y / 2;}
    }
    //-----------------------------------------LoHi----------------------------------------\\
    public static class LoHi implements Serializable{
        public int lo, hi;
        public LoHi(int min, int max){lo = min; hi = max;}
        public void add(int v){if(v < lo){lo = v;} if (v > hi){hi = v;}}
        public void set(int v){lo = v; hi = v;}
        public int size(){return (hi-lo) > 0 ? hi-lo: 1;}
    }
    //---------------------------------------BBox----------------------------------------\\
    public static class BBox implements Serializable{
        public LoHi h, v;
        public BBox(){h = new LoHi(0,0); v = new LoHi(0, 0);}
        public void set (int x, int y){h.set(x); v.set(y);}
        public void add (int x, int y){h.add(x); v.add(y);}
        public void add(V v){h.add(v.x); this.v.add(v.y);}
        public VS getNewVS(){return new VS(h.lo, v.lo, h.size(), v.size());}
        public void draw(Graphics g){g.drawRect(h.lo, v.lo, h.size(), v.size());}
    }
    //----------------------------------------PL------------------------------------------\\
    public static class PL implements Serializable {
        public V[] points;
        public PL(int n){
            points  = new V[n];
            for(int i = 0; i < n; i++){points[i] = new V(0,0);}
        }
        public int size(){return points.length;}
        public void transform(){
            for(int i = 0; i < points.length; i++){
                points[i].setT(points[i]);
            }
        }
        public void drawN(Graphics g, int n){
            for(int i = 1; i < n; i++){
                g.drawLine(points[i-1].x, points[i-1].y, points[i].x, points[i].y);
            }
        }
        public void draw(Graphics g){drawN(g, size());}
        public void drawNDots(Graphics g, int n){
            for(int i = 1; i < n; i++){
                g.drawOval(points[i].x - 1, points[i].y -1, 3, 3);
            }
        }
        public void drawDots(Graphics g){drawNDots(g, size());}
    }
}


