package Game;

import Exceptions.BlockOutOfBoundsException;
import Blocks.Block;
import Blocks.Square;
import Blocks.SpawnBlock;
import Blocks.BlockManager;

import java.util.ArrayList;
import java.util.Random;
//import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A {@code Grid} represents the game field of Tetris.
 */
public class Grid {
    private Square[][] grid;
    private ArrayList<Block> blocks;
    private SpawnBlock spawnBlock;
    private BlockManager<Block> manager;

    /**
     * Creates a new {@code Grid} with the given height and width.
     * @param height the height of the {@code Grid}
     * @param width the width of the {@code Grid}
     */
    public Grid(int height, int width) {
        grid = new Square[height][width];
        blocks = new ArrayList<Block>();
    }

    /**
     * Checks the passed {@code Block} to see if it can be placed
     * at the given {@code Location} without going out of bounds.
     * @param block the {@code Block} to be placed
     * @param location  the {@code Location} at which the {@code Block} is
     *                  to be placed
     * @return  whether or not the block can be placed inside the {@code Grid}
     *          at the given {@code Location}
     */
    private boolean blockPlaceable(Block block, Location location) {
        Square[][] tile = block.getShape();

        if (location.getR() < 0 || location.getR() >= grid.length || location.getC() < 0 || location.getC() >= grid[0].length)  //If the location doesn't exist
            return false;
        if (location.getR() + tile[0].length > grid[0].length - 1)  //If it goes out on the right
            return false;
        if (location.getC() + tile.length > grid.length - 1)    //If it goes out on the bottom
            return false;

        return true;
    }

    /**
     * Adds a {@code Block} to the coordinates represented by the
     * passed {@code Location} object.
     * @param block to be placed
     * @param location of the top left corner of the area the {@code Block} is to be placed in
     */
    public void addBlock(Block block, Location location) {
        //Error if the block will be placed outside of the area
        if (!blockPlaceable(block, location)) throw new BlockOutOfBoundsException(location);
        else {
            Square[][] blockShape = block.getShape();

            for (int r = 0; r < blockShape.length; r++) {
                for (int c = 0; c < blockShape[0].length; c++) {
                    if (blockShape[r][c] != null)
                        grid[location.getR() + r][location.getC() + c] = blockShape[r][c];
                }
            }

            block.setLocation(location);
            blocks.add(block);
            System.out.println("> Block successfully added");
        }
    }

    /**
     * Retreives the {@code Square} at given {@code Location} coordinates.
     * @param location the {@code Location} to get the {@code Square} at
     * @return the {@code Square} at the passed {@code Location}
     */
    public Square getSquareAt(Location location) {
        return grid[location.getR()][location.getC()];
    }

    /**
     * Gets the size of the {@code Grid} in the format [rows, columns].
     * @return {@code int[]} [rows, columns]
     */
    public int[] getSize() {
        return new int[] {grid.length, grid[0].length};
    }

    //Still needs work
    /**
     * Standard update to the {@code Grid}; each block is moved one unit down.
     */
    public synchronized void regularStep() {
        for (int r = grid.length - 2; r >= 0; r--) {    //Start up from bottom
            for (int c = 0; c < grid[0].length; c++) {
                //If the square is supposed to be moving
                if (grid[r][c] != null && !grid[r][c].isSettled()) {
                    grid[r + 1][c] = grid[r][c];
                    grid[r][c] = null;
                }
            }
        }
        
        updateBlockLocations();        
        checkIfSettled();   //Settle all blocks that have settled      
        //System.out.println(this);   //Display grid to console
    }

    /**
     * Update the {@code location} instance variables
     * of each {@code Block} in the {@code Grid}.
     */
    private void updateBlockLocations() {
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            System.out.println("> Updater checking a block");
            System.out.println("> Block " + i + " is settled: " + b.isSettled());
            if (!b.isSettled()) {
                b.setLocation(new Location(b.getLocation().getR() + 1, b.getLocation().getC()));
                System.out.println("> Updated location " + b.getLocation());
            }
        }
    }

    /**
     * Check if each {@code Block} in the {@code Grid}
     * is settled, i.e. can no longer move. If the {@code Block}
     * is settled, the instance variable {@code settled} of the 
     * {@code Block} will be changed to reflect this. 
     */
    private synchronized void checkIfSettled() {
        Block[] blockArray = blocks.toArray(new Block[0]);
        boolean blockSettled = false;

        for (Block b : blockArray) {
            Location loc = b.getLocation();
            Square[][] shape = b.getShape();
            System.out.println("> Now determining whether to settle " + loc);

            //Automatically settled if it's at the bottom
            if (loc.getR() + shape.length - 1 >= grid.length - 1) {
                System.out.println("> Block settled");
                b.settle();
                blockSettled = true;
            } else {
                //Get bottom blocks
                ArrayList<Square> bottomSquares = b.getEdgeSquares();
                for (Square square : bottomSquares) {
                    System.out.println("> Checking new bottom square at " + square.getLocation());
                    if (grid[square.getLocation().getR() + 1][square.getLocation().getC()] != null) {   
                        System.out.println("> Block settled");                     
                        b.settle();
                        blockSettled = true;
                    }
                }
            }
        }

        if (blockSettled) {
            manager.addToGrid(this, new Location(0, new Random().nextInt(this.getSize()[1] - 2)));
            System.out.println("> Calling spawn");
            blockSettled = false;
        }
    }

    public void setManager(BlockManager<Block> manager) {
        this.manager = manager;
    }

    /**
     * Sets the {@code SpawnBlock} of this
     * {@code Grid} (the thing that spawns stuff).
     * @param s the {@code SpawnBlock}
     */
    public void setSpawnBlock(SpawnBlock s) {
        this.spawnBlock = s;
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String result = "";

        for (Square[] a : grid) {
            for (Square b : a) {
                if (b == null) result += "[ ]";
                else result += b;
            }
            result += "\n";
        }

        return result;
    }
}