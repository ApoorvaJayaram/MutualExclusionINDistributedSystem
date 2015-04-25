package Project2Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class TestApp {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		HashMap<Character, Integer> hm = new HashMap<Character , Integer>();
		System.out.println("========= \n");
		 FileReader file = null;
		 String line = "";
		int noOfNodes=10;
		      file = new FileReader("Configuration.txt");
		      BufferedReader reader = new BufferedReader(file);
		    
		      	
		      	  try {
					while ((line = reader.readLine()) != null) 
					  {  System.out.println(line);
					  if(!line.startsWith("#"))
					  {
						  noOfNodes=Integer.parseInt(line.trim());
					      System.out.println(noOfNodes);
					      break;
						  
					  }
					  }
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		ConfigReader rd = new ConfigReader(noOfNodes);
		rd.readFile("Configuration.txt");
		int expected_lines = 2 * rd.noOfNodes * rd.noOfRequests;
		System.out.println("number of lines expected : "+ expected_lines) ;
		
		
		System.out.println("========= \n");
		int count = 0;
		boolean CSvoilated = false;
		file = new FileReader("MLFile.txt");
		reader = new BufferedReader(file);
		  String[] array = new String[100];
		  int lineCount = 0;
      	  try {
			while ((line = reader.readLine()) != null && !line.isEmpty()) 
			  {System.out.println(line);
			  count++;
			  array = line.split(" ");
			  
			  if(hm.containsKey(array[0].charAt(0)) && line.contains("leaving"))
			  { CSvoilated= false;
				  lineCount = hm.get(array[0].charAt(0)); 
			  hm.put(array[0].charAt(0),++lineCount);
			  
			  }
			  else if(!hm.containsKey(array[0].charAt(0)) && line.contains("entering"))
			  {
				  CSvoilated = true;
				  hm.put(array[0].charAt(0), 1);
				  lineCount = 1;
			  }
			  else if(hm.containsKey(array[0].charAt(0)) && line.contains("entering"))
			  {
				  lineCount = hm.get(array[0].charAt(0));
				  hm.put(array[0].charAt(0),++lineCount);
				  CSvoilated = true;
			  }
			  }
		} catch(Exception e){e.printStackTrace();}
		
		/*InputStream is = new BufferedInputStream(new FileInputStream("MLFile.txt"));
		
		try {
		        byte[] c = new byte[1024];
		        int first = 0 ;
		        int readChars = 0;
		        String l="";
		        boolean empty = true;
		        try {
					while ((readChars = is.read()) != -1) {
						//System.out.println("line read==>"+readChars);
					    empty = false;
					    if(first == 0){
					    	System.out.println((char)c[first]);
					    	hm.put((char)c[first], 1);
					    	first++;
					    }
					    for (int i = 0; i < c.length; ++i) {
					        if (c[i] == '\n') {
					        	System.out.println((char)c[i]);
					        	int j = i+1;
					        	if(hm.containsKey((char)c[j])){
					        		int value = hm.get((char) c[j]);
					        		hm.put((char)c[j], ++value);
					        	}else{
					        		hm.put((char)c[j], 1);
					        	}
					            ++count;
					        }
					    }
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        //return (count == 0 && !empty) ? 1 : count;
		    } finally {
		        try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		*/
		for(Character c : hm.keySet())
		{
			System.out.println("Entries for Host number " + c + " is ===> "+ hm.get(c));
			//HostInfo h = rd.hosts.get(c);
		}
		
	    	System.out.println("Total number of lines counted : " + count);
		    
	    	if(expected_lines != count || CSvoilated){
	    		System.out.println(" Failure... Critical Sections Overlapping... ");
		    }else{
		    	System.out.println(" Success... No Overlapping of Critical Sections !!");
		    	
		    }
	    	
	    
	}

}
