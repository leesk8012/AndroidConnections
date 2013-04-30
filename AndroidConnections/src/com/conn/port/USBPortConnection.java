package com.conn.port;

import java.io.IOException;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import com.conn.request.RequestData;
import com.conn.request.RequestQueue;

public class USBPortConnection
{
	private Thread sendHandler;
	private RequestQueue sendQueue;
	
	private UsbDeviceConnection usbDeviceConnection;
	private UsbInterface usbInterface;
	private UsbEndpoint endPointInput;
	private UsbEndpoint endPointOutput;
	
	protected USBPortConnection(UsbDeviceConnection deviceConnection)
	{
		usbDeviceConnection = deviceConnection;
		sendQueue = new RequestQueue();
		sendHandler = new Thread(new SenderThread());
		sendHandler.start();
	}
	
	protected void setInterface(UsbInterface usbInterface)
	{
		this.usbInterface = usbInterface;
	}
	protected void setEndPointIn(UsbEndpoint endpoint)
	{
		endPointInput = endpoint;
//		inputBufferMaxSize = endpoint.getMaxPacketSize();
	}
	protected void setEndPointOut(UsbEndpoint endpoint)
	{
		endPointOutput = endpoint;
//		outputBufferMaxSize = endpoint.getMaxPacketSize();
	}
	
	public int readUSB(byte [] buffer)
	{
		return readUSB(buffer, 2000);
	}
	
	public int readUSB(byte [] buffer, int timeout)
	{
		return usbDeviceConnection.bulkTransfer(endPointInput, buffer, buffer.length, timeout);
	}
	
	public void closePort() throws IOException, InterruptedException
	{
		int count = 0;
		while(!sendQueue.isEmpty() && (count < 3))
		{
			Thread.sleep(1000);
			count++;
		}
		usbDeviceConnection.releaseInterface(usbInterface);
		usbDeviceConnection.close();
		// end Handler Thread.
		if(sendHandler != null && sendHandler.isAlive())
			sendHandler.interrupt();
	}
	
	public void sendData(byte[] data)
	{
		sendQueue.enqueue(new RequestData(data));		
	}

	public int readData(byte [] buffer,int timeout) throws InterruptedException
	{
		return usbDeviceConnection.bulkTransfer(endPointInput, buffer, buffer.length, timeout);
	}
	
	class SenderThread implements Runnable 
	{
		@Override
		public void run()
		{
			byte [] data;
			try
			{
				while(!Thread.currentThread().isInterrupted())
				{
					data = (sendQueue.dequeue()).getRequestData();
			    	usbDeviceConnection.bulkTransfer(endPointOutput, data, data.length, 2000);
					Thread.sleep(10);
				}
			}
			catch(Exception e)
			{
				sendQueue.clearQueue();
			}
		}
	}
}
