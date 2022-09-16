package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

public class FactoryController {

	static class Monitor {
		Conveyor conv; 
		boolean painting = false;
		boolean pressing = false;
		
		public Monitor (Conveyor conv) {
			this.conv = conv;
		}
		
		public synchronized void pressConvOn() throws InterruptedException{
			while(painting || pressing) { //vänta på att paint ska vara klar, jag sovar medans.
				 wait();
			}
			conv.on(); //fortsätt bandet
		}
		
		public synchronized void paintFinished() {
			painting = !painting;
			notifyAll(); //Paint är klar, väck press!
		}
		
		public synchronized void pressFinished() {
			pressing = !pressing;
			notifyAll();
		}
		
		public synchronized void convOff() {
			conv.off();
		}
	}

	//Väntar på Paint att göra färdigt
	public static void press(Monitor mon, Tool press) {
		while (true) {
			try {
				press.waitFor(Widget.GREEN_BLOB);
				mon.pressFinished(); //pressing true
				mon.convOff();
				press.performAction();
				mon.pressFinished(); //pressing false
				mon.pressConvOn(); //Vi väntar på att paint ska vara färdigt och sen kan vi köra bandet
			} catch (InterruptedException e) {
				throw new Error(e);
			}
		}
	}
	
	//Gör en Notify() när den är klar
	public static void paint(Monitor mon, Tool paint) {
		while (true) {
			try {
				paint.waitFor(Widget.ORANGE_MARBLE);
				mon.paintFinished();
				mon.convOff(); //Stannar bandet 
				paint.performAction();
				mon.paintFinished();
				mon.pressConvOn();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Factory factory = new Factory();

		Conveyor conveyor = factory.getConveyor();

		Tool press = factory.getPressTool();
		Tool paint = factory.getPaintTool();

		Monitor mon = new Monitor(conveyor);

		Thread t1 = new Thread(() -> press(mon, press));
		Thread t2 = new Thread(() -> paint(mon, paint));

		t1.start();
		t2.start();
	}
}
