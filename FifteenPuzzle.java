import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;


public class FifteenPuzzle {

    private class TilePos {
        int x,y;

        TilePos(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    private final static int DIMS = 4;
    private int[][] tiles;
    private int display_width;
    private TilePos blank1,blank2;
    private int numOfShuffle = 100;

    private FifteenPuzzle() {
        tiles = new int[DIMS][DIMS];
        int cnt = 1;
        for(int i = 0; i<DIMS; i++) {
            for(int j = 0; j<DIMS; j++) {
                tiles[i][j] = cnt;
                cnt++;
            }
        }
        display_width = Integer.toString(cnt).length();

        blank1 = new TilePos(DIMS-1,DIMS-1);
        tiles[blank1.x][blank1.y] = 0;

        blank2 = new TilePos(DIMS-1,DIMS-2);
        tiles[blank2.x][blank2.y] = 0;
    }

    private final static FifteenPuzzle SOLVED=new FifteenPuzzle();


    private FifteenPuzzle(FifteenPuzzle toClone) {
        this();
        for(TilePos p: allTilePos()) {
            tiles[p.x][p.y] = toClone.tile(p);
        }
        blank1 = toClone.getBlank1();
        blank2 = toClone.getBlank2();
    }

    private List<TilePos> allTilePos() {
        ArrayList<TilePos> out = new ArrayList<>();
        for(int i = 0; i < DIMS; i++) {
            for(int j = 0; j < DIMS; j++) {
                out.add(new TilePos(i,j));
            }
        }
        return out;
    }


    private int tile(TilePos p) {
        return tiles[p.x][p.y];
    }


    private TilePos getBlank1() {
        return blank1;
    }

    private TilePos getBlank2() {
        return blank2;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof FifteenPuzzle) {
            for(TilePos p: allTilePos()) {
                if( this.tile(p) != ((FifteenPuzzle) o).tile(p)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public int hashCode() {
        int out = 0;
        for(TilePos p: allTilePos()) {
            out = (out * DIMS * DIMS) + this.tile(p);
        }
        return out;
    }


    private void show() {
        System.out.println("-----------------");
        for(int i=0; i<DIMS; i++) {
            System.out.print("| ");
            for(int j=0; j<DIMS; j++) {
                int n = tiles[i][j];
                StringBuilder s;
                if( n>0) {
                    s = new StringBuilder(Integer.toString(n));
                } else {
                    s = new StringBuilder();
                }
                while( s.length() < display_width ) {
                    s.append(" ");
                }
                System.out.print(s + "| ");
            }
            System.out.print("\n");
        }
        System.out.print("-----------------\n\n");
    }


    private List<TilePos> allValidMoves() {
        ArrayList<TilePos> out = new ArrayList<>();
        for(int x = 0; x < DIMS; x++) {
            for(int y = 0; y < DIMS; y++) {
                TilePos tp = new TilePos(x, y);
                if(isValidMove(tp))
                    out.add(tp);
            }
        }
        return out;
    }


    private boolean isValidMove(TilePos p) {
        if(tiles[p.x][p.y] != 0) {
            if ((p.y - 1) >= 0) {
                if (tiles[p.x][p.y - 1] == 0)
                    return true;
            }
            if ((p.x - 1) >= 0) {
                if (tiles[p.x - 1][p.y] == 0)
                    return true;
            }
            if ((p.y + 1) < DIMS) {
                if (tiles[p.x][p.y + 1] == 0)
                    return true;
            }
            if ((p.x + 1) < DIMS) {
                if (tiles[p.x + 1][p.y] == 0)
                    return true;
            }
        }
        return false;
    }


    private void move(TilePos p) {
        if( !isValidMove(p) ) {
            throw new RuntimeException("Invalid move");
        }
        int x1 = p.x; int y1 = p.y - 1;
        int x2 = p.x - 1; int y2 = p.y;
        int x3 = p.x; int y3 = p.y + 1;
        int x4 = p.x + 1; int y4 = p.y;

        if ((x1 == blank1.x && y1 == blank1.y) ||
                (x2 == blank1.x && y2 == blank1.y) ||
                (x3 == blank1.x && y3 == blank1.y) ||
                (x4 == blank1.x && y4 == blank1.y))
            swap1(p);
        else if ((x1 == blank2.x && y1 == blank2.y) ||
                (x2 == blank2.x && y2 == blank2.y) ||
                (x3 == blank2.x && y3 == blank2.y) ||
                (x4 == blank2.x && y4 == blank2.y))
            swap2(p);
    }

    private void swap1(TilePos p) {
        assert tiles[blank1.x][blank1.y]==0;
        tiles[blank1.x][blank1.y] = tiles[p.x][p.y];
        tiles[p.x][p.y]=0;
        blank1 = p;
    }

    private void swap2(TilePos p) {
        assert tiles[blank2.x][blank2.y]==0;
        tiles[blank2.x][blank2.y] = tiles[p.x][p.y];
        tiles[p.x][p.y]=0;
        blank2 = p;
    }

    private TilePos whereIs(int x) {
        for(TilePos p: allTilePos()) {
            if( tile(p) == x ) {
                return p;
            }
        }
        return null;
    }

    private FifteenPuzzle moveClone(TilePos p) {
        FifteenPuzzle out = new FifteenPuzzle(this);
        out.move(p);
        return out;
    }


    private void shuffle() {
        for(int i = 0; i < numOfShuffle; i++) {
            List<TilePos> possible = allValidMoves();
            int which = (int) (Math.random() * possible.size());
            TilePos move = possible.get(which);
            this.move(move);
        }
    }


    private int manhattanDistance() {
        int distance = 0;
        for (TilePos p : allTilePos()) {
            int val = tile(p);
            if( val != 0 ) {
                TilePos correct = SOLVED.whereIs(val);
                assert correct != null;
                distance += Math.abs(correct.x - p.x);
                distance += Math.abs(correct.y - p.y);
            }
        }
        return distance;
    }

    private boolean isSolved() {
        return manhattanDistance() == 0;
    }

    private int estimateError() {
        return this.manhattanDistance();
    }


    private List<FifteenPuzzle> allAdjacentPuzzles() {
        ArrayList<FifteenPuzzle> out = new ArrayList<>();
        for( TilePos move: allValidMoves() ) {
            out.add( moveClone(move) );
        }
        return out;
    }

    private List<FifteenPuzzle> aStarSolve() {
        HashMap<FifteenPuzzle,FifteenPuzzle> predecessor = new HashMap<>();
        HashMap<FifteenPuzzle,Integer> depth = new HashMap<>();
        final HashMap<FifteenPuzzle,Integer> score = new HashMap<>();
        Comparator<FifteenPuzzle> comparator = Comparator.comparingInt(score::get);
        PriorityQueue<FifteenPuzzle> toVisit = new PriorityQueue<>(10000,comparator);

        predecessor.put(this, null);
        depth.put(this,0);
        score.put(this, this.estimateError());
        toVisit.add(this);
        int cnt=0;
        while( toVisit.size() > 0) {
            FifteenPuzzle candidate = toVisit.remove();
            cnt++;
            if( cnt % 10000 == 0) {
                System.out.printf("Considered %,d positions. Queue = %,d\n", cnt, toVisit.size());
            }
            if( candidate.isSolved() ) {
                System.out.printf("Solution considered %d boards\n", cnt);
                LinkedList<FifteenPuzzle> solution = new LinkedList<>();
                FifteenPuzzle backtrace=candidate;
                while( backtrace != null ) {
                    solution.addFirst(backtrace);
                    backtrace = predecessor.get(backtrace);
                }
                return solution;
            }
            for(FifteenPuzzle fp: candidate.allAdjacentPuzzles()) {
                if( !predecessor.containsKey(fp) ) {
                    predecessor.put(fp,candidate);
                    depth.put(fp, depth.get(candidate)+1);
                    int estimate = fp.estimateError();
                    score.put(fp, depth.get(candidate)+1 + estimate);
                    toVisit.add(fp);
                }
            }
        }
        return null;
    }

    private static void showSolution(List<FifteenPuzzle> solution) {
        if (solution != null ) {
            System.out.printf("Success!  Solution with %d moves:\n", solution.size());
            for( FifteenPuzzle sp: solution) {
                sp.show();
            }
        } else {
            System.out.println("Did not solve. :(");
        }
    }


    public static void main(String[] args) {
        FifteenPuzzle p = new FifteenPuzzle();
        p.shuffle();
        System.out.println("Shuffled board:");
        p.show();

        List<FifteenPuzzle> solution;

        System.out.println("Solving with A*");
        solution = p.aStarSolve();
        showSolution(solution);
    }
}