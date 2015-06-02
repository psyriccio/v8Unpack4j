package com.minimajack._v8.format;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.io.IOUtils;

import com.minimajack._v8.stream.V8InputStream;
import com.minimajack._v8.utils.Context;

public class V8FileBody extends BlockHeader {
	public byte[] data;
	boolean needSave = true;
	InputStream dataStream;

	private String fileId;

	public V8FileBody(ByteBuffer buffer) {
		super(buffer);
	}

	public V8FileBody(ByteBuffer buffer, int position) {
		super(buffer, position);
	}

	public V8FileBody(ByteBuffer buffer, int position, V8FileAttribute irh) {
		this(buffer, position);
		this.fileId = irh.id.trim();
	}

	@Override
	public void write(ByteBuffer buffer) throws IOException {
		super.write(buffer);
		buffer.put(data);
	}

	@Override
	public void read() throws IOException {
		int dSize = this.getDocSize();
		if (dSize == 0) {
			return;
		}
		V8InputStream v8stream = new V8InputStream(this);
		//v8stream.readExact = true;
		//dataStream = v8stream;
		if (getContext().isInflated()) {
			dataStream = new InflaterInputStream(v8stream, new Inflater(true));
		}else{
			v8stream.readExact = true;
			dataStream = v8stream;
		}
		
		if(dSize > 34){
			/*byte[] header = new byte[4];
			dataStream.read(header);
			
			ByteBuffer bb = ByteBuffer.wrap(header);
			Long d = bb.getInt(0) & 0xffffffffL;
			bb.order(ByteOrder.LITTLE_ENDIAN);
			boolean isContainer = d.equals(4294967167L);*/
			boolean isContainer = dataStream.read() == 0xFF 
					& dataStream.read() == 0xFF
					& dataStream.read() == 0xFF
					& dataStream.read() == 0x7F;
			
			v8stream.reset();
			

			if (isContainer) {
				needSave = false;
				Context childContext = getContext().createChildContext(fileId);
				byte[] data = new byte[dSize];
				IOUtils.readFully(v8stream, data);
				dataStream = null;
				Container childContainer = new Container(data, getContext().isInflated(), childContext);
				childContext.parseContainer(childContainer);
			}else{
				if (getContext().isInflated()) {
					dataStream = new InflaterInputStream(v8stream, new Inflater(true));
				}else{
					dataStream = v8stream;
				}			
			}

		}
	}

	public void save(File file) throws IOException {
		if(dataStream == null || !needSave){
			return;
		}
		try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(file)) ) {
			IOUtils.copyLarge(dataStream, fos);
			dataStream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clean(){
		this.data = null;
	}

}
