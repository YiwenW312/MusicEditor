package reactions;

import global.UC;
import graphicsLib.G;

import javax.imageio.stream.FileImageOutputStream;
import javax.xml.crypto.Data;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class Shape implements Serializable {
    public static Database DB = Database.load();
    public static Shape DOT = DB.get("DOT");
    public static Collection<Shape> LIST = DB.values();
    public static Shape bestMatch;
    public Prototype.List prototypes = new Prototype.List();
    public String name;
    public Shape(String name){this.name = name;}

    public static Shape recognize(Ink ink){
        if (ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold){return DOT;}
        Shape.bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for(Shape s: LIST){
            int d = s.prototypes.bestDist(ink.norm);
            if (d < bestSoFar){
                bestMatch = s;
                bestSoFar = d;
            }
        }
        return bestMatch;
    }

    //--------------------------------------Prototype------------------------------------------\\
    public static class Prototype extends Ink.Norm implements Serializable{
        public int nBlend = 1;
        public void  blend(Ink.Norm norm){
            blend(norm, nBlend); nBlend++;
        }

        //---------------------------------------List------------------------------------\\
        public static class List extends ArrayList<Prototype> implements Serializable{
            public static Prototype bestMatch;
            public int bestDist(Ink.Norm norm){
                bestMatch = null;
                int res = UC.noMatchDist;
                for(Prototype p: this){
                    int d = p.dist(norm);
                    if (d < res){bestMatch = p; res = d;}
                }
                return res;
            }
            private static int m = 10, w = 60;
            private static G.VS showBox = new G.VS(m, m, w, w);
            public void show(Graphics g){
                g.setColor(Color.ORANGE);
                for(int i = 0; i < size(); i++){
                    Prototype p = get(i);
                    int x = m + i * (m + w);
                    showBox.loc.set(x, m);
                    p.drawAt(g, showBox);
                    g.drawString("" + p.nBlend, x, 20);
                }
            }
            public int hitProto(int x, int y){
                if (y < m || x < m || y > m + w){return -1;}
                int res = (x - m) / (m + w);
                return res < size() ? res : -1;
            }
            public void train(Ink.Norm norm){
                if (bestDist(norm) < UC.noMatchDist) {
                    bestMatch.blend(norm);
                }else{
                    add(new Shape.Prototype());
                }

            }
        }
    }

    //----------------------------------Database--------------------------------\\
    public static class Database extends HashMap<String, Shape>{
        public Database(){
            super();
            String dot = "DOT";
            put(dot, new Shape(dot));
        }
        public static Database load(){
           Database res = null;
            try{
                System.out.println("attempting db load...");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(UC.shapeDBFilename));
                res =(Database) ois.readObject();
                System.out.println("successful load - found" + res.keySet());
                ois.close();
            } catch (Exception e) {
                System.out.println("load failed");
                System.out.println(e);
                res = new Database();
                res.put("DOT", new Shape("DOT"));
            }
            return res;
        }
        public static void save() {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(UC.shapeDBFilename));
                oos.writeObject(DB);
                System.out.println("saved" + UC.shapeDBFilename);
                oos.close();
            } catch (Exception e) {
                System.out.println("failed db save");
                System.out.println(e);
            }
        }
        public Shape forcedGet(String name){
            if (!DB.containsKey(name)){DB.put(name, new Shape(name));}
            return DB.get(name);
        }
        public void train(String name, Ink.Norm norm){
            if (isLegal(name)){
                forcedGet(name).prototypes.train(norm);
            }
        }
        public static boolean isLegal(String name){return !name.equals("") && !name.equals("DOT");}
    }
}
