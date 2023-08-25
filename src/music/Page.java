package music;

import global.UC;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

import java.awt.*;
import java.util.ArrayList;

import static global.UC.noBid;
import static music.MusicEd.PAGE;

public class Page extends Mass {
    public Margin margin = new Margin();
    public Sys.Fmt sysFmt;
    public int sysGap, nSys;
    public ArrayList<Sys> sysList = new ArrayList<>();

    public Page(Sys.Fmt sysfmt){
        super("BACK");
        this.sysFmt = sysfmt;
        addReaction(new Reaction("E-E"){//add new staff
            public int bid(Gesture g){
                int y = g.vs.yM();
                if (y <= PAGE.margin.top + sysFmt.height() + 30)
                {return noBid;}
                return 50;
            }
            public void act(Gesture g){
                int y = g.vs.yM();
                PAGE.addNewStaff(y - PAGE.margin.top);
            }
        });

        addReaction(new Reaction("E-W") { //add new sys
            public int bid(Gesture g) {
                int y = g.vs.yM();
                int yBot = PAGE.sysTop(nSys);
                if (y <= yBot) {
                    return noBid;
                }
                return 50;
            }
            public void act(Gesture g) {
                int y = g.vs.yM();
                if(PAGE.nSys == 1){PAGE.sysGap = y - PAGE.sysTop(1);}
                PAGE.addNewSys();
            }
        });
    }

    public int sysTop(int iSys) {return margin.top + iSys * (sysFmt.height() + sysGap);}

    @Override
    public void show(Graphics g) {
        g.setColor(Color.BLACK);
        for(int i = 0; i < nSys; i++){sysFmt.showAt(g, sysTop(i));}
    }

    public void addNewStaff(int yOff){
        Staff.Fmt sf = new Staff.Fmt();
        int n = sysFmt.size();
        sysFmt.add(sf);
        sysFmt.staffOffSet.add(yOff);
        for(int i = 0; i < nSys; i++){sysList.get(i).addStaff(new Staff(n, sf));}
    }

    public void addNewSys(){sysList.add(new Sys(nSys, sysFmt)); nSys++;}

    //----------------------------------Margin-----------------------------------\\
    public static class Margin {
        private static final int M = 50;
        public int top = M, left = M;
        public int bot = UC.MAIN_WINDOW_HEIGHT - M;
        public int right = UC.MAIN_WINDOW_WIDTH - M;
    }
}
