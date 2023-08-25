package music;

import reactions.Mass;

import java.awt.*;

public abstract class Duration extends Mass{
    public abstract void show(Graphics g);
    public int nFlag = 0, nDot = 0;
    public Duration(){
        super("NOTE");
    }
    public void incFlag(){if (nFlag < 4){nFlag++;}}
    public void decFlag(){if (nFlag > -2){nFlag--;}}
    public void cycleDot(){nDot++; if (nDot > 3){nDot = 0;}}
}
