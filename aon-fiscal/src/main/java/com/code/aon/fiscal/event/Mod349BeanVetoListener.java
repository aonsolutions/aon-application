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
import com.code.aon.fiscal.Mod349;
import com.code.aon.fiscal.enumeration.Mod349Status;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod349BeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Mod349 mod349 = (Mod349) evt.getTo();
		if ( mod349.getYear() > 2017) {
			throw new ManagerBeanVetoListenerException("A partir del ejercicio 2018, utilice el nuevo programa de declaración recapitulativa de operaciones intracomunitarias.");
		}
		checkMod349(mod349);
		if (mod349.isExtraDeclaration()) {
			int last = getLastNumber(mod349);
			mod349.setReplacedNumber(Integer.toString(last));
			mod349.setNumber(Integer.toString(++last));
		} else {
			mod349.setNumber("1");
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Mod349 mod349 = (Mod349) evt.getTo();
		checkMod349(mod349);
	}

	private void checkMod349(Mod349 mod349) throws ManagerBeanVetoListenerException {
		if (mod349.isComplementary() && mod349.isReplacement()) {
			throw new ManagerBeanVetoListenerException("La declaración no puede ser Complementaria y Sustitutiva.");
		}
		if (mod349.isExtraDeclaration()) {
			checkPeriodMod349(mod349);
		} else {
			checkNormalMod349(mod349);
		}
	}

	private void checkNormalMod349(Mod349 mod349) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Mod349.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.MOD349_YEAR), mod349.getYear());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.MOD349_PERIOD), mod349.getPeriod());
			if (mod349.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.MOD349_ID), mod349.getId()));
			}
			int size = bean.getCount(c);
			if (size > 0) {
				throw new ManagerBeanVetoListenerException("Ya existe una declaración para este ejercicio.");
			}

		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void checkPeriodMod349(Mod349 mod349) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Mod349.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.MOD349_YEAR), mod349.getYear());
			if (mod349.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.MOD349_ID), mod349.getId()));
			}
			List<ITransferObject> list = bean.getList(c);
			if (list == null || list.size() == 0) {
				throw new ManagerBeanVetoListenerException("No existe una declaración para este periodo a la que complementar o sustituir.");
			}
			for (ITransferObject to : list) {
				Mod349 tax = (Mod349) to;	
				if (tax.getStatus() != Mod349Status.FINISHED) {
					throw new ManagerBeanVetoListenerException("La declaración de este periodo a la que complementar o sustituir, no está FINALIZADA.");
				}
				if ((mod349.isComplementary() && tax.isReplacement()) || (mod349.isReplacement() && tax.isComplementary())) {
					throw new ManagerBeanVetoListenerException("No se permite la existencia de declaraciones complementarias y sustitutivas en un mismo periodo.");	
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private Integer getLastNumber(Mod349 mod349) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Mod349.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.MOD349_YEAR), mod349.getYear());
			c.addOrder(bean.getFieldName(IEntityAlias.MOD349_NUMBER), false);
			List<ITransferObject> list = bean.getList(c);
			if (list == null || list.size() == 0) {
				return 1;
			}
			Mod349 last = (Mod349) list.get(0);
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
