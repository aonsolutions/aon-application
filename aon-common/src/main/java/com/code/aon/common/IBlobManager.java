package com.code.aon.common;

public interface IBlobManager {

	byte[] getBlob( IBlobObject blobObject, String property );
	
	void setBlobs(boolean insert, IBlobObject blobObject);
	
	void deleteBlobs(IBlobObject blobObject);
	
}
