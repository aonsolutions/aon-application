package com.code.aon.ui.record.event;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.record.Contract;
import com.code.aon.record.Position;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.ContractController;
import com.code.aon.ui.record.controller.PositionController;
import com.code.aon.ui.util.AonUtil;

/**
 * The position listener class for receiving contract events. 
 * 
 * @author iayerbe
 *
 */
public class PositionContractListener extends ControllerAdapter {

	private static final long serialVersionUID = 5831837830390797362L;

	@SuppressWarnings("unchecked")
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ContractController cc = (ContractController) event.getController();
		Contract c = (Contract) cc.getTo();
		Date endingDate = c.getEndingDate();
		if ( endingDate != null ) {
			String SELECT = "SELECT position FROM Position as position WHERE position.employee.id = :id ORDER BY id";
			Query query = HibernateUtil.getSession().createQuery( SELECT );
			query.setInteger( "id", c.getEmployee().getId() );
			List l= query.list();
			if ( l.size() > 0 ) {
				Position p = (Position) l.get( l.size() - 1 );
				p.setEndingDate( endingDate );
				try {
					BeanManager.getManagerBean( Position.class ).update( p );
					PositionController pc = 
						(PositionController) AonUtil.getController( PositionController.MANAGER_BEAN_NAME );
					pc.onSearch(null);
				} catch (ManagerBeanException ex) {
					throw new ControllerListenerException( ex );
				}
			}
		}
	}
	
}