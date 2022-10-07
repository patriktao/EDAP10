package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {

	private WashingIO io;
	private ActorThread<WashingMessage> sender;
	private Order order;
	private State spinState;

	public enum State {
		RIGHT, LEFT, FAST, STOP,
	}

	public SpinController(WashingIO io) {
		this.io = io;
		this.spinState = State.STOP;
		this.order = Order.SPIN_OFF;
	}

	@Override
	public void run() {
		try {

			while (true) {
				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

				// if m is null, it means a minute passed and no message was received
				if (m != null) {
					order = m.getOrder();
					sender = m.getSender();
					System.out.println("got " + m);
				}

				switch (order) {
				case SPIN_SLOW:
					switch(spinState) {
					case LEFT:
						spinState = State.RIGHT;
						io.setSpinMode(WashingIO.SPIN_RIGHT);
						break;
					case RIGHT:
						spinState = State.LEFT;
						io.setSpinMode(WashingIO.SPIN_LEFT);
						break;
					case FAST:
						spinState = State.RIGHT;
						io.setSpinMode(WashingIO.SPIN_RIGHT);
						break;
					case STOP:
						spinState = State.RIGHT;
						io.setSpinMode(WashingIO.SPIN_RIGHT);
						break;
					}
					break;
				case SPIN_FAST:
					io.setSpinMode(WashingIO.SPIN_FAST);
					spinState = State.FAST;
					break;
				case SPIN_OFF:
					io.setSpinMode(WashingIO.SPIN_IDLE);
					spinState = State.STOP;
					break;
				default:
					throw new Error("Spincontroller Error");
				}

				if (m != null) {
					sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
				}
			}
		} catch (InterruptedException unexpected) {
			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);
		}
	}
}
