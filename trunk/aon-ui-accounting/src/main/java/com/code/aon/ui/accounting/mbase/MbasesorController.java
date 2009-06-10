package com.code.aon.ui.accounting.mbase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.sql.AonSQLException;
import com.code.aon.common.sql.AonSQLFile;
import com.code.aon.common.sql.AonSQLScript;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.util.AonUtil;

public class MbasesorController  {

	private AonFile aonFile;
	private List<String> statements;

	public AonFile getAonFile() {
		return this.aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public void fileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName(item.getFileName());
//			f.addAonFileListener(this);
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void fileDeleted(AonFile aonFile) {
		setAonFile(null);
	}

	public void onInitialize(ActionEvent event) {
		setAonFile(null);
		setStatements(null);
	}

	@SuppressWarnings("deprecation")
	public void onExecute(ActionEvent event) {
		try {
			Connection c = HibernateUtil.getSQLConnection();
			ByteArrayInputStream input = new ByteArrayInputStream(getAonFile().getData());

			AonSQLFile sqlFile = new AonSQLFile(input);
			AonSQLScript script = new AonSQLScript(sqlFile, c);
			script.execute();
			AonUtil.addInfoMessage("OK!");
		} catch (AonSQLException e) {
			AonUtil.addErrorMessage("Se han producido errores en la importación.");
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onView(ActionEvent event) {
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(getAonFile().getData());

			AonSQLFile sqlFile = new AonSQLFile(input);
			setStatements(sqlFile.getStatementList(20));
		} catch (AonSQLException e) {
			AonUtil.addErrorMessage("No se puede visualizar el contenido del fichero.");
			throw new AbortProcessingException(e);
		}
	}

	public void setStatements(List<String> statements) {
		this.statements = statements;
	}

	public List<String> getStatements() {
		return statements;
	}

}