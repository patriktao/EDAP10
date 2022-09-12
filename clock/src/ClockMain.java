import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;
import java.lang.Thread;
import java.util.concurrent.Semaphore;
import java.lang.Exception;

public class ClockMain {

	static class Monitor {
		private Semaphore mutex = new Semaphore(1);
		private ClockOutput out;
		private boolean alarmState;
		private int alarm_h, alarm_m, alarm_s, hours, min, sec;

		public Monitor(ClockOutput out) {
			this.out = out;
			out.displayTime(hours, min, sec); // arbitrary time: just an example
			hours = 23;
			min = 59;
			sec = 58;
			alarm_h = Integer.MAX_VALUE;
			alarm_m = Integer.MAX_VALUE;
			alarm_s = Integer.MAX_VALUE;
		}

		public void clockIncrement() throws InterruptedException {
			mutex.acquire();
			if (sec == 59) {
				sec = -1;
				min++;
			}
			if (min == 60) {
				min = 0;
				hours++;
			}
			if (hours == 24) {
				hours = 0;
			}
			sec++;
			out.displayTime(hours, min, sec);
			mutex.release();
		}

		public void setAlarm(int hours, int min, int sec) throws InterruptedException {
			mutex.acquire();
			this.alarm_h = hours;
			this.alarm_m = min;
			this.alarm_s = sec;
			out.setAlarmIndicator(true);
			alarmState = true;
			mutex.release();
		}

		public boolean getAlarmState() {
			return alarmState;
		}

		public void OnOffAlarm() throws InterruptedException {
			mutex.acquire();
			if (alarmState == true) {
				out.setAlarmIndicator(false);
			} else {
				out.setAlarmIndicator(true);
			}
			alarmState = !alarmState;
			mutex.release();
		}

		public void setTime(int hours, int min, int sec) throws InterruptedException {
			mutex.acquire();
			this.hours = hours;
			this.min = min;
			this.sec = sec;
			mutex.release();
		}

		public boolean checkAlarm() throws InterruptedException {
			mutex.acquire();
			boolean alert = (alarm_h == hours && alarm_m == min && alarm_s == sec);
			mutex.release();
			return alert;
		}
	};

	public static void alarmFunc(Monitor mon, ClockOutput out) {
		try {
			while (true) {
				if (mon.checkAlarm()) {
					long t0 = System.currentTimeMillis();
					for (int i = 1; i <= 20; i++) {
						if (mon.getAlarmState() == true) {
							long now = System.currentTimeMillis();
							long t = t0 + i * 1000; // Vi beräknar när den ska sluta
							out.alarm();
							Thread.sleep(t - now);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	};

	public static void inputFunc(Monitor mon, ClockInput in, Semaphore sem) {
		try {
			while (true) {
				sem.acquire();

				// Get input
				UserInput userInput = in.getUserInput();
				int choice = userInput.getChoice();
				int h = userInput.getHours();
				int m = userInput.getMinutes();
				int s = userInput.getSeconds();
				System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);

				// Setting alarm
				switch (choice) {
				case 1: // setting time
					mon.setTime(h, m, s);
					break;
				case 2: // set new alarm
					mon.setAlarm(h, m, s);
					break;
				case 3: // Reset
					mon.OnOffAlarm();
					break;
				default:

				}
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}

	public static void clockFunc(Monitor mon) {
		long t0 = System.currentTimeMillis();
		int i = 1;
		try {
			while (true) {
				long now = System.currentTimeMillis();
				long t = t0 + i * 1000; // Vi beräknar när den ska sluta
				Thread.sleep(t - now); // Tiden vi vill att den ska sluta - tiden nu
				mon.clockIncrement();
				i++;
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();
		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();
		Semaphore sem = in.getSemaphore();
		Monitor mon = new Monitor(out);
		Thread alarmThread = new Thread(() -> alarmFunc(mon, out));
		Thread clockThread = new Thread(() -> clockFunc(mon));
		Thread inputThread = new Thread(() -> inputFunc(mon, in, sem));
		clockThread.start();
		inputThread.start();
		alarmThread.start();
	}
}
