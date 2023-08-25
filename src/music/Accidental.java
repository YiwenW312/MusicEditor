package music;

import reactions.Mass;

import java.awt.*;

public class Accidental extends Mass {

    public static final Glyph[] glyphs = {Glyph.DOUBLE_FLAT, Glyph.FLAT, Glyph.NATURAL, Glyph.SHARP, Glyph.DOUBLE_SHARP};
    public int iGlyph;
    public Head head;

    public Accidental(Head head) {
        super("NOTE");
        this.head = head;
        iGlyph = 2;
    }

    @Override
    public void show(Graphics g) {
        int x = head.x() - head.W(), y = head.y();
        g.setColor(Color.BLACK);
        glyphs[iGlyph].showAt(g, head.staff.h(), x, y);
    }

    public void inc() {iGlyph = iGlyph == glyphs.length - 1 ? 0: iGlyph + 1;}

    public void dec() {iGlyph = iGlyph == glyphs.length + 1 ? 0: iGlyph - 1;}
}
