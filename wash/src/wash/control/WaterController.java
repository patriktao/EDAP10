package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

	private WashingIO io;
	private ActorThread<WashingMessage> sender;
	private Order order;

	public WaterController(WashingIO io) {
		this.io = io;
		order = Order.WATER_IDLE;
	}

	public void sendAck() throws InterruptedException {
		sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
	}

	@Override
	public void run() {
		try {
			while (true) {
				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage m = receiveWithTimeout(1000 / Settings.SPEEDUP);

				if (m != null) {
					System.out.println("got " + m);
					order = m.getOrder();
					sender = m.getSender();
				}

				switch (order) {
				case WATER_IDLE:
					io.drain(false);
					io.fill(false);
					break;
				case WATER_FILL:
					if (io.getWaterLevel() < (WashingIO.MAX_WATER_LEVEL / 2)) {
						io.drain(false);
						io.fill(true);
					} else {
						io.fill(false);
						if(sender != null) {
							sendAck();
							sender = null;
						}
						order = Order.WATER_IDLE;
					}
					break;
				case WATER_DRAIN:
					if(io.getWaterLevel() == 0) {
						if(sender != null) {
							sendAck();
							sender = null;
						}
					}
					if (io.getWaterLevel() > 0) {
						io.fill(false);
						io.drain(true);
					}
					break;
				default:
					throw new Error("WaterController Error");
				}

			}
		} catch (InterruptedException unexpected) {
			throw new Error(unexpected);
		}
	}
}
