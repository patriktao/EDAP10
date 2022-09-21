import java.util.ArrayDeque;

import lift.LiftView;
import lift.Passenger;

public class LiftMonitor {
	private int[] toEnter = new int[Main.NBR_FLOORS];
	private int[] toExit = new int[Main.NBR_FLOORS];
	private int currentFloor;
	private int passengersInLift;
	private boolean closedDoors = true;
	private boolean moving = false;
	private ArrayDeque<Integer> passengersToLift;
	private ArrayDeque<Integer> movement;
	private LiftView view;

	public LiftMonitor(LiftView view) {
		this.passengersInLift = 0;
		this.currentFloor = 0;
		this.view = view;
		this.passengersToLift = new ArrayDeque<Integer>();
		this.movement = new ArrayDeque<Integer>();
	}

	/* Returns the state whether the lift is moving or not */
	public synchronized boolean isMoving() {
		return moving;
	}

	/* Stops the elevator if there are no more passengers left */
	private synchronized void StopIfFinished() {
		if (noMorePassengers()) {
			moving = false;
		}
	}
	/* Checks if there are no more passengers */
	public synchronized boolean noMorePassengers() {
		return passengersToLift.isEmpty() && passengersInLift == 0;
	}


	/* Is there anyone entering or exiting the lift? */
	public synchronized boolean personEntersOrExistsLift() {
		return !movement.isEmpty();
	}

	public synchronized void openDoor(int floor) {
		view.openDoors(floor);
		closedDoors = false;
		notifyAll();
	}
	public synchronized void closeDoor(int floor) throws InterruptedException {
		while ((toEnter[floor] > 0 && passengersInLift < Main.MAX_PASSENGERS) || toExit[floor] > 0
				|| personEntersOrExistsLift()) {
			wait();
		}
		closedDoors = true;
		view.closeDoors();
	}

	public synchronized void handleDoors(int floor) throws InterruptedException {
		currentFloor = floor;
		StopIfFinished();
		if (passengersInLift < Main.MAX_PASSENGERS && (toEnter[floor] > 0 || toExit[floor] > 0)) {
			// Lift is not full and there are passengers waiting
			openDoor(floor);
			closeDoor(floor);
		} else if (toExit[floor] > 0) {
			// Lift is full and can let passengers exit
			openDoor(floor);
			closeDoor(floor);
		}
	}

	public synchronized void enter(int fromFloor) throws InterruptedException {
		toEnter[fromFloor]++;
		passengersToLift.add(0);
		moving = true;
		notifyAll(); // Notify that a passenger has arrived
		while (this.currentFloor != fromFloor || passengersInLift == Main.MAX_PASSENGERS || closedDoors) {
			wait();
		}
		movement.add(0);
		toEnter[fromFloor]--;
		passengersInLift++;
	}

	public synchronized void exit(int toFloor) throws InterruptedException {
		toExit[toFloor]++;
		while (this.currentFloor != toFloor || closedDoors) {
			wait();
		}
		movement.add(0);
		toExit[toFloor]--;
		passengersInLift--;
	}
	
	public synchronized void notify_entered() {
		movement.pop();
		if (!personEntersOrExistsLift()) {
			notifyAll();
		}
	}

	public synchronized void notify_exited() {
		passengersToLift.pop();
		movement.pop();
		if (!personEntersOrExistsLift()) {
			notifyAll();
		}
	}

}
