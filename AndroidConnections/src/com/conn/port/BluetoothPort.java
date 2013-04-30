package com.conn.port;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

/**
 * Bluetooth Port
 * @author leesk
 * 
 */
public class BluetoothPort implements DevicePort
{
	// Bluetooth SPP(Serial Port Profile) Connection.
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static BluetoothPort bluetoothPort;
	
	private BluetoothPort(){}
	
	public static BluetoothPort getInstance()
	{
		if(bluetoothPort == null)
		{
			bluetoothPort = new BluetoothPort();
		}
		return bluetoothPort;
	}
	
	@Override
	public BluetoothConnection openPort(String address) throws IOException
	{
		// Check Bluetooth Address.
		if(!BluetoothAdapter.checkBluetoothAddress(address))
		{
			throw new IOException("Invalid Bluetooth address"); 
		}
		
		BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
		return openPort(bluetoothDevice);
	}
	
	/**
	 * 
	 * @param bluetoothDevice
	 * @return
	 * @throws IOException
	 */
	public BluetoothConnection openPort(BluetoothDevice bluetoothDevice) throws IOException
	{
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter.isDiscovering())
			mBluetoothAdapter.cancelDiscovery();
		
		BluetoothSocket socket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
		socket.connect();
		return new BluetoothConnection(null);
	}
	
	/**
	 * 
	 * @param address
	 * @return
	 * @throws IOException
	 */
	public BluetoothConnection openInsecurePort(String address) throws IOException
	{
		// Check Bluetooth Address.
		if(!BluetoothAdapter.checkBluetoothAddress(address))
		{
			throw new IOException("Invalid Bluetooth address"); 
		}
		
		BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
		return openInsecurePort(bluetoothDevice);
	}

	/**
	 * 
	 * @param bluetoothDevice
	 * @return
	 * @throws IOException
	 */
	public BluetoothConnection openInsecurePort(BluetoothDevice bluetoothDevice) throws IOException
	{
		// Insecure - Over Android 2.3.3 ,Android 3.x , 4.x 
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1)
		{
			throw new IOException("Method not supported this OS version.");
		}
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter.isDiscovering())
			mBluetoothAdapter.cancelDiscovery();
		
		BluetoothSocket socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
		socket.connect();
		return new BluetoothConnection(socket);
	}	
}
