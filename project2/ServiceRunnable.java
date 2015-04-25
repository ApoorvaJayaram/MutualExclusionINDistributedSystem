package project2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;


public class ServiceRunnable implements Runnable{
	
	HostInfo hst;
	HashMap<Integer,HostInfo> map;
	String hostName;
	int nodeNum;
	Thread t;
	int noOfNodes;
	String message;
	Message msg;
	boolean myPriority;
	final int MESSAGE_SIZE = 50000;
	ServiceModule ser;
	Semaphore semaphore;
	
	public ServiceRunnable(int node, HashMap<Integer,HostInfo> m, int nodes,Semaphore sem)
	{
		map=m;
		nodeNum=node;
		noOfNodes=nodes;
		semaphore=sem;
		//ser=s;
		//System.out.println("Our host is: " + hostName);
		
	}
	
	public void run()
	{
		HostInfo hh = map.get(nodeNum);
		ser = new ServiceModule(hh.nodeNumber,map,noOfNodes,semaphore);
		
		try
		{
			//Create a server socket at port 5000
			ServerSocket serverSock = new ServerSocket(hh.port);
			//Server goes into a permanent loop accepting connections from clients		
			while(true)
			{
				
				System.out.println(nodeNum + "I am stuck at accept()");
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				Socket sock = serverSock.accept();
				System.out.println(nodeNum + "done accept()");
				OutputStream os = sock.getOutputStream();
			    ObjectOutputStream oos = new ObjectOutputStream(os);
			    InputStream is = sock.getInputStream();
			    ObjectInputStream ois = new ObjectInputStream(is);
			    
			    msg = (Message)ois.readObject();
				
		        if(msg.type == MsgType.REQUEST)
		        {
		        	HostInfo ha = map.get(nodeNum);
		        	
		        	System.out.println(nodeNum + "Accepted request from " + msg.sender + " service sending for processing");
		        	ser.treatRequestMessage(msg);
		        }
		        else
		        {
		        	if(msg.type == MsgType.REPLY)
		        	{
		        		System.out.println(nodeNum + "Received reply from " + msg.sender + "service sending for processng");
		        		ser.treatReplyMessage(msg);
		        	}
		        	else
		        	{
		        		if(msg.type == MsgType.REQUESTREPLY)
		        		{
		        			System.out.println(nodeNum + "Received request-reply from " + msg.sender + "service sending for processng");
		        			ser.treatReplyMessage(msg);
		        			ser.treatRequestMessage(msg);
		        		}
		        	}
		        }
		        
		        ois.close();
		        oos.close();
		        //Thread.sleep(100);
			}

		}
		/*catch(ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}*/
		catch(Exception ex)
		{
			System.out.println("There is an exception at " + nodeNum);
			System.out.println("The message from " + msg.sender + " to " + msg.receiver);
			ex.printStackTrace();
		}
		/*catch(InterruptedException ex)
		{
			ex.printStackTrace();
		}*/
		
	}
	
	public void start ()
	{
	    //  System.out.println("Starting Server " +  threadName );
	      if (t == null)
	      {
	         t = new Thread (this, "Servicehost " + nodeNum);
	         t.start ();
	      }
	}
	
	public byte[] toByteArray(ByteBuffer byteBuffer)
	{
		byteBuffer.position(0);
		byteBuffer.limit(MESSAGE_SIZE);
		byte[] bufArr = new byte[byteBuffer.remaining()];
		byteBuffer.get(bufArr);
		return bufArr;
	}

}
