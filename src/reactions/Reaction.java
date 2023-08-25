package reactions;

import global.I;
import global.UC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Keep all the reactions\\
public abstract class Reaction implements I.React{
    private static Map byShape = new Map();
    public static List initialReactions = new List();//use by undo to restart\\
    public Shape shape;
    public Reaction(String shapeName){
        shape = Shape.DB.get(shapeName);
        if(shape == null){System.out.println("WTF? Shape DB does not contain" + shapeName);}
    }
    public void enable(){
        List list = byShape.getList(shape);
        if(!list.contains(this)){
            list.add(this);
        }
    }
    public void disable(){
        List list = byShape.getList(shape);
        list.remove(this);
    }
    public static Reaction best(Gesture g){//It can return null\\
        return byShape.getList(g.shape).loBid(g);
    }
    public static void nuke(){//nukes reactions before undo\\
        byShape.clear();
        initialReactions.enable();
    }
    //---------------------------------List---------------------------------\\
    public static class List extends ArrayList<Reaction>{
        public void addReaction(Reaction r){
            add(r);
            r.enable();
        }
        public void removeReaction(Reaction r){
            remove(r);
            r.disable();
        }
        public void clearAll(){
            for(Reaction r: this){
                r.disable();
            }
            this.clear();
        }
        public Reaction loBid(Gesture g){//Can return null\\
            Reaction res = null;
            int bestSoFar = UC.noBid;
            for(Reaction r: this){
                int b = r.bid(g);
                if(b < bestSoFar){
                    bestSoFar = b;
                    res = r;
                }
            }
            return res;
        }
        public void enable(){for (Reaction r : this){r.enable();}}
    }

    //----------------------------------Map---------------------------------\\
    public static class Map extends HashMap<Shape,List>{
        public List getList(Shape s){
            List res = get(s);
            //This always are forced to return list\\
            if (res == null){
                res = new List();
                put(s,res);
            }
            return res;
        }
    }
}
