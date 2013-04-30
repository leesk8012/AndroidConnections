/**
 * Common Package. <br>
 */
package com.conn.request;

import java.util.Vector;

/**
 * <p>This class stores Request Data.</p>
 * 
 * @since 1.00
 */
public class RequestQueue
{
	private Vector<RequestData> queue;

	public RequestQueue()
	{
		queue = new Vector<RequestData>();
	}
	
	public synchronized RequestData dequeue() throws InterruptedException
	{
		int retry = 0;
		while (queue.isEmpty() && (retry < 5))
		{
			wait(200, 0);
			retry++;
		}
		if(queue.isEmpty())
			throw new InterruptedException("dequeue() : Queue is empty");
		RequestData retValue = (RequestData) queue.firstElement();
		queue.removeElementAt(0);
		return retValue;
	}

	public synchronized void enqueue(RequestData requestData)
	{
		if (requestData.isPrintImmediate())
		{
			queue.insertElementAt(requestData, 0);
		}
		else
		{
			queue.addElement(requestData);
		}
		notify();
	}

	public synchronized void clearQueue()
	{
		queue.removeAllElements();
	}

	public synchronized boolean isEmpty()
	{
		return queue.isEmpty();
	}
}