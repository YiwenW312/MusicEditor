package global;

import reactions.Gesture;

import java.awt.*;
//keeping all the interfaces in one & use . in the names to identify\\
public interface I {
    interface Draw {void draw(Graphics g);}
    interface Hit {boolean hit(int x, int y);}
    interface Area extends Hit{
        void dn(int x, int y);
        void up(int x, int y);
        void drag(int x, int y);
    }
    interface Show{void show(Graphics g);}
    interface Act{void act(Gesture g);}
    interface React extends Act{int bid(Gesture g);}
}
