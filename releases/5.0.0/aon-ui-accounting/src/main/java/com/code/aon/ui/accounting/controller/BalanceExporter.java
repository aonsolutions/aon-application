package com.code.aon.ui.accounting.controller;

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

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class BalanceExporter {
	private static final String ROOT = "balances";
	private static final String BALANCE = "balance";
	
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String REMOVABLE = "removable";
	private static final String TYPE = "type";
	private static final String DETAILS = "details";
	
	//DETAIL
	private static final String DETAIL = "detail";
	private static final String LINE_ID = "id";	
	private static final String LINE_BALANCE = "balance";
	private static final String LINE_CODE = "code";
	private static final String LINE_DESCRIPTION = "description";
	private static final String LINE_ACCOUNTS = "accounts";
	private static final String LINE_SORT_KEY = "sortKey";
	private static final String LINE_TITLE = "title";
	private static final String LINE_INTERNAL_CALCULATION = "internalCalculation";
	private static final String LINE_VISIBLE = "visible";
	private static final String LINE_ZERO_FLAG = "zeroFlag";
	private static final String LINE_CREDIT_NATURE = "creditNature";
		
	public void onExport(ActionEvent event) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			OutputStream out = res.getOutputStream();
			res.setContentType("text/xml");
			res.setHeader("Content-Disposition", "attachment; filename=\"balance.xml\";");
			export(out);
			ctx.responseComplete();
		} catch (ManagerBeanException e) {
			String message = "Imposible realizar la exportación de los balances";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (ParserConfigurationException e) {
			String message = "Imposible realizar la exportación de los balances";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (TransformerConfigurationException e) {
			String message = "Imposible realizar la exportación de los balances";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (TransformerException e) {
			String message = "Imposible realizar la exportación de los balances";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		} catch (IOException e) {
			String message = "Imposible realizar la exportación de los balances";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
	}

	public void export(OutputStream out) throws TransformerException, ManagerBeanException, ParserConfigurationException {
		IManagerBean bean = BeanManager.getManagerBean(Balance.class);
		IManagerBean beanDetail = BeanManager.getManagerBean(BalanceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IAccountingAlias.BALANCE_ID));
		Criteria criteriaDetail;
		List<ITransferObject> list = bean.getList(criteria);
		List<ITransferObject> listDetail;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document xmldoc = builder.newDocument();
		Element root = xmldoc.createElement(ROOT);
		xmldoc.appendChild(root);
		for (ITransferObject bal : list) {
			Balance b = (Balance)bal;
			
			Element balance = xmldoc.createElement(BALANCE);
			
			Element id = xmldoc.createElement(ID);
			id.appendChild(xmldoc.createTextNode(b.getId().toString()));
			balance.appendChild(id);
			
			Element name = xmldoc.createElement(NAME);
			if (b.getName() != null) {
				name.appendChild(xmldoc.createCDATASection(b.getName()));
			}
			balance.appendChild(name);
			
			Element removable = xmldoc.createElement(REMOVABLE);
			removable.appendChild(xmldoc.createTextNode(String.valueOf(b.isRemovable())));
			balance.appendChild(removable);
			
			Element type = xmldoc.createElement(TYPE);
			if (b.getType() != null) {
				type.appendChild(xmldoc.createTextNode(b.getType().toString()));
			}
			balance.appendChild(type);

			Element details = xmldoc.createElement(DETAILS);
			
			criteriaDetail = new Criteria();
			criteriaDetail.addOrder(beanDetail.getFieldName(IAccountingAlias.BALANCE_DETAIL_ID));
			criteriaDetail.addEqualExpression(beanDetail.getFieldName(IAccountingAlias.BALANCE_DETAIL_BALANCE_ID), b.getId());
			listDetail = beanDetail.getList(criteriaDetail);
			for (ITransferObject det : listDetail) {
				BalanceDetail d = (BalanceDetail) det;
				
				Element detail = xmldoc.createElement(DETAIL);
				details.appendChild(detail);

				Element lineId = xmldoc.createElement(LINE_ID);
				lineId.appendChild(xmldoc.createTextNode(d.getId().toString()));
				detail.appendChild(lineId);
				
				Element lineBalance = xmldoc.createElement(LINE_BALANCE);
				lineBalance.appendChild(xmldoc.createTextNode(d.getBalance().getId().toString()));
				detail.appendChild(lineBalance);
				
				Element lineCode = xmldoc.createElement(LINE_CODE);
				lineCode.appendChild(xmldoc.createCDATASection(d.getCode()));
				detail.appendChild(lineCode);
				
				Element lineDescription = xmldoc.createElement(LINE_DESCRIPTION);
				lineDescription.appendChild(xmldoc.createCDATASection(d.getDescription()));
				detail.appendChild(lineDescription);
				
				Element lineAccounts = xmldoc.createElement(LINE_ACCOUNTS);
				lineAccounts.appendChild(xmldoc.createCDATASection(d.getAccounts()));
				detail.appendChild(lineAccounts);
				
				Element lineSortKey = xmldoc.createElement(LINE_SORT_KEY);
				lineSortKey.appendChild(xmldoc.createTextNode(d.getSortKey().toString()));
				detail.appendChild(lineSortKey);
				
				Element lineTitle = xmldoc.createElement(LINE_TITLE);
				lineTitle.appendChild(xmldoc.createTextNode(String.valueOf(d.isTitle())));
				detail.appendChild(lineTitle);
				
				Element lineInternalCalculation = xmldoc.createElement(LINE_INTERNAL_CALCULATION);
				lineInternalCalculation.appendChild(xmldoc.createTextNode(String.valueOf(d.isInternalCalculation())));
				detail.appendChild(lineInternalCalculation);
				
				Element lineVisible = xmldoc.createElement(LINE_VISIBLE);
				lineVisible.appendChild(xmldoc.createTextNode(String.valueOf(d.isVisible())));
				detail.appendChild(lineVisible);
				
				Element lineZeroFlag = xmldoc.createElement(LINE_ZERO_FLAG);
				lineZeroFlag.appendChild(xmldoc.createTextNode(String.valueOf(d.isZeroFlag())));
				detail.appendChild(lineZeroFlag);
				
				Element lineCreditNature = xmldoc.createElement(LINE_CREDIT_NATURE);
				lineCreditNature.appendChild(xmldoc.createTextNode(String.valueOf(d.isCreditNature())));
				detail.appendChild(lineCreditNature);
			}
			balance.appendChild(details);
			root.appendChild(balance);
		}

		DOMSource domSource = new DOMSource(xmldoc);
		StreamResult streamResult = new StreamResult(out);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.transform(domSource, streamResult);
	}
}
