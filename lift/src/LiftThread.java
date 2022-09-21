import lift.LiftView;

public class LiftThread extends Thread {
	private LiftView view;
	private LiftMonitor mon;
	
	public LiftThread(LiftView view, LiftMonitor mon) {
		this.view = view;
		this.mon = mon;
	}
	
	public void run() {
		try {
			while (true) {
				if (mon.isMoving()) {
					for (int i = 0; i < Main.NBR_FLOORS - 1; i++) {
						mon.handleDoors(i);
						if(mon.noMorePassengers()) {
							return;
						}
						view.moveLift(i, i + 1);
					}
					for (int i = Main.NBR_FLOORS - 1; i > 0; i--) {
						mon.handleDoors(i);
						if(mon.noMorePassengers()) {
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
}
