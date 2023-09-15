package com.code.aon.ui.loader.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.MessageFormat;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.servlet.http.HttpServletResponse;

import org.richfaces.event.UploadEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.ILogger;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ui.loader.Loader;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.servlet.ZippedMultiLoad;
import com.code.aon.ui.util.AonUtil;

public class AonZipLoaderController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private LoaderParams params;
	private AonFile aonFile;

	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		if ( this.aonFile != null ) {
			this.aonFile.clean();
		}
		this.aonFile = aonFile;
	}
	public LoaderParams getParams() {
		return params;
	}
	public void setParams(LoaderParams params) {
		this.params = params;
	}
	
	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
	}

	public void onStart(ActionEvent event ) {
		onClear(event);
	}
	
	public void onClear(ActionEvent event ) {
		setAonFile(null);
		setParams(new LoaderParams());
	}
	
	public void onLoad(ActionEvent event ) {
		processFile(false);
	}

	public void onPayrollLoad(ActionEvent event ) {
		processFile(true);
	}	
	
	private void processFile(boolean isPayrollFile) {
		HttpServletResponse response = DownloadUtil.getResponse();
		response.setContentType( MimeType.MIME_HTML.getName() );
		response.setCharacterEncoding("ISO-8859-1");
		
		HTMLLogger logger;
		try {
			PrintWriter writer = response.getWriter();
			logger = new HTMLLogger(writer);
			logger.start();
		} catch (IOException e1) {
			AonUtil.addErrorMessage(e1.getMessage());
			throw new AbortProcessingException(e1.getMessage());
		}
		Loader loader = new Loader(params);
		try {
			byte[] data = getAonFile().getData();
			ByteArrayInputStream input = new ByteArrayInputStream(data);			
			ZippedMultiLoad zml = new ZippedMultiLoad(getParams());
			zml.load(input, logger, isPayrollFile);
			input.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (AonException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			loader.setFactoryManager(null);
			if (logger != null) logger.end();
			FacesContext context = FacesContext.getCurrentInstance();
	        context.responseComplete();    			
		}
	}
	
	public static class HTMLLogger implements  ILogger {
		
		private static String PATTERN = "<div style=\"{0}\"><b>{1}</b><span style=\"margin-left: 5px;\">{2}</span></div>";
		
		private PrintWriter out;
		private int mustFlush;
		
		private HTMLLogger(PrintWriter printWriter) {
			this.out = printWriter;
			mustFlush = 0;
		}
		@Override
		public void error(String msg) {
			print(MessageFormat.format(PATTERN,"color:red;","ERR",msg));
		}

		@Override
		public void warn(String msg) {
			print(MessageFormat.format(PATTERN,"color:orange;","WRN",msg));
		}

		@Override
		public void info(String msg) {
			print(MessageFormat.format(PATTERN,"","INF",msg));
		}
		
		public void print(String format) {
			this.out.print(format);
			++mustFlush;
			if  (mustFlush >= 5) {
				this.out.print("<script>window.scrollTo(0,document.body.scrollHeight);</script>");			
				this.out.flush();
				mustFlush = 0;
			}
		}
		
		public void start() {
			this.out.print("<div style=\"font-family: monospace;\">");			
			this.out.print("<div style=\"text-align: center\">");
//			this.out.print("<a href=\"/aon-aio\">Volver</a>");
			this.out.print("<a href=\"./\">Volver</a>");
			this.out.print("</div>");
		}
		
		public void end() {
			this.out.print("<div style=\"text-align: center\">");
			//this.out.print("<a href=\"/aon-aio\">Volver</a>");
			this.out.print("<a href=\"./\">Volver</a>");
			this.out.print("</div>");
			this.out.print("<script>window.scrollTo(0,document.body.scrollHeight);</script>");
			this.out.print("</div>");
			this.out.flush();
		}
	}
}
