package com.code.aon.ui.account.controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.code.aon.account.Account;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.entity.IEntityAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class AccountExporter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String ROOT = "accounts";
	private static final String ACCOUNT = "account";
	private static final String CODE = "code";
	private static final String DESCRIPTION = "description";
	private static final String ALIAS = "alias";

	private boolean exportCustomerAccount;
	private boolean exportSupplierAccount;
	private boolean exportCreditorAccount;
	private boolean exportBankAccount;

	public boolean isExportCustomerAccount() {
		return exportCustomerAccount;
	}

	public void setExportCustomerAccount(boolean exportCustomerAccount) {
		this.exportCustomerAccount = exportCustomerAccount;
	}

	public boolean isExportSupplierAccount() {
		return exportSupplierAccount;
	}

	public void setExportSupplierAccount(boolean exportSupplierAccount) {
		this.exportSupplierAccount = exportSupplierAccount;
	}

	public boolean isExportCreditorAccount() {
		return exportCreditorAccount;
	}

	public void setExportCreditorAccount(boolean exportCreditorAccount) {
		this.exportCreditorAccount = exportCreditorAccount;
	}

	public boolean isExportBankAccount() {
		return exportBankAccount;
	}

	public void setExportBankAccount(boolean exportBankAccount) {
		this.exportBankAccount = exportBankAccount;
	}



	public void onExport(ActionEvent event) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			OutputStream out = res.getOutputStream();
			res.setContentType("text/xml");
			res.setHeader("Content-Disposition", "attachment; filename=\"account.xml\";");
			export(out);
			ctx.responseComplete();
		} catch (ManagerBeanException e) {
			String message = "Imposible realizar la exportación del Plan General Contable";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (ParserConfigurationException e) {
			String message = "Imposible realizar la exportación del Plan General Contable";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (TransformerConfigurationException e) {
			String message = "Imposible realizar la exportación del Plan General Contable";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (TransformerException e) {
			String message = "Imposible realizar la exportación del Plan General Contable";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (IOException e) {
			String message = "Imposible realizar la exportación del Plan General Contable";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
	}

	public void export(OutputStream out) throws TransformerException, ManagerBeanException, ParserConfigurationException {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IEntityAlias.ACCOUNT_CODE));
		List<ITransferObject> list = bean.getList(criteria);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document xmldoc = builder.newDocument();
		Element root = xmldoc.createElement(ROOT);
		xmldoc.appendChild(root);
		for (ITransferObject to : list) {
			Account a = (Account) to;
			if (a.getLevel() < 5 || isExportable(a)) {
				Element account = xmldoc.createElement(ACCOUNT);
	
				Element id = xmldoc.createElement(CODE);
				id.appendChild(xmldoc.createTextNode(a.getCode()));
				account.appendChild(id);
	
				Element description = xmldoc.createElement(DESCRIPTION);
				if (a.getDescription() != null) {
					description.appendChild(xmldoc.createCDATASection(a.getDescription()));
				}
				account.appendChild(description);
	
				Element alias = xmldoc.createElement(ALIAS);
				if (a.getAlias() != null) {
					alias.appendChild(xmldoc.createCDATASection(a.getAlias()));	
				}
				account.appendChild(alias);
	
				root.appendChild(account);
			}
		}

		DOMSource domSource = new DOMSource(xmldoc);
		StreamResult streamResult = new StreamResult(out);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.transform(domSource, streamResult);
	}

	private boolean isExportable(Account a) {
		if (isExportBankAccount() && isExportCreditorAccount() &&
			isExportCustomerAccount() && isExportSupplierAccount()) {
			return true;
		}
		String code = a.getCode();
		if (!isExportBankAccount() && code.startsWith("572") ) return false;
		if (!isExportCreditorAccount() && code.startsWith("410") ) return false;
		if (!isExportCustomerAccount() && code.startsWith("430") ) return false;
		if (!isExportSupplierAccount() && code.startsWith("400") ) return false;
		return true;
	}

	public static void main(String[] args) throws FileNotFoundException, ManagerBeanException, TransformerException, ParserConfigurationException {
		AccountExporter exp = new AccountExporter();
		FileOutputStream fos = new FileOutputStream("/tmp/account.xml");
		exp.export(fos);
	}
}
