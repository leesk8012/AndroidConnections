package com.conn.port;

import java.io.IOException;

/**
 * <p>Port Interface.</p>
 * @since 2013.04.24
 * @version 2.0
 */
public interface DevicePort
{
	/**
	 * Connect the interface to the destination address.
	 * @param address
	 * @throws IOException
	 */
	public DeviceConnection openPort(String address) throws IOException;
}
