package com.minimajack._v8.parser;

import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.minimajack._v8.format.Container;
import com.minimajack._v8.threadpool.CommonThreadPoolManager;
import com.minimajack._v8.utils.Context;
import com.minimajack._v8.utils.CountHolder;

public class Reader {

	public Container container;

	public final void unpack(String string, String destination){
		long times = System.currentTimeMillis();
		try (RandomAccessFile aFile = new RandomAccessFile(string, "r");
				FileChannel inChannel = aFile.getChannel();) {
			MappedByteBuffer buffer = inChannel.map(
					FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
			buffer.order(ByteOrder.LITTLE_ENDIAN);

			CommonThreadPoolManager tpm = new CommonThreadPoolManager(); 
			tpm.start();
			Context root = new Context();
			root.setName(destination);
			root.setInflated(true);
			root.setTpm(tpm);	
			this.container = new Container(buffer, root);
			root.parseContainer(container);
			do {
				Thread.sleep(1000);
				System.out.println("Active tasks: " + CountHolder.count.get());
			} while(CountHolder.count.get() != 0); 
			tpm.stop();
			System.out.println("Time: " + (int)(System.currentTimeMillis() - times) / 1000 );

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
