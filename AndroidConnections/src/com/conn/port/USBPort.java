package com.conn.port;

import java.util.HashMap;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class USBPort
{
	private static final String TAG = "USBPORT";
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private UsbManager mUsbManager;
	
	/** @deprecated */
	public USBPort(UsbManager usbManager)
	{
		mUsbManager = usbManager;
	}
	
	// TODO Intent
	private PendingIntent mPermissionIntent;
	public USBPort(UsbManager usbManager, Context context)
	{
		this(usbManager);
		// mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		// Regist BroadCast Receiver. (To acquire Permission.)
		mPermissionIntent = PendingIntent.getBroadcast(context,0,new Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	//TODO 교체
	public USBPortConnection connect_device(int vid, int pid)
	{
		USBPortConnection connection = null;
		UsbDevice device = search_device(vid, pid);
		if(device != null)
		{
			/** Important */
			if(!mUsbManager.hasPermission(device))
			{
				mUsbManager.requestPermission(device, mPermissionIntent);
				return null;
			}
			connection = connect_device(device);
			return connection;
		}
		return null;
	}
	
	/** @deprecated */
	public UsbDevice search_device(int vid, int pid)
	{
		HashMap<String, UsbDevice> usblist = mUsbManager.getDeviceList();				
		Iterator<String> iterator = usblist.keySet().iterator(); 
		UsbDevice usbDev = null;

		while (iterator.hasNext()) 
		{
			usbDev = usblist.get(iterator.next());
			if((usbDev.getVendorId() == vid) && 
			(usbDev.getProductId() == pid)) 
			{
				Log.i(TAG,"USB Connected. VID "+Integer.toHexString(usbDev.getVendorId())+", PID "+Integer.toHexString(usbDev.getProductId()));
				break;
			}
			else
				usbDev = null;
		}
		return usbDev;
	}
	
	/** @deprecated */
	public USBPortConnection connect_device(UsbDevice usbDev)
	{
		if (usbDev != null)
		{
			UsbInterface intf = null;
			int interfaceCount = 0;
			int endPointCount = 0;
			UsbEndpoint epin = null;
			UsbEndpoint epout = null;
			
			interfaceCount = usbDev.getInterfaceCount();
			Log.i(TAG,"Interface count "+interfaceCount);
			if(interfaceCount <= 0)
				return null;

			// Interface 는 보통 1개
			for(int i=0;i<interfaceCount;i++)
			{
				intf = usbDev.getInterface(i);
				endPointCount = intf.getEndpointCount();
				Log.i(TAG,"Endpoint count "+endPointCount);
				if(endPointCount <= 0)
					return null;

				// EndPoint 는 보통 2개
				for(int j=0;j<endPointCount;j++)
				{
					UsbEndpoint usbEndPoint = intf.getEndpoint(j);
					if(usbEndPoint.getDirection() == UsbConstants.USB_DIR_IN)
					{
						epin = usbEndPoint;
					}
					else if(usbEndPoint.getDirection() == UsbConstants.USB_DIR_OUT)
					{
						epout = usbEndPoint;
					}
				}
			}
			UsbDeviceConnection connection = mUsbManager.openDevice(usbDev);
			if (connection != null && connection.claimInterface(intf, true))
			{
				USBPortConnection portConnection = new USBPortConnection(connection);
				portConnection.setInterface(intf);
				portConnection.setEndPointIn(epin);
				portConnection.setEndPointOut(epout);
				return portConnection;
			}
			return null;
		}
		else
			return null;
	}
	// Request Queue 참고
}
