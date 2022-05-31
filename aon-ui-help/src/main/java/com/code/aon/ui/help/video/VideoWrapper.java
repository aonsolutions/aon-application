package com.code.aon.ui.help.video;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.apache.commons.lang.NotImplementedException;

public final class VideoWrapper {

	private VideoWrapper() {
		
	}
	
	private enum Protocol{
		HTTP("http://"),
		HTTPS("https://"),
		DRIVE("drive://"),
		FILE("")
		;
		
		private String suffix;
		private Protocol(String suffix) {
			this.suffix = suffix;
		}
		
		static Protocol getBySuffix(String value) {
			for(Protocol e: Protocol.values()) {
			   if(e.suffix.equals(value)) {
			     return e;
			   }
			 }
			 return FILE;
		}
		
	}
	
	
	/**
	 * Get video from the original source and return 
	 * an InputStream containing it.
	 * @param path The video path
	 * @return The inputStream containing the video content
	 */
	public static InputStream getVideo(String path) {
		
		Protocol protocol = Protocol.getBySuffix("");
		
		switch (protocol) {
		case HTTP:
		case HTTPS: // http handler call
			return getHTTPVideo(path);
			
		case DRIVE: // Drive handler call
			return getDriveVideo(path);
			
		case FILE: // File handler call
			return getFileVideo(path);
			
		default:
			return null;
		}
				
	}	
	
	
	public static InputStream getFileVideo(String path) {
		return new BufferedInputStream(VideoWrapper.class.getResourceAsStream(path));
	}
	
	public static InputStream getDriveVideo(String path) {
		throw new NotImplementedException("not implemented yet");
	}
	
	public static InputStream getHTTPVideo(String path) {
		throw new NotImplementedException("not implemented yet");
	}
	
}
