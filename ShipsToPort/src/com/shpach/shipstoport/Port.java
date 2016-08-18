package com.shpach.shipstoport;

public class Port {
	private Berth[] berths;
	private int berthCount = 1;

	public Port(int berthCount) {
		super();
		this.berthCount = berthCount;
		berths = new Berth[this.berthCount];
		for (int i = 0; i < berths.length; i++) {
			berths[i] = new Berth(this, Integer.toString(i + 1));

		}
		UnmooringDispetcher ud = new UnmooringDispetcher(berths);
	}

	public void enterToPort(Ship ship, PortCall portCall, boolean autoLoadUnload) {
		mooring(ship, portCall, autoLoadUnload);
	}

	private synchronized void mooring(Ship ship, PortCall portCall, boolean autoLoadUnload) {
		
		if (ship != null) {
			Berth berth;
			for (; (berth = getFirstEmptyBerth()) == null;) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Vessel \"" + ship.getName() + "\" is moored on berth #" + berth.getName());
			berth.setShip(ship, portCall, autoLoadUnload);
		}
		notifyAll();
	}

	private Berth getFirstEmptyBerth() {
		for (Berth berth : berths) {
			if (berth.getShip() == null)
				return berth;
		}
		return null;
	}

	public class UnmooringDispetcher implements Runnable {
		private Berth[] berths;

		public UnmooringDispetcher(Berth[] berths) {
			super();
			this.berths = berths;
			Thread th1 = new Thread(this);
			th1.setDaemon(true);
			th1.start();

		}

		@Override
		public void run() {
			for (;;) {
				for (Berth berth : berths) {
					if (berth.isLoadUnLoadOperationFinish() && berth.getShip() != null) {
						System.out.println("Vessel \"" + berth.getShip().getName() + "\" on berth #" + berth.getName()
								+ " is unmooring");
						berth.setShip(null);
						mooring(null, null, false);
					}
				}
			}
		}
	}

}
