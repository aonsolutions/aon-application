package com.code.aon.ui.seller.controller;

import static com.code.aon.ui.common.ICommonMessages.ACTIVE;
import static com.code.aon.ui.common.ICommonMessages.ADDRESS;
import static com.code.aon.ui.common.ICommonMessages.ALIAS;
import static com.code.aon.ui.common.ICommonMessages.BLOCKED;
import static com.code.aon.ui.common.ICommonMessages.CELLULAR;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_NAME;
import static com.code.aon.ui.common.ICommonMessages.DOCUMENT;
import static com.code.aon.ui.common.ICommonMessages.ENTITY;
import static com.code.aon.ui.common.ICommonMessages.FAX;
import static com.code.aon.ui.common.ICommonMessages.ID;
import static com.code.aon.ui.common.ICommonMessages.INACTIVE;
import static com.code.aon.ui.common.ICommonMessages.PHONE;
import static com.code.aon.ui.common.ICommonMessages.POSTAL_CODE;
import static com.code.aon.ui.common.ICommonMessages.STATE;
import static com.code.aon.ui.common.ICommonMessages.STATUS;
import static com.code.aon.ui.common.ICommonMessages.WEB;
import static com.code.aon.ui.config.controller.ConfigConstants.CONFIG_BANK;
import static com.code.aon.ui.config.controller.ConfigConstants.CONFIG_BANK_ACCOUNT;
import static com.code.aon.ui.config.controller.ConfigConstants.CONFIG_PAY_METHOD;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_CITY;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_DAYS_BETWEEN_PAYMENTS;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_DAYS_TO_FIRST_PAYMENT;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_EMAIL;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_NATIONALITY;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_NUMBER_OF_PAYMENTS;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_PAYMENT_DAYS;
import static com.code.aon.ui.registry.controller.IRegistryConstants.SELLER_REPORT;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.persistence.Table;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.PayMethod;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.pool.AonConnectionException;
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
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.report.export.ReportExporter;
import com.code.aon.ui.util.AonUtil;

public class SellerController extends RegistryController {

    public String getReportTitle(){
    	return AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME, SELLER_REPORT);
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

			
			String mappingPrefix = pojoClass.getSimpleName();
			Table table = pojoClass.getAnnotation(Table.class);
			String masterTable = table.name();
			
			FacesContext faces = FacesContext.getCurrentInstance();
			String select = "SELECT" 
			+" r.id `" + AonUtil.getMessage(ID) + "`"
			+",ELT(c.status+1"
			+",'"+AonUtil.getMessage(ACTIVE)+"'"
			+",'"+AonUtil.getMessage(INACTIVE)+"'"
			+",'"+AonUtil.getMessage(BLOCKED)+"'"
				+") `" + AonUtil.getMessage(STATUS) + "`"
			+",ELT(r.type+1" 
				+",'"+RegistryType.LEGAL.getName(AonUtil.getCurrentLocale())+"'"
				+",'"+RegistryType.NATURAL.getName(AonUtil.getCurrentLocale())+"'"
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
			+",r.nationality `" + AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME,REGISTRY_NATIONALITY) + "`"
			+",CAST( CONCAT_WS(' ',ra.street_type,ra.address,ra.number,ra.address2,ra.address3) AS CHAR) `" + AonUtil.getMessage(ADDRESS) + "`"
			+",ra.city `" + AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME,REGISTRY_CITY) + "`"
			+",ra.zip `" + AonUtil.getMessage(POSTAL_CODE) + "`"
			+",gz.code `Id " + AonUtil.getMessage(STATE) + "`"
			+",gz.name `" + AonUtil.getMessage(STATE) + "`"
			+",(SELECT rm1.value FROM rmedia rm1 WHERE r.id = rm1.registry  AND rm1.media = 1 LIMIT 1) `" + AonUtil.getMessage(PHONE) + "`"
			+",(SELECT rm2.value FROM rmedia rm2 WHERE r.id = rm2.registry  AND rm2.media = 2 LIMIT 1) `" + AonUtil.getMessage(CELLULAR) + "`"
			+",(SELECT rm3.value FROM rmedia rm3 WHERE r.id = rm3.registry  AND rm3.media = 3 LIMIT 1) `" + AonUtil.getMessage(FAX) + "`"
			+",(SELECT rm4.value FROM rmedia rm4 WHERE r.id = rm4.registry  AND rm4.media = 4 LIMIT 1) `" + AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME,REGISTRY_EMAIL) + "`"
			+",(SELECT rm5.value FROM rmedia rm5 WHERE r.id = rm5.registry  AND rm5.media = 5 LIMIT 1) `" + AonUtil.getMessage(WEB) + "`"
			+",pm.name `" + AonUtil.getMessage(ConfigConstants.BUNDLE_NAME,CONFIG_PAY_METHOD) + "`"
			+",b.name `" + AonUtil.getMessage(ConfigConstants.BUNDLE_NAME,CONFIG_BANK) + "`"
			+",rb.bank_account `" + AonUtil.getMessage(ConfigConstants.BUNDLE_NAME,CONFIG_BANK_ACCOUNT) + "`"
			+",rpm.number_of_pymnts `" + AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME,REGISTRY_NUMBER_OF_PAYMENTS) + "`"
			+",rpm.days_to_first_pymnt `" + AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME,REGISTRY_DAYS_TO_FIRST_PAYMENT) + "`"
			+",rpm.days_between_pymnts `" + AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME,REGISTRY_DAYS_BETWEEN_PAYMENTS) + "`"
			+",rpm.pymnt_days `" + AonUtil.getMessage(IRegistryConstants.BUNDLE_NAME,REGISTRY_PAYMENT_DAYS) + "`"
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
	

}