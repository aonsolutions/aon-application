package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.common.ICommonMessages.ACTIVE;
import static com.code.aon.ui.common.ICommonMessages.ADDRESS;
import static com.code.aon.ui.common.ICommonMessages.ALIAS;
import static com.code.aon.ui.common.ICommonMessages.BANK;
import static com.code.aon.ui.common.ICommonMessages.BANK_ACCOUNT;
import static com.code.aon.ui.common.ICommonMessages.BLOCKED;
import static com.code.aon.ui.common.ICommonMessages.CELLULAR;
import static com.code.aon.ui.common.ICommonMessages.COMMENT;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_NAME;
import static com.code.aon.ui.common.ICommonMessages.DATE;
import static com.code.aon.ui.common.ICommonMessages.DOCUMENT;
import static com.code.aon.ui.common.ICommonMessages.ENTITY;
import static com.code.aon.ui.common.ICommonMessages.FAX;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_ACCOUNT;
import static com.code.aon.ui.common.ICommonMessages.ID;
import static com.code.aon.ui.common.ICommonMessages.INACTIVE;
import static com.code.aon.ui.common.ICommonMessages.PAY_METHOD;
import static com.code.aon.ui.common.ICommonMessages.PHONE;
import static com.code.aon.ui.common.ICommonMessages.POSTAL_CODE;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_CITY;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_DAYS_BETWEEN_PAYMENTS;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_DAYS_TO_FIRST_PAYMENT;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_DOCUMENT_ERROR;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_EMAIL;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_NATIONALITY;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_NUMBER_OF_PAYMENTS;
import static com.code.aon.ui.common.ICommonMessages.REGISTRY_PAYMENT_DAYS;
import static com.code.aon.ui.common.ICommonMessages.STATE;
import static com.code.aon.ui.common.ICommonMessages.STATUS;
import static com.code.aon.ui.common.ICommonMessages.WEB;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.persistence.Table;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.Person;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectActivity;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Category;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.RegistrySeller;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ReportExporter;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.event.RegistryFormListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.master.TargetDB;

public class RegistryController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(RegistryController.class);
	
	private String selectedTab;
	private boolean showNewFeeWindow;
	private boolean showNewProjectWindow;
	private boolean showNewNoteWindow;
	private boolean showNewRelationshipWindow;
	private boolean showNewRecordDataWindow;
	private boolean showNewSegmentWindow;
	private boolean showNewAddInfoWindow;
	private boolean showNewDirStaffWindow;
	private boolean showNewRegistryItemWindow;
	private boolean showNewRegistryTaxWindow;
	private boolean showNewRegistryProfileWindow;
	private boolean showNewRegistrySellerWindow;
	private boolean showNewRegistrySupplierWindow;
	private boolean showNewDocumentWindow;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public boolean isShowNewFeeWindow() {
		return showNewFeeWindow;
	}

	public void setShowNewFeeWindow(boolean showNewFeeWindow) {
		this.showNewFeeWindow = showNewFeeWindow;
	}

	public boolean isShowNewNoteWindow() {
		return showNewNoteWindow;
	}

	public void setShowNewNoteWindow(boolean showNewNoteWindow) {
		this.showNewNoteWindow = showNewNoteWindow;
	}

	public boolean isShowNewRecordDataWindow() {
		return showNewRecordDataWindow;
	}

	public boolean isShowNewRelationshipWindow() {
		return showNewRelationshipWindow;
	}

	public void setShowNewRelationshipWindow(boolean showNewRelationshipWindow) {
		this.showNewRelationshipWindow = showNewRelationshipWindow;
	}

	public void setShowNewRecordDataWindow(boolean showNewRecordDataWindow) {
		this.showNewRecordDataWindow = showNewRecordDataWindow;
	}

	public boolean isShowNewSegmentWindow() {
		return showNewSegmentWindow;
	}

	public void setShowNewSegmentWindow(boolean showNewSegmentWindow) {
		this.showNewSegmentWindow = showNewSegmentWindow;
	}

	public boolean isShowNewAddInfoWindow() {
		return showNewAddInfoWindow;
	}

	public void setShowNewAddInfoWindow(boolean showNewAddInfoWindow) {
		this.showNewAddInfoWindow = showNewAddInfoWindow;
	}

	public boolean isShowNewDirStaffWindow() {
		return showNewDirStaffWindow;
	}

	public void setShowNewDirStaffWindow(boolean showNewDirStaffWindow) {
		this.showNewDirStaffWindow = showNewDirStaffWindow;
	}

	public boolean isShowNewProjectWindow() {
		return showNewProjectWindow;
	}

	public void setShowNewProjectWindow(boolean showNewProjectWindow) {
		this.showNewProjectWindow = showNewProjectWindow;
	}
	
	public boolean isShowNewRegistryItemWindow() {
		return showNewRegistryItemWindow;
	}

	public void setShowNewRegistryItemWindow(boolean showNewRegistryItemWindow) {
		this.showNewRegistryItemWindow = showNewRegistryItemWindow;
	}

	public boolean isShowNewRegistryTaxWindow() {
		return showNewRegistryTaxWindow;
	}

	public void setShowNewRegistryTaxWindow(boolean showNewRegistryTaxWindow) {
		this.showNewRegistryTaxWindow = showNewRegistryTaxWindow;
	}

	public boolean isShowNewRegistryProfileWindow() {
		return showNewRegistryProfileWindow;
	}

	public void setShowNewRegistryProfileWindow(boolean showNewRegistryProfileWindow) {
		this.showNewRegistryProfileWindow = showNewRegistryProfileWindow;
	}

	public boolean isShowNewRegistrySellerWindow() {
		return showNewRegistrySellerWindow;
	}

	public void setShowNewRegistrySellerWindow(boolean showNewRegistrySellerWindow) {
		this.showNewRegistrySellerWindow = showNewRegistrySellerWindow;
	}

	public boolean isShowNewRegistrySupplierWindow() {
		return showNewRegistrySupplierWindow;
	}

	public void setShowNewRegistrySupplierWindow(
			boolean showNewRegistrySupplierWindow) {
		this.showNewRegistrySupplierWindow = showNewRegistrySupplierWindow;
	}

	public boolean isShowNewDocumentWindow() {
		return showNewDocumentWindow;
	}

	public void setShowNewDocumentWindow(boolean showNewDocumentWindow) {
		this.showNewDocumentWindow = showNewDocumentWindow;
	}

	public boolean isNaturalType() {
		IRegistry iRegistry = (IRegistry) getTo();
		return iRegistry.getRegistry().getType().equals(RegistryType.NATURAL);
	}

	public void onChangeRegistryType(ActionEvent event) {
		IRegistry iRegistry = (IRegistry) getTo();
		iRegistry.getRegistry().setDocumentType(iRegistry.getRegistry().getType() == RegistryType.LEGAL ? DocumentType.CIF : DocumentType.NIF);
	}
	
	public void onChangeDocument(ActionEvent event) {
		IRegistry iRegistry = (IRegistry) getTo();
		iRegistry.getRegistry().setType(iRegistry.getRegistry().getDocumentType() == DocumentType.CIF ? RegistryType.LEGAL : RegistryType.NATURAL);

		try {
			if (isNevv()) {
				RegistryController.validateDocument(iRegistry, getManagerBean());
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("unable to check Document.",e);
		}
	}
	
	public static void validateDocument(IRegistry iRegistry, IManagerBean bean) throws ManagerBeanException {
		validateDocument(iRegistry.getRegistry(), ClassUtils.getShortClassName(bean.getPOJOClass()) + ".registry", bean);
	}
	
	public static void validateDocument(Registry registry, String preffix, IManagerBean bean) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(registry.getDocument())) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(preffix + ".document", registry.getDocument());
			criteria.addEqualExpression(preffix + ".documentCountry", registry.getDocumentCountry());
			List<ITransferObject> list = bean.getList(criteria);
			if (list.size() > 0 ) {
				String msg = AonUtil.getMessage(REGISTRY_DOCUMENT_ERROR); 
				AonUtil.addWarningMessage(msg + " " + registry.getDocument());
			}
		}
	}
	
	public void onLoadGeozone(ActionEvent event){ 
		RegistryFormListener registryForm = (RegistryFormListener) AonUtil.getRegisteredBean(this.getBeanName()+"Form"); 
		RegistryAddress address = registryForm.getMainAddress();
		if(address!=null && address.getZip()!=null) {
			address.loadGeoZoneByZip();
		}
	}

	public void onDetailReport(ActionEvent event){
		Class<?> pojoClass = null;
		try {
			pojoClass = (Class<?>) Class.forName( getPojo() );
		} catch (ClassNotFoundException e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			
			FacesContext faces = FacesContext.getCurrentInstance();
			String select = "SELECT"
			+ getDetailColumns(pojoClass)
			+ getSqlTables(pojoClass);
			String where = getSqlCriteria(pojoClass);
			select = select + " " + where;
			int i = StringUtils.indexOfIgnoreCase(select, " order by ");
			if (i == -1) {
				select = select + " GROUP BY `" + AonUtil.getMessage(ID) + "`";	
			} else {
				select = select.substring(0,i) + " GROUP BY `" + AonUtil.getMessage(ID) + "` " + select.substring(i+1);
			}
			ps = conn.prepareStatement(select);
			ReportExporter rm = new ReportExporter();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "Listado Detallado";
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
			rm.run2Excel(ps, output);
			response.flushBuffer();
			faces.responseComplete();
		} catch (SQLException e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ReportException e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (IOException e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	private String getSqlTables(Class<?> pojoClass) {
		Table table = pojoClass.getAnnotation(Table.class);
		String masterTable = table.name();
		String scopeJoin = " INNER JOIN scope scp ON c.scope = scp.id";
		String accountJoin = " LEFT OUTER JOIN account acc ON c.account = acc.id";
		String sellerJoin = " LEFT OUTER JOIN rseller rsl ON rsl.registry = r.id"
				+" LEFT OUTER JOIN seller sl ON rsl.seller = sl.registry";
		String personJoin = " INNER JOIN person p ON c.registry = p.registry";
		String rnoteJoin = " INNER JOIN rnote rn ON c.registry = rn.registry AND rn.note_type = "+NoteType.OBSERVATION.ordinal();
		return " FROM " + masterTable +" c"
			+" INNER JOIN registry r ON r.id = c.registry"
			+ (!"com.code.aon.seller.Seller".equals(getPojo())?scopeJoin:"")
			+ ((IAccount.class.isAssignableFrom(pojoClass))?accountJoin:"")
			+" LEFT OUTER JOIN rmedia rm ON r.id = rm.registry"
			+" LEFT OUTER JOIN raddress ra ON r.id = ra.registry AND ra.type = 0"
			+" LEFT OUTER JOIN geozone gz ON ra.geozone = gz.id"
			+" LEFT OUTER JOIN rpaymethod rpm ON rpm.registry = r.id"
			+" LEFT OUTER JOIN pay_method pm ON rpm.pay_method = pm.id"
			+" LEFT OUTER JOIN rbank rb ON rpm.rbank = rb.id"
			+" LEFT OUTER JOIN rsegment rs ON rs.registry = r.id"
			+" LEFT OUTER JOIN segment s ON rs.segment = s.id"
			+" LEFT OUTER JOIN rattach cd ON cd.registry = r.id"
			+(!"com.code.aon.seller.Seller".equals(getPojo())?sellerJoin:"")
			+ ("com.code.aon.customer.Customer".equals(getPojo())?personJoin:"")
			+ ("com.code.aon.customer.Customer".equals(getPojo())?rnoteJoin:"")
			+" LEFT OUTER JOIN category cdc ON cd.category = cdc.id";
	}

	private String getDetailColumns(Class<?> pojoClass) {
		String accountColumn = ",acc.code `" + AonUtil.getMessage(FINANCE_ACCOUNT) + "`";
		return " r.id `" + AonUtil.getMessage(ID) + "`"
		+",ELT(c.status+1"
		+",'"+AonUtil.getMessage(ACTIVE)+"'"
		+",'"+AonUtil.getMessage(INACTIVE)+"'"
		+",'"+AonUtil.getMessage(BLOCKED)+"'"
			+") `" + AonUtil.getMessage(STATUS) + "`"
		+",ELT(r.type+1" 
			+",'"+RegistryType.NATURAL.getName(AonUtil.getCurrentLocale())+"'"			
			+",'"+RegistryType.LEGAL.getName(AonUtil.getCurrentLocale())+"'"
		 	+") `" + AonUtil.getMessage(ENTITY) + "`"
		+",CAST( CONCAT_WS('/',"
		+"ELT(r.document_type+1" 
			+",'"+DocumentType.NIF.getName(AonUtil.getCurrentLocale())+"'"
			+",'"+DocumentType.CIF.getName(AonUtil.getCurrentLocale())+"'"
			+",'"+DocumentType.NIE.getName(AonUtil.getCurrentLocale())+"'"
			+",'"+DocumentType.PASSPORT.getName(AonUtil.getCurrentLocale())+"'"
			+",'"+DocumentType.WORK_PERMIT.getName(AonUtil.getCurrentLocale())+"'"
			+",'"+DocumentType.COMMUNITY_CARD.getName(AonUtil.getCurrentLocale())+"'"
			+",'"+DocumentType.OTHER.getName(AonUtil.getCurrentLocale())+"'"
		 	+")" 
		+",r.document_country,r.document) AS CHAR) `" + AonUtil.getMessage(DOCUMENT) + "`"
	 	+",r.name `" + AonUtil.getMessage(COMPANY_NAME) + "`"
		+",r.alias `" + AonUtil.getMessage(ALIAS) + "`"
		+ ((IAccount.class.isAssignableFrom(pojoClass))?accountColumn:"")		
		+",r.nationality `" + AonUtil.getMessage(REGISTRY_NATIONALITY) + "`"
		+",CAST( CONCAT_WS(' ',ra.street_type,ra.address,ra.number,ra.address2,ra.address3) AS CHAR) `" + AonUtil.getMessage(ADDRESS) + "`"
		+",ra.city `" + AonUtil.getMessage(REGISTRY_CITY) + "`"
		+",ra.zip `" + AonUtil.getMessage(POSTAL_CODE) + "`"
		+",gz.code `Id " + AonUtil.getMessage(STATE) + "`"
		+",gz.name `" + AonUtil.getMessage(STATE) + "`"
		+",(SELECT rm1.value FROM rmedia rm1 WHERE r.id = rm1.registry  AND rm1.media = 1 LIMIT 1) `" + AonUtil.getMessage(PHONE) + "`"
		+",(SELECT rm2.value FROM rmedia rm2 WHERE r.id = rm2.registry  AND rm2.media = 2 LIMIT 1) `" + AonUtil.getMessage(CELLULAR) + "`"
		+",(SELECT rm3.value FROM rmedia rm3 WHERE r.id = rm3.registry  AND rm3.media = 3 LIMIT 1) `" + AonUtil.getMessage(FAX) + "`"
		+",(SELECT rm4.value FROM rmedia rm4 WHERE r.id = rm4.registry  AND rm4.media = 4 LIMIT 1) `" + AonUtil.getMessage(REGISTRY_EMAIL) + "`"
		+",(SELECT rm5.value FROM rmedia rm5 WHERE r.id = rm5.registry  AND rm5.media = 5 LIMIT 1) `" + AonUtil.getMessage(WEB) + "`"
		+",pm.name `" + AonUtil.getMessage(PAY_METHOD) + "`"
		+",rb.alias `" + AonUtil.getMessage(BANK) + "`"
		+",rb.bank_account `" + AonUtil.getMessage(BANK_ACCOUNT) + "`"
		+",rpm.number_of_pymnts `" + AonUtil.getMessage(REGISTRY_NUMBER_OF_PAYMENTS) + "`"
		+",rpm.days_to_first_pymnt `" + AonUtil.getMessage(REGISTRY_DAYS_TO_FIRST_PAYMENT) + "`"
		+",rpm.days_between_pymnts `" + AonUtil.getMessage(REGISTRY_DAYS_BETWEEN_PAYMENTS) + "`"
		+",rpm.pymnt_days `" + AonUtil.getMessage(REGISTRY_PAYMENT_DAYS) + "`"
		+("com.code.aon.customer.Customer".equals(getPojo())?",p.birth_date `" + AonUtil.getMessage(DATE) + "`":"")
		+("com.code.aon.customer.Customer".equals(getPojo())?",rn.comments `" + AonUtil.getMessage(COMMENT) + "`":"")
		;
	}

	private String getSqlCriteria(Class<?> pojoClass) throws ManagerBeanException {
		String mappingPrefix = pojoClass.getSimpleName();
		Map<String,String> tableMapping = new HashMap<String, String>();
		tableMapping.put(mappingPrefix, "c");
		tableMapping.put(mappingPrefix + ".scope", "scp");
		tableMapping.put(mappingPrefix + ".registry", "r");
		tableMapping.put(mappingPrefix + ".registry.addresses", "ra");
		tableMapping.put(mappingPrefix + ".registry.medias", "rm");
		tableMapping.put(mappingPrefix + ".registry.segments", "rs");
		tableMapping.put(mappingPrefix + ".registry.segments.segment", "s");
		tableMapping.put(mappingPrefix + ".registry.addresses.geozone", "gz");
		tableMapping.put(mappingPrefix + ".registry.payMethods", "rpm");
		tableMapping.put(mappingPrefix + ".registry.payMethods.payment", "pm");
		if(!"com.code.aon.seller.Seller".equals(getPojo())){
			tableMapping.put(mappingPrefix + ".sellers", "rsl");
			tableMapping.put(mappingPrefix + ".sellers.seller", "sl");
		}
		tableMapping.put(mappingPrefix + ".documents", "cd");
		tableMapping.put(mappingPrefix + ".documents.category", "cdc");
		tableMapping.put(mappingPrefix + ".account", "acc");
		if("com.code.aon.customer.Customer".equals(getPojo())){
			tableMapping.put(mappingPrefix + ".person", "p");
			tableMapping.put(mappingPrefix + ".rnote", "rnp");
		}
		tableMapping.put("Project", "project");
		tableMapping.put("ProjectActivity", "project_activity");
		
		Map<String,Class<?>> pojoMapping = new HashMap<String, Class<?>>();
		pojoMapping.put(mappingPrefix, pojoClass);
		pojoMapping.put(mappingPrefix + ".scope", Scope.class);
		pojoMapping.put(mappingPrefix + ".registry", Registry.class);
		pojoMapping.put(mappingPrefix + ".registry.medias", RegistryMedia.class);
		pojoMapping.put(mappingPrefix + ".registry.segments", RegistrySegment.class);
		pojoMapping.put(mappingPrefix + ".registry.segments.segment", Segment.class);
		pojoMapping.put(mappingPrefix + ".registry.addresses", RegistryAddress.class);
		pojoMapping.put(mappingPrefix + ".registry.addresses.geozone", GeoZone.class);
		pojoMapping.put(mappingPrefix + ".registry.payMethods", RegistryPayMethod.class);
		pojoMapping.put(mappingPrefix + ".registry.payMethods.payment", PayMethod.class);
		if(!"com.code.aon.seller.Seller".equals(getPojo())){
			pojoMapping.put(mappingPrefix + ".sellers", RegistrySeller.class);
			try {
				pojoMapping.put(mappingPrefix + ".sellers.seller", Class.forName("com.code.aon.seller.Seller"));
			} catch (ClassNotFoundException e) {
				String msg = "Unable to map pojo: com.code.aon.seller.Seller";
				LOGGER.error(msg, e);
			}
		}
		pojoMapping.put(mappingPrefix + ".documents", RegistryAttachment.class);
		pojoMapping.put(mappingPrefix + ".documents.category", Category.class);
		pojoMapping.put(mappingPrefix + ".account", Account.class);
		if("com.code.aon.customer.Customer".equals(getPojo())){
			pojoMapping.put(mappingPrefix + ".person", Person.class);
			pojoMapping.put(mappingPrefix + ".rnote", RegistryNote.class);
		}
		pojoMapping.put("Project", Project.class);
		pojoMapping.put("ProjectActivity", ProjectActivity.class);

		return CriteriaUtilities.toSQLString(getCriteria(), true, pojoMapping, tableMapping);
	}

	@Override
	public Collection<ITransferObject> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	@Override
	public Collection<ITransferObject> getCollection() {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Registry.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			Class<?> pojoClass = (Class<?>) Class.forName( getPojo() );
			String select = "SELECT "
					+"c.registry" 
					+",r.document" 
					+",r.name" 
					+",(SELECT rm1.value FROM rmedia rm1 WHERE r.id = rm1.registry  AND rm1.media = 1 LIMIT 1) phone" 
					+",r.alias" 
					+",ELT(c.status+1"
					+",'"+AonUtil.getMessage(ACTIVE)+"'"
					+",'"+AonUtil.getMessage(INACTIVE)+"'"
					+",'"+AonUtil.getMessage(BLOCKED)+"'"
					+") "
					
					+((!TargetDB.class.isAssignableFrom(pojoClass))?"":(",ELT(c.advertising+1"+",'"+Advertising.ALLOWED.getName(AonUtil.getCurrentLocale())+"'"+",'"+Advertising.AUTO_EXCLUSION.getName(AonUtil.getCurrentLocale())+"'"+",'"+Advertising.DENIED.getName(AonUtil.getCurrentLocale())+"'"+",'"+Advertising.ROBINSON.getName(AonUtil.getCurrentLocale())+"'"+") "))
					+ getSqlTables(pojoClass);
			String where = getSqlCriteria(pojoClass);
			select = select + " " + where;
			
			if (TargetDB.class.isAssignableFrom(pojoClass)) {
				// Se chequea el caso especial del @Formula que hay en Target para saber si es cliente o no.
				select = StringUtils.replace(select, "c.customer = 'true'", "(1 IN (SELECT 1 FROM customer WHERE registry = c.registry))");
				select = StringUtils.replace(select, "c.customer = 'false'", "(1 NOT IN (SELECT 1 FROM customer WHERE registry = c.registry))");
			}

			int i = StringUtils.indexOfIgnoreCase(select, " order by ");
			if (i == -1) {
				select = select + " GROUP BY c.registry";	
			} else {
				select = select.substring(0,i) + " GROUP BY c.registry " + select.substring(i+1);
			}
			System.out.println(" ----------------------------- ");
			System.out.println( select );
			System.out.println(" ----------------------------- ");
			Query query = session.createSQLQuery(select);
			query.setReadOnly(true);
			
			List<?> list = query.list();
			List<ITransferObject> registries = new LinkedList<ITransferObject>();
			for (Object obj : list) {
				Object[] o = (Object[]) obj;
				Integer id = (Integer) o[0];
				String document = (String) o[1];
				String name= (String) o[2];
				String phone= (String) o[3];
				String alias= (String) o[4];
				String status= (String) o[5];
				String advertising = null;
				if (TargetDB.class.isAssignableFrom(pojoClass)) {
					advertising = (String) o[6];	
				}
				ITransferObject rr = new RegistryReport(id, document, name,phone, alias, status, advertising);
				registries.add(rr);
			}
			list = null;
			HibernateUtil.commitTransaction(sessionName);
			return registries;
		} catch (Exception e) {
			AonUtil.addErrorMessage(e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "Error durante la carga de datos. ";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg  + e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
}
