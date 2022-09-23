import lift.LiftView;
import lift.Passenger;

public class Main {

	protected static final int NBR_FLOORS = 7;
	protected static final int MAX_PASSENGERS = 4;
	protected static final int TOTAL_PASSENGERS = 5;

	public static void main(String[] args) {
		LiftView view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
		LiftMonitor mon = new LiftMonitor(view);

		// Init Passengers
		for (int i = 0; i < TOTAL_PASSENGERS; i++) {
			new PassengerThread(view,mon).start();
		}

		// Init Lift
		new LiftThread(view, mon).start();
	}
}
