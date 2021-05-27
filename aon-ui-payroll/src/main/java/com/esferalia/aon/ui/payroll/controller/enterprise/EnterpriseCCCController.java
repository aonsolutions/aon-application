package com.esferalia.aon.ui.payroll.controller.enterprise;

import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.JoinType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.geozone.GeoZone;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.util.PayrollUtils;

public class EnterpriseCCCController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public boolean isNewCCCLineAvailable(){
		try {
			if(this.getModel()!=null && this.getModel().getRowCount() < CCCType.values().length){
				return true;
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error al obtener el modelo de datos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return false;
	}

	public boolean isValidGeozone(){
		try {
			EnterpriseCCC ccc = (EnterpriseCCC) this.getModel().getRowData();
			if(this.getModel()!=null && existWorplaceGeozone(ccc.getGeozone())){
				return true;
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error al validar la cuenta.";
			AonUtil.addErrorMessage(msg);
		}
		return false;
	}
	
	public List<SelectItem> getCCCTypes() {
		List<SelectItem> cccTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for( CCCType cccType : CCCType.values() ) {
			String name = cccType.getName(locale);
			SelectItem item = new SelectItem(cccType, name);
			if(cccType==CCCType.LEARNING){
				item.setDisabled(Boolean.TRUE);
			}
			cccTypes.add(item);			
		}
		return cccTypes;
	}
	
	public String getQuoteRegimeCode() throws ManagerBeanException{
		if(this.getModel().isRowAvailable()){
			return PayrollUtils.getInstance().getRegimeCode((EnterpriseCCC) this.getModel().getRowData());
		} else if(this.isNevv()){
			return PayrollUtils.getInstance().getRegimeCode((EnterpriseCCC) this.getTo());
		}
		return null; 
	}
	
	public boolean isValidSSNumber() throws ManagerBeanException{
		if(this.getModel().isRowAvailable()){
			return PayrollUtils.getInstance().isValidSSNumber((EnterpriseCCC) this.getModel().getRowData());
		} else if(this.getTo()!=null){
			return PayrollUtils.getInstance().isValidSSNumber((EnterpriseCCC) this.getTo());
		}
		return false; 
	}
	
	public void onChangeCcc(ActionEvent event){
		EnterpriseCCC ccc = (EnterpriseCCC) this.getTo();
		if(ccc!=null && StringUtils.isNotBlank(ccc.getCcc()) && StringUtils.length(ccc.getCcc())>1){
			ccc.setGeozone(getGeoZoneByValue(ccc.getCcc()));
		}
	}

	private GeoZone getGeoZoneByValue(String code) {
		try {
			Domain domain = getCurrentDomain();
			IManagerBean bean = BeanManager.getManagerBean(GeoZone.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEO_ZONE_CODE), code.substring(0,2));
			if(domain.isEnableHeredity()){
				Integer[] ids = {domain.getId(), domain.getParent().getId()};
				criteria.addInExpression(bean.getFieldName(IEntityAlias.GEO_ZONE_DOMAIN), ids);
				criteria.setSkipDomainFilter(true);
			}
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (GeoZone) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA, no se autocompleta la provincia
		}
		return null;
	}
	
	public Domain getCurrentDomain(){
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_ID), DomainManager.getCurrentDomain());
			if( bean.getCount(criteria)<1 ) {
				String msg = "No hay datos de empresa definidos.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else {
				return (Domain) bean.getList(criteria).get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA. se devuelve nulo
		}
		return null;
	}

	private boolean existWorplaceGeozone(GeoZone geoZone) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			int recordCount = ctx
					.select()
					.from(RADDRESS).join(WORKPLACE, JoinType.LEFT_OUTER_JOIN)
					.where(RADDRESS.DOMAIN.equal(DomainManager.getCurrentDomain()))
					.and(RADDRESS.GEOZONE.equal(geoZone.getId()))
					.fetchCount();
			return recordCount > 0;
		} catch (AonConnectionException e) {
			AonUtil.addErrorMessage(e.getMessage());
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
		return false;
	}

}
