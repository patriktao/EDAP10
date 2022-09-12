package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

public class FactoryController {

	static class Monitor {
		Conveyor conv; 
		boolean paint_finished;
		
		public Monitor (Conveyor conv) {
			this.conv = conv;
			this.paint_finished = false;
		}
		
		public synchronized void pressConvOn() throws InterruptedException{
			while(paint_finished == false) { //vänta på att paint ska vara klar, jag sovar medans.
				 wait();
			}
			conv.on(); //fortsätt bandet
		}
		
		public synchronized void paintConvOn() throws InterruptedException{
			notifyAll(); //Paint är klar, väck press!
		}
		
		public synchronized void paintFinished(boolean bool) {
			paint_finished = bool;
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
				mon.convOff();
				press.performAction();
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
				mon.convOff(); //Stannar bandet 
				paint.performAction();
				mon.paintFinished(true); //ändrar villkoret
				mon.paintConvOn(); //notifyar pressen att vi är klara!
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
