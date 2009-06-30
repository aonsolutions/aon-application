package com.code.aon.ui.accounting.event;

import java.util.List;

import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;


public class BalanceDetailControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		BalanceDetail processDetail = (BalanceDetail) controller.getTo();
		try {
			int index = controller.getModel().getRowCount();
			processDetail.setSortKey( index );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		try {		
			Criteria criteria = controller.getCriteria();
			String field = controller.getFieldName(IAccountingAlias.BALANCE_DETAIL_SORT_KEY);
			criteria.addOrder( field );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		BalanceDetail balanceDetail = (BalanceDetail) controller.getTo();
		try {
			
			Criteria criteria = controller.getCriteria();
			String field = controller.getFieldName(IAccountingAlias.BALANCE_DETAIL_SORT_KEY);
			criteria.addGreaterThanExpression(field, balanceDetail.getSortKey() );
			criteria.addOrder( field );
			List<ITransferObject> list = controller.getManagerBean().getList(criteria);			
			for( ITransferObject to: list ) {
				BalanceDetail bd = (BalanceDetail) to;
				bd.setSortKey( bd.getSortKey() -1 );
				controller.getManagerBean().update(bd);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
