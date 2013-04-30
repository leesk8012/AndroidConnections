/**
 * Common Package. <br>
 */
package com.conn.request;

/**
 * <p>Wrapping Request Data formmed Byte Array.</p>
 *
 * @since 1.00
 */
public class RequestData
{
	private boolean asyncMode;
	private boolean printImmediate;
	private byte[] requestData;
	private boolean encryptMode;

	public RequestData(byte [] requestData)
	{
		this.requestData = requestData;
		this.asyncMode = false;
		this.printImmediate = false;
		this.encryptMode = false;
	}
	public RequestData(byte [] requestData, boolean encryptMode)
	{
		this.requestData = requestData;
		this.encryptMode = encryptMode;
		this.asyncMode = false;
		this.printImmediate = false;
	}
	public RequestData(byte[] requestData, boolean encryptMode, boolean asyncMode)
	{
		this.requestData = requestData;
		this.encryptMode = encryptMode;
		this.asyncMode = asyncMode;
		this.printImmediate = false;
	}
	public RequestData(byte [] requestData, boolean encryptMode, boolean asyncMode, boolean printImmediate)
	{
		this.requestData = requestData;
		this.encryptMode = encryptMode;
		this.asyncMode = asyncMode;
		this.printImmediate = printImmediate;
	}
        
	public boolean isPrintImmediate()
	{
		return printImmediate;
	}

        public byte[] getRequestData()
	{
		return requestData;
	}
        
	public boolean getAsyncMode()
	{
		return asyncMode;
	}
	
	public boolean getEncryptMode()
	{
		return encryptMode;
	}
}