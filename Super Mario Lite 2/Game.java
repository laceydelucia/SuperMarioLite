import java.awt.Color;

// While SuperMarioLite.java sets the behind-the-scenes rules of the game,
// Game.java runs it by also setting forth more rules that dictate the game's
// behavior. This runs the actual game, with no tests.
public class Game {
    public static void main(String[] args) {
        // Smoother animation, makes new Mario, plays music, draws canvas
        // and draws everything for the beginning of the game
        StdDraw.enableDoubleBuffering();
        SuperMarioLite mario = new SuperMarioLite(); // Creates a new Mario
        StdAudio.loop("theme song.wav");
        StdDraw.setCanvasSize(1400, 800);
        mario.instructions();
        mario.drawAll();
        while (true) {
            // Makes Mario reset if certain conditions are met
            if (StdDraw.hasNextKeyTyped()) {
                mario.moveMario();
                if (mario.death() || mario.outBounds()) {
                    mario.reset();
                }
                // Animates Mario
                mario.drawAll();
                StdDraw.show();
                StdDraw.pause(50);
            }

            // Triggers the lose screen
            if (mario.finalDeath()) {
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(0.5, 0.45,
                             "You Lose! Press any key to exit.");
                StdDraw.show();
                StdDraw.pause(500);
                StdDraw.clear();
                mario.playAgain("game over.png");
            }

            // Triggers the win screen
            if (mario.finishGame()) {
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(0.5, 0.5,
                             "You Win! Press any key to exit.");
                StdDraw.show();
                StdDraw.pause(500);
                StdDraw.clear();
                mario.playAgain("win.png");
            }

            // Changes the level
            if (mario.finishLevel() && (mario.numLevel() == 1 ||
                    mario.numLevel() == 2)) {
                mario.reset();
                mario.changeLevel();
            }
            StdDraw.show();
        }
    }
}