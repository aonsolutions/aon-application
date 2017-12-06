package com.code.aon.fiscal.event;


import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.enumeration.Mod347Status;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod347BeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Mod347 mod347 = (Mod347) evt.getTo();
		checkMod347(mod347);
		if (mod347.isExtraDeclaration()) {
			int last = getLastNumber(mod347);
			mod347.setReplacedNumber(Integer.toString(last));
			mod347.setNumber(Integer.toString(++last));
		} else {
			mod347.setNumber("1");
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Mod347 mod347 = (Mod347) evt.getTo();
		checkMod347(mod347);
	}

	private void checkMod347(Mod347 mod347) throws ManagerBeanVetoListenerException {
		if (mod347.isComplementary() && mod347.isReplacement()) {
			throw new ManagerBeanVetoListenerException("La declaración no puede ser Complementaria y Sustitutiva.");
		}
		if (mod347.isExtraDeclaration()) {
			checkPeriodMod347(mod347);
		} else {
			checkNormalMod347(mod347);
		}
	}

	private void checkNormalMod347(Mod347 mod347) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Mod347.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.MOD347_YEAR), mod347.getYear());
			if (mod347.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.MOD347_ID), mod347.getId()));
			}
			int size = bean.getCount(c);
			if (size > 0) {
				throw new ManagerBeanVetoListenerException("Ya existe una declaración para este ejercicio.");
			}

		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void checkPeriodMod347(Mod347 mod347) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Mod347.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.MOD347_YEAR), mod347.getYear());
			if (mod347.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.MOD347_ID), mod347.getId()));
			}
			List<ITransferObject> list = bean.getList(c);
			if (list == null || list.size() == 0) {
				throw new ManagerBeanVetoListenerException("No existe una declaración para este periodo a la que complementar o sustituir.");
			}
			for (ITransferObject to : list) {
				Mod347 tax = (Mod347) to;	
				if (tax.getStatus() != Mod347Status.FINISHED) {
					throw new ManagerBeanVetoListenerException("La declaración de este periodo a la que complementar o sustituir, no está FINALIZADA.");
				}
				if ((mod347.isComplementary() && tax.isReplacement()) || (mod347.isReplacement() && tax.isComplementary())) {
					throw new ManagerBeanVetoListenerException("No se permite la existencia de declaraciones complementarias y sustitutivas en un mismo periodo.");	
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private Integer getLastNumber(Mod347 mod347) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Mod347.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.MOD347_YEAR), mod347.getYear());
			c.addOrder(bean.getFieldName(IEntityAlias.MOD347_NUMBER), false);
			List<ITransferObject> list = bean.getList(c);
			if (list == null || list.size() == 0) {
				return 1;
			}
			Mod347 last = (Mod347) list.get(0);
			try {
				return Integer.parseInt(last.getNumber());
			} catch (NumberFormatException e) {
				return 1;
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}
	
}
