package com.minimajack._v8.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.minimajack._v8.stream.V8InputStream;

public class V8FileAttribute extends BlockHeader {

	public static final int IRH_SIZE = 20; // 2x8 + 1x4
	public long creationDate;
	public long modifyDate;
	public int reserved;

	public String id;

	public V8FileAttribute(ByteBuffer buffer, int position) {
		super(buffer, position);
	}

	@Override
	public void read() throws IOException {
		byte[] data = new byte[this.getDocSize()];
		InputStream sream = new V8InputStream(this);
		IOUtils.readFully(sream, data);
		
		ByteBuffer tempBuffer = ByteBuffer.wrap(data).order(
				ByteOrder.LITTLE_ENDIAN);
		this.creationDate = tempBuffer.getLong();
		this.modifyDate = tempBuffer.getLong();
		this.reserved = tempBuffer.getInt();
		byte[] stringArray = new byte[this.getDocSize() - IRH_SIZE];
		tempBuffer.get(stringArray);
		V8FileAttribute.this.id = new String(stringArray,
				Charset.forName("UnicodeLittle"));
	}

	@Override
	public void write(ByteBuffer buffer) throws IOException {
		super.write(buffer);
		buffer.putLong(creationDate);
		buffer.putLong(modifyDate);
		buffer.putInt(reserved);
		buffer.put(id.getBytes("UnicodeLittle"));
	}

}
