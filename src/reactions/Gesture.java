package reactions;

import global.I;
import graphicsLib.G;

import java.util.ArrayList;

public class Gesture {
    public static List UNDO = new List();
    public Shape shape;
    public G.VS vs;
    private Gesture(Shape shape, G.VS vs){
        this.shape = shape;
        this.vs = vs;
    }
    public static Gesture getNew(Ink ink){//can return null\\
        Shape s = Shape.recognize(ink);
        return (s == null)? null: new Gesture(s,ink.vs);
    }
    public void doGesture(){
        Reaction r = Reaction.best(this);
        if(r != null){UNDO.add(this);r.act(this);}
    }
    public void redoGesture(){
        Reaction r = Reaction.best(this);
        if(r != null){r.act(this);}
    }
    public static void undo(){
        if(UNDO.size() == 0){return;}
        UNDO.remove(UNDO.size() - 1);//remove last element\\
        Layer.nuke();//eliminating all the masses\\
        Reaction.nuke();//clear the byShape map and reload initial reactions\\
        UNDO.redo();
    }
    public static I.Area AREA = new I.Area(){
        public boolean hit(int x, int y){
            return true;
        }
        public void dn(int x, int y){
            Ink.BUFFER.dn(x,y);
        }
        public void drag(int x, int y){
            Ink.BUFFER.drag(x,y);
        }
        public void up(int x, int y){
            Ink.BUFFER.add(x,y);
            Ink ink = new Ink();
            Gesture gesture = Gesture.getNew(ink);//that can fail\\
            Ink.BUFFER.clear();
            if(gesture != null){
                if(gesture.shape.name.equals("N-N")){
                    undo();
                }else{
                    gesture.doGesture();
                }
            }
            /*if(gesture != null){
                Reaction r = Reaction.best(gesture);//can fail\\
                if(r != null){
                    System.out.println("Reaction");
                    r.act(gesture);}
            }*/
        }
    };
    //-------------------------------List------------------------------\\
    public static class List extends ArrayList<Gesture>{
        public void redo(){for (Gesture g : this) {g.redoGesture();}}
    }

}
