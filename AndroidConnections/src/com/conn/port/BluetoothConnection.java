package com.conn.port;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.conn.request.RequestData;
import com.conn.request.RequestQueue;

import android.bluetooth.BluetoothSocket;
import android.os.Build;

public class BluetoothConnection implements DeviceConnection
{
	private BluetoothSocket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private RequestQueue sendQueue;
	private RequestQueue recvQueue;
	private Thread sendHandler;
	private Thread recvHandler;

	protected BluetoothConnection(BluetoothSocket socket) throws IOException
	{
		if((socket != null) && (socket.isConnected()))
		{
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			this.socket = socket;
			sendQueue = new RequestQueue();
			recvQueue = new RequestQueue();
			// start Handler Thread.	
			sendHandler = new Thread(new SenderThread());
			sendHandler.start();
			recvHandler = new Thread(new ReceiveThread());
			recvHandler.start();
		}
	}
	
	@Override
	public void closePort() throws IOException, InterruptedException
	{
		int count = 0;
		while((!sendQueue.isEmpty()) && (count < 3))
		{
			Thread.sleep(1000);
			count++;
		}
		if(outputStream != null)
			outputStream.close();
		if(inputStream != null)
			inputStream.close();
		if(socket != null)
			socket.close();
		socket = null;
	}

	/**
	 * 
	 */
	public boolean isConnected() throws IOException
	{
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			throw new IOException("Method not supported this OS version.");
		}
		return socket.isConnected();
	}

	@Override
	public void sendData(byte[] data)
	{
		sendQueue.enqueue(new RequestData(data));
	}

	@Override
	public byte[] recvData() throws InterruptedException
	{
		RequestData rData = recvQueue.dequeue();
		return rData.getRequestData();
	}
	
	private InputStream getInputStream()
	{
		return inputStream;
	}

	private OutputStream getOutputStream()
	{
		return outputStream;
	}
	
	// Receive Data.
	class ReceiveThread implements Runnable
	{
		@Override
		public void run()
		{
			byte [] data;
			byte [] temp;
			int rin = 0;
			try
			{
				InputStream inputStream = getInputStream();
				while(!Thread.currentThread().isInterrupted())
				{
					data = new byte[256];
					rin = inputStream.read(data, 0, data.length);
					temp = new byte[rin];
					System.arraycopy(data, 0, temp, 0, rin);
					Thread.sleep(100);
					recvQueue.enqueue(new RequestData(temp));
				}
			}
			catch(Exception e)
			{
				recvQueue.clearQueue();
			}
		}
	}
	
	// Send Data.
	class SenderThread implements Runnable 
	{
		final byte [] dummy = {0x00,0x00};
		@Override
		public void run()
		{
			byte [] data;
			try
			{
				OutputStream os = getOutputStream();
				// Initial Wake up.
				long lastTime = System.currentTimeMillis();
				os.write(dummy);
				os.flush();
				Thread.sleep(400);
				while(!Thread.currentThread().isInterrupted())
				{
					data = (sendQueue.dequeue()).getRequestData();
					// Wake up. (PowerSave Mode)
					if((System.currentTimeMillis() - lastTime) > 59000)
					{
						lastTime = System.currentTimeMillis();
						os.write(dummy);
						os.flush();
						Thread.sleep(400);
					}
					// Write Data.
					os.write(data);
			        os.flush();
			    	Thread.sleep(100);
				}
			}
			catch(Exception e)
			{
				sendQueue.clearQueue();
			}
		}
	}
}
