package project2;

import java.io.Serializable;

public class Message implements Serializable{

	int timestamp;
	int sender;
	int receiver;
	MsgType type;
	
	public Message(int t, int send, int receive, MsgType mType)
	{
		timestamp = t;
		sender = send;
		receiver = receive;
		type = mType;
	}
	
	public Message(int send, int receive, MsgType mType)
	{
		sender = send;
		receiver = receive;
		type = mType;
	}
	
}

enum MsgType
{
	REQUEST,REPLY,REQUESTREPLY
}
