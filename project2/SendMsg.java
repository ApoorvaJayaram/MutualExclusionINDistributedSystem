package project2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class SendMsg {
	
	static int MESSAGE_SIZE =50000;
	
	public static synchronized void sendMessage(Message msg, int nodeNum,HashMap<Integer,HostInfo> map) {
		ByteBuffer bBuffer = ByteBuffer.allocate(MESSAGE_SIZE + 50000);
		HostInfo h = map.get(msg.receiver);
		System.out.println("Connecting to node" + h.nodeNumber + "at port: "
				+ h.port);

		String hname = h.hostName + ".utdallas.edu";
		SocketAddress socketAddress = new InetSocketAddress(hname, h.port);
		try {
			SctpChannel sChannel = SctpChannel.open();
			while (true) {
				try {
					System.out.println(map.get(nodeNum).port);
					sChannel.bind(new InetSocketAddress(map.get(nodeNum).clientPort));
					break;
				} catch (Exception e) {
					try {
						System.out.println("Trying to reconnect...");
						Thread.sleep(100);
					} catch (InterruptedException f) {
						e.printStackTrace();
					}
					// Create a socket address for server at net01 at port 5000
					// sctpChannel.bind(new
					// InetSocketAddress(Integer.parseInt(myc.configData[position][2])));

					e.printStackTrace();
					System.out.println("Binding Exception");
				}
			}

			sChannel.connect(socketAddress);

			// Before sending messages add additional information about the
			// message
			MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
			// convert the string message into bytes and put it in the byte
			// buffer
			bBuffer.clear();

			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
			oos.writeObject(msg);
			oos.flush();
			byte[] msgBytes = bytesOut.toByteArray();
			bytesOut.close();
			oos.close();

			bBuffer.put(msgBytes);
			// Reset a pointer to point to the start of buffer
			bBuffer.flip();
			// Send a message in the channel (byte format)
			sChannel.send(bBuffer, messageInfo);
			bBuffer.clear();
			sChannel.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


}
