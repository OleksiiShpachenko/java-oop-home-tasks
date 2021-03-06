package com.shpach.shipstoport;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.shpach.shipstoport.PortCall;
import com.shpach.shipstoport.Ship.emptyShipException;
import com.shpach.shipstoport.Ship.overLoadException;

public class Berth {
	private Ship ship;
	private Port port;
	private boolean isLoadUnLoadOperationFinish = false;
	private String name;

	public Berth(Port port, String name) {
		super();
		this.port = port;
		this.name = name;
	}

	public Berth() {
		super();
	}

	public String getName() {
		return name;
	}

	public Ship getShip() {
		return ship;
	}

	public boolean isLoadUnLoadOperationFinish() {
		return isLoadUnLoadOperationFinish;
	}

	public synchronized void setShip(Ship ship, PortCall portCall, boolean autoLoadUnload) {
		this.ship = ship;
		isLoadUnLoadOperationFinish = false;
		if (autoLoadUnload) {
			System.out
					.println("Vessel \"" + ship.getName() + "\" on berth #" + this.name + " starts operation load/unload");
			Thread th1 = new Thread(new Runnable() {

				@Override
				public void run() {
					beginUnload(portCall.isLoad(), portCall.getContainerValue());

				}
			});
			th1.start();

		}
	}

	public void setShip(Ship ship) {
		setShip(ship, null, false);

	}

	public int beginUnload(boolean load, int containerCount) {
		int result = 0;
		FutureTask<Integer> res = new FutureTask<>(new loadUnLoad(load, containerCount));
		Thread thread = new Thread(res);
		thread.start();
		try {
			result = res.get();
			isLoadUnLoadOperationFinish = true;
			System.out
					.println("Vessel \"" + ship.getName() + "\" on berth #" + this.name + " finish operation load/unload");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public class loadUnLoad implements Callable<Integer> {
		private boolean load;
		private int containerCount;

		public loadUnLoad(boolean load, int containerCount) {
			super();
			this.load = load;
			this.containerCount = containerCount;
		}

		@Override
		public Integer call() throws Exception {
			int count = 0;
			for (; count < containerCount;) {
				try {
					ship.loadUnLoadContainer(load);
					count++;
					Thread.sleep(500);
				} catch (overLoadException | emptyShipException e) {
					break;
				}
			}
			return count;
		}

	}

}
