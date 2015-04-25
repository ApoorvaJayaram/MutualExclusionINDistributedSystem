package project2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class ServiceModule
{
	HashMap<Integer,HostInfo> map1;
	int nodeNo;
	int totalNodes;
	boolean myPriority;
	final int MESSAGE_SIZE = 50000;
	Semaphore semaphore;
	
	public ServiceModule(int n, HashMap<Integer,HostInfo> m, int nodes,Semaphore sem)
	{
		nodeNo = n;
		map1 = m;
		totalNodes = nodes;
		semaphore=sem;
	}
	
	public void cs_enter()
	{
		HostInfo hst = map1.get(nodeNo);
		//hst.waiting = true;
	    //if(hst.hgstSeqNo>=hst.ourSeqNo)
			hst.ourSeqNo = hst.hgstSeqNo + 1;
		//else
			//hst.ourSeqNo = hst.ourSeqNo + 1;
		hst.rTime = hst.ourSeqNo;
		hst.waiting = true;
		try
		{
			this.semaphore.acquire();
			for(int j=0;j<totalNodes;j++)
			{
				if(j!=nodeNo && !hst.A[j] && !hst.requestHost.contains(j))
				{
					System.out.println("The node" + nodeNo +" is Sendng request to" + j);
					Message reqMsg = new Message(hst.ourSeqNo,nodeNo,j,MsgType.REQUEST);
					
					/*if(!hst.requestHost.contains(j))
					{
						hst.requestHost.add(j);
					}*/
						
						sendRequest(reqMsg);
						updateRequestQueue(1,j);
						
					//hst.waiting = true;
				}
			}
			this.semaphore.release();
		}
		catch(Exception e) {e.printStackTrace();}
		boolean flag;
		System.out.println(nodeNo + " before wait ");
		print();
		System.out.println(nodeNo + "Entering into wait");
		while(true)
		{
			if(updateRequestQueue(4,0))
			{
				System.out.println(nodeNo + " Empty breaking.. ");
				break;
			}
		/*	else
			{
				//System.out.println(nodeNum + " Wait sleep.. ");
				try {Thread.sleep(60); }
				catch(InterruptedException e) {e.printStackTrace();}
			}*/
		}
		hst.waiting=false;
		hst.using=true;
	}
	
	public void cs_leave()
	{
		HostInfo hst = map1.get(nodeNo);
		hst.using = false;
		try
		{
			this.semaphore.acquire();
			for(int i=0;i<hst.replyDeferred.length;i++)
			{
				if(hst.replyDeferred[i] && hst.deferHost.contains(i) && i!=nodeNo)
				{
					Message rMsg = new Message(hst.ourSeqNo,hst.nodeNumber,i,MsgType.REPLY);
					System.out.println(nodeNo + " sending reply to " + rMsg.receiver);
					sendReply(rMsg);
					hst.A[i] = false;
					hst.replyDeferred[i] = false;
					hst.deferHost.removeElement(i);
				}
			}
			this.semaphore.release();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public synchronized void updateDeferQueue(int option,int node)
	{
		HostInfo hh = map1.get(nodeNo);
		switch(option)
		{
		case 1:
			if(!hh.deferHost.contains(node))
				hh.deferHost.add(node);
			break;
			
		case 2:
			int host;
			try
			{
				this.semaphore.acquire();
				for(int i=0;i<hh.replyDeferred.length;i++)
				{
					if(hh.replyDeferred[i] && hh.deferHost.contains(i) && i!=nodeNo)
					{
						Message rMsg = new Message(hh.ourSeqNo,hh.nodeNumber,i,MsgType.REPLY);
						System.out.println(nodeNo + " sending reply to " + rMsg.receiver);
						sendReply(rMsg);
						hh.A[i] = false;
						hh.replyDeferred[i] = false;
						hh.deferHost.removeElement(i);
					}
				}
				this.semaphore.release();
			}
			catch(Exception e) {e.printStackTrace();}
			break;
			
		case 3:
			System.out.println(nodeNo + " has defered reply to " + hh.deferHost);
			break;
			
		}
	}
	
	public synchronized boolean updateRequestQueue(int option, int node)
	{
		HostInfo hd  = map1.get(nodeNo);
		synchronized(hd.requestHost)
		{
		switch(option)
		{
			case 1:
				if(!hd.requestHost.contains(node))
					hd.requestHost.add(node);
				break;
				
			case 2:
				if(hd.requestHost.contains(node))
					hd.requestHost.removeElement(node);
				break;
				
			case 3:
				System.out.println(nodeNo + " has sent request to " + hd.requestHost);
				break;
				
			case 4:
				if(hd.requestHost.isEmpty())
					return true;
				
				
		}
		}
		return false;
	}
	
	public void print()
	{
		HostInfo hh = map1.get(nodeNo);
		//System.out.println(nodeNum + " has sent request to " + hh.requestHost);
		updateRequestQueue(3, 0);
		System.out.println(nodeNo + " our seq num " + hh.ourSeqNo + " hst seq num" + hh.hgstSeqNo);
		updateDeferQueue(3, 0);
		System.out.println(nodeNo + " status of A ");
		for(int i=0;i<hh.A.length;i++)
		{
			System.out.print(hh.A[i] + " ");
		}
	}
	
	
	public void treatReplyMessage(Message m)
	{
		System.out.println("Inside treat reply ");
		HostInfo hh = map1.get(nodeNo);
		//hh.hgstSeqNo = max(m.timestamp, hh.hgstSeqNo);
		hh.A[m.sender]=true;
		System.out.println("Calling update req fun");
		try
		{
			this.semaphore.acquire();
			updateRequestQueue(2,m.sender);
			this.semaphore.release();
		}
		catch(Exception e) {e.printStackTrace();}
		System.out.println(" Updated request queue with " + m.sender);
		/*if(hh.requestHost.contains(m.sender))
			hh.requestHost.remove(m.sender);*/
	}
	
	public void treatRequestMessage(Message m)
	{
		System.out.println("The thread is " + Thread.currentThread().getName());
		HostInfo hst = map1.get(nodeNo);
		if(hst.requestHost.contains(m.sender))
		{
			
		}
		hst.hgstSeqNo = max(m.timestamp, hst.hgstSeqNo);
		if((m.timestamp>hst.ourSeqNo) || ((m.timestamp==hst.ourSeqNo) && (m.sender>hst.nodeNumber)))
		{
			myPriority=true;
		}
		else
			myPriority=false;
		
		if(hst.using || (hst.waiting && myPriority))
		{
			hst.replyDeferred[m.sender] = true;
			/*if(!hst.defertHost.contains(m.sender))
			{
				hst.defertHost.add(m.sender);
			}*/
			updateDeferQueue(1, m.sender);
			System.out.println("Reply deferred for " + m.sender + " by " + nodeNo);
		}
		
		if(!(hst.using || hst.waiting) || (hst.waiting && (!hst.A[m.sender]) && !myPriority))
		{
			hst.A[m.sender] = false;
			//hst.ourSeqNo = hst.hgstSeqNo + 1;
			Message rMsg = new Message(hst.ourSeqNo,hst.nodeNumber,m.sender,MsgType.REPLY);
			System.out.println("Host " + hst.nodeNumber + " sending reply to " + rMsg.receiver);
			sendReply(rMsg);
			System.out.println("Sent reply case 2 " + rMsg.sender + "-->" + rMsg.receiver);
		}
		if(hst.waiting && hst.A[m.sender] && !myPriority)
		{
			hst.A[m.sender] = false;
			System.out.println("request reply" + nodeNo + " sending to " + m.sender);
			Message reqMsg = new Message(hst.rTime,nodeNo,m.sender,MsgType.REQUESTREPLY);
			try
			{
				this.semaphore.acquire();
				sendRequest(reqMsg);
				if(!hst.requestHost.contains(m.sender))
				{
					//hst.requestHost.add(m.sender);
					updateRequestQueue(1, m.sender);
					System.out.println("Sent request reply case 3 " + reqMsg.sender + "-->" + reqMsg.receiver);
				}
				this.semaphore.release();
			
			}
			catch(Exception e) {e.printStackTrace();}
		}
		
	}  
	
	int max(int x, int y)
	{
	  return (x > y) ? x : y;
	}
	
	public void sendReply(Message rMsg)
	{
		int c = (int) (Math.random()*101);
		sendMessage1(rMsg,c);
	}
	
	public void sendRequest(Message rMsg)
	{
		int c = (int) (Math.random()*101);
		sendMessage1(rMsg,c);
	}
	
	public void sendMessage1(Message rMsg,int c)
	{
		System.out.println("The thread is " + Thread.currentThread().getName());
		HostInfo h = map1.get(rMsg.receiver);
		String hname = h.hostName + ".utdallas.edu";
		System.out.println( nodeNo + " Connecting to node " + h.nodeNumber + "at port: " + h.port + " " + rMsg.type);
		
		try
		{
			//Create a client socket and connect to server at 127.0.0.1 port 5000
			Socket clientSocket = new Socket(hname,h.port);
			//Read messages from server. Input stream are in bytes. They are converted to characters by InputStreamReader
			//Characters from the InputStreamReader are converted to buffered characters by BufferedReader
			 OutputStream os = clientSocket.getOutputStream();
		     ObjectOutputStream oos = new ObjectOutputStream(os);
		     //InputStream is = clientSocket.getInputStream();
		     //ObjectInputStream ois = new ObjectInputStream(is);
		     oos.writeObject(rMsg);
		     System.out.println(nodeNo + "Message sent to " +  h.nodeNumber);
		     clientSocket.shutdownOutput();
		     oos.flush();
		     oos.close();
		     //clientSocket.close();
		}
		catch(Exception ex)
		{
			System.out.println("There is an exception in application at " + nodeNo);
			System.out.println("The message from " + rMsg.sender + " to " + rMsg.receiver);
			ex.printStackTrace();
		}
			
	
	}
}
