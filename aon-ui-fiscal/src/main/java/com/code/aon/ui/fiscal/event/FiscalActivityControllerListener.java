package com.code.aon.ui.fiscal.event;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.fiscal.controller.activity.FiscalActivityController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FiscalActivityControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		FiscalActivityController c = (FiscalActivityController) event.getController();
		c.initialize();
		FiscalActivity fiscalActivity = (FiscalActivity) c.getTo();
		Calendar calendar = Calendar.getInstance();
		fiscalActivity.setYear( calendar.get(Calendar.YEAR) );
	}
	
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FiscalActivityController c = (FiscalActivityController) event.getController();
		validateInfo( c );
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		FiscalActivityController c = (FiscalActivityController) event.getController();
		validateInfo( c );
	}
	
	private void validateInfo(FiscalActivityController c) throws ControllerListenerException {
		Locale locale = AonUtil.getCurrentLocale();
		for (FiscalActivityInfo info : c.getActivityInfoList()) {
			info.setValue( StringUtils.trim( info.getValue() ) );
			if (StringUtils.isNotBlank( info.getValue() )) {
				try {
					Object value = info.cast();
					validate( info.getInfoKey() , value , c );
				} catch (NumberFormatException e) {
					String name = info.getInfoKey().getName(locale); 
					throw new ControllerListenerException(name + ": El valor \"" + info.getValue() + "\" no es un número válido." );
				} catch (AonException e) {
					throw new ControllerListenerException(e.getMessage(),e);
				}
			}
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FiscalActivityController c = (FiscalActivityController) event.getController();
		FiscalActivity fa = (FiscalActivity) c.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(FiscalActivityInfo.class);
			for (FiscalActivityInfo info : c.getActivityInfoList()) {
				info.setFiscalActivity(fa);
				info.setType( FiscalActivityInfoType.INFO);
				bean.insert(info);
			}
			for (FiscalActivityInfo info : c.getIrpfModulesList()) {
				info.setFiscalActivity(fa);
				info.setType(FiscalActivityInfoType.IRPF_MODULE);
				bean.insert(info);
			}
			for (FiscalActivityInfo info : c.getIrpfInfoList()) {
				info.setFiscalActivity(fa);
				info.setType( FiscalActivityInfoType.IRPF_INFO);
				bean.insert(info);
			}
			for (FiscalActivityInfo info : c.getVatModulesList()) {
				info.setFiscalActivity(fa);
				info.setType(FiscalActivityInfoType.VAT_MODULE);
				bean.insert(info);
			}
			for (FiscalActivityInfo info : c.getVatInfoList()) {
				info.setFiscalActivity(fa);
				info.setType( FiscalActivityInfoType.VAT_INFO);
				bean.insert(info);
			}
			for (FiscalActivityInfo info : c.getM311List()) {
				info.setFiscalActivity(fa);
				info.setType( info.getInfoKey().getType());
				bean.insert(info);
			}
			for (List<FiscalActivityInfo> list : c.getModulesDetailMap().values()) {
				for (FiscalActivityInfo info : list) {
					info.setFiscalActivity(fa);
					info.setType( FiscalActivityInfoType.MODULE_DETAIL);
					bean.insert(info);
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);			
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		FiscalActivityController c = (FiscalActivityController) event.getController();
		try {
			IManagerBean bean = BeanManager.getManagerBean(FiscalActivityInfo.class);
			for (FiscalActivityInfo info : c.getActivityInfoList()) {
				bean.update(info);
			}
			for (FiscalActivityInfo info : c.getIrpfModulesList()) {
				bean.update(info);
			}
			for (FiscalActivityInfo info : c.getIrpfInfoList()) {
				bean.update(info);
			}
			for (FiscalActivityInfo info : c.getVatModulesList()) {
				bean.update(info);
			}
			for (FiscalActivityInfo info : c.getVatInfoList()) {
				bean.update(info);
			}
			for (FiscalActivityInfo info : c.getM311List()) {
				if (info.getId() == null) {
					bean.insert(info);	
				} else {
					bean.update(info);	
				}
			}
			for (List<FiscalActivityInfo> list : c.getModulesDetailMap().values()) {
				for (FiscalActivityInfo info : list) {
					bean.update(info);
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);			
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		FiscalActivityController c = (FiscalActivityController) event.getController();
		try {
			IManagerBean bean = BeanManager.getManagerBean(FiscalActivityInfo.class);
			for (FiscalActivityInfo info : c.getActivityInfoList()) {
				bean.remove(info);
			}
			for (FiscalActivityInfo info : c.getVatModulesList()) {
				bean.remove(info);
			}
			for (FiscalActivityInfo info : c.getVatInfoList()) {
				bean.remove(info);
			}
			for (FiscalActivityInfo info : c.getIrpfModulesList()) {
				bean.remove(info);
			}
			for (FiscalActivityInfo info : c.getIrpfInfoList()) {
				bean.remove(info);
			}
			for (FiscalActivityInfo info : c.getM311List()) {
				if (info.getId() != null) {
					bean.remove(info);
				}
			}
			for (List<FiscalActivityInfo> list : c.getModulesDetailMap().values()) {
				for (FiscalActivityInfo info : list) {
					bean.remove(info);
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);			
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalActivityController c = (FiscalActivityController) event.getController();
			c.setCalculator(null);
			c.initialize();
			loadActivityInfo(c);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);			
		}
	}

	private void loadActivityInfo(FiscalActivityController c) throws ManagerBeanException {
		try {
			FiscalActivity fa = (FiscalActivity) c.getTo();
			IManagerBean bean = BeanManager.getManagerBean(FiscalActivityInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_INFO_FISCAL_ACTIVITY_ID), fa.getId());
			criteria.addOrder( bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_INFO_ID ));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to :  list ) {
				FiscalActivityInfo info = (FiscalActivityInfo) to;
				if (info.getType() == FiscalActivityInfoType.INFO ) {
					c.getActivityInfoList().add(info);
				} else if (info.getType() == FiscalActivityInfoType.VAT_MODULE ) {
					c.getVatModulesList().add(info);					
				} else if (info.getType() == FiscalActivityInfoType.VAT_INFO) {
					c.getVatInfoList().add(info);
				} else if (info.getType() == FiscalActivityInfoType.IRPF_MODULE) {
					c.getIrpfModulesList().add(info);
				} else if (info.getType() == FiscalActivityInfoType.IRPF_INFO) {
					c.getIrpfInfoList().add(info);
				} else if (info.getType() == FiscalActivityInfoType.M311_DETAIL) {
					c.getM311List().add(info);
				} else if (info.getType() == FiscalActivityInfoType.MODULE_DETAIL) {
					FiscalActivityInfoKey parentKey = info.getInfoKey().getParentKey();
					List<FiscalActivityInfo> detailList = c.getModulesDetailMap().get(parentKey);
					if (detailList == null) {
						detailList = new LinkedList<FiscalActivityInfo>();
						c.getModulesDetailMap().put(parentKey,detailList);
					}
					detailList.add(info);
				}
			}
			if (fa.getYear() < 2014 && (c.getM311List() == null || c.getM311List().size() == 0) ) {
				c.fillM311(fa);
				c.calculateM311();
			}
			c.fillInfoChoices();
		} catch (AonException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}

	private void validate(FiscalActivityInfoKey infoKey, Object v, FiscalActivityController c) throws AonException {
		FiscalActivity fa = (FiscalActivity) c.getTo();
//		IManagerBean bean = BeanManager.getManagerBean(FiscalActivity.class);
//		Criteria criteria = new Criteria();
//		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_YEAR), fa.getYear() );
//		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_EPIGRAPH), fa.getEpigraph() );
//		if ( fa.getId() != null) {
//			criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_ID), fa.getId() );
//		}
//		List<ITransferObject> list = bean.getList(criteria);
//		if (list != null && list.size()>0) {
//			throw new AonException("Ya existe la definición del epígrafe "+ fa.getEpigraph() +" para el ejercicio " + fa.getYear()); 
//		}
		
		if (infoKey == FiscalActivityInfoKey.A02) {
			Double d = (Double) v;
			if (d < 0.0 || d > 100.0) {
				throw new AonException("El porcentaje de participación en la entidad debe ser un número entro 0 y 100.");
			}
		} else if (infoKey == FiscalActivityInfoKey.A03) {
			Integer d = (Integer) v;
			if (d < 0.0 || d > 180.0) {
				throw new AonException("El número de días de ejercicio de la actividad durante el año anterior. debe ser un número entro 0 y 180.");
			}
		} else if (infoKey == FiscalActivityInfoKey.A04) {
			Integer d = (Integer) v;
			int fromYear = fa.getYear() - 1;
			int toYear = fa.getYear();
			if (d < fromYear || d > toYear) {
				throw new AonException("El número de días de ejercicio de la actividad durante el año anterior. debe ser un número entre " + fromYear + " y " + toYear + ".");
			}
			FiscalActivityInfo info = c.getInfo(FiscalActivityInfoKey.A03);
			if (info != null && info.getValue() != null) {
				if (info.getDoubleValue() != 0) {
					throw new AonException("No se puede indicar ejercicio para NUEVAS ACTIVIDADES cuando se trate de actividades de temporada.");
				}
			}
		} else if (infoKey == FiscalActivityInfoKey.A07) {
			Integer i = (Integer) v;
			if (i < 0 ) {
				throw new AonException("El número de vehículos afectos a la actividad no es válido.");
			}
		} else if (infoKey == FiscalActivityInfoKey.A08) {
			Integer d = (Integer) v;
			if (d == 1) {
				FiscalActivityInfo info = c.getInfo(FiscalActivityInfoKey.A07);
				if (info.getDoubleValue() != 1 ) {
					throw new AonException("Si dispone de UN SOLO vehículo afecto a la actividad, señale si su capacidad de carga supera o no 1.000 kg.");
				}
			}
//		} else if (infoKey == FiscalActivityInfoKey.A10) {
//			Integer i = (Integer) v;
//			if (i < 0 ) {
//				throw new AonException("El número de empleados al inicio de ejercicio (o al inicio de la actividad) no es válido.");
//			}
		}
	}
}
