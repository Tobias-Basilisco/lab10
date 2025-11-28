package it.unibo.mvc;

/**
 * View interface for the Draw Number game.
 */
public interface DrawNumberView {

    /**
     * Sets the observer/controller for this view.
     *
     * @param observer the controller to attach
     */
    void setObserver(DrawNumberViewObserver observer);

    /**
     * This method is called before the UI is used. It should finalize its status (if needed).
     */
    void start();

    /**
     * Informs the user that the inserted number is not correct.
     */
    void numberIncorrect();

    /**
     * Informs the user about the result of the last draw.
     *
     * @param res the result of the last draw
     */
    void result(DrawResult res);

    /**
     * Informs the user about an error
     * 
     * @param message
     */
    void displayError(String message);
}
