package com.minimajack._v8.format;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.minimajack._v8.utils.BufferUtils;
import com.minimajack._v8.utils.BufferedObject;

public class BlockHeader extends BufferedObject {
	private int docSize;
	private int blockSize;
	private int nextBlock;
	public boolean ready = false;
	
	private static short V8_SEPARATOR = 0x0A0D;
	public static int V8_ENDBLOCK = Integer.MAX_VALUE; 

	public BlockHeader(ByteBuffer buffer) {
		super(buffer);
	}

	public BlockHeader(ByteBuffer buffer, int position) {
		super(buffer, position);
	}

	public boolean hasNext() throws IOException {
		return !this.getNextBlock().equals(Integer.MAX_VALUE);
	}

	@Override
	public void write(ByteBuffer buffer) throws IOException {
		buffer.putShort(V8_SEPARATOR);
		BufferUtils.writeLongToString(buffer, this.docSize);
		BufferUtils.writeLongToString(buffer, this.blockSize);
		BufferUtils.writeLongToString(buffer, this.nextBlock);
		buffer.putShort(V8_SEPARATOR);
	}

	public void read() throws IOException {
		readHeader();
	}

	public void reset() throws IOException{
		this.readHeader();
	}
	public void readHeader() throws IOException {
		this.setPosition();
		ByteBuffer buffer = getBuffer();
		buffer.getShort();
		this.docSize = BufferUtils.getLongFromString(buffer);
		this.blockSize = BufferUtils.getLongFromString(buffer);
		this.nextBlock = BufferUtils.getLongFromString(buffer);
		buffer.getShort();
		this.ready = true;
	}

	public byte[] getRawData() throws IOException {
		byte[] rawdata = new byte[this.getBlockSize()];
		getBuffer().get(rawdata);
		return rawdata;
	}

	public int getDocSize() throws IOException { 
		if(!this.ready){
			this.readHeader();
		}
		return docSize;
	}

	public void setDocSize(int docSize) {
		this.docSize = docSize;
	}

	public int getBlockSize() throws IOException {
		if(!this.ready){
			this.readHeader();
		}
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public Integer getNextBlock() throws IOException {
		if(!this.ready){
			this.readHeader();
		}
		return nextBlock;
	}

	public void setNextBlock(int nextBlock) {
		this.nextBlock = nextBlock;
	}

}
