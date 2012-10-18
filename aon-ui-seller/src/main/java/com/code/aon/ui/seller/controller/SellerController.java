package com.code.aon.ui.seller.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.persistence.Table;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.PayMethod;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.Category;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.report.export.ReportExporter;
import com.code.aon.ui.util.AonUtil;

public class SellerController extends RegistryController {
	  /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_seller_report";
	
	public String getReportTitle(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX);
	}

	public void onDetailReport(ActionEvent event){
		try {
			Class<?> pojoClass = (Class<?>) Class.forName( getPojo() );
			String sessionName = HibernateUtil.getSessionFactoryName(pojoClass.getName());
			String mappingPrefix = pojoClass.getSimpleName();
			Table table = pojoClass.getAnnotation(Table.class);
			String masterTable = table.name();
			
			FacesContext faces = FacesContext.getCurrentInstance();
			Connection c = HibernateUtil.getSQLConnection(sessionName);
			String REGISTRY_BUNDLE = "registryBundle";
			String CONFIG_BUNDLE = "configBundle";
			String select = "SELECT" 
			+" r.id `" + AonUtil.getMessage("aon_id") + "`"
			+",ELT(c.status+1"
			+",'"+AonUtil.getMessage("aon_active")+"'"
			+",'"+AonUtil.getMessage("aon_inactive")+"'"
			+",'"+AonUtil.getMessage("aon_blocked")+"'"
				+") `" + AonUtil.getMessage("aon_status") + "`"
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
			+",pm.name `" + AonUtil.getMessage(CONFIG_BUNDLE,"config_pay_method") + "`"
			+",b.name `" + AonUtil.getMessage(CONFIG_BUNDLE,"config_bank") + "`"
			+",rb.bank_account `" + AonUtil.getMessage(CONFIG_BUNDLE,"config_bank_account") + "`"
			+",rpm.number_of_pymnts `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_number_of_payments") + "`"
			+",rpm.days_to_first_pymnt `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_days_to_first_payment") + "`"
			+",rpm.days_between_pymnts `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_days_between_payments") + "`"
			+",rpm.pymnt_days `" + AonUtil.getMessage(REGISTRY_BUNDLE,"registry_payment_days") + "`"
			+" FROM " + masterTable +" c"
			+" INNER JOIN registry r ON r.id = c.registry"
			+" LEFT OUTER JOIN rmedia rm ON r.id = rm.registry"
			+" LEFT OUTER JOIN raddress ra ON r.id = ra.registry AND ra.type = 0"
			+" LEFT OUTER JOIN geozone gz ON ra.geozone = gz.id"
			+" LEFT OUTER JOIN rpaymethod rpm ON rpm.registry = r.id"
			+" LEFT OUTER JOIN pay_method pm ON rpm.pay_method = pm.id"
			+" LEFT OUTER JOIN rbank rb ON rpm.rbank = rb.id"
			+" LEFT OUTER JOIN bank b ON rb.bank = b.id"
			+" LEFT OUTER JOIN rsegment rs ON rs.registry = r.id"
			+" LEFT OUTER JOIN segment s ON rs.segment = s.id"
			+" LEFT OUTER JOIN rattach cd ON cd.registry = r.id"
			+" LEFT OUTER JOIN category cdc ON cd.category = cdc.id";

			Map<String,String> tableMapping = new HashMap<String, String>();
			tableMapping.put(mappingPrefix, "c");
			tableMapping.put(mappingPrefix + ".registry", "r");
			tableMapping.put(mappingPrefix + ".registry.addresses", "ra");
			tableMapping.put(mappingPrefix + ".registry.medias", "rm");
			tableMapping.put(mappingPrefix + ".registry.segments", "rs");
			tableMapping.put(mappingPrefix + ".registry.segments.segment", "s");
			tableMapping.put(mappingPrefix + ".registry.addresses.geozone", "gz");
			tableMapping.put(mappingPrefix + ".registry.payMethods", "rpm");
			tableMapping.put(mappingPrefix + ".registry.payMethods.payment", "pm");
			tableMapping.put(mappingPrefix + ".documents", "cd");
			tableMapping.put(mappingPrefix + ".documents.category", "cdc");
			
			Map<String,Class<?>> pojoMapping = new HashMap<String, Class<?>>();
			pojoMapping.put(mappingPrefix, pojoClass);
			pojoMapping.put(mappingPrefix + ".registry", Registry.class);
			pojoMapping.put(mappingPrefix + ".registry.medias", RegistryMedia.class);
			pojoMapping.put(mappingPrefix + ".registry.segments", RegistrySegment.class);
			pojoMapping.put(mappingPrefix + ".registry.segments.segment", Segment.class);
			pojoMapping.put(mappingPrefix + ".registry.addresses", RegistryAddress.class);
			pojoMapping.put(mappingPrefix + ".registry.addresses.geozone", GeoZone.class);
			pojoMapping.put(mappingPrefix + ".registry.payMethods", RegistryPayMethod.class);
			pojoMapping.put(mappingPrefix + ".registry.payMethods.payment", PayMethod.class);
			pojoMapping.put(mappingPrefix + ".documents", RegistryAttachment.class);
			pojoMapping.put(mappingPrefix + ".documents.category", Category.class);

			String where = CriteriaUtilities.toSQLString(getCriteria(), true, pojoMapping, tableMapping);
			select = select + " " + where;
			int i = StringUtils.indexOfIgnoreCase(select, " order by ");
			if (i == -1) {
				select = select + " GROUP BY `" + AonUtil.getMessage("aon_id") + "`";	
			} else {
				select = select.substring(0,i) + " GROUP BY `" + AonUtil.getMessage("aon_id") + "` " + select.substring(i+1);
			}
			
			PreparedStatement ps = c.prepareStatement(select);
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
		} catch (ClassNotFoundException e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	

}