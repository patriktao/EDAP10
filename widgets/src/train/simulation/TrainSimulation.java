package train.simulation;

import train.model.Route;
import train.model.Segment;
import train.view.TrainView;
import java.util.*;

public class TrainSimulation {

	static class Monitor {
		private Set<Segment> busySegments;

		public Monitor() {
			busySegments = new HashSet<>();
		}

		public synchronized void remove(Segment s) throws InterruptedException {
			busySegments.remove(s);
			notifyAll();
		}

		public synchronized void add(Segment s) throws InterruptedException {
			 while (busySegments.contains(s)) {
				wait();
			}
			busySegments.add(s);
		}
	}

	public static void createTrain(TrainView view, Monitor mon) {
		LinkedList<Segment> q = new LinkedList<Segment>();
		Route route = view.loadRoute();

		List<Segment> r = new LinkedList<>();

		for (int i = 0; i < 3; i++) {
			r.add(route.next());
		}

		for (Segment s : r) {
			q.add(s);
		}
		
		for (Segment s : r) {
			s.enter();
		}

		try {
			while (true) {
				// Simulerar tåget, lägger till Head och tar bort Tail
				Segment head = route.next();
				mon.add(head);
				head.enter(); // markerar
				q.addFirst(head);
				Segment tail = q.removeLast();
				tail.exit(); // tar bort markering
				mon.remove(tail);
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}

	}

	public static void main(String[] args) {
		Monitor mon = new Monitor();
		TrainView view = new TrainView();

		List<Thread> trains = new LinkedList<Thread>();

		for (int i = 0; i < 20; i++) {
			trains.add(new Thread(() -> createTrain(view, mon)));
		}

		for (Thread t : trains) {
			t.start();
		}

	}

}
