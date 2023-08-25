package reactions;

import global.I;
import global.UC;
import graphicsLib.G;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;

public class Ink implements I.Show{
    public static Buffer BUFFER = new Buffer();
    public Norm norm;
    public G.VS vs;
    public Ink(){
       norm = new Norm();
       vs = BUFFER.bBox.getNewVS();
    }
    @Override
    public void show(Graphics g) {
        g.setColor(UC.inkColor);
        norm.drawAt(g, vs);
    }
    //-------------------------------------------------Norm------------------------------------------\\
    public static class Norm extends G.PL implements Serializable {
        public static final int N = UC.normSampleSize, MAX = UC.normCordMax;
        public static final G.VS normCordSystem = new G.VS(0,0, MAX, MAX);
        public Norm(){
            super(N);
            BUFFER.subSample(this);
            G.V.T.set(BUFFER.bBox, normCordSystem);
            transform();
        }
        public void drawAt(Graphics g, G.VS vs){
            G.V.T.set(normCordSystem, vs);
            for(int i = 1; i < N; i++){
                g.drawLine(points[i-1].tx(), points[i-1].ty(), points[i].tx(), points[i].ty());
            }
        }
        public int dist(Norm n){
            int res = 0;
            for (int i = 0; i < N; i++){
                int dx = points[i].x - n.points[i].x;
                int dy = points[i].y - n.points[i].y;
                res += dx * dx + dy * dy;
            }
            return res;
        }
        public void blend(Norm norm, int n){
            for(int i = 0; i < N; i++){points[i].blend(norm.points[i], n);}
        }
    }

    //--------------------------------------------Buffer--------------------------------------------\\
    public static class Buffer extends G.PL implements I.Show, I.Area{
        public static final int MAX = UC.inkBufferMAX;
        public int n;
        public G.BBox bBox = new G.BBox();
        private Buffer(){super(MAX);}
        public void add(int x, int y){
            if(n < MAX){points[n].set(x,y); bBox.add(x,y); n++;}
        }
        public void clear(){n = 0;}
        public void show(Graphics g){drawNDots(g,n);}
        public boolean hit(int x, int y){return true;}
        public void dn(int x, int y){clear(); add(x,y); bBox.set(x, y);}
        public void drag(int x, int y){add(x, y);}
        public void up (int x, int y){}
        public void subSample(G.PL pl){
            int K = pl.size();
            for(int i = 0; i < K; i++){
                int j = i * (n - 1) / (K - 1);
                pl.points[i].set(points[j]);
            }
        }
    }
    //--------------------------------------------List-------------------------------------------\\
    public static class List extends ArrayList<Ink> implements I.Show {
        @Override
        public void show(Graphics g){
            for(Ink ink: this){ink.show(g);}
        }
    }
}
