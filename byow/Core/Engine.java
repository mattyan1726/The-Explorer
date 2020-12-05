package byow.Core;

import byow.Build;
import byow.InputDemo.StringInputDevice;
import byow.Monster;
import byow.Player;
import byow.Point;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.algs4.Stopwatch;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private Random rd;
    private TETile[][] wld;
    private TETile[][] playerWld; // the part of world within the sight of player
    private Point position; // of the avatar
    private List<Monster> monsters;
    private Point door;
    private boolean doorLocked;
    public static final TETile KEY = Tileset.FLOWER;
    private static final TETile LANDMINE = Tileset.MOUNTAIN;
    public static final TETile MONSTER = Tileset.SAND;
    public static final TETile PROP = Tileset.TREE;
    public static final TETile BOOTY = Tileset.GRASS;
    private String currentTileName = "";
    private boolean stagedToSave = false;
    private String playerName = "player";

    private long seed = 0;
    private long floor = 1;
    private long score = 0;
    private long numberOfLandmines = 2;

    private long sight = 1;
    public static final int UP = 1, DOWN = 2, LEFT = 3, RIGHT = 4;

    private int mode = 1;
    private static final int MENU = 1, PROMPTSCREEN = 2, PLAYSCREEN = 3, NAMESCREEN = 4, GAMEOVER = 5;


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        drawMenu();
        Stopwatch sw = new Stopwatch();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char cur = StdDraw.nextKeyTyped();
                if (mode == MENU) {
                    runMenu(cur);
                } else if (mode == PROMPTSCREEN) {
                    runPromptScreen(cur);
                } else if (mode == PLAYSCREEN) {
                    play(cur);
                } else if (mode == NAMESCREEN) {
                    runNameScreen(cur);
                } else if (mode == GAMEOVER) {
                    runGameOver(cur);
                }
            }

            while (mode == PLAYSCREEN) {
                //a while loop that only goes through once each time a key is passed in.
                if (sw.elapsedTime() > 0.4) {
                    moveMonsters();
                    if (mode == GAMEOVER) {
                        break;
                    }
                    ter.renderFrame(playerWld);
                    sw = new Stopwatch();
                }
                readMouse();
                drawHUD();
                break;
            }
        }
    }

    private void drawMenu() {
        ter.initialize(WIDTH, HEIGHT + 4);
        StdDraw.setPenColor(Color.WHITE);
        //title
        Font title = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(title);
        StdDraw.text(WIDTH / 2, HEIGHT * .75, "THE EXPLORER");
        //choices
        Font text = new Font("Monaco", Font.BOLD, 16);
        StdDraw.setFont(text);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 1, "Choose Name (C)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Quit (Q)");
        StdDraw.show();
    }

    private void drawHUD() {
        ter.renderFrame(playerWld);
        StdDraw.setPenColor(Color.WHITE);
        Font text = new Font("Monaco", Font.BOLD, 18);
        StdDraw.setFont(text);
        StdDraw.textLeft(0, HEIGHT + 3, "This is " + currentTileName);
        StdDraw.text(WIDTH / 2, HEIGHT + 3, "Name: " + playerName);
        StdDraw.textRight(WIDTH, HEIGHT + 3, "Floor: " + floor);
        StdDraw.textLeft(0, HEIGHT + 1, "Landmines you have: " + numberOfLandmines);
        StdDraw.text(WIDTH / 2, HEIGHT + 1, "Monsters left: " + monsters.size());
        StdDraw.textRight(WIDTH, HEIGHT + 1, "Score: " + score);
        StdDraw.show();
        StdDraw.pause(50);
        Font t = new Font("Monaco", Font.BOLD, 16);
        StdDraw.setFont(t);
    }

    private void runMenu(char cur) {
        if (cur == 'N' || cur == 'n') {
            drawPrompt("Enter random seed (press S when done): ");
            mode = PROMPTSCREEN;
        } else if (cur == 'L' || cur == 'l') {
            load();
            mode = PLAYSCREEN;
            playerWld = Player.getPlayerWld(sight, wld, position);
            ter.renderFrame(playerWld);
        } else if (cur == 'Q' || cur == 'q') {
            System.exit(0);
        } else if (cur == 'C' || cur == 'c') {
            mode = NAMESCREEN;
            playerName = "";
            drawPrompt("Enter a name (press ';' when done): ");
        }
    }

    private void drawPrompt(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font title = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(title);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    private void runPromptScreen(char cur) {
        drawPrompt("Enter random seed (press S when done): ");
        if (cur != 'S' && cur != 's') {
            if (cur - 48 >= 0 && cur - 48 <= 9) {
                seed = seed * 10 + cur - 48;
            }
            drawPrompt("Enter random seed (press S when done): " + seed);
        } else {
            initWorld();
            ter.initialize(WIDTH, HEIGHT + 4);
            playerWld = Player.getPlayerWld(sight, wld, position);
            ter.renderFrame(playerWld);
            mode = PLAYSCREEN;
        }
    }

    private void initWorld() {
        // initialize the world and place avatar
        rd = new Random(seed);
        Build b = new Build(rd);
        wld = b.getWorld();
        doorLocked = true;
        generateAvatar();
        generateDoor();
        generateKey();
        generateProps((int) floor / 2 + 1);
        generateMonsters((int) floor / 3 + 2);
    }

    private void generateAvatar() {
        // choose the starting point of avatar randomly
        int x = rd.nextInt(WIDTH);
        int y = rd.nextInt(HEIGHT);
        if (wld[x][y].equals(Tileset.FLOOR)) {
            wld[x][y] = Tileset.AVATAR;
            position = new Point(x, y);
        } else {
            generateAvatar();
        }
    }

    private void generateDoor() {
        int x = rd.nextInt(WIDTH - 1) + 1;
        int y = rd.nextInt(HEIGHT - 1) + 1;
        if (wld[x][y].equals(Tileset.WALL)) {
            if (wld[x - 1][y].equals(Tileset.FLOOR) || wld[x + 1][y].equals(Tileset.FLOOR)
                    || wld[x][y - 1].equals(Tileset.FLOOR) || wld[x][y + 1].equals(Tileset.FLOOR)) {
                // check if the door is accessible
                wld[x][y] = Tileset.LOCKED_DOOR;
                door = new Point(x, y);
            } else {
                generateDoor();
            }
        } else {
            generateDoor();
        }
    }

    private void generateKey() {
        int x = rd.nextInt(WIDTH);
        int y = rd.nextInt(HEIGHT);
        if (wld[x][y].equals(Tileset.FLOOR)) {
            wld[x][y] = KEY;
        } else {
            generateKey();
        }
    }

    private void generateProps(int n) {
        while (n > 0) {
            generateOneProp();
            n--;
        }
    }

    private void generateOneProp() {
        int x = rd.nextInt(WIDTH);
        int y = rd.nextInt(HEIGHT);
        if (wld[x][y].equals(Tileset.FLOOR)) {
            wld[x][y] = PROP;
        } else {
            generateOneProp();
        }
    }

    private void generateMonsters(int n) {
        monsters = new ArrayList<>();
        while (n > 0) {
            Monster m = new Monster(wld, rd, position);
            Point mPos = m.getPos();
            monsters.add(m);
            wld[mPos.getX()][mPos.getY()] = MONSTER;
            n--;
        }
    }

    private void play(char cur) {
        if (cur == 'd' || cur == 'D' || cur == 's' || cur == 'S'
                || cur == 'a' || cur == 'A' || cur == 'w' || cur == 'W') {
            moveAvatar(cur);
        } else if (cur == 'j' || cur == 'J') {
            putLandmine();
        } else if (cur == ':') {
            stagedToSave = true;
        } else if (cur == 'Q' || cur == 'q') {
            if (stagedToSave) {
                write();
                System.exit(0);
            }
        }
    }

    private void putLandmine() {
        if (numberOfLandmines > 0) {
            wld[position.getX()][position.getY()] = LANDMINE;
            numberOfLandmines--;
        }
    }

    private void runNameScreen(char cur) {
        //drawPrompt("Enter a name (press ';' when done): ");
        if (cur != ';') {
            playerName += cur;
            drawPrompt("Enter a name (press ';' when done): " + playerName);
        } else {
            mode = MENU;
            drawMenu();
        }
    }

    private void moveAvatar(char ch) {
        Point newP;
        if (ch == 'd' || ch == 'D') {
            newP = new Point(position.getX() + 1, position.getY());
            sight = RIGHT;
        } else if (ch == 's' || ch == 'S') {
            newP = new Point(position.getX(), position.getY() - 1);
            sight = DOWN;
        } else if (ch == 'a' || ch == 'A') {
            newP = new Point(position.getX() - 1, position.getY());
            sight = LEFT;
        } else {
            newP = new Point(position.getX(), position.getY() + 1);
            sight = UP;
        }
        if (check(newP)) {
            TETile nextTile = wld[newP.getX()][newP.getY()];
            if (!nextTile.equals(LANDMINE)) {
                wld[newP.getX()][newP.getY()] = Tileset.AVATAR;
            }
            TETile prevTile = wld[position.getX()][position.getY()];
            if (!prevTile.equals(LANDMINE)) {
                wld[position.getX()][position.getY()] = Tileset.FLOOR;
            }
            position = newP;
            playerWld = Player.getPlayerWld(sight, wld, position);
        } else if (newP.equals(door)) {
            if (!doorLocked) {
                generateNextFloor();
                write();
            }
        }
    }

    private void moveMonsters() {
        List<Monster> diedMonsters = new ArrayList<>();
        for (Monster m : monsters) {
            Point oldPos = m.getPos();
            wld[oldPos.getX()][oldPos.getY()] = Tileset.FLOOR;
            m.move(wld, rd, position);
            Point newPos = m.getPos();
            if (newPos.equals(position)) {
                mode = GAMEOVER;
                seed = 0;
                drawGameOver();
            } else if (wld[newPos.getX()][newPos.getY()].equals(LANDMINE)) {
                diedMonsters.add(m);
                wld[newPos.getX()][newPos.getY()] = BOOTY;
            } else {
                wld[newPos.getX()][newPos.getY()] = MONSTER;
            }
        }
        monsters.removeAll(diedMonsters);
        playerWld = Player.getPlayerWld(sight, wld, position);
    }

    private void drawGameOver() {
        //displays message of "Game Over" and gives option to quit (saved to current floor) or restart
        ter.initialize(WIDTH, HEIGHT + 4);
        StdDraw.setPenColor(Color.WHITE);
        //title
        Font title = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(title);
        StdDraw.text(WIDTH / 2, HEIGHT * .75, "GAME OVER, YOU REACHED FLOOR " + floor);
        StdDraw.text(WIDTH / 2, HEIGHT * .68, "Your score is " + score);
        //choices
        Font text = new Font("Monaco", Font.BOLD, 16);
        StdDraw.setFont(text);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1, "Quit (Q)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Return to Main Menu (R)");
        StdDraw.show();
    }

    private void runGameOver(char cur) {
        if (cur == 'Q' || cur == 'q') {
            System.exit(0);
        } else if (cur == 'R' || cur == 'r') {
            drawMenu();
            mode = MENU;
        }
    }

    private boolean check(Point p) {
        // check if avatar can move to point p
        TETile tile = wld[p.getX()][p.getY()];
        if (tile.equals(Tileset.FLOOR) || tile.equals(LANDMINE)) {
            return true;
        } else if (tile.equals(KEY)) {
            doorLocked = false;
            wld[door.getX()][door.getY()] = Tileset.UNLOCKED_DOOR;
            return true;
        } else if (tile.equals(PROP)) {
            numberOfLandmines += 1;
            return true;
        } else if (tile.equals(BOOTY)) {
            score += floor;
            return true;
        }
        return false;
    }

    private void generateNextFloor() {
        seed += 1;
        floor += 1;
        initWorld();
        write();
        ter.initialize(WIDTH, HEIGHT + 4);
        playerWld = Player.getPlayerWld(sight, wld, position);
        ter.renderFrame(playerWld);
    }

    private void readMouse() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (mode == PLAYSCREEN) {
            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                TETile tile = playerWld[x][y];
                currentTileName = tile.description();
                if (currentTileName.equals(KEY.description())) {
                    currentTileName = "key";
                } else if (currentTileName.equals(MONSTER.description())) {
                    currentTileName = "monster";
                } else if (currentTileName.equals(LANDMINE.description())) {
                    currentTileName = "landmine";
                } else if (currentTileName.equals(PROP.description())) {
                    currentTileName = "prop";
                } else if (currentTileName.equals(BOOTY.description())) {
                    currentTileName = "booty";
                }
            }
        }
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        StringInputDevice sid = new StringInputDevice(input);
        char key = sid.getNextKey();
        if (key == 'C' || key == 'c') {
            getName(sid);
        }
        key = sid.getNextKey();
        if (key == 'N' || key == 'n') {
            getSeed(sid);
            initWorld();
            execute(sid);
        } else if (key == 'L' || key == 'l') {
            load();
            execute(sid);
        }
        return wld;
    }

    private void getSeed(StringInputDevice sid) {
        while (sid.possibleNextInput()) {
            char curr = sid.getNextKey();
            if (curr != 'S' && curr != 's') {
                long val = Long.valueOf(curr) - 48;
                if (val < 0 || val > 9) {
                    throw new IllegalArgumentException("invalid seed");
                }
                seed = seed * 10 + val;
            } else {
                return;
            }
        }
        throw new IllegalArgumentException("seed needs to end with S");
    }

    private void getName(StringInputDevice sid) {
        playerName = "";
        while (sid.possibleNextInput()) {
            char curr = sid.getNextKey();
            if (curr != ';') {
                playerName += curr;
            } else {
                return;
            }
        }
    }

    private void execute(StringInputDevice sid) {
        // execute commands from sid
        while (sid.possibleNextInput()) {
            char next = sid.getNextKey();
            if (next == 'd' || next == 'D' || next == 's' || next == 'S'
                    || next == 'a' || next == 'A' || next == 'w' || next == 'W') {
                moveAvatar(next);
            } else if (next == ':') {
                if (sid.possibleNextInput()) {
                    char nextKey = sid.getNextKey();
                    if (nextKey == 'Q' || nextKey == 'q') {
                        write();
                    }
                }
            }
        }
    }


    /* Save current world to Save.txt */
    private void write() {
        String fileName = "Save.txt";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            recordCurrentStage(bufferedWriter);
        } catch (IOException e) {
            // Exception handling
        }
    }

    private void recordCurrentStage(BufferedWriter bufferedWriter) throws IOException {
        List<Long> vars = collectVar();
        List<String> tiles = collectTiles();
        for (String tile : tiles) {
            bufferedWriter.write(tile, 0, tile.length());
            bufferedWriter.newLine();
        }
        for (long var : vars) {
            String s = Long.toString(var);
            bufferedWriter.write(s, 0, s.length());
            bufferedWriter.newLine();
        }
        bufferedWriter.write(playerName, 0, playerName.length());
    }

    private List<Long> collectVar() {
        List<Long> longs = new ArrayList<>();
        longs.add(floor);
        longs.add(sight);
        longs.add(seed);
        longs.add((long) position.getX());
        longs.add((long) position.getY());
        longs.add(numberOfLandmines);
        longs.add(score);
        return longs;
    }

    private List<String> collectTiles() {
        List<String> tiles = new ArrayList<>();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                tiles.add(wld[x][y].description());
            }
        }
        return tiles;
    }

    /* Load from Save.txt */
    private void load() {
        String fileName = "Save.txt";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line = bufferedReader.readLine();
            wld = new TETile[WIDTH][HEIGHT];
            monsters = new ArrayList<>();
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    wld[x][y] = getTileAndSetVar(line, x, y);
                    line = bufferedReader.readLine();
                }
            }
            floor = new Long(line);
            sight = new Long(bufferedReader.readLine());
            seed = new Long(bufferedReader.readLine());
            int x = new Integer(bufferedReader.readLine());
            int y = new Integer(bufferedReader.readLine());
            position = new Point(x, y);
            numberOfLandmines = new Long(bufferedReader.readLine());
            score = new Long(bufferedReader.readLine());
            if (playerName.equals("player")) {
                playerName = bufferedReader.readLine();
            }
            rd = new Random(seed);
        } catch (FileNotFoundException e) {
            // Exception handling
        } catch (IOException e) {
            // Exception handling
        }
    }

    private TETile getTileAndSetVar(String tileName, int x, int y) {
        if (tileName.equals("you")) {
            position = new Point(x, y);
            return Tileset.AVATAR;
        } else if (tileName.equals("nothing")) {
            return Tileset.NOTHING;
        } else if (tileName.equals("wall")) {
            return Tileset.WALL;
        } else if (tileName.equals("floor")) {
            return Tileset.FLOOR;
        } else if (tileName.equals("locked door")) {
            doorLocked = true;
            door = new Point(x, y);
            return Tileset.LOCKED_DOOR;
        } else if (tileName.equals("unlocked door")) {
            doorLocked = false;
            door = new Point(x, y);
            return Tileset.UNLOCKED_DOOR;
        } else if (tileName.equals(KEY.description())) {
            return KEY;
        } else if (tileName.equals(MONSTER.description())) {
            Point pos = new Point(x, y);
            monsters.add(new Monster(pos));
            return MONSTER;
        } else if (tileName.equals(LANDMINE.description())) {
            return LANDMINE;
        } else if (tileName.equals(PROP.description())) {
            return PROP;
        } else {
            return null;
        }
    }

}
