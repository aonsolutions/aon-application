package com.code.aon.ui.accounting.controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;
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

import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class AmortizationTypeExporter {
	private static final String ROOT = "amortizationTypes";
	private static final String AMORTIZATION_TYPE = "amortizationType";
	
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String FIXED_ASSET_ACCOUNT = "fixedAssetAccount";
	private static final String ACUMULATED_ACCOUNT = "accumulatedAccount";
	private static final String ALLOCATION_ACCOUNT = "allocationAccount";
	private static final String PERCENTAGE = "percentage";
	// ACCOUNT
	private static final String LEVEL = "level";

	public void onExport(ActionEvent event) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			OutputStream out = res.getOutputStream();
			res.setContentType("text/xml");
			res.setHeader("Content-Disposition", "attachment; filename=\"amortizationType.xml\";");
			export(out);
			ctx.responseComplete();
		} catch (ManagerBeanException e) {
			String message = "Imposible realizar la exportación de los tipos de amortización";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (ParserConfigurationException e) {
			String message = "Imposible realizar la exportación de los tipos de amortización";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (TransformerConfigurationException e) {
			String message = "Imposible realizar la exportación de los tipos de amortización";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (TransformerException e) {
			String message = "Imposible realizar la exportación de los tipos de amortización";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (IOException e) {
			String message = "Imposible realizar la exportación de los tipos de amortización";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
	}

	public void export(OutputStream out) throws TransformerException, ManagerBeanException, ParserConfigurationException {
		IManagerBean bean = BeanManager.getManagerBean(AmortizationType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IAccountingAlias.AMORTIZATION_TYPE_ID));
		List<ITransferObject> list = bean.getList(criteria);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document xmldoc = builder.newDocument();
		Element root = xmldoc.createElement(ROOT);
		xmldoc.appendChild(root);
		for (ITransferObject to : list) {
			AmortizationType a = (AmortizationType)to;
			
			Element amortizationType = xmldoc.createElement(AMORTIZATION_TYPE);
			
			Element id = xmldoc.createElement(ID);
			id.appendChild(xmldoc.createTextNode(a.getId().toString()));
			amortizationType.appendChild(id);
			
			Element description = xmldoc.createElement(DESCRIPTION);
			if (a.getDescription() != null) {
				description.appendChild(xmldoc.createCDATASection(a.getDescription()));
			}
			amortizationType.appendChild(description);

			Element fixedAssetAccount = xmldoc.createElement(FIXED_ASSET_ACCOUNT);
			if (a.getFixedAssetAccount() != null) {
				id = xmldoc.createElement(ID);
				id.appendChild(xmldoc.createTextNode(a.getFixedAssetAccount().getId()));
				fixedAssetAccount.appendChild(id);
				description = xmldoc.createElement(DESCRIPTION);
				description.appendChild(xmldoc.createCDATASection(a.getFixedAssetAccount().getDescription()));
				fixedAssetAccount.appendChild(description);
				Element level = xmldoc.createElement(LEVEL);
				level.appendChild(xmldoc.createTextNode(String.valueOf(a.getFixedAssetAccount().getLevel())));
				fixedAssetAccount.appendChild(level);
			}
			amortizationType.appendChild(fixedAssetAccount);

			Element accumulatedAccount = xmldoc.createElement(ACUMULATED_ACCOUNT);
			if (a.getAccumulatedAccount() != null) {
				id = xmldoc.createElement(ID);
				id.appendChild(xmldoc.createTextNode(a.getAccumulatedAccount().getId()));
				accumulatedAccount.appendChild(id);
				description = xmldoc.createElement(DESCRIPTION);
				description.appendChild(xmldoc.createCDATASection(a.getAccumulatedAccount().getDescription()));
				accumulatedAccount.appendChild(description);
				Element level = xmldoc.createElement(LEVEL);
				level.appendChild(xmldoc.createTextNode(String.valueOf(a.getAccumulatedAccount().getLevel())));
				accumulatedAccount.appendChild(level);
			}
			amortizationType.appendChild(accumulatedAccount);
			
			Element allocationAccount = xmldoc.createElement(ALLOCATION_ACCOUNT);
			if (a.getAllocationAccount() != null) {
				id = xmldoc.createElement(ID);
				id.appendChild(xmldoc.createTextNode(a.getAllocationAccount().getId()));
				allocationAccount.appendChild(id);
				description = xmldoc.createElement(DESCRIPTION);
				description.appendChild(xmldoc.createCDATASection(a.getAllocationAccount().getDescription()));
				allocationAccount.appendChild(description);
				Element level = xmldoc.createElement(LEVEL);
				level.appendChild(xmldoc.createTextNode(String.valueOf(a.getAllocationAccount().getLevel())));
				allocationAccount.appendChild(level);
			}
			amortizationType.appendChild(allocationAccount);
			
			Element percentage = xmldoc.createElement(PERCENTAGE);
			if ((new Double(a.getPercentage())) != null) {
				percentage.appendChild(xmldoc.createTextNode(String.valueOf(a.getPercentage())));
			}
			amortizationType.appendChild(percentage);
			
			root.appendChild(amortizationType);
		}

		DOMSource domSource = new DOMSource(xmldoc);
		StreamResult streamResult = new StreamResult(out);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.transform(domSource, streamResult);
	}

	public static void main(String[] args) throws FileNotFoundException, ManagerBeanException, TransformerException, ParserConfigurationException {
		AmortizationTypeExporter exp = new AmortizationTypeExporter();
		FileOutputStream fos = new FileOutputStream("/tmp/amortizationType.xml");
		exp.export(fos);
	}
}
