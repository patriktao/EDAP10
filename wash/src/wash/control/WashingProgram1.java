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
public class WashingProgram1 extends ActorThread<WashingMessage> {

	private WashingIO io;
	private ActorThread<WashingMessage> temp;
	private ActorThread<WashingMessage> water;
	private ActorThread<WashingMessage> spin;
	private WashingMessage ack;

	public WashingProgram1(WashingIO io, ActorThread<WashingMessage> temp, ActorThread<WashingMessage> water,
			ActorThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
	}

	public void warmWash() throws InterruptedException {
		// Let water into the machine
		System.out.println("setting WATER_FILL...");
		water.send(new WashingMessage(this, WATER_FILL));
		ack = receive();
		System.out.println("washing program 1 got " + ack);

		// Instruct SpinController to rotate barrel slowly, back and forth
		System.out.println("setting SPIN_SLOW...");
		spin.send(new WashingMessage(this, SPIN_SLOW));
		ack = receive();
		System.out.println("washing program 1 got " + ack);

		// Heat to 40%
		System.out.println("setting TEMP_SET_40...");
		temp.send(new WashingMessage(this, TEMP_SET_40));
		ack = receive();
		System.out.println("washing program 1 got " + ack);

		// Spin for 30 simulated minutes (one minute == 60000 milliseconds)
		Thread.sleep(30 * 60000 / Settings.SPEEDUP);

		// STOP
		System.out.println("setting SPIN_OFF...");
		spin.send(new WashingMessage(this, SPIN_OFF));
		ack = receive();
		System.out.println("washing program 1 got " + ack);

		// Lower Temp
		System.out.println("setting TEMP_IDLE...");
		temp.send(new WashingMessage(this, TEMP_IDLE));
		ack = receive();
		System.out.println("washing program 1 got " + ack);

		// Drain
		System.out.println("setting WATER_DRAIN...");
		water.send(new WashingMessage(this, WATER_DRAIN));
		ack = receive();
		System.out.println("washing program 1 got " + ack);
	}

	public void rinse() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			// Let cold water into the machine
			System.out.println("setting WATER_FILL...");
			water.send(new WashingMessage(this, WATER_FILL));
			ack = receive();
			System.out.println("washing program 1 got " + ack);

			// Instruct SpinController to rotate barrel slowly, back and forth
			System.out.println("setting SPIN_SLOW...");
			spin.send(new WashingMessage(this, SPIN_SLOW));
			ack = receive();
			System.out.println("washing program 1 got " + ack);

			// Spin for 2 minutes
			Thread.sleep(2 * 60000 / Settings.SPEEDUP);

			// STOP
			System.out.println("setting SPIN_OFF...");
			spin.send(new WashingMessage(this, SPIN_OFF));
			ack = receive();
			System.out.println("washing program 1 got " + ack);

			// Drain
			System.out.println("setting WATER_DRAIN...");
			water.send(new WashingMessage(this, WATER_DRAIN));
			ack = receive();
			System.out.println("washing program 1 got " + ack);
		}
	}
	
	public void centrifuge() throws InterruptedException{
		// Centrifuge
		System.out.println("setting SPIN_FAST...");
		spin.send(new WashingMessage(this, SPIN_FAST));
		ack = receive();
		System.out.println("washing program 1 got " + ack);

		// Spin for 5 minutes
		Thread.sleep(5 * 60000 / Settings.SPEEDUP);

		// Stop Macihne
		System.out.println("setting SPIN_OFF...");
		spin.send(new WashingMessage(this, SPIN_OFF));
		ack = receive();
		System.out.println("washing program 1 got " + ack);
	}

	@Override
	public void run() {
		try {
			io.lock(true);
			warmWash();
			rinse();
			centrifuge();
			io.lock(false);
			System.out.println("washing program 1 finished");
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
