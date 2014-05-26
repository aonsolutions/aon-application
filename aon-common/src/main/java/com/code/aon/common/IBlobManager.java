package com.code.aon.common;

public interface IBlobManager {

	byte[] getBlob( IBlobObject blobObject, String property );
	
	void setBlobs(IBlobObject blobObject);
	
}
