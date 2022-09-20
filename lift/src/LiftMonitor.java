import lift.LiftView;
import lift.Passenger;

public class LiftMonitor {

	private int[] toEnter = new int[Main.NBR_FLOORS];
	private int[] toExit = new int[Main.NBR_FLOORS];
	private int currentFloor;
	private int passengers;
	private LiftView view;
	private boolean closedDoors = true;
	private boolean moving = false;

	public LiftMonitor(LiftView view) {
		this.passengers = 0;
		this.currentFloor = 0;
		this.view = view;
	}

	public synchronized void setFloor(int floor) {
		currentFloor = floor;
	}

	public synchronized int getFloor() {
		return currentFloor;
	}

	public synchronized boolean moving() {
		return moving;
	}

	public synchronized void handleDoors(int floor) throws InterruptedException {
		setFloor(floor);
		if (passengers < Main.MAX_PASSENGERS) { // kan släppa av och ta in folk
			if (toEnter[floor] > 0 || toExit[floor] > 0) {
				System.out.println("Våning" + floor + ",ToEnter:" + toEnter[floor]);
				view.openDoors(floor);
				closedDoors = false;
				notifyAll();
				while ((toEnter[floor] > 0 || toExit[floor] > 0) && passengers < Main.MAX_PASSENGERS) {
					wait();
				}
				view.closeDoors();
				closedDoors = true;
			}
		} else { // kan bara släppa av
			if (toExit[floor] > 0) {
				view.openDoors(floor);
				closedDoors = false;
				notifyAll();
				while (toExit[floor] > 0) {
					wait();
				}
				view.closeDoors();
				closedDoors = true;
			}
		}
	}

	public synchronized void enter(Passenger pass, int fromFloor) throws InterruptedException {
		toEnter[fromFloor]++;
		moving = true;
		while (getFloor() != fromFloor || passengers >= Main.MAX_PASSENGERS || closedDoors) {
			wait();
		}
		pass.enterLift(); // step inside
		toEnter[fromFloor]--;
		passengers++;
		notifyAll();
	}

	public synchronized void exit(Passenger pass, int toFloor) throws InterruptedException {
		toExit[toFloor]++;
		while (getFloor() != toFloor || closedDoors) {
			wait();
		}
		pass.exitLift(); // step outside
		toExit[toFloor]--;
		passengers--;
		if(passengers == 0) {			
			moving = false;
		}
		notifyAll();
	}
}
