package com.esferalia.aon.ui.payroll.event;

import java.util.Iterator;

import javax.faces.event.AbortProcessingException;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.TrainingCourse;

public class TrainingCourseControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		completeCNO();
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		completeCNO();
	}
	
	private void completeCNO(){
		String courseCode = ((TrainingCourse) this.getController().getTo()).getCode();
		if( courseCode.length() < 4 ){
			String msg = "";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		try {
			IManagerBean bean = BeanManager.getManagerBean(CNO.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CNO_CODE), courseCode.substring(0, 4));
			Iterator<ITransferObject> it = bean.getList(criteria).iterator();
			if(it.hasNext()){
				((TrainingCourse)this.getController().getTo()).setCNO((CNO) it.next());
			} else {
				String msg = "Se ha producido un error al validar el código.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error al validar el código.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
}