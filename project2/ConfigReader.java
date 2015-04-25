package project2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class ConfigReader {
	
	int noOfNodes;
	int noOfRequests;
	int nodeNum;
	double meanDelayRequests;
	double meanDurationCS;
	int c=0;
	HashMap<Integer,HostInfo> hosts = new HashMap<Integer,HostInfo>();
	
	int num;
	
	public ConfigReader(int n)
	{
		nodeNum = n;
	}
	
	public void readFile(String fileName)
	{
		 FileReader file = null;
		 String line = "";
		 try 
		 {
		      file = new FileReader(fileName);
		      BufferedReader reader = new BufferedReader(file);
		      while ((line = reader.readLine()) != null) 
		      {
		      	  //System.out.println(line);
		    	  if(!line.startsWith("#") && c==0)
		    	  {
		    		  noOfNodes=Integer.parseInt(line.trim());
				      //System.out.println(noOfNodes);
		    		  c++;
		    	  }
		    	  else
		    	  {
		    		  if(!line.startsWith("#") && c==1)
		    		  {
		    			  do
		    			  {
		    				 //System.out.println(line);
		    				HostInfo hst= new HostInfo(noOfNodes);
		    			  	String str[] = line.split(" ");
		    			  	//System.out.println(str[0]);
		    			  	//System.out.println(str[2]);
		    			  	hst.nodeNumber = Integer.parseInt(str[0].trim());
		    			  	hst.hostName = str[1].trim();
		    			  	//System.out.println("The host name is: " + str[1]);
		    			  	hst.port = Integer.parseInt(str[2].trim());
						    hst.clientPort = Integer.parseInt(str[3].trim());
		    			  	num = hst.nodeNumber;
		    			  	//System.out.println(num);
		    			  	hosts.put(num, hst);
		    			  	
		    			  }while((line = reader.readLine()) != null && !line.startsWith("#") && c==1);
		    			  
		    			  c++;
		    			  continue;
		    		  }
		    		  
		    		  if(!line.startsWith("#") && c==2)
		    		  {
		    			  noOfRequests = Integer.parseInt(line.trim());
		    			  c++;
		    			  continue;
		    		  }
		    		  
		    		  if(!line.startsWith("#") && c==3)
		    		  {
		    			  meanDelayRequests = Double.parseDouble(line.trim());
		    			  c++;
		    			  continue;
		    		  }
		    		  
		    		  if(!line.startsWith("#") && c==4)
		    		  {
		    			  meanDurationCS = Double.parseDouble(line.trim());
		    			  c++;
		    			  continue;
		    		  }
		    		  
		    	  }
		      
		      }
		 }
		 catch (Exception e) 
		 {
		      //System.out.println("File not found");
			 System.out.println("Node id is " + nodeNum);
		      e.printStackTrace();
		 } 
		/* catch (IOException e) 
		 {
		      System.out.println("IO Error occured");
		      e.printStackTrace();
		 } */
		 /*finally 
		 {
		    if (file != null) 
		    {
		        try {
		          file.close();
		        } catch (IOException e) {
		          e.printStackTrace();
		        }
		    }
		 }*/
		
	}

}
