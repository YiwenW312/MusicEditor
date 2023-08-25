package music;

import reactions.Gesture;
import reactions.Mass;
import reactions.Reaction;

import java.awt.*;

public class Clef extends Mass {

    public static final Glyph G = Glyph.CLEF_G, F = Glyph.CLEF_F;

    public Glyph glyph;
    public Staff staff;
    public int x;

    public Clef(Staff staff, int x, Glyph glyph) {
        super("NOTE");
        this.staff = staff;
        this.x = x;
        this.glyph = glyph;

        addReaction(new Reaction("DOT") {
            @Override
            public int bid(Gesture g) {
                return Math.abs(g.vs.xM() - Clef.this.x) + Math.abs(g.vs.yM() - Clef.this.staff.yLine(4));
            }

            @Override
            public void act(Gesture g) {
                toggleClef();
            }
        });
    }

    public void toggleClef(){glyph = glyph == G? F: G;}

    @Override
    public void show(Graphics g) {
        g.setColor(Color.BLACK);
        glyph.showAt(g, staff.h(), x, staff.yLine(4));// middle line
    }
}
