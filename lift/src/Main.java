import lift.LiftView;
import lift.Passenger;

public class Main {

	protected static final int NBR_FLOORS = 7;
	protected static final int MAX_PASSENGERS = 4;
	protected static final int TOTAL_PASSENGERS = 20;

	public static void simulateLift(LiftView view, LiftMonitor mon) {
		try {
			while (true) {
				if (mon.moving()) {
					for (int i = 0; i < NBR_FLOORS - 1; i++) {
						mon.handleDoors(i);
						if(mon.NoMorePassengers()) {
							return;
						}
						view.moveLift(i, i + 1);
					}
					for (int i = NBR_FLOORS - 1; i > 0; i--) {
						mon.handleDoors(i);
						if(mon.NoMorePassengers()) {
							return;
						}
						view.moveLift(i, i - 1);
					}
				}
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}

	public static void simulatePerson(LiftView view, LiftMonitor mon) {
		try {
			Passenger pass = view.createPassenger();
			int fromFloor = pass.getStartFloor();
			int toFloor = pass.getDestinationFloor();
			pass.begin(); // walk in (from left)
			mon.enter(pass, fromFloor);
			mon.exit(pass, toFloor);
			pass.end();
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}

	public static void main(String[] args) {
		LiftView view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
		LiftMonitor mon = new LiftMonitor(view);

		// Passengers
		for (int i = 0; i < TOTAL_PASSENGERS; i++) {
			new Thread(() -> simulatePerson(view, mon)).start();
		}

		// Lift
		new Thread(() -> simulateLift(view, mon)).start();

	}
}
