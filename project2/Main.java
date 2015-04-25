package project2;

import java.util.concurrent.Semaphore;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int nodeNum = Integer.parseInt(args[0].trim());
		//int nodeNum = 5;
		//String fName = "OutforNode" + nodeNum;
		//File f = new File("./" + fName);
		ConfigReader rd = new ConfigReader(nodeNum);
		System.out.println("The read file");
		rd.readFile("Configuration.txt");
		System.out.println("No of requests are: " + rd.noOfRequests);
		System.out.println("Mean delay between requests is: " + rd.meanDelayRequests);
		System.out.println("Mean duration if CS: " + rd.meanDurationCS);
		for(Integer host : rd.hosts.keySet())
		{
			System.out.println("Host number is: " + host);
			HostInfo h = rd.hosts.get(host);
			System.out.println("Host name is: " + h.hostName);
			System.out.println("Port is: " + h.port);
			System.out.println("Initial seq is: " + h.ourSeqNo);
			System.out.println("Hgst seq is: " + h.hgstSeqNo);
			System.out.println("Using is: " + h.using);
			System.out.println("Waiting is: " + h.waiting);
			System.out.println("A length is: " + h.A.length);
			System.out.println("Reply Deferred length is: " + h.replyDeferred.length);
			
		}
		
		HostInfo h = rd.hosts.get(nodeNum);
		
		Semaphore semaphore=new Semaphore(1);
		ServiceRunnable serviceModule = new ServiceRunnable(h.nodeNumber,rd.hosts,rd.noOfNodes,semaphore);
		serviceModule.start();
		
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e) {}
		
		ServiceModule ser = new ServiceModule(h.nodeNumber,rd.hosts,rd.noOfNodes,semaphore);
		
		ApplicationRunnable applicationModule = new ApplicationRunnable(h.nodeNumber,rd.hosts,rd.noOfNodes,rd,ser,semaphore);
		applicationModule.start();


	}

}
