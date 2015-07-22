package de.leetgeeks.jgl.gl.font;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static de.leetgeeks.jgl.util.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBTruetype.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 21.07.2015
 * Time: 19:27
 */
public class TrueTypeFont {
    private static final Logger log = LogManager.getLogger();
    private static final int BITMAP_W = 512;
    private static final int BITMAP_H = 512;

    private static final STBTTAlignedQuad quad = new STBTTAlignedQuad();
    private static final FloatBuffer xb = BufferUtils.createFloatBuffer(1);
    private static final FloatBuffer yb = BufferUtils.createFloatBuffer(1);

    private int font_tex;

    private ByteBuffer chardata;

    private String ttfFile;

    private float pointSize;

    public TrueTypeFont(final String ttfFile, float pointSize) {
        this.ttfFile = ttfFile;
        this.pointSize = pointSize;
    }

    public void init() {
        prepareFontTexture();
    }


    private void prepareFontTexture() {
        font_tex = glGenTextures();
        chardata = BufferUtils.createByteBuffer(3 * 128 * STBTTPackedchar.SIZEOF);

        try {
            final File f = new File(this.getClass().getClassLoader().getResource(ttfFile).toURI());
            ByteBuffer ttf = ioResourceToByteBuffer(f.getAbsolutePath(), 160 * 1024);

            ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);

            ByteBuffer pc = BufferUtils.createByteBuffer(STBTTPackContext.SIZEOF);
            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, null);
            chardata.position((0 * 128 + 32) * STBTTPackedchar.SIZEOF);
            stbtt_PackSetOversampling(pc, 1, 1);
            stbtt_PackFontRange(pc, ttf, 0, pointSize, 32, 95, chardata);

            chardata.position((1 * 128 + 32) * STBTTPackedchar.SIZEOF);
            stbtt_PackSetOversampling(pc, 2, 2);
            stbtt_PackFontRange(pc, ttf, 0, pointSize, 32, 95, chardata);

            chardata.position((2 * 128 + 32) * STBTTPackedchar.SIZEOF);
            stbtt_PackSetOversampling(pc, 3, 1);
            stbtt_PackFontRange(pc, ttf, 0, pointSize, 32, 95, chardata);
            stbtt_PackEnd(pc);

            glBindTexture(GL_TEXTURE_2D, font_tex);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

/*    private void draw_init() {
        glDisable(GL_CULL_FACE);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);

        glViewport(0, 0, fbw, fbh);
        if ( black_on_white )
            glClearColor(255, 255, 255, 0);
        else
            glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, ww, wh, 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }*/

    private static void drawBoxTC(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1) {
        glTexCoord2f(s0, t0);
        glVertex2f(x0, y0);
        glTexCoord2f(s1, t0);
        glVertex2f(x1, y0);
        glTexCoord2f(s1, t1);
        glVertex2f(x1, y1);
        glTexCoord2f(s0, t1);
        glVertex2f(x0, y1);
    }

    public void printOnScreen(float x, float y, final String text, int screenWidth, int screenHeight) {
        xb.put(0, x);
        yb.put(0, y);

        int font = 0; // 0 has no oversampling / 1 : 2x2 oversampling
        chardata.position(font * 128 * STBTTPackedchar.SIZEOF);



        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, screenWidth, screenHeight, 0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL13.GL_TEXTURE0 + 0);
        glBindTexture(GL_TEXTURE_2D, font_tex);

        GL20.glUseProgram(0);
        glPushAttrib(GL_COLOR_BUFFER_BIT | GL_ENABLE_BIT);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(1, 1, 1, 1);
        glBegin(GL_QUADS);
        for ( int i = 0; i < text.length(); i++ ) {
            stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, text.charAt(i), xb, yb, quad.buffer(), 1);
            drawBoxTC(
                    quad.getX0(), quad.getY0(), quad.getX1(), quad.getY1(),
                    quad.getS0(), quad.getT0(), quad.getS1(), quad.getT1()
            );
        }
        glEnd();

        glPopAttrib();
        glDisable(GL_TEXTURE_2D);
    }
}
