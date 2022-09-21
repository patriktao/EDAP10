import java.util.ArrayDeque;

import lift.LiftView;
import lift.Passenger;

public class LiftMonitor {

	private int[] toEnter = new int[Main.NBR_FLOORS];
	private int[] toExit = new int[Main.NBR_FLOORS];
	private int currentFloor;
	private int passengersInLift;
	private LiftView view;
	private boolean closedDoors = true;
	private boolean moving = false;
	private ArrayDeque<Integer>stack = new ArrayDeque<Integer>();

	public LiftMonitor(LiftView view) {
		this.passengersInLift = 0;
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
	
	public synchronized boolean NoMorePassengers() {
		return stack.isEmpty() && passengersInLift == 0;
	}

	public synchronized void handleDoors(int floor) throws InterruptedException {
		setFloor(floor);
		if(NoMorePassengers()) {
			moving = false;
		}
		if (passengersInLift < Main.MAX_PASSENGERS) { // kan släppa av och ta in folk
			if (toEnter[floor] > 0 || toExit[floor] > 0) {
				System.out.println("Våning" + floor + ",ToEnter:" + toEnter[floor]);
				view.openDoors(floor);
				closedDoors = false;
				notifyAll();
				while ((toEnter[floor] > 0 || toExit[floor] > 0) && passengersInLift < Main.MAX_PASSENGERS) {
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
		stack.add(1);
		moving = true;
		while (getFloor() != fromFloor || passengersInLift >= Main.MAX_PASSENGERS || closedDoors) {
			wait();
		}
		pass.enterLift(); // step inside
		toEnter[fromFloor]--;
		passengersInLift++;
		notifyAll();
	}

	public synchronized void exit(Passenger pass, int toFloor) throws InterruptedException {
		toExit[toFloor]++;
		while (getFloor() != toFloor || closedDoors) {
			wait();
		}
		pass.exitLift(); // step outside
		toExit[toFloor]--;
		passengersInLift--;
		stack.pop();
		notifyAll();
	}
}
