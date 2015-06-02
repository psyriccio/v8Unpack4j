package com.minimajack._v8.utils;

import java.io.IOException;

import com.minimajack._v8.format.Container;
import com.minimajack._v8.threadpool.ThreadPoolManager;

public class Context {
	private Context parent;
	private String name;
	private ThreadPoolManager tpm;
	private boolean inflated = false;
	
	private class ContainerReader implements Runnable{

		private Container container;
		
		public ContainerReader(Container container) {
			this.container = container;
		}

		@Override
		public void run() {
			try {
				container.read(); 
				container.fileSystem.saveToFile();
				CountHolder.count.decrementAndGet();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	public Context getParent() {
		return parent;
	}

	public Context createChildContext(String name) {
		Context child = new Context();
		child.setParent(this);
		child.setName(name);
		child.setTpm(getTpm());
		return child;
	}

	public void parseContainer(final Container container) throws IOException {
		CountHolder.count.incrementAndGet();
		this.tpm.executeInstant(new ContainerReader(container));
	}

	public ThreadPoolManager getTpm() {
		return tpm;
	}

	public void setTpm(ThreadPoolManager tpm) {
		this.tpm = tpm;
	}

	public void setParent(Context parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getPath() {
		if (this.parent != null) {
			return this.parent.getPath() + "/" + name;
		} else {
			return name;
		}
	}

	public boolean isInflated() {
		return inflated;
	}

	public void setInflated(boolean inflated) {
		this.inflated = inflated;
	}
}
