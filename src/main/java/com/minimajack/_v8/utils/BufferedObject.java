package com.minimajack._v8.utils;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class BufferedObject {
	private ByteBuffer buffer;
	private int position;
	private Context context;

	public BufferedObject(){
		
	}
	
	public BufferedObject(ByteBuffer buffer) {
		this(buffer, buffer.position());
	}

	public BufferedObject(ByteBuffer buffer, int position) {
		this.buffer = buffer;
		this.position = position;
	}
	
	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	
	public void setPosition() throws IOException {
		getBuffer().position(getPosition());
	}

	public abstract void write(ByteBuffer buffer) throws IOException;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
