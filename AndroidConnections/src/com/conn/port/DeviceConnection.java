package com.conn.port;

import java.io.IOException;

public interface DeviceConnection
{
	/**
	 * Disconnect the current connection.
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void closePort() throws IOException, InterruptedException;
	
	/**
	 * Get a status that the interface were connected.
	 * @return Interface whether connected or not.
	 * @throws IOException 
	 */
	public boolean isConnected() throws IOException;
	
	/**
	 * Send a data to connected port.
	 * @param data
	 */
	public void sendData(byte [] data);
	
	/**
	 * Receive a data from port.
	 * @return Data.
	 * @throws InterruptedException
	 */
	public byte [] recvData() throws InterruptedException;
}
