package com.code.aon.ui.commercial.controller;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.persistence.Table;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Question;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.TargetItem;
import com.code.aon.commercial.TargetProfile;
import com.code.aon.commercial.TargetSeller;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.commercial.enumeration.QuestionType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.registry.Category;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.report.ReportException;
import com.code.aon.sales.bridge.util.SalesBridgeUtil;
import com.code.aon.seller.Seller;
import com.code.aon.ui.commercial.ICommercialMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.report.export.ReportExporter;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetController extends RegistryController implements ICommercialConstants {

	private Set<Integer> checks = new HashSet<Integer>();
	
	private IControllerListener questionListener;
	
	private ResourceBundle bundle;

	public TargetController() {
		setBundleName(ICommercialMessages.BUNDLE_KEY);
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundleName(String bundleName) {
		this.bundle = AonUtil.getResourceBundle(bundleName);
	}	
		
	public String getAliasPreffix() {
		return getPojoShortName();
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		checkNone(event);
	}
	
	public Set<Integer> getCheckedTargets() {
		return checks;
	}
	
	protected Integer getId( Object o ) {
		return ((Target) o).getId();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator<ITransferObject> iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			checks.add( getId(iter.next()) );
		}
	}

	public void checkNone(ActionEvent event) {
		this.checks.clear();
	}

	public boolean getRowChecked() {
		Integer id = getId( model.getRowData() );
		return checks.contains(id);
	}

	public void setRowChecked(boolean rowChecked) {
		Integer id = getId(model.getRowData());		
		if (rowChecked) {
			if (!checks.contains(id)) {
				checks.add(id);
			}
		} else {
			if (checks.contains(id)) {
				checks.remove(id);
			}
		}
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

	public IControllerListener getQuestionListener() {
		if ( questionListener == null ) {
			questionListener = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {
						String alias = controller.getFieldName(IEntityAlias.QUESTION_TYPE);
						controller.getCriteria().addNotEqualExpression(alias, QuestionType.INFO);
					} catch (ManagerBeanException e) {
						throw new ControllerListenerException(e);
					} 
				}		
			};
		}
		return questionListener;
	}

	public void setQuestionListener(IControllerListener questionListener) {
		this.questionListener = questionListener;
	}

	public void onDetailReport(ActionEvent event){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			
			String mappingPrefix = Target.class.getSimpleName();
			Table table = Target.class.getAnnotation(Table.class);
			String masterTable = table.name();
			FacesContext faces = FacesContext.getCurrentInstance();
			String REGISTRY_BUNDLE = "registryBundle";
			String COMMERCIAL_BUNDLE = "commercialBundle";
			String select = "SELECT" 
			+" r.id `" + AonUtil.getMessage("aon_id") + "`"
			+",ELT(c.status+1"
			+",'"+AonUtil.getMessage("aon_active")+"'"
			+",'"+AonUtil.getMessage("aon_inactive")+"'"
			+",'"+AonUtil.getMessage("aon_blocked")+"'"
				+") `" + AonUtil.getMessage("aon_status") + "`"
				+",ELT(c.advertising+1"
				+",'"+Advertising.ALLOWED.getName(AonUtil.getCurrentLocale())+"'"
				+",'"+Advertising.AUTO_EXCLUSION.getName(AonUtil.getCurrentLocale())+"'"
				+",'"+Advertising.DENIED.getName(AonUtil.getCurrentLocale())+"'"
				+",'"+Advertising.ROBINSON.getName(AonUtil.getCurrentLocale())+"'"
					+") `" + AonUtil.getMessage(COMMERCIAL_BUNDLE,"commercial_target_advertising") + "`"
			+",ELT(r.type+1" 
				+",'"+RegistryType.LEGAL.getName(AonUtil.getCurrentLocale())+"'"
				+",'"+RegistryType.NATURAL.getName(AonUtil.getCurrentLocale())+"'"
			 	+") `" + AonUtil.getMessage("aon_entity") + "`"
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
			+",r.document_country,r.document) AS CHAR) `" + AonUtil.getMessage("aon_document") + "`"
		 	+",r.name `" + AonUtil.getMessage("aon_company_name") + "`"
			+",r.alias `" + AonUtil.getMessage("aon_alias") + "`"
			+",r.nationality `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_nationality") + "`"
			+",CAST( CONCAT_WS(' ',ra.street_type,ra.address,ra.number,ra.address2,ra.address3) AS CHAR) `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_address") + "`"
			+",ra.city `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_city") + "`"
			+",ra.zip `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_zip") + "`"
			+",gz.code `Id " + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_geozone") + "`"
			+",gz.name `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_geozone") + "`"
			+",(SELECT rm1.value FROM rmedia rm1 WHERE r.id = rm1.registry  AND rm1.media = 1 LIMIT 1) `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_phone") + "`"
			+",(SELECT rm2.value FROM rmedia rm2 WHERE r.id = rm2.registry  AND rm2.media = 2 LIMIT 1) `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_cellular") + "`"
			+",(SELECT rm3.value FROM rmedia rm3 WHERE r.id = rm3.registry  AND rm3.media = 3 LIMIT 1) `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_fax") + "`"
			+",(SELECT rm4.value FROM rmedia rm4 WHERE r.id = rm4.registry  AND rm4.media = 4 LIMIT 1) `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_email") + "`"
			+",(SELECT rm5.value FROM rmedia rm5 WHERE r.id = rm5.registry  AND rm5.media = 5 LIMIT 1) `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_web") + "`"
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
			+" LEFT OUTER JOIN target_item ti ON ti.target = r.id"
			+" LEFT OUTER JOIN item i ON ti.item = i.id"
			+" LEFT OUTER JOIN product p ON p.id = i.product"
			+" LEFT OUTER JOIN pcategory pc ON pc.id = p.category"
			+" LEFT OUTER JOIN target_seller tsll ON tsll.target = r.id"
			+" LEFT OUTER JOIN seller sll ON sll.registry = tsll.seller"
			+" LEFT OUTER JOIN registry sllr ON sllr.id = sll.registry"
			+" LEFT OUTER JOIN target_profile ps ON ps.target = r.id"
			+" LEFT OUTER JOIN question q ON ps.question = q.id"
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
			pojoMapping.put(mappingPrefix + ".items", TargetItem.class);
			pojoMapping.put(mappingPrefix + ".items.item", Item.class);
			pojoMapping.put(mappingPrefix + ".items.item.product", Product.class);
			pojoMapping.put(mappingPrefix + ".items.item.product.category", ProductCategory.class);
			pojoMapping.put(mappingPrefix + ".sellers", TargetSeller.class);
			pojoMapping.put(mappingPrefix + ".sellers.seller", Seller.class);
			pojoMapping.put(mappingPrefix + ".sellers.seller.registry", Registry.class);
			pojoMapping.put(mappingPrefix + ".profiles", TargetProfile.class);
			pojoMapping.put(mappingPrefix + ".profiles.question", Question.class);

			String where = CriteriaUtilities.toSQLString(getCriteria(), true, pojoMapping, tableMapping);
			select = select + " " + where;
			int i = StringUtils.indexOfIgnoreCase(select, " order by ");
			if (i == -1) {
				select = select + " GROUP BY `" + AonUtil.getMessage("aon_id") + "`";	
			} else {
				select = select.substring(0,i) + " GROUP BY `" + AonUtil.getMessage("aon_id") + "` " + select.substring(i+1);
			}

			System.out.println( " ----------------------" );
			System.out.println( select );
			System.out.println( " ----------------------" );
			
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
	
}