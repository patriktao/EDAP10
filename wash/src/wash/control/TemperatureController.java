package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

	private int dt = 10;
	private WashingIO io;
	private double m_u = dt * 0.0478 + 0.2;
	private double m_l = dt * 0.00952 + 0.2;
	private Order order = Order.TEMP_IDLE;
	private ActorThread<WashingMessage> sender;
	private boolean heatOn = false;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}

	public void setTemp(int temp) throws InterruptedException {
		if (io.getTemperature() >= (temp - m_u) && heatOn) {
			io.heat(false);
			heatOn = false;
			if(sender != null) {
				sendAck();
				sender = null;
			}
		} else if (io.getTemperature() < (temp - 2 + m_l) && !heatOn && io.getWaterLevel() > 0.1) {
			io.heat(true);
			heatOn = true;
		}
	}

	public void sendAck() throws InterruptedException {
		sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
	}

	@Override
	public void run() {
		try {
			while (true) {
				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage m = receiveWithTimeout(dt * 1000 / Settings.SPEEDUP);

				// if m is null, it means a 10 sec passed and no message was received
				if (m != null) {
					System.out.println("got " + m);
					order = m.getOrder();
					sender = m.getSender();
				}

				switch (order) {
				case TEMP_IDLE:
					heatOn = false;
					io.heat(false);
					if (sender != null) {
						sendAck();
						sender = null;
					}
					break;
				case TEMP_SET_40:
					setTemp(40);
					break;
				case TEMP_SET_60:
					setTemp(60);
					break;
				default:
					throw new Error("TemperatureController Error");
				}
			}
		} catch (InterruptedException unexpected) {
			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);
		}
	}
}
