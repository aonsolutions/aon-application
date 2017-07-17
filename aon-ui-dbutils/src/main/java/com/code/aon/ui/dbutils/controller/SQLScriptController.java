package com.code.aon.ui.dbutils.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.richfaces.event.UploadEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.util.AonFile;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.AonSQLFile;
import net.aonsolutions.core.dbutils.AonSQLScript;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;

public class SQLScriptController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private AonFile aonFile;
	private List<String> statements;

	public AonFile getAonFile() {
		return this.aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		if ( this.aonFile != null ) {
			this.aonFile.clean();
		}
		this.aonFile = aonFile;
	}

	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
	}

	public void fileDeleted(AonFile aonFile) {
		setAonFile(null);
	}

	public void onInitialize(ActionEvent event) {
		setAonFile(null);
		setStatements(null);
	}

	public void onExecute(ActionEvent event) {
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			ByteArrayInputStream input = new ByteArrayInputStream(getAonFile().getData());
			AonSQLFile sqlFile = new AonSQLFile(input);
			AonSQLScript script = new AonSQLScript(sqlFile, conn);
			script.execute();
			AonUtil.addInfoMessage("Proceso realizado correctamente.");
		} catch (AonSQLException e) {
			AonUtil.addErrorMessage("Se han producido errores en la importación.");
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (AonConnectionException e) {
			AonUtil.addErrorMessage("Se han producido errores en la importación.");
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
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