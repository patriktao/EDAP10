package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram2 extends ActorThread<WashingMessage> {

	private WashingIO io;
	private ActorThread<WashingMessage> temp;
	private ActorThread<WashingMessage> water;
	private ActorThread<WashingMessage> spin;
	private WashingMessage ack;

	public WashingProgram2(WashingIO io, ActorThread<WashingMessage> temp, ActorThread<WashingMessage> water,
			ActorThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
	}

	public void preWash() throws InterruptedException {
		// Let water into machine
		water.send(new WashingMessage(this, WATER_FILL));
		ack = receive();
		System.out.println("washing program 2 got " + ack);
			
		// Rotate Slowly
		spin.send(new WashingMessage(this, SPIN_SLOW));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Heat to 40%
		temp.send(new WashingMessage(this, TEMP_SET_40));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Spin for 20 simulated minutes
		Thread.sleep(20 * 60000 / Settings.SPEEDUP);
	}

	public void changeWash() throws InterruptedException {
		// STOP
		spin.send(new WashingMessage(this, SPIN_OFF));
		ack = receive();
		System.out.println("washing program 2 got " + ack);
		
		// Lower Temp
		temp.send(new WashingMessage(this, TEMP_IDLE));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Drain
		water.send(new WashingMessage(this, WATER_DRAIN));
		ack = receive();
		System.out.println("washing program 2 got " + ack);
	}

	public void mainWash() throws InterruptedException{
		// New water 
		water.send(new WashingMessage(this, WATER_FILL));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Spin Slowly
		spin.send(new WashingMessage(this, SPIN_SLOW));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Heat to 60%
		temp.send(new WashingMessage(this, TEMP_SET_60));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Spin for 30 simulated minutes (one minute == 60000 milliseconds)
		Thread.sleep(30 * 60000 / Settings.SPEEDUP);

		// Stop Spinning
		spin.send(new WashingMessage(this, SPIN_OFF));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Lower Temp
		temp.send(new WashingMessage(this, TEMP_IDLE));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Drain water
		water.send(new WashingMessage(this, WATER_DRAIN));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Rinse 5 times! 2 min each
		for (int i = 0; i < 5; i++) {
			// Let cold water into the machine
			water.send(new WashingMessage(this, WATER_FILL));
			ack = receive();
			System.out.println("washing program 2 got " + ack);

			// Rotate Slowly
			spin.send(new WashingMessage(this, SPIN_SLOW));
			ack = receive();
			System.out.println("washing program 2 got " + ack);

			// Spin for 2 minutes
			Thread.sleep(2 * 60000 / Settings.SPEEDUP);

			// Stop
			spin.send(new WashingMessage(this, SPIN_OFF));
			ack = receive();
			System.out.println("washing program 2 got " + ack);

			// Drain
			water.send(new WashingMessage(this, WATER_DRAIN));
			ack = receive();
			System.out.println("washing program 2 got " + ack);
		}

		/* Part 4: Centrifuge */
		spin.send(new WashingMessage(this, SPIN_FAST));
		ack = receive();
		System.out.println("washing program 2 got " + ack);

		// Spin for 5 minutes
		Thread.sleep(5 * 60000 / Settings.SPEEDUP);

		/* Part 5: Stop Machine */
		spin.send(new WashingMessage(this, SPIN_OFF));
		ack = receive();
		System.out.println("washing program 2 got " + ack);
	}

	@Override
	public void run() {
		try {
			io.lock(true);
			preWash();
			changeWash();
			mainWash();
			io.lock(false);
			System.out.println("washing program 2 finished");
		} catch (InterruptedException e) {

			// If we end up here, it means the program was interrupt()'ed:
			// set all controllers to idle
			try {
				temp.send(new WashingMessage(this, TEMP_IDLE));
				water.send(new WashingMessage(this, WATER_IDLE));
				spin.send(new WashingMessage(this, SPIN_OFF));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("washing program 2 terminated");
		}
	}
}
