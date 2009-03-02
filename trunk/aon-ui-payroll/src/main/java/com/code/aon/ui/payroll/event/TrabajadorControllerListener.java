package com.code.aon.ui.payroll.event;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class TrabajadorControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
			
		super.afterBeanCreated(event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {

		super.beforeBeanAdded(event);
	}
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println(" beforeBeanSelected ");

		super.beforeBeanSelected(event);
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		
		resetLinesModel();
		super.beforeModelInitialized(event);
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		
		resetLinesModel();
		super.afterBeanCanceled(event);
	}
	
	
	private void resetLinesModel() throws ControllerListenerException {
		System.out.println("  **********************************  resetLinesModel");
		
		//LinesController controller = (LinesController) FormUtil.getController(IPayrollConstants.TRABAJO_CONTROLLER_NAME);
		List<LinesController> controllers = new ArrayList<LinesController>();
		
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.TRABAJO_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.BONIFICA_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.PERCEP_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.TRABDTO_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.EMBARGO_LINES_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.PARTEIT_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.TRABINCI_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.NOMINAIT_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.AVISO_CONTROLLER_NAME));
		controllers.add((LinesController) FormUtil.getController(IPayrollConstants.PRCDIVTRAB_CONTROLLER_NAME));
		
		
		try {
			for(LinesController lc:controllers){
				lc.clearCriteria();
				
				if(lc.getBeanName().equals("trabajo"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.TRABAJO_EMPRPER_CDG));
				else if(lc.getBeanName().equals("bonifica"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.BONIFICA_EMPRPER_CDG));
				else if(lc.getBeanName().equals("percep"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.PERCEP_TRABAJADOR_CDG));
				else if(lc.getBeanName().equals("trabdto"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.TRABDTO_EMPRPER_CDG));
				else if(lc.getBeanName().equals("embargo"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.EMBARGO_TRABAJADOR_CDG));
				else if(lc.getBeanName().equals("parteit"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.PARTEIT_EMPRPER_CDG));
				else if(lc.getBeanName().equals("trabinci"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.TRABINCI_EMPRPER_CDG));
				else if(lc.getBeanName().equals("nominait"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.NOMINAIT_EMPRPER_CDG));
				else if(lc.getBeanName().equals("aviso"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.AVISOS_EMPRPER_CDG));
				else if(lc.getBeanName().equals("prcdivtrab"))
					lc.getCriteria().addNullExpression(lc.getFieldName(IPayrollAlias.PRCDIVTRAB_EMPRPER_CDG));
				
				
				lc.initializeModel();
			}
			
			
			
			//Criteria criteria = controller.getCriteria();
			//criteria.addNullExpression(controller.getFieldName(IPayrollAlias.TRABAJO_EMPRPER_CDG));
			
			//controller.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}
		
		
	}
	
}
