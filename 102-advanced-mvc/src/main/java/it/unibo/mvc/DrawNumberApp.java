package it.unibo.mvc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Application entry point for DrawNumber game.
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private static final String CONFIG_FILE_NAME = "config.yml";
    private static final int MIN = 0;
    private static final int MAX = 100;
    private static final int ATTEMPTS = 10;

    private int configMin = MIN;
    private int configMax = MAX;
    private int configAttempts = ATTEMPTS;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * Constructor.
     *
     * @param configFile
     *            the configuration file path
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final String configFileName, final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }

        readConfigFile(configFileName);
        this.model = new DrawNumberImpl(configMin, configMax, configAttempts);
        // System.out.println(configFileName);
        // System.out.println(configMin);
        // System.out.println(configMax);
        // System.out.println(configAttempts);
    }

    private void readConfigFile(final String configFileName){
        try (final BufferedReader bReader = new BufferedReader(
                new InputStreamReader(
                    ClassLoader.getSystemResourceAsStream(configFileName),
                    "UTF-8"
                )    
            )){
            configMin = readNextInt(bReader);
            configMax = readNextInt(bReader);
            configAttempts = readNextInt(bReader);
            
        } catch (IOException e){
            for (final DrawNumberView view: views){
                view.displayError("An error ocurred during config file read:" + e.getMessage());
            }
        }
    }

    private int readNextInt(final BufferedReader bReader) throws IOException{
        return Integer.parseInt(
                    Objects.requireNonNull(bReader.readLine())
                    .split(":")[1]
                    .trim()
                );
    }



    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (final IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    @SuppressFBWarnings(
        value = "DM_EXIT",
        justification = "Acceptable for exercising purposes."
    )
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * Application entry point.
     *
     * @param args
     *            ignored
     * @throws FileNotFoundException if the configuration file cannot be fetched
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(
            CONFIG_FILE_NAME,
            new DrawNumberViewImpl(),
            new DrawNumberViewImpl(),
            new PrintStreamView(System.out)
        );
    }

}
