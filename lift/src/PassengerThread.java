import lift.LiftView;
import lift.Passenger;

public class PassengerThread extends Thread{
	private LiftView view;
	private LiftMonitor mon;
	
	public PassengerThread(LiftView view, LiftMonitor mon) {
		this.view=view;
		this.mon=mon;
	}
	
	public void run() {
		try {
			//CREATE PASSENGER
			Passenger pass = view.createPassenger();
			//ENTER
			pass.begin();
			mon.enter(pass.getStartFloor());
			pass.enterLift();
			mon.notify_entered();
			//EXIT
			mon.exit(pass.getDestinationFloor()); 
			pass.exitLift();
			mon.notify_exited();
			pass.end();
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}
}
