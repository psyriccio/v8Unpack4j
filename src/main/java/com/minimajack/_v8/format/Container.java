package com.minimajack._v8.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.io.IOUtils;

import com.minimajack._v8.utils.BufferedObject;
import com.minimajack._v8.utils.Context;

public class Container extends BufferedObject{

	private int freeBlock;
	private int sizeBlock;
	private int version;
	private int reserved;
	
	private boolean ready = false;
	private byte[] data;
	private boolean isDataPacked = false;
	
	public V8FileSystem fileSystem;
	
	public Container(ByteBuffer buffer, Context context) {
		super(buffer);
		ready = true;
		this.setContext(context);
	}

	public Container(byte[] data, Boolean isPacked, Context context) {
		this.setContext(context);
		this.data = data;
		this.isDataPacked = isPacked;
		if(!isDataPacked){
			System.out.println("Data not packed");
		}
	}
	
	@Override
	public void write(ByteBuffer buffer) throws IOException {
		buffer.putInt(this.freeBlock);
		buffer.putInt(this.sizeBlock);
		buffer.putInt(this.version);
		buffer.putInt(this.reserved);
		fileSystem.write(buffer);
	}

	public void read() throws IOException {
		if(!ready){
			init();
		}
		ByteBuffer buffer = getBuffer();
		this.freeBlock = buffer.getInt();
		this.sizeBlock = buffer.getInt();
		this.version = buffer.getInt();
		this.reserved = buffer.getInt();
		fileSystem = new V8FileSystem(buffer);
		fileSystem.setContext(getContext());
		fileSystem.read();
		buffer.clear();
		this.data = null;
	}

	private void init() throws IOException {
		if (isDataPacked) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream dataStream = new InflaterInputStream(new ByteArrayInputStream(data), new Inflater(true));
			IOUtils.copy(dataStream, baos);
			data = baos.toByteArray();
		}
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		setBuffer(bb);
		
	}

	public long getFreeBlock() {
		return freeBlock;
	}

	public void setFreeBlock(int freeBlock) {
		this.freeBlock = freeBlock;
	}

	public int getSizeBlock() {
		return sizeBlock;
	}

	public void setSizeBlock(int sizeBlock) {
		this.sizeBlock = sizeBlock;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

}
