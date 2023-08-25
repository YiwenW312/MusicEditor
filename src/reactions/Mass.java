package reactions;

import global.I;
import graphicsLib.G;

public abstract class Mass extends Reaction.List implements I.Show {
    public Layer layer;
    public Mass(String layerName){
        layer = Layer.byName.get(layerName);
        if(layer != null){
            layer.add(this);
        }else{
            System.out.println("bad layer name: " + layerName);
        }
    }
    public void deleteMass(){
        clearAll();//clear all reactions from this list and byShape\\
        layer.remove(this);//remove itself from layers\\
    }
    public boolean equals(Object o){return this == o;}
    private int hashcode = G.rnd(100000000);
    public int hashCode(){return hashcode;}
}
