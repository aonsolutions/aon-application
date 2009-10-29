package com.code.aon.ui.form.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.lookup.SQLLookupBean;

/**
 * Listener associated with the <code>LookupBean</code>. It is not necessary to register it in <code>faces-bean-config.xml</code>
 */
public class LookupBeanListener extends ControllerAdapter {
	
	/** The lookup bean. */
	private SQLLookupBean lookupBean;
	
	/**
	 * The Constructor.
	 * 
	 * @param bean the LookupBean
	 */
	public LookupBeanListener(SQLLookupBean bean) {
		this.lookupBean = bean;
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		this.lookupBean.resetValues();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			ITransferObject to = event.getController().getTo();
			Map<String,Object> map = this.lookupBean.getCurrentMap( to );
			if ( map != null ) {
				for( Map.Entry<String,Object> entry : map.entrySet() ) {
					this.lookupBean.getValues().put( entry.getKey(), entry.getValue() );
				}
			} else {
				this.lookupBean.resetValues();
			}
		} catch (IllegalAccessException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		} catch (InvocationTargetException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		} catch (NoSuchMethodException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
}