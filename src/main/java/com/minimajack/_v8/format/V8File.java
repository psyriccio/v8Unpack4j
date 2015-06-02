package com.minimajack._v8.format;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.minimajack._v8.utils.BufferedObject;

public class V8File extends BufferedObject {

	public static final int FILE_DESCRIPTION_SIZE = 12;
	public Integer headerAddress;
	public Integer bodyAddress;
	public Integer reserved;

	public V8FileAttribute attribute;
	public V8FileBody body;

	public V8File(ByteBuffer buffer) {
		super(buffer);
	}

	public void read() throws IOException {
		setPosition();
		ByteBuffer buffer = getBuffer();
		this.headerAddress = buffer.getInt() & 0xFFFFFFFF;
		this.bodyAddress = buffer.getInt() & 0xFFFFFFFF;
		this.reserved = buffer.getInt() & 0xFFFFFFFF;
		if(!this.reserved.equals(Integer.MAX_VALUE)){
			throw new RuntimeException("Bad magec number");
		}
	}

	public void readHeader(ByteBuffer dataBufer) throws IOException{
		if (this.headerAddress != BlockHeader.V8_ENDBLOCK ) {
			this.attribute = new V8FileAttribute(dataBufer, this.headerAddress);
			this.attribute.setContext(getContext());
			this.attribute.read();
		}
	}
	
	public void readBody(ByteBuffer dataBufer) throws IOException{
		if( this.headerAddress != BlockHeader.V8_ENDBLOCK && this.bodyAddress != BlockHeader.V8_ENDBLOCK) {
			this.body = new V8FileBody(dataBufer, this.bodyAddress, this.attribute);
			this.body.setContext(getContext());
			this.body.read();
		}
	}
	
	@SuppressWarnings("restriction")
	public void saveToFile() throws IOException{
		if (this.attribute != null && this.body != null && this.body.needSave) {
			String name = this.attribute.id;
			File file = new File(getContext().getPath());
			file.mkdirs();
			file = new File(getContext().getPath() + "/" + name.trim() + ".txt");
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (Exception e) {
					String newname = new String(new sun.misc.BASE64Encoder().encode(name.trim().getBytes()));
					newname = newname.substring(0, newname.length()-1);
					System.out.println("Rename " + name + " to " + newname);
					file = new File(getContext().getPath() + "/" + newname
							+ ".txt");
					file.createNewFile();
				}
			}
			this.body.save(file);
			this.body.clean();
		}	
	}
	@Override
	public void write(ByteBuffer buffer) throws IOException {
		buffer.putInt(this.headerAddress);
		buffer.putInt(this.bodyAddress);
		buffer.putInt(this.reserved);
	}

}
