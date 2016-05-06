package com.esferalia.aon.payroll.event;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseCCC;

public class EnterpriseCCCVetoableBeanListener extends
		ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void vetoableBeanInserted(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		try {
			validate((EnterpriseCCC) evt.getTo());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(
					"Ha ocurrido un error inesperado. Vuelva a intentarlo.");
		}
	}

	public void vetoableBeanUpdated(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		try {
			validate((EnterpriseCCC) evt.getTo());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(
					"Ha ocurrido un error inesperado. Vuelva a intentarlo.");
		}
	}

	private void validate(EnterpriseCCC ccc)
			throws ManagerBeanVetoListenerException, ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_CCC),
				ccc.getCcc());
		Projection projection = Projection.rowCount();
		Object value = bean.getUniqueResult(projection, criteria);
		if (value != null && NumberUtils.isNumber(value.toString())
				&& Integer.parseInt(value.toString()) > 0) {
			throw new ManagerBeanVetoListenerException(
					"El valor ya ha sido dado de alta.");
		}
	}

}
