package com.code.aon.ui.dbutils.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.GregorianCalendar;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.dbutils.MySQLDBDumper;
import net.aonsolutions.core.dbutils.event.DBUtilsEvent;
import net.aonsolutions.core.dbutils.event.DBUtilsListener;
import net.aonsolutions.core.dbutils.runner.DBUtilsRunner;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;

public class DumpController implements DBUtilsListener, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(DumpController.class.getName());

	private String msg = "";
	private boolean end;
	private AonFile aonFile;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		LOGGER.debug(msg);
		this.msg = msg;
	}

	public boolean isEnd() {
		return end;
	}

	public AonFile getAonFile() {
		return this.aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public void onDump(ActionEvent event) {
		try {
			this.end = false;
			setMsg("");
			dump();
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} catch (AonConnectionException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void onDownloadDisk(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + getAonFile().getFileName() + ".txt\";");

			ServletOutputStream output = response.getOutputStream();
			InputStream input = new ByteArrayInputStream(getAonFile().getData());
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();
			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}

	private void dump() throws IOException, AonConnectionException {
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			GregorianCalendar gc = new GregorianCalendar();
			String prefix = "dbdump_" + gc.get(GregorianCalendar.DATE) + "_" + (gc.get(GregorianCalendar.MONTH) + 1) + "_"
					+ gc.get(GregorianCalendar.YEAR) + "_" + gc.get(GregorianCalendar.HOUR) + "_" + gc.get(GregorianCalendar.MINUTE) + "_";
			File file = File.createTempFile(prefix, ".sql");
			FileOutputStream fos = new FileOutputStream(file);
			MySQLDBDumper dumper = new MySQLDBDumper(conn, fos);
			dumper.addDBUtilsListener(this);
			setMsg("CREANDO COPIA: " + file);
			DBUtilsRunner thread = new DBUtilsRunner(dumper);
			thread.run(); // No se lanza como otro hilo a posta.
			// new Thread(thread,"COPIA-SEGURIDAD").start();
			FileReader reader = new FileReader(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(reader, baos);
			if (getAonFile() == null) {
				setAonFile(new AonFile());
			}
			getAonFile().setFileName(file.getName());
			getAonFile().setData(baos.toByteArray());
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
	}

	@Override
	public void eventHappen(DBUtilsEvent event) {
		if (event.getMessage() != null) {
			setMsg(event.getMessage());
		} else {
			setMsg("\n FINALIZADO.");
		}
		this.end = true;
	}

}
