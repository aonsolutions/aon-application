package com.code.aon.ui.account.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.richfaces.event.UploadEvent;
import org.xml.sax.SAXException;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ui.util.AonUtil;

public class AccountImporter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String RULES_FILE = "account_import_digester.xml";
	private static Digester DIGESTER;
	private AonFile aonFile;

	private static final Digester getDigester() {
		if (DIGESTER == null) {
			DIGESTER = DigesterLoader.createDigester(AccountImporter.class.getResource(RULES_FILE));
			DIGESTER.setUseContextClassLoader(true);
		}
		return DIGESTER;
	}

	public AonFile getAonFile() {
		return this.aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
	}

	public void onInitialize(ActionEvent event) {
		setAonFile(null);
	}

	public void onImport(ActionEvent event) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(getAonFile().getData());
			parse(in);
			AonUtil.addInfoMessage("Proceso finalizado correctamente");
		} catch (Exception e) {
			String message = "Imposible realizar la importación del Plan General Contable";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
	}

	private void parse(InputStream in) throws ManagerBeanException {
		try {
			Digester digester = getDigester();
			digester.parse(in);
		} catch (IOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

}


