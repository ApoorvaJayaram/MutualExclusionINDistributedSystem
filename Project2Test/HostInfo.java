package Project2Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class HostInfo {
	
	int nodeNumber;
	String hostName;
	int port;
	int clientPort;
	int ourSeqNo;
	int hgstSeqNo;
	boolean A[];
	boolean using;
	boolean waiting;
	boolean replyDeferred[];
	//Queue<Integer> requestHost = new LinkedList<Integer>();
	//Queue<Integer> defertHost = new LinkedList<Integer>();
	Vector<Integer> requestHost = new Vector<Integer>();
	Vector<Integer> deferHost = new Vector<Integer>();
	int rTime;
	//ArrayList<String> neighbours = new ArrayList<String>();
	//Queue<Integer> newHost = new LinkedList<Integer>();
	//boolean e;
	
	public HostInfo()
	{
		ourSeqNo = 0;
		hgstSeqNo = 0;
		using = false;
		waiting = false;
	}
	
	public HostInfo(int n)
	{
		ourSeqNo = 0;
		hgstSeqNo = 0;
		using = false;
		waiting = false;
		A = new boolean[n];
		replyDeferred = new boolean[n];
		Arrays.fill(A, Boolean.FALSE);
		Arrays.fill(replyDeferred, Boolean.FALSE);
	}
}
