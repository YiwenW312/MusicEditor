package music;

import global.UC;
import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

import java.awt.*;
import static music.MusicEd.PAGE;

public class Staff extends Mass {
    public Sys sys; //Staff lives in Sys\\
    public int iStaff;//index of Staff in Sys\\
    public Staff.Fmt fmt;

    public Staff(int iStaff, Staff.Fmt sf){
        super("BACK");
        this.iStaff = iStaff;
        this.fmt = sf;

        addReaction(new Reaction("S-S") { //bar line\\
            public int bid(Gesture g){
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yM();
                if(x < PAGE.margin.left || x > PAGE.margin.right + UC.barToMarginSnap){return UC.noBid;}
                int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
                return (d < 30)? d + UC.barToMarginSnap: UC.noBid;
            }
            public void act(Gesture g){new Bar(Staff.this.sys, g.vs.xM());}
        });

        addReaction(new Reaction("SW-SW") {//add note to staff
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < PAGE.margin.left || x > PAGE.margin.right) {return UC.noBid;}
                int H = Staff.this.h();
                int top = Staff.this.yTop() - H;
                int bot = Staff.this.yBot() + H;
                if (y < top || y > bot) {
                    return UC.noBid;
                }
                return 10;
            }

            @Override
            public void act(Gesture g) {
                new Head(Staff.this, g.vs.xM(), g.vs.yM());
            }
        });

        addReaction(new Reaction("S-S") {//toggling bar continues
            public int bid(Gesture g){
                if(Staff.this.sys.iSys != 0){return UC.noBid;}
                int y1 = g.vs.yL(), y2 = g.vs.yH();
                int iStaff = Staff.this.iStaff;
                if(iStaff == PAGE.sysFmt.size() - 1){return UC.noBid;}
                if(Math.abs(y1 - Staff.this.yBot()) > 30){return UC.noBid;}
                Staff nextStaff = sys.staffs.get(iStaff + 1);
                if(Math.abs(y2 - nextStaff.yTop()) > 30){return UC.noBid;}
                return 10;
            }
            public void act(Gesture g){
                PAGE.sysFmt.get(Staff.this.iStaff).toggleBarContinues();
            }
        });

        addReaction(new Reaction("W-S"){
            @Override
            public int bid(Gesture g){
                int x = g.vs.xL(), y = g.vs.yM();
                if(x< PAGE.margin.left || x > PAGE.margin.right){return UC.noBid;}
                int H = Staff.this.h();
                int top = Staff.this.yTop() - H;
                int bot = Staff.this.yBot() + H;
                if(y < top || y > bot){return UC.noBid;}
                return 10;
            }

            public void act (Gesture g){
                Time t = Staff.this.sys.getTime(g.vs.xL());
                new Rest(Staff.this, t);
            }
        });

        addReaction(new Reaction("E-S"){
            @Override
            public int bid(Gesture g){
                int x = g.vs.xL(), y = g.vs.yM();
                if(x< PAGE.margin.left || x > PAGE.margin.right){return UC.noBid;}
                int H = Staff.this.h();
                int top = Staff.this.yTop() - H;
                int bot = Staff.this.yBot() + H;
                if(y < top || y > bot){return UC.noBid;}
                return 10;
            }

            public void act (Gesture g){
                Time t = Staff.this.sys.getTime(g.vs.xL());
                (new Rest(Staff.this, t)).nFlag = 1;
            }
        });

        addReaction(new Reaction("SW-SE") {//Add clef
            @Override
            public int bid(Gesture g) {
                int y1 = g.vs.yL(), y2 = g.vs.yH();
                return Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
            }

            @Override
            public void act(Gesture g) {
                new Clef(Staff.this, g.vs.xM(), Clef.G);
            }
        });

        addReaction(new Reaction("SE-SW") {//Add clef
            @Override
            public int bid(Gesture g) {
                int y1 = g.vs.yL(), y2 = g.vs.yH();
                return Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
            }

            @Override
            public void act(Gesture g) {
                new Clef(Staff.this, g.vs.xM(), Clef.F);
            }
        });
    }

    public int sysOff(){return sys.fmt.staffOffSet.get(iStaff);}
    public int yTop(){return sys.yTop() + sysOff();}
    public int yBot(){return yTop() + fmt.height();}
    public int h(){return fmt.H;}
    public int yLine(int n){return yTop() + n * h();}
    public int lineOfY(int y){
        int H = h();
        int bias = 100;
        int top = yTop() - H * bias;
        return (y - top + H / 2) / H - bias;
    }

    @Override
    public void show(Graphics g) {}

    //--------------------------------------------Staff.Fmt------------------------------------------------------\\
    public static class Fmt {
        public int nLine = 5, H = UC.defaultStaffH;
        public boolean barContinues = false;

        public int height(){
            return 2 * H * (nLine - 1);
        }
        public void toggleBarContinues(){barContinues = !barContinues;}
        public void showAt(Graphics g, int y){
            int left = PAGE.margin.left, right = PAGE.margin.right;
            for(int i = 0; i < nLine; i++){g.drawLine(left, y + 2 * H * i, right, y + 2 * H * i);}
        }
    }
}
