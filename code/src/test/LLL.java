package test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class LLL {

	public static void main(String[] args) throws IOException {

		String path = "/home/seb/P2P/ex";
		File f = new File(path);
		f.delete();
		// créer le fichier
		RandomAccessFile writer = new RandomAccessFile(path, "rw");
		// obtenir un channel
		FileChannel ch = writer.getChannel();
		// obtenir un buffer
		byte[] b = { '1', '2', '3', '5', '6', '7', '8'};
		ByteBuffer buff = ByteBuffer.wrap(b);
		System.out.println(buff.remaining());
		ch.write(buff);
		System.out.println("apres : "+buff.remaining());
		
		// decaler
		ch.position(50);
		ch.write(buff);
	}

}
