package com.code.aon.ui.webmail.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.webmail.exception.WebmailException;
import com.icesoft.faces.context.effects.JavascriptContext;

public class AonAttachment{
	
	private int position;
	
	private Part part;
	
    /**
	 * @return the part
	 */
	public Part getPart() {
		return part;
	}

	/**
	 * @param part the part to set
	 */
	public void setPart(Part part) {
		this.part = part;
	}

	public String getFileName(){
		String fileName;
		try {
			fileName = part.getFileName();
		} catch (MessagingException e1) {
			fileName = "Error reading name";
		}
		if (fileName==null)
			fileName = "no_name_file_"+position;
		try {
			if (part.getContentType().startsWith("message/") &&
					!fileName.endsWith(".eml")){
				fileName += ".eml";
			}
		} catch (MessagingException e1) {
		}
		if (fileName.indexOf("=?iso") >= 0) {
			try {
				fileName = MimeUtility.decodeWord(fileName);
			}
			catch (Exception e) {
			}
		}
		return fileName;
	}
	
	public void download() throws WebmailException {
		try {
			String filename = part.getFileName();
			if (filename==null)
				filename = "no_name_file";
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces
					.getExternalContext().getResponse();
			response.setContentType("aplication/disk");
			response.setHeader("content-disposition", "attachment;filename=\""
					+ filename + "\"");
			BufferedOutputStream bos = new BufferedOutputStream(response
					.getOutputStream());

			byte[] data = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(part.getInputStream());
			boolean eof = false;
			while (!eof) {
				int length = bis.read(data);
				if (length == -1) {
					eof = true;
				} else {
					bos.write(data, 0, length);
				}
			}
			bos.flush();
			bos.close();
			bis.close();
			JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.open('report.iface', 'myWindow');"); 
			faces.responseComplete();
		} catch (IOException e) {
			throw new WebmailException(e);
		} catch (MessagingException e) {
			throw new WebmailException(e);
		}
	}
	
	public void download(HttpServletResponse response) {
		try {
			String filename = getFileName();
			response.setContentType(part.getContentType());
			response.setHeader("content-disposition", "attachment;filename=\""
					+ filename + "\"");
			ServletOutputStream sos = response.getOutputStream();
			byte[] data = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(part.getInputStream());
			boolean eof = false;
			while (!eof) {
				int length = bis.read(data);
				if (length == -1) {
					eof = true;
				} else {
					sos.write(data, 0, length);
				}
			}
			sos.flush();
			sos.close();
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}


}
