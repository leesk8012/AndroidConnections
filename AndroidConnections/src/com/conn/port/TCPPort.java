package com.conn.port;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * TCP Port
 * @author leesk
 * 
 */
public class TCPPort implements DevicePort
{
	private final int defaultPort = 9100;
	private final int timeout = 3000;
	private static TCPPort tcpPort;
	
	private TCPPort(){}
	
	public static TCPPort getInstance()
	{
		if(tcpPort == null)
			tcpPort = new TCPPort();
		return tcpPort;
	}
	
	public TCPConnection openPort(String address) throws IOException
	{
		return openPort(address, defaultPort);
	}
	
	/**
	 * Connect the interface to the destination address.
	 * @param address		IP address.
	 * @param port			Port Number.
	 * @throws IOException
	 */
	public TCPConnection openPort(String address, int port) throws IOException
	{
		InetSocketAddress socketAdress = new InetSocketAddress(InetAddress.getByName(address), port);
		Socket socket = new Socket();
		socket.connect(socketAdress, timeout);
		return new TCPConnection(socket);
	}
}
