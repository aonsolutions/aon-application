package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.CUSTOMER_CONTROLLER_NAME;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.NAVIGATION_TARGET_FORM;
import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET_CONTROLLER_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.persistence.Table;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.registry.Category;
import com.code.aon.registry.Question;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryProfile;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.RegistrySeller;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.report.poi.ReportExporter;
import com.code.aon.sales.bridge.util.SalesBridgeUtil;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;

public class TargetController extends RegistryController implements ICommonMessages, IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean showAuditInfoWindow;

	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		if ( getBackAction() != null ) {
			onBackActionListener(event);
		}
	}	
	
	public String getAliasPreffix() {
		return getPojoShortName();
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		checkNone(event);
	}
	
	protected Integer getId( Object o ) {
		return ((Target) o).getId();
	}
	
	public String getListReportKey() {
		return ICommercialConstants.TARGET_LIST;
	}

	public String getListDetailReportKey() {
		return ICommercialConstants.TARGET_LIST_DETAIL;
	}

	public String getListDetailExcelReportKey() {
		return ICommercialConstants.TARGET_LIST_DETAIL_EXCEL;
	}
	
	public void onLoadCustomer(ActionEvent event) throws ManagerBeanException {
		BasicController customerController = (BasicController)AonUtil.getRegisteredBean(CUSTOMER_CONTROLLER_NAME);
		customerController.onLoad(event, ((Target)getTo()).getId(), NAVIGATION_TARGET_FORM, TARGET_CONTROLLER_NAME + ".refresh");
	}

	public void onCreateCustomer(ActionEvent event) throws ManagerBeanException {
		SalesBridgeUtil salesUtil = new SalesBridgeUtil();
		Customer customer = salesUtil.createCustomer((Target)getTo());
		((Target)getTo()).setCustomer(customer.getId()!=null);
	}

	public void onDetailReport(ActionEvent event){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			
			Locale locale = AonUtil.getCurrentLocale();
			
			String mappingPrefix = Target.class.getSimpleName();
			Table table = Target.class.getAnnotation(Table.class);
			String masterTable = table.name();
			FacesContext faces = FacesContext.getCurrentInstance();
			String select = "SELECT" 
			+" r.id `" + AonUtil.getMessage(ID) + "`"
			+",ELT(c.status+1"
			+",'"+AonUtil.getMessage(ACTIVE)+"'"
			+",'"+AonUtil.getMessage(INACTIVE)+"'"
			+",'"+AonUtil.getMessage(BLOCKED)+"'"
			+") `" + AonUtil.getMessage(STATUS) + "`"
			+",(select IF(r.id > '0', 'SI', '-') from customer c where r.id = c.registry LIMIT 1) `" + AonUtil.getMessage(CUSTOMER) + "`"
				+",ELT(c.advertising+1"
				+",'"+Advertising.ALLOWED.getName(locale)+"'"
				+",'"+Advertising.AUTO_EXCLUSION.getName(locale)+"'"
				+",'"+Advertising.DENIED.getName(locale)+"'"
				+",'"+Advertising.ROBINSON.getName(locale)+"'"
					+") `" + AonUtil.getMessage(TARGET_ADVERTISING) + "`"
			+",ELT(r.type+1" 
				+",'"+RegistryType.LEGAL.getName(locale)+"'"
				+",'"+RegistryType.NATURAL.getName(locale)+"'"
			 	+") `" + AonUtil.getMessage(ENTITY) + "`"
			+",CAST( CONCAT_WS('/',"
			+"ELT(r.document_type+1" 
				+",'"+DocumentType.NIF.getName(locale)+"'"
				+",'"+DocumentType.CIF.getName(locale)+"'"
				+",'"+DocumentType.NIE.getName(locale)+"'"
				+",'"+DocumentType.PASSPORT.getName(locale)+"'"
				+",'"+DocumentType.WORK_PERMIT.getName(locale)+"'"
				+",'"+DocumentType.COMMUNITY_CARD.getName(locale)+"'"
				+",'"+DocumentType.OTHER.getName(locale)+"'"
			 	+")" 
			+",r.document_country,r.document) AS CHAR) `" + AonUtil.getMessage(DOCUMENT) + "`"
		 	+",r.name `" + AonUtil.getMessage(COMPANY_NAME) + "`"
		 	+",r.alias `" + AonUtil.getMessage(ALIAS) + "`"
		 	+",scp.description `" + AonUtil.getMessage("aon_scope") + "`"
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
			+" FROM " + masterTable +" c"
			+" INNER JOIN registry r ON r.id = c.registry"
			+" INNER JOIN scope scp ON c.scope = scp.id"
			+" LEFT OUTER JOIN rmedia rm ON r.id = rm.registry"
			+" LEFT OUTER JOIN raddress ra ON r.id = ra.registry AND ra.type = 0"
			+" LEFT OUTER JOIN geozone gz ON ra.geozone = gz.id"
			+" LEFT OUTER JOIN rpaymethod rpm ON rpm.registry = r.id"
			+" LEFT OUTER JOIN rsegment rs ON rs.registry = r.id"
			+" LEFT OUTER JOIN segment s ON rs.segment = s.id"
			+" LEFT OUTER JOIN rattach cd ON cd.registry = r.id"
			+" LEFT OUTER JOIN category cdc ON cd.category = cdc.id"
			+" LEFT OUTER JOIN ritem ti ON ti.registry = r.id AND ti.type = 0"
			+" LEFT OUTER JOIN item i ON ti.item = i.id"
			+" LEFT OUTER JOIN product p ON p.id = i.product"
			+" LEFT OUTER JOIN pcategory pc ON pc.id = p.category"
			+" LEFT OUTER JOIN rseller tsll ON tsll.registry = r.id"
			+" LEFT OUTER JOIN seller sll ON sll.registry = tsll.seller"
			+" LEFT OUTER JOIN registry sllr ON sllr.id = sll.registry"
			+" LEFT OUTER JOIN rprofile ps ON ps.registry = r.id"
			+" LEFT OUTER JOIN question q ON ps.question = q.id"
			+" LEFT OUTER JOIN raddinfo ai ON c.registry = ai.registry"
			+" LEFT OUTER JOIN customer ct ON r.id = ct.registry"
			;

			Map<String,String> tableMapping = new HashMap<String, String>();
			tableMapping.put(mappingPrefix, "c");
			tableMapping.put(mappingPrefix + ".scope", "scp");
			tableMapping.put(mappingPrefix + ".registry", "r");
			tableMapping.put(mappingPrefix + ".registry.addresses", "ra");
			tableMapping.put(mappingPrefix + ".registry.medias", "rm");
			tableMapping.put(mappingPrefix + ".registry.segments", "rs");
			tableMapping.put(mappingPrefix + ".registry.segments.segment", "s");
			tableMapping.put(mappingPrefix + ".registry.addresses.geozone", "gz");
			tableMapping.put(mappingPrefix + ".documents", "cd");
			tableMapping.put(mappingPrefix + ".documents.category", "cdc");
			tableMapping.put(mappingPrefix + ".items", "ti");
			tableMapping.put(mappingPrefix + ".items.item", "i");
			tableMapping.put(mappingPrefix + ".items.item.product", "p");
			tableMapping.put(mappingPrefix + ".items.item.product.category", "pc");
			tableMapping.put(mappingPrefix + ".sellers", "tsll");
			tableMapping.put(mappingPrefix + ".sellers.seller", "sll");
			tableMapping.put(mappingPrefix + ".sellers.seller.registry", "sllr");
			tableMapping.put(mappingPrefix + ".profiles", "ps");
			tableMapping.put(mappingPrefix + ".profiles.question", "q");
			tableMapping.put(mappingPrefix + ".addInfos", "ai");
			tableMapping.put(mappingPrefix + ".addInfos.attribute", "aia");
			
			Map<String,Class<?>> pojoMapping = new HashMap<String, Class<?>>();
			pojoMapping.put(mappingPrefix, Target.class);
			pojoMapping.put(mappingPrefix + ".scope", Scope.class);
			pojoMapping.put(mappingPrefix + ".registry", Registry.class);
			pojoMapping.put(mappingPrefix + ".registry.medias", RegistryMedia.class);
			pojoMapping.put(mappingPrefix + ".registry.segments", RegistrySegment.class);
			pojoMapping.put(mappingPrefix + ".registry.segments.segment", Segment.class);
			pojoMapping.put(mappingPrefix + ".registry.addresses", RegistryAddress.class);
			pojoMapping.put(mappingPrefix + ".registry.addresses.geozone", GeoZone.class);
			pojoMapping.put(mappingPrefix + ".documents", RegistryAttachment.class);
			pojoMapping.put(mappingPrefix + ".documents.category", Category.class);
			pojoMapping.put(mappingPrefix + ".items", RegistryItem.class);
			pojoMapping.put(mappingPrefix + ".items.item", Item.class);
			pojoMapping.put(mappingPrefix + ".items.item.product", Product.class);
			pojoMapping.put(mappingPrefix + ".items.item.product.category", ProductCategory.class);
			pojoMapping.put(mappingPrefix + ".sellers", RegistrySeller.class);
			pojoMapping.put(mappingPrefix + ".sellers.seller", Seller.class);
			pojoMapping.put(mappingPrefix + ".sellers.seller.registry", Registry.class);
			pojoMapping.put(mappingPrefix + ".profiles", RegistryProfile.class);
			pojoMapping.put(mappingPrefix + ".profiles.question", Question.class);
			pojoMapping.put(mappingPrefix + ".addInfos", RegistryAddInfo.class);
			pojoMapping.put(mappingPrefix + ".addInfos.attribute", String.class);

			String where = CriteriaUtilities.toSQLString(getCriteria(), true, pojoMapping, tableMapping);
			select = select + " " + where;
			int i = StringUtils.indexOfIgnoreCase(select, " order by ");
			if (i == -1) {
				select = select + " GROUP BY `" + AonUtil.getMessage(ID) + "`";	
			} else {
				select = select.substring(0,i) + " GROUP BY `" + AonUtil.getMessage(ID) + "` " + select.substring(i+1);
			}
			select = StringUtils.replace(select, "c.customer = 'true'", "(1 IN (SELECT 1 FROM customer WHERE registry = c.registry))");
			select = StringUtils.replace(select, "c.customer = 'false'", "(1 NOT IN (SELECT 1 FROM customer WHERE registry = c.registry))");
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
		} catch (Throwable e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
}