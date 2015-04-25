package project2;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class ApplicationRunnable implements Runnable{
	
	HostInfo hst;
	HashMap<Integer,HostInfo> map;
	String hostName;
	int nodeNum;
	Thread t;
	int noOfNodes;
	String message;
	Message msg;
	boolean myPriority;
	ConfigReader cReader;
	//ServiceRunnable service;
	ServiceModule service;
	final int MESSAGE_SIZE = 50000;
	Semaphore semaphore;
	
	public ApplicationRunnable(int node, HashMap<Integer,HostInfo> m, int nodes,ConfigReader rd,ServiceModule ser,Semaphore sem)
	{
		map=m;
		nodeNum=node;
		noOfNodes=nodes;
		cReader = rd;
		service=ser;
		semaphore=sem;
		//System.out.println("Our host is: " + hostName);
	}
	

	public void run()
	{
		System.out.println("Application Module for " + nodeNum); 
		try
		{
			File file = new File("MLFile.txt");
			ExpoGen gen = new ExpoGen();
			int n = cReader.noOfRequests;
			for(int k=0;k<cReader.noOfRequests;k++)
			{
				System.out.println("Host " + nodeNum+ " makng req for critical Section");
				service.cs_enter();
				System.out.println("Host " + nodeNum+ " is entering into the critical Section request number is " + k);
				long time = gen.generateExpo(cReader.meanDurationCS);
				
				//Thread.sleep(time);
				
				RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
				FileChannel channel = randomAccessFile.getChannel();
				//FileLock lock = channel.lock();
				FileLock lock = channel.tryLock();
				if(lock!=null)
				{
					 System.out.println(nodeNum + " Got a lock...");
					 System.out.println("cs exec time= "+time + "for node " + nodeNum);
					 System.out.println(nodeNum + " Entering into cs at " + System.currentTimeMillis());
					 long fileLength2 = file.length();
					 randomAccessFile.seek(fileLength2);
					 ByteBuffer bytes = ByteBuffer.allocate(MESSAGE_SIZE);
					 String msg1 = nodeNum + " entering into CS " + System.currentTimeMillis() + "\n";
					 bytes.put(msg1.getBytes());
					 bytes.flip();
					 channel.write(bytes);
					 channel.force(false);
					 Thread.sleep(time);
					 long fileLength = file.length();
					 randomAccessFile.seek(fileLength);
					 bytes.clear();
					 //bytes.put(new byte[MESSAGE_SIZE+50000]);
					 String msg2 = nodeNum + " leaving CS " + System.currentTimeMillis() + "\n";
					 bytes.put(msg2.getBytes());
					 bytes.flip();
					 channel.write(bytes);
					 channel.force(false);
					 long fileLength1 = file.length();
					 randomAccessFile.seek(fileLength1);
					 bytes.clear();
					 System.out.println(nodeNum + " leaving cs at " + System.currentTimeMillis());
					 System.out.println("Host " + nodeNum+ " is releasing the critical Section\n");
				}
				else
				{
					throw (new OverlappingFileLockException());
				}

				lock.release();
				channel.close();
				service.cs_leave();
				time = gen.generateExpo(cReader.meanDelayRequests);
				System.out.println("host " + nodeNum + " out of the CS");
				if(k==(n-1))
				{
					System.out.println("*******Done With processing all CS requests*******");
					break;
				}
				Thread.sleep(time);


			}
			//System.out.println("All CS requests are satisfied for node: " + nodeNum);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
		catch(IOException  ex)
		{
			System.out.println("Error in getting lock" + nodeNum);
			ex.printStackTrace();
		}
	}
	
	
	
	public void start ()
	{
	    //  System.out.println("Starting Server " +  threadName );
	      if (t == null)
	      {
	         t = new Thread (this, "Applicationhost " + nodeNum);
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
