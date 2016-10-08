package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private int steps;
    private PuzzleBoard previousBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        int count=0, size=parentWidth/NUM_TILES;
        tiles = new ArrayList<PuzzleTile>();
        Bitmap initial=Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        for(int i=0; i<NUM_TILES; i++){
            for(int j=0;j<NUM_TILES;j++){
                if(count < 8) {
                    Bitmap tileItem = Bitmap.createBitmap(initial, i*size, j*size, size, size);
                    PuzzleTile tile = new PuzzleTile(tileItem, count);
                    tiles.add(tile);
                    count++;
                }
                else
                    tiles.add(null);
            }
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps+1;
        previousBoard = otherBoard;
    }

    public PuzzleBoard getPreviousBoard(){
        return previousBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> temp = new ArrayList<>();
        int xco_ord, yco_ord, i, j=0;
        for(i=0; i<NUM_TILES;i++){
            for(j=0;j<NUM_TILES;j++){
                if(tiles.get(i*NUM_TILES+j)==null)
                    break;
            }
            if (j!=NUM_TILES)
                break;
        }
        xco_ord=i;
        yco_ord=j;

        for (i=0; i<4; i++){
            int x=xco_ord+NEIGHBOUR_COORDS[i][0];
            int y=yco_ord+NEIGHBOUR_COORDS[i][1];
            if(x>=0 && y>0 && x<NUM_TILES && y<NUM_TILES){
                PuzzleBoard dup=new PuzzleBoard(this);
                dup.swapTiles(yco_ord*NUM_TILES+xco_ord, y*NUM_TILES+x);
                temp.add(dup);
            }
        }
        return temp;
    }

    public int priority() {
        int count=steps, act_pos;
        for (int i=0; i<NUM_TILES; i++){
            for (int j=0; j<NUM_TILES; j++){
                if (tiles.get(NUM_TILES * i + j) == null)
                    continue;
                act_pos = tiles.get(NUM_TILES * i + j).getNumber();
                count += Math.abs( (act_pos / NUM_TILES) - i );
                count += Math.abs( (act_pos % NUM_TILES) - j );
            }
        }
        return count;
    }

}
