package factory.model;

/** A tool in the factory (press or paint gun). */
public interface Tool {
    /**
     * For a press, move the press piston down, then back up. For a paint gun, spray paint.
     * This method blocks until the action has completed.
     */
    void performAction();

    /**
     * Blocks until a widget of the indicated kind is positioned under this tool
     * (detected by sensor).
     */
    void waitFor(Widget w);
}
