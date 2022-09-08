import java.awt.Color;

// This .java file dictates almost all the rules for Super Mario Lite, from
// jumping, to death, to drawing out the map.
public class SuperMarioLite {

    private static final double MOVE_MAGNITUDE = 0.05; // How much Mario moves each time
    private static final double BASE_POSITION = 0.025; // Where Mario starts out (x-coordinate)
    private static final double BASE_HEIGHT = 0.18; // Where Mario starts out (y-coordinate)
    private static final String JUMP_AUDIO = "jump.wav";
            // Jump audio filename (used in two methods)

    private double px; // Mario's position (x-coordinate)
    private double py; // Mario's position (y-coordinate)

    private int levelNumber; // Level that Mario's on
    private int lives; // Lives left

    // X and Y-Coordinates of platforms in Lvl 1
    private double[] xCoord;
    private double[] yCoord;

    // X and Y-Coordinates of plants in Lvl 2
    private double[] xCoord2;
    private double[] yCoord2;

    // Constructor for a new Mario; sets position, level number, and lives
    public SuperMarioLite() {
        px = BASE_POSITION;
        py = BASE_HEIGHT;
        levelNumber = 1;
        lives = 3;
    }

    public int numLives() {
        return lives;
    } // Returns number of lives left

    public int numLevel() {
        return levelNumber;
    } // Returns level that Mario's on

    // Draws the entire level, including platforms and obstacles, using a
    // pre-determined set of x- and y-coordinates in arrays
    public void drawLevel() {
        StdDraw.clear(Color.cyan); // Background
        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(10);
        StdDraw.text(0.5, 0.98, "Super Mario Lite");

        // Sets color, locations, and lengths of platforms
        if (levelNumber == 1) {
            StdDraw.setPenRadius(0.1);
            StdDraw.setPenColor(Color.darkGray);
            xCoord = new double[] { 0, 0.1, 0.27, 0.4, 0.6, 0.65, 0.85, 1 };
            yCoord = new double[] {
                    BASE_HEIGHT - 0.08, BASE_HEIGHT - 0.08, 0.3, 0.3,
                    0.5, 0.5, 0.7, 0.7
            };

            // Draws the platforms
            for (int i = 0; i < xCoord.length; i += 2) {
                StdDraw.setPenRadius(0.03);
                StdDraw.setPenColor(Color.black);
                StdDraw.line(xCoord[i], yCoord[i], xCoord[i + 1], yCoord[i + 1]);
            }
        }

        // Sets color of the floor and locations of the plants & Goombas
        else {
            if (levelNumber == 2) {
                StdDraw.clear(Color.green);
            }
            else {
                StdDraw.clear(Color.red);
            }
            StdDraw.setPenRadius(0.05);
            StdDraw.line(0, BASE_HEIGHT - 0.07, 1, BASE_HEIGHT - 0.07);
            xCoord2 = new double[] { 0.2, 0.5, 0.8 };
            double y = BASE_HEIGHT - 0.01;
            yCoord2 = new double[] { y, y, y };
            if (levelNumber == 2) {
                for (int i = 0; i < xCoord2.length; i++) {
                    drawPipePlant(xCoord2[i], yCoord2[i]);
                }
            }
            else {
                for (int i = 0; i < xCoord2.length; i++) {
                    drawGoomba(xCoord2[i], yCoord2[i]);
                }
            }
        }
    }

    // Draws Mario using an image file and a set of coordinates
    public void drawMario() {
        StdDraw.picture(px, py, "mario_PNG88.png");
    }

    // Draws Piranha Plants using an image file and a set of coordinates
    public void drawPipePlant(double x, double y) {
        StdDraw.picture(x, y, "piranha plant.png");
    }

    public void drawGoomba(double x, double y) {
        StdDraw.picture(x, y, "goomba.png");
    }

    // Moves and animates Mario according to key input
    public void moveMario() {
        if (StdDraw.hasNextKeyTyped()) {
            char type = StdDraw.nextKeyTyped();
            if (type == 'd') {
                moveRight();
            }
            else if (type == 'w') {
                jumpUp();
            }
            else if (type == 'e') {
                jumpUpRight();
            }
        }
    }

    // Moves Mario to the right
    public void moveRight() {
        px += 0.5 * MOVE_MAGNITUDE;
        StdDraw.clear();
        drawAll();
        StdDraw.show();
        StdDraw.pause(50);
    }

    // Triggers an upward jump animation
    public void jumpUp() {
        StdAudio.play(JUMP_AUDIO);
        for (int timer = 0; timer < 14; timer++) {
            if (timer < 7) {
                py += MOVE_MAGNITUDE;
            }
            else {
                py -= MOVE_MAGNITUDE;
            }
            // Draws Mario's animation, then checks whether he's landed
            StdDraw.clear();
            drawAll();
            StdDraw.show();
            StdDraw.pause(50);
            if (checkLanding()) break;
        }
    }

    // Triggers an upright-ward jump animation
    public void jumpUpRight() {
        StdAudio.play(JUMP_AUDIO);
        for (int timer = 0; timer < 10; timer++) {
            // Mario's movement calculations
            py += 0.5 * MOVE_MAGNITUDE;
            px += 0.5 * MOVE_MAGNITUDE;
            StdDraw.clear(Color.cyan);
            drawAll();
            StdDraw.show();
            StdDraw.pause(50);
            if (death()) { // If he dies, reset the level
                reset();
                break;
            }
            if (checkLanding()) break; // If he's landed, stop moving
        }
        if (!checkLanding()) fall(); // If he hasn't landed, fall
    }

    // Triggers a falling animation
    public void fall() {
        while (!checkLanding() && py > 0) {
            py -= MOVE_MAGNITUDE;
            StdDraw.clear(Color.cyan);
            drawAll();
            StdDraw.show();
            StdDraw.pause(50);
        }
    }

    // How close Mario must be to the edge of the screen to finish the level
    public boolean finishLevel() {
        return (px >= 0.95);
    }

    // Increments level count
    public void changeLevel() {
        levelNumber++;
    }

    // If Mario finished level 3, he's done with the game
    public boolean finishGame() {
        return (levelNumber == 3 && finishLevel());
    }

    // Draws the hearts on the top right of the screen
    public void drawLives() {
        double x = 0.9;
        for (int i = 0; i < lives; i++) {
            StdDraw.picture(x, 0.95, "heart.png");
            x += 0.03;
        }
    }

    // Bundles all the necessary drawing functions together
    public void drawAll() {
        drawLevel();
        drawMario();
        drawLives();
    }

    // Checks if Mario has landed
    public boolean checkLanding() {
        if (levelNumber == 1) {
            for (int i = 0; i < xCoord.length; i += 2) {
                if (px >= (xCoord[i]) && px <= (xCoord[i + 1])
                        && (py - yCoord[i]) <= 0.1) {
                    return true;
                }
            }
        }

        if (levelNumber == 2 || levelNumber == 3) {
            double y = BASE_HEIGHT + 0.01;
            if (py - y <= 0.01) {
                return true;
            }
        }
        return false;
    }

    // Sets the requirements for Mario to die
    public boolean death() {
        // How close Mario has to be to an obstacle to consider him "on it"
        double obstacleRadius = 0.03;

        boolean death = false;

        if (levelNumber == 1) {
            death = py < BASE_HEIGHT; // If Mario falls from the floor, he dies
            // If Mario is too far from the platforms, then make him fall & die
            for (int i = 0; i < xCoord.length; i += 2) {
                if (px > (xCoord[i] - obstacleRadius) && px < (xCoord[i + 1] + obstacleRadius)
                        && (py - yCoord[i]) < 0.03) {
                    fall();
                    death = true;
                }
            }
            for (int i = 1; i < xCoord.length - 1; i = i + 2) {
                if (px > (xCoord[i] + obstacleRadius) && px < (xCoord[i + 1] - obstacleRadius)
                        && py < (yCoord[i] + 0.1)) {
                    fall();
                    death = true;
                }
            }
        }

        // If Mario gets close enough to a plant or a Goomba, make him die
        if (levelNumber == 2 || levelNumber == 3) {
            for (int i = 0; i < xCoord2.length; i++) {
                if (Math.abs(xCoord2[i] - px) < 0.06 && (Math.abs(py - yCoord2[i]) < 0.1)) {
                    death = true;
                }
            }
        }

        if (death) {
            --lives;
        }

        return death;
    }

    // Has he reached zero lives?
    public boolean finalDeath() {
        return lives == 0;
    }

    // Triggers the "play again" screen
    public void playAgain(String filename) {
        StdDraw.clear();
        StdDraw.picture(0.5, 0.5, filename);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setPenRadius(1000);
        StdDraw.text(0.5, 0.4, "Press a twice to play again.");
        if (StdDraw.hasNextKeyTyped()) {
            if (StdDraw.nextKeyTyped() == 'a') {
                totalReset();
            }
            else {
                System.exit(0);
            }
        }
    }

    // Informs the player of the controls
    public void instructions() {
        String background = "skybackground.jpeg";
        StdDraw.picture(0.5, 0.5, background);
        for (int timer = 5; timer >= 0; --timer) {
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.setPenRadius(100);
            StdDraw.text(0.5, 0.6, "Press w to jump up,");
            StdDraw.text(0.5, 0.5, "press e to jump right,");
            StdDraw.text(0.5, 0.4, "and press d to move right.");
            StdDraw.text(0.5, 0.3, "Game begins in " + timer + "...");
            StdDraw.pause(1000);
            StdDraw.show();
            StdDraw.picture(0.5, 0.5, background);
        }
    }

    // Accounts for death by the void
    public boolean outBounds() {
        if (px < 0 || py < BASE_HEIGHT || py > 1) {
            --lives;
            return true;
        }
        return false;
    }

    // Resets Mario's position
    public void reset() {
        px = BASE_POSITION;
        py = BASE_HEIGHT;
    }

    // Resets the entire game if the player wants to play again
    public void totalReset() {
        reset();
        levelNumber = 1;
        lives = 3;
    }

    // A bunch of tests for our methods
    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering();
        SuperMarioLite mario = new SuperMarioLite();
        StdAudio.loop("theme song.wav");
        StdOut.printf("Lives: %d; On level %d; \n", mario.numLives(),
                      mario.numLevel());
        StdDraw.setCanvasSize(1400, 800);
        mario.instructions();
        mario.drawAll();

        // Should print 0.025, then 0.18
        StdOut.println("Base x-position: " + BASE_POSITION);
        StdOut.println("Base y-position: " + BASE_HEIGHT);

        // Should print 0.025, then 0.18
        mario.jumpUp();
        StdOut.println("Mario x-position: " + mario.px);
        StdOut.println("Mario y-position: " + mario.py);

        // Should print 0.27499999999999997, then 0.38000000000000017
        mario.jumpUpRight();
        StdOut.println("Mario x-position: " + mario.px);
        StdOut.println("Mario y-position: " + mario.py);

        // Should print 0.3, then 0.38000000000000017
        mario.moveRight();
        StdOut.println("Mario x-position: " + mario.px);
        StdOut.println("Mario y-position: " + mario.py);

        // Should print "Lives left: 3, Dead? false; Out of bounds? false"
        StdOut.printf("Lives left: %d, Dead? %b; Out of bounds? %b\n",
                      mario.numLives(), mario.death(), mario.outBounds());

        // Prints out Mario's stats in certain situations (like when he has to
        // reset)
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                mario.moveMario();
                if (mario.death() || mario.outBounds()) {
                    // Should be one less life, then true for death, false, false
                    StdOut.println("Lives left: " + mario.numLives());
                    mario.reset();
                }
                // Should print Mario's position after every time he moves
                StdOut.println("X-position: " + mario.px);
                StdOut.println("Y-position: " + mario.py);
                mario.drawAll();
                StdDraw.show();
                StdDraw.pause(50);
            }

            if (mario.finalDeath()) {
                // NOTE: The while loop is always true, so this StdOut test will
                // keep filling up the command line until you close out StdDraw

                // If Mario has lost the game, should print true
                StdOut.printf("Final death? " + mario.finalDeath());
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(0.5, 0.45,
                             "You Lose! Press any key to exit.");
                StdDraw.show();
                StdDraw.pause(50);
                StdDraw.clear();
                mario.playAgain("game over.png");
            }

            if (mario.finishGame()) {
                // NOTE: The while loop is always true, so this StdOut test will
                // keep filling up the command line until you close out StdDraw

                // If Mario has won the game, should print true
                StdOut.println("Finished game? " + mario.finishGame());
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(0.5, 0.5,
                             "You Win! Press any key to exit.");
                StdDraw.show();
                StdDraw.pause(50);
                StdDraw.clear();
                mario.playAgain("win.png");
            }

            // Should print the number of lives left before level 2, true, then
            // false
            if (mario.finishLevel() && (mario.numLevel() == 1 ||
                    mario.numLevel() == 2)) {
                StdOut.printf("Lives left: %d; Finished level? %b; "
                                      + "Out of bounds? %b\n",
                              mario.numLives(), mario.finishLevel(),
                              mario.outBounds());
                mario.reset();
                mario.changeLevel();
            }
            StdDraw.show();
        }
    }
}