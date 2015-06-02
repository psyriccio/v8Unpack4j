package com.minimajack._v8.utils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BufferUtils {
	
	public static final int getLongFromString(ByteBuffer buffer)
			throws IOException {
		byte[] stringBuffer = new byte[8];
		buffer.get(stringBuffer);
		buffer.get(); // space
		return (int) Long.parseLong(new String(stringBuffer), 16);
	}

	public static final void writeLongToString(ByteBuffer buffer, long value)
			throws IOException {
		String formatted = String.format("%08x ", value);
		buffer.put(formatted.getBytes());
	}
}
