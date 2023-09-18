package com.code.aon.ui.common.session;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Collection;
import java.util.Collections;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.NullOutputStream;

public class MockHttpServletResponse implements HttpServletResponse {
	
	private PrintWriter writer;

	public void setCharacterEncoding(String characterEncoding) {
	}

	public String getCharacterEncoding() {
		return null;
	}

	public ServletOutputStream getOutputStream() {
		return null;
	}

	public PrintWriter getWriter() throws UnsupportedEncodingException {
		if ( writer == null ) {
			writer = new PrintWriter(new NullOutputStream());	
		}
		return writer;
	}

	public void setContentLength(int contentLength) {
	}

	public void setContentType(String contentType) {
	}

	public String getContentType() {
		return null;
	}

	public void setBufferSize(int bufferSize) {
	}

	public int getBufferSize() {
		return -1;
	}

	public void flushBuffer() {
	}

	public void resetBuffer() {
	}

	public boolean isCommitted() {
		return false;
	}

	public void reset() {
		resetBuffer();
	}

	public void setLocale(Locale locale) {
	}

	public Locale getLocale() {
		return null;
	}

	public void addCookie(Cookie cookie) {
	}

	public boolean containsHeader(String name) {
		return false;
	}

	public String encodeURL(String url) {
		return url;
	}

	public String encodeRedirectURL(String url) {
		return encodeURL(url);
	}

	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	public void sendError(int status, String errorMessage) throws IOException {
	}

	public void sendError(int status) throws IOException {
	}

	public void sendRedirect(String url) throws IOException {
	}

	public void setDateHeader(String name, long value) {
	}

	public void addDateHeader(String name, long value) {
	}

	public void setHeader(String name, String value) {
	}

	public void addHeader(String name, String value) {
	}

	public void setIntHeader(String name, int value) {
	}

	public void addIntHeader(String name, int value) {
	}

	public void setStatus(int status) {
	}

	public void setStatus(int status, String errorMessage) {
	}
  
  // --------------------------------------------------------------------------
  // Since Servlet API 3.1.0

  public int getStatus(){
    return SC_NOT_IMPLEMENTED ;
  }

  public String getHeader(String name){
    return null;
  }

  public Collection<String> getHeaderNames(){
    return Collections.emptyList();
  }

  public Collection<String> getHeaders(String name){
    return Collections.emptyList();
  }

  public void setContentLengthLong(long len){
  }


}
