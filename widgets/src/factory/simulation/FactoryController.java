package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

public class FactoryController {
    
    public static void main(String[] args) {
        Factory factory = new Factory();

        Conveyor conveyor = factory.getConveyor();
        
        Tool press = factory.getPressTool();
        Tool paint = factory.getPaintTool();

        while (true) {
            press.waitFor(Widget.GREEN_BLOB);
            conveyor.off();
            press.performAction();
            conveyor.on();
        }
    }
}
