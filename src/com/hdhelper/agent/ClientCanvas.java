package com.hdhelper.agent;

import com.hdhelper.Main;
import com.hdhelper.api.Equipment;
import com.hdhelper.api.W2S;
import com.hdhelper.peer.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ClientCanvas extends Canvas {

    private BufferedImage rawImage;
    private BufferedImage backBuffer;

    private Canvas buddy;

    public ClientCanvas() {
        super();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width  = (int) screen.getWidth();
        int height = (int) screen.getHeight();
        rawImage   = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        backBuffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    }

    void draw0(Graphics2D g2) {

        g2.setColor(Color.GREEN);

        int base_x = 15;
        int base_y = 20;
        int gap = 15;
        int i = 0;


        RSClient client = Main.client;

        assert client != null;

        final int bx = client.getRegionBaseX();
        final int by = client.getRegionBaseY();

        final int floor = client.getFloor();

        //Local debug:
        RSPlayer me = client.getMyPlayer();
        if(me != null) {

            int rx = me.getRegionX();
            int ry = me.getRegionY();
            int x = bx + rx;
            int y = by + ry;

            g2.drawString( "Name:" + me.getName(), base_x, base_y + gap * i++);
            g2.drawString( "X:" + x + "(" + rx + ")", base_x, base_y + gap * i++);
            g2.drawString( "Y:" + y + "(" + ry + ")", base_x, base_y + gap * i++);

        }

        //Render Players:
        g2.setColor(Color.RED);
        for(RSPlayer p : client.getPlayers()) {
            if(p == null) continue;
            int rx = p.getRegionX();
            int ry = p.getRegionY();
            W2S.draw3DBox(floor,rx,ry,p.getHeight(),g2);
            Point P = W2S.tileToViewport(p.getStrictX(),p.getStrictY(),floor,p.getHeight());
            if(P.x == -1) continue;
            g2.drawString(p.getName() + " | Lvl:" + p.getCombatLevel() + " | Anim:" + p.getAnimation() + " | Target:" + p.getTargetIndex() + "Orintation:" + p.getOrientation(),P.x,P.y);
        }

        //Render Npcs:
        g2.setColor(Color.BLUE);
        for(RSNpc p : client.getNpcs()) {
            if(p == null) continue;
            int rx = p.getRegionX();
            int ry = p.getRegionY();
            W2S.draw3DBox(floor,rx,ry,p.getHeight(),g2);
            if(p.getDef()==null) continue;
            Point P = W2S.tileToViewport(p.getStrictX(), p.getStrictY(), floor, p.getHeight());
            if(P.x == -1) continue;
            RSNpcDefintion def = p.getDef();
            g2.drawString(def.getName() + " | Anim:" + p.getAnimation() + " | Target:" + p.getTargetIndex() + "| Orintation:" + p.getOrientation(), P.x, P.y);
        }


        //Render GroundItems:
        g2.setColor(Color.YELLOW);
        RSDeque[][] items = client.getGroundItems()[floor];
        for(int x = 0; x < 104; x++) {
            for(int y = 0; y < 104; y++) {
                RSDeque pile = items[x][y];
                if(pile == null) continue;
                RSNode[] nodes = pile.toArray();
                for(RSNode node : nodes) {
                    RSGroundItem g = (RSGroundItem) node;
                    W2S.draw3DBox(floor,x,y,g.getHeight(),g2);
                    final int sx = (x << 7) + 64;
                    final int sy = (y << 7) + 64;
                    int id = g.getId();
                    RSItemDefinition def = client.getItemDef(id);
                    Point P = W2S.tileToViewport(sx, sy, floor, g.getHeight());
                    if(P.x == -1) continue;
                    g2.drawString(def.getName() + "(" + id + ") x " + g.getQuantity(),P.x,P.y);
                }
            }
        }


        g2.setColor(Color.GREEN);

        g2.drawString("Floor:" + client.getFloor(), base_x, base_y + gap * i++) ;
        i += 1;

        g2.drawString("Pitch:" + client.getPitch(), base_x, base_y + gap * i++);
        g2.drawString("Yaw:" + client.getYaw(), base_x, base_y + gap * i++);
        g2.drawString( "CamX:" + client.getCameraX(), base_x, base_y + gap * i++);
        g2.drawString( "CamY:" + client.getCameraY(), base_x, base_y + gap * i++);
        g2.drawString( "CamZ:" + client.getCameraZ(), base_x, base_y + gap * i++);
        i += 1;

        g2.drawString( "Scale:" + client.getViewportScale(), base_x, base_y + gap * i++);
        g2.drawString( "Width:" + client.getViewportWidth(), base_x, base_y + gap * i++);
        g2.drawString( "Height:" + client.getViewportHeight(), base_x, base_y + gap * i++);
        i += 1;

        // XTEA Debug:
        int[][] keys = client.getKeys();
        int[] chunks = client.getChunkIds();
        if(chunks!=null&&keys!=null) {
            int num_chunks = keys.length;
            g2.drawString( "Chunks:" + num_chunks, base_x, base_y + gap * i++);
            for(int k = 0; k < num_chunks; k++) {
                int chunkId = chunks[k];
                int[] key = keys[k];
                int rx = chunkId >> 8 & 255;
                int ry = chunkId & 255;
                g2.drawString( "<" + rx + "_" + ry + "> => " + Arrays.toString(key), base_x, base_y + gap * i++);
            }
        }
        i += 1;

        //Equipment Debug:
        for(Equipment.Slot s : Equipment.Slot.values()) {
            g2.drawString(s.name() + ":" + s.get(), base_x, base_y + gap * i++);
        }
        g2.drawString(client.getCacheDirectory().toString(), base_x, base_y + gap * i++);

    }

    @Override
    public Graphics getGraphics() {

        Graphics g = super.getGraphics();

        Graphics2D paint = (Graphics2D) backBuffer.getGraphics();
        paint.clearRect(0, 0, getWidth(), getHeight());
        paint.drawImage(rawImage, 0, 0, null);
        //draw0(paint);
        paint.dispose();
        g.drawImage(backBuffer, 0, 0, null);
        backBuffer.flush();
        g.dispose();
        Graphics rawG = rawImage.getGraphics();
        rawImage.flush();

        return rawG;

    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
    }

}

