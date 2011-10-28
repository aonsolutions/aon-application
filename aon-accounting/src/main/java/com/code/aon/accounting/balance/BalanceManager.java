package com.code.aon.accounting.balance;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;

public class BalanceManager {
	
	private static final String EMPTY = "";
	private static final String SPACE = " ";
	private static final String HYPHEN = "-";
	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";
	private static final String PIPE = "|";
	private static final String ASTERISK = "*";
	private static final String QUESTION_MARK = "?";
	private static final String COMMA = ",";
	private static final String HTTP = "http://www.";
	private static final String DOTCOM = ".com";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String EXT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
	private static final SimpleDateFormat ISO8601Local = new SimpleDateFormat(EXT_DATE_FORMAT);
	
	// XML TAGS
	private static final String ROOT_NAMESPACE = "http://www.inteco.es/xbrl/pgc07/interfazES";
	private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String ENCODING = "utf-8";
	private static final String ROOT = "report";
	private static final String ID = "id";
	private static final String MODULE_ID = "pgc07abreviado";
	private static final String VALUE = "value";
	private static final String DATE = "date";
	private static final String XSI = "xsi";
	private static final String SCHEMA_LOCATION = "schemaLocation";
	private static final String SCHEMA_LOCATION_VALUE = "http://www.inteco.es/xbrl/pgc07/interfazES http://www.inteco.es/xbrl/pgc07/interfazES/pgc07-io-interface.xsd";
	private static final String ENTITY = "entity";
	private static final String URI = "uri";
	private static final String MODULE = "module";
	private static final String REPORTING_DATE_START = "reportingDateStart";
	private static final String REPORTING_DATE_END = "reportingDateEnd";
	private static final String BASE_UNIT = "baseUnit";
	private static final String EURO = "euro";
	private static final String BASE_DECIMALS = "baseDecimals";
	private static final String ZERO = "0";
	private static final String ITEM = "item";
	private static final String SIGN = "sign";
	private static final String PLUS = "+";
	private static final String MINUS = "-";
	private static final String RECORD = "record";

	
	private List<BalanceItem> list;
	private Company company;
	
	private Company getCompany() {
		try {
			if (company == null) {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
				Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
				if (iter.hasNext()) {
					company = (Company) iter.next();
				}
			}
		} catch (ManagerBeanException e) {
			company = null;
		}
		return company;
	}
	
	private String getCompanyURI() {
		StringBuffer buf = new StringBuffer(HTTP);
		buf.append( StringUtils.replace(getCompany().getFullName(), SPACE,EMPTY));
		buf.append( HYPHEN );
		buf.append( getCompany().getDocument() );
		buf.append( DOTCOM );
		return buf.toString();
	}

	public List<BalanceItem> getList() {
		return list;
	}
	public void setList(List<BalanceItem> list) {
		this.list = list;
	}

	public List<BalanceItem> getBalanceCollection(SummaryProviderParameters parameters,Balance balance) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			dumpTable(balance);
			resolveTable(parameters,balance);
			cleanTable();

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return list;

		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				throw new ManagerBeanException(daoe.getMessage(),daoe);	
			}
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}

				
	}

	private void dumpTable(Balance balance) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(BalanceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IAccountingAlias.BALANCE_DETAIL_BALANCE_ID),
				balance.getId());
		criteria.addOrder(bean.getFieldName(IAccountingAlias.BALANCE_DETAIL_SORT_KEY));
		List<ITransferObject> balanceDetailList = bean.getList(criteria);
		list = new LinkedList<BalanceItem>();
		for (ITransferObject to : balanceDetailList) {
			BalanceDetail bd = (BalanceDetail) to;
			BalanceItem b = new BalanceItem();
			b.setDetail(bd);
			list.add(b);
		}
		
	}

	private void resolveTable(SummaryProviderParameters parameters,Balance balance) throws ManagerBeanException {
		try {
			SummaryProviderParameters previous = null;
			if (parameters.isPreviousPeriodVisible()) {
				previous = parameters.clone();
				changeParameters(previous);
				parameters.setPreviousPeriodVisible(previous.isPreviousPeriodVisible());
			}
			for (BalanceItem item: list) {
				if (!item.isResolved()) {
					resolveItem(item,parameters,previous);
				}
			}
		} catch (CloneNotSupportedException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
		
	}

	private void cleanTable() {
		List<BalanceItem> removableItems = new LinkedList<BalanceItem>();
		for (BalanceItem item: list) {
			if (!item.isVisible()) {
				removableItems.add(item);
			} else if (item.getDetail().isZeroFlag() && (item.getAmount() == null || CommonUtil.round(item.getAmount()) == 0.0)) {
				removableItems.add(item);
			}
		}
		list.removeAll(removableItems);
	}

	private void resolveItem(BalanceItem item,SummaryProviderParameters parameters,SummaryProviderParameters previous) throws ManagerBeanException {
		BalanceDetail bd = item.getDetail();
		if (StringUtils.isNotBlank(bd.getAccounts())) {
			if (bd.isInternalCalculation()) {
				resolveInternalCalculation(item,parameters,previous);
			} else {
				resolveExternalCalculation(item,parameters,previous);
			}
		}
	}

	private void resolveInternalCalculation(BalanceItem item, SummaryProviderParameters parameters, SummaryProviderParameters previous) throws ManagerBeanException {
		BalanceDetail bd = item.getDetail();
		String[] tokens = StringUtils.split(bd.getAccounts(),COMMA);
		for (String token:tokens) {
			token = token.trim();
			if (StringUtils.isNotBlank(token)) {
				String t = token;
				boolean negative = false;
				if (token.startsWith(OPEN_BRACKET) && token.endsWith(CLOSE_BRACKET)) {
					t = token.replace(OPEN_BRACKET, EMPTY).replace(CLOSE_BRACKET, EMPTY);
					negative = true;
				}
				BalanceItem bi = searchItem( t );
				if (!bi.isResolved()) {
					resolveItem(bi,parameters,previous);	
				}
				if (negative) {
					item.subtract(bi);	
				} else {
					item.add(bi);
				}
			}
		}
	}

	private void resolveExternalCalculation(BalanceItem item, SummaryProviderParameters parameters, SummaryProviderParameters previous) throws ManagerBeanException {
		BalanceDetail bd = item.getDetail();
		String[] tokens = StringUtils.split(bd.getAccounts(),COMMA);
		StringBuilder positiveExp = new StringBuilder();
		List<String> conditionalPositiveExp = new LinkedList<String>();
		StringBuilder negativeExp = new StringBuilder();
		List<String> conditionalNegativeExp = new LinkedList<String>();
		for (String token:tokens) {
			token = token.trim();
			if (StringUtils.isNotBlank(token)) {
				StringBuilder tmpExp = positiveExp;
				List<String> tmpConditionalExp = conditionalPositiveExp;
				if (token.startsWith(OPEN_BRACKET) && token.endsWith(CLOSE_BRACKET)) {
					token = token.replace(OPEN_BRACKET, EMPTY).replace(CLOSE_BRACKET, EMPTY);
					tmpExp = negativeExp;
					tmpConditionalExp = conditionalNegativeExp;
				}
				if (token.startsWith(QUESTION_MARK)) {
					String t = token.replace(QUESTION_MARK, EMPTY);
					tmpConditionalExp.add(t+ASTERISK);
				} else {
					tmpExp.append(tmpExp.length()>0?PIPE:EMPTY);
					tmpExp.append(token);	
					tmpExp.append(ASTERISK);
				}
			}
		}
		Double pAmount = new Double(0.0);
		Double pPreviousAmount = new Double(0.0);
		if (positiveExp.length() > 0) {
			parameters.setAccountExpression(positiveExp.toString());
			if (parameters.isPreviousPeriodVisible()) {
				previous.setAccountExpression(positiveExp.toString());	
			}
			pAmount = getAccountsAmount(parameters,bd.isCreditNature());
			if (parameters.isPreviousPeriodVisible()) {
				pPreviousAmount = getAccountsAmount(previous,bd.isCreditNature());
			}
		}
		if ( conditionalPositiveExp.size() > 0 ) {
			for (String exp: conditionalPositiveExp ) {
				parameters.setAccountExpression(exp);
				if (parameters.isPreviousPeriodVisible()) {
					previous.setAccountExpression(exp);
				}
				Double a = getAccountsAmount(parameters,bd.isCreditNature());
				if (a > 0 ) {
					pAmount = CommonUtil.round(pAmount + a);
				}
				if (parameters.isPreviousPeriodVisible()) {
					Double p = getAccountsAmount(previous,bd.isCreditNature());
					if (p > 0 ) { 
						pPreviousAmount = CommonUtil.round(pPreviousAmount + p);
					}
				}
			}
		}
		Double nAmount = new Double(0.0);
		Double nPreviousAmount = new Double(0.0);
		if (negativeExp.length() > 0) {
			parameters.setAccountExpression(negativeExp.toString());
			if (parameters.isPreviousPeriodVisible()) {
				previous.setAccountExpression(negativeExp.toString());
			}
			nAmount = getAccountsAmount(parameters,!bd.isCreditNature());
			if (parameters.isPreviousPeriodVisible()) {
				nPreviousAmount = getAccountsAmount(previous,!bd.isCreditNature());
			}
		}
		if ( conditionalNegativeExp.size() > 0 ) {
			for (String exp: conditionalNegativeExp ) {
				parameters.setAccountExpression(exp);
				if (parameters.isPreviousPeriodVisible()) {
					previous.setAccountExpression(exp);
				}
				Double a = getAccountsAmount(parameters,!bd.isCreditNature());
				if (a > 0 ) {
					nAmount = CommonUtil.round(nAmount + a);
				}
				if (parameters.isPreviousPeriodVisible()) {
					Double p = getAccountsAmount(previous,!bd.isCreditNature());
					if (p > 0 ) { 
						nPreviousAmount = CommonUtil.round(nPreviousAmount + p);
					}
				}
			}
		}
		item.setAmount( CommonUtil.round(pAmount - nAmount ));
		item.setPreviousAmount(CommonUtil.round(pPreviousAmount - nPreviousAmount));
	}
		
	private BalanceItem searchItem(String token) throws ManagerBeanException {
		for (BalanceItem item: list) {
			if (ObjectUtils.equals(item.getDetail().getCode(), token)) {
				return item;
			}
		}
		throw new ManagerBeanException("La clave interna " + token + " no está definida");
	}

	private Double getAccountsAmount(SummaryProviderParameters params, boolean creditNature) throws ManagerBeanException {
		if (params.getPeriod() != null && params.getPeriod().getId() != null) {
			SummaryCollection summaryCollection = new SummaryCollection();
			SummaryProvider summaryProvider = new SummaryProvider();
			summaryCollection = summaryProvider.getSummaryCollection(params,false);
			if (creditNature) {
				return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getDebit());
			}
			return CommonUtil.round(summaryCollection.getDebit() - summaryCollection.getCredit());
		}
		return 0.0;
	}
	
	private void changeParameters(SummaryProviderParameters previous) throws ManagerBeanException {
		if (previous.getFromDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(previous.getFromDate());
			c.add(Calendar.YEAR, -1);
			previous.setFromDate(c.getTime());
		}
		if (previous.getToDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(previous.getToDate());
			c.add(Calendar.YEAR, -1);
			previous.setToDate(c.getTime());
		}
		if (previous.getPeriod() != null) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			Criteria criteria = new Criteria();
			String deadlineAlias = periodBean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE);
			criteria.addLessThanExpression(deadlineAlias, previous.getPeriod().getInitiationDate());
			criteria.addOrder(deadlineAlias, false);
			Iterator<ITransferObject> iter = periodBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				previous.setPeriod( (Period) iter.next());
			} else {
				previous.setPeriod( null );
				previous.setPreviousPeriodVisible(false);
			}
		}
	}
	// *******************************************
	// ******************* INTECO ****************
	// *******************************************
	public void getIntecoCover(Element root,SummaryProviderParameters parameters) throws ManagerBeanException  {
		Element cover = root.addElement(MODULE,ROOT_NAMESPACE)
			.addAttribute(ID, "apartado0")
			.addAttribute(REPORTING_DATE_START,DATE_FORMATTER.format(parameters.getPeriod().getInitiationDate()) )
			.addAttribute(REPORTING_DATE_END, DATE_FORMATTER.format(parameters.getPeriod().getDeadline()) )
			.addAttribute(BASE_UNIT, EURO)
			.addAttribute(BASE_DECIMALS, ZERO);
		Element record = cover.addElement(RECORD,ROOT_NAMESPACE).addAttribute(ID, "0100000");
		
		Element item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "0101001");
		item.addElement(VALUE,ROOT_NAMESPACE).addText("NIF");
		
		item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "01010");
		item.addElement(VALUE,ROOT_NAMESPACE).addText(getCompany().getDocument());
		
		item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "0102000");
		item.addElement(VALUE,ROOT_NAMESPACE).addText("DS");
		
		item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "01020");
		item.addElement(VALUE,ROOT_NAMESPACE).addText(getCompany().getFullName());

		item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "0102200");
		item.addElement(VALUE,ROOT_NAMESPACE).addText("01");

		if (getCompany().getDefaultAddress() != null) {
			RegistryAddress address = getCompany().getDefaultAddress();

			if (!StringUtils.isEmpty(address.getAddress())) {
				item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "01022");
				String number = address.getNumber();
				item.addElement(VALUE,ROOT_NAMESPACE).addText(getCompany().getDefaultAddress().getAddress() + (StringUtils.isEmpty(number)?"":", " + number));
			}
			
			if (!StringUtils.isEmpty(address.getCity())) {
				item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "01023");
				item.addElement(VALUE,ROOT_NAMESPACE).addText(address.getCity());
			}
			
			if (!StringUtils.isEmpty(address.getZip())) {
				item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "01024");
				item.addElement(VALUE,ROOT_NAMESPACE).addText(address.getZip());
			}

			if (address.getGeozone() != null) {
				item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "01025");
				item.addElement(VALUE,ROOT_NAMESPACE).addText(address.getGeozone().getId().toString());
			}
		}
		
		if (getCompany().getPhone() != null) {
			RegistryMedia phone = getCompany().getPhone();
			if (!StringUtils.isEmpty(phone.getValue())) {
				item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "01031");
				item.addElement(VALUE,ROOT_NAMESPACE).addText(phone.getValue());
			}
		}
		
		record = cover.addElement(RECORD,ROOT_NAMESPACE).addAttribute(ID, "0900000");
		item = record.addElement(ITEM,ROOT_NAMESPACE).addAttribute(ID, "09001");
		item.addElement(VALUE,ROOT_NAMESPACE).addText("01");
		
	}
	
	public void getIntecoBalance(Element root,SummaryProviderParameters parameters, Balance balance) throws ManagerBeanException  {
		try {
			List<BalanceItem> list = getBalanceCollection(parameters,balance);
			
			SummaryProviderParameters previousParameters = null;
			if (parameters.isPreviousPeriodVisible()) {
				previousParameters = parameters.clone();
				changeParameters(previousParameters);
				parameters.setPreviousPeriodVisible(previousParameters.isPreviousPeriodVisible());
			}
			DocumentFactory factory = DocumentFactory.getInstance();
			
			Element current = root.addElement(MODULE,ROOT_NAMESPACE)
				.addAttribute(ID, getIntecoBalanceId(balance))
				.addAttribute(REPORTING_DATE_START,DATE_FORMATTER.format(parameters.getPeriod().getInitiationDate()) )
				.addAttribute(REPORTING_DATE_END, DATE_FORMATTER.format(parameters.getPeriod().getDeadline()) )
				.addAttribute(BASE_UNIT, EURO)
				.addAttribute(BASE_DECIMALS, ZERO);
			Element previous = factory.createElement(MODULE,ROOT_NAMESPACE);
			if (parameters.isPreviousPeriodVisible()) {
				previous.addAttribute(ID, getIntecoBalanceId(balance))
				.addAttribute(REPORTING_DATE_START, DATE_FORMATTER.format(previousParameters.getPeriod().getInitiationDate() ))
				.addAttribute(REPORTING_DATE_END, DATE_FORMATTER.format(previousParameters.getPeriod().getDeadline() ))
				.addAttribute(BASE_UNIT, EURO)
				.addAttribute(BASE_DECIMALS, ZERO);
				root.add(previous);
			}
			for (BalanceItem item: list) {
				if (item.getDetail() != null && item.getAmount() != null) {
					Element currentItem = factory.createElement(ITEM,ROOT_NAMESPACE)
							.addAttribute(ID, item.getDetail().getCode())
							.addAttribute(SIGN, item.getAmount()<0?MINUS:PLUS);
					Element currentValue = factory.createElement(VALUE,ROOT_NAMESPACE)
							.addText(Double.toString(Math.abs(item.getAmount())));
					currentItem.add(currentValue);
					current.add(currentItem);
				}
				if (parameters.isPreviousPeriodVisible() && item.getDetail() != null && item.getPreviousAmount() != null) {
					Element previousItem = factory
						.createElement(ITEM,ROOT_NAMESPACE)
						.addAttribute(ID, item.getDetail().getCode())
						.addAttribute(SIGN, item.getPreviousAmount()<0?MINUS:PLUS);
					Element previousValue = factory
						.createElement(VALUE,ROOT_NAMESPACE)
						.addText(Double.toString(Math.abs(item.getPreviousAmount())));
					previousItem.add(previousValue);
					previous.add(previousItem);
				}
			}
		} catch (CloneNotSupportedException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}

	private String getIntecoBalanceId(Balance balance) {
		if (balance.getType() == BalanceType.CLOSING) {
			return "bal";
		} else if (balance.getType() == BalanceType.OPERATING) {
			return "pyg";
		} else if (balance.getType() == BalanceType.PATRIMONY) {
			// TODO
			return "patnetA";
		}
		return null;
	}

	public Element getIntecoModuleRoot(SummaryProviderParameters parameters)  {
		DocumentFactory factory = DocumentFactory.getInstance();
		Document doc = factory.createDocument( ENCODING );
		Element root = factory.createElement( ROOT );
		doc.setRootElement(root);
		root.addAttribute(ID, MODULE_ID);
		root.addAttribute(DATE, ISO8601Local.format(new Date()));
		root.addNamespace(EMPTY, ROOT_NAMESPACE);
		root.addNamespace(XSI, XSI_NAMESPACE);
		root.addAttribute(QName.get(SCHEMA_LOCATION, XSI, XSI_NAMESPACE), SCHEMA_LOCATION_VALUE);
		root.addElement(ENTITY, ROOT_NAMESPACE)
				.addAttribute(ID, getCompany().getFullName())
				.addAttribute(URI, getCompanyURI() );
		return root;		
	}

	public void writeIntecoDocument(Document doc,OutputStream out) throws ManagerBeanException {
		try {
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(doc);
			writer.flush();		
		} catch (UnsupportedEncodingException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} catch (IOException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}

	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************
	public static void main1(String[] args) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Period period = (Period) periodBean.get("2010");

		IManagerBean bean = BeanManager.getManagerBean(Balance.class);
		Balance balance = (Balance) bean.get(new Integer(3));

		BalanceManager bm = new BalanceManager();
		SummaryProviderParameters parameters = new SummaryProviderParameters();
		parameters = new SummaryProviderParameters();
		parameters.setPeriod(period);
		parameters.setFromDate(null);
		parameters.setToDate(null);
		parameters.setDate(new Date());
		parameters.setAccountExpression(null);
		parameters.setLowerLevelVisible(false);
		parameters.setNoTouchedAccountVisible(false);
		parameters.setRowsPerPage(20);
		parameters.setAccountLevel(4);
		List<BalanceItem> list = bm.getBalanceCollection(parameters, balance);
		System.out.println(  );
		System.out.println(  );
		System.out.println(  );
		System.out.println( "************** " + balance.getName() + "********************");
		for (BalanceItem item: list) {
			System.out.println(  item.toString() );
		}
	}
	
	public static void main(String[] args) throws ManagerBeanException{
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Period period = (Period) periodBean.get("2010");

		IManagerBean bean = BeanManager.getManagerBean(Balance.class);
		Balance balance = (Balance) bean.get(new Integer(3));

		BalanceManager bm = new BalanceManager();
		SummaryProviderParameters parameters = new SummaryProviderParameters();
		parameters = new SummaryProviderParameters();
		parameters.setPeriod(period);
		parameters.setFromDate(null);
		parameters.setToDate(null);
		parameters.setDate(new Date());
		parameters.setAccountExpression(null);
		parameters.setLowerLevelVisible(false);
		parameters.setNoTouchedAccountVisible(false);
		parameters.setRowsPerPage(20);
		parameters.setAccountLevel(4);
		OutputStream out = System.out;
		Element root = bm.getIntecoModuleRoot(parameters);
		bm.getIntecoCover(root,parameters);
		bm.getIntecoBalance(root,parameters, balance);
		bm.writeIntecoDocument(root.getDocument(), out);
	}

}
