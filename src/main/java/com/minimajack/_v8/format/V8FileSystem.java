package com.minimajack._v8.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.minimajack._v8.stream.V8InputStream;

public class V8FileSystem extends BlockHeader {

	public List<V8File> v8FileList = new LinkedList<V8File>();

	public V8FileSystem(ByteBuffer buffer) {
		this(buffer, buffer.position());
	}
	
	public V8FileSystem(ByteBuffer buffer, int position) {
		super(buffer, position);
	}
	
	public void read() throws IOException {
		byte[] data = new byte[this.getDocSize()];
		IOUtils.readFully(new V8InputStream(this), data);
		ByteBuffer tempBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

		int numberPointers = data.length / V8File.FILE_DESCRIPTION_SIZE;
		
		for (int i = 0; i < numberPointers; i++) {
			V8File v8File = new V8File(tempBuffer);
			v8File.setContext(getContext());
			v8File.read();
			v8File.readHeader(getBuffer());
			this.v8FileList.add(v8File);
		}
	}

	public void saveToFile() throws IOException{
		for (V8File v8File : v8FileList) {
			v8File.readBody(getBuffer());
			v8File.saveToFile();
		}		
	}
	
	@Override
	public void write(ByteBuffer buffer) throws IOException {
		super.write(buffer);
		for (V8File v8File : v8FileList) {
			v8File.write(buffer);
		}
		int delta = this.getBlockSize() - this.getDocSize();
		byte[] zeroFillBuffer = new byte[delta];
		buffer.put(zeroFillBuffer);
	}

}
