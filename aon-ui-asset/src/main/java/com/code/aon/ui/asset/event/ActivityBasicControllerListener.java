package com.code.aon.ui.asset.event;

import static com.code.aon.ui.common.ICommonConstants.LOGGED_USER_CONTROLLER_NAME;

import com.code.aon.asset.Asset;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.asset.controller.ActivityBasicController;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ActivityBasicControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		((ActivityBasicController)this.getController()).buildFromTime();
		((ActivityBasicController)this.getController()).buildToTime();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((ActivityBasicController)this.getController()).setControllerTime();
	}
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		Criteria criteria;
		try {
			String who = ((ActivityBasicController)getController()).getWho();
			if(who==null){
				criteria = this.getController().getCriteria();
				LoggedUser logged = (LoggedUser) AonUtil.getRegisteredBean(LOGGED_USER_CONTROLLER_NAME);
				String name = logged.getLoggedUserName();
				criteria.addEqualExpression(getController().getFieldName(IEntityAlias.ASSET_ACTIVITY_HOLDER), name);			
			} else if(!who.equals("none")){
				criteria = this.getController().getCriteria();
				LoggedUser logged = (LoggedUser) AonUtil.getRegisteredBean(LOGGED_USER_CONTROLLER_NAME);
				String name = logged.getLoggedUserName();
				criteria.addEqualExpression(getController().getFieldName(IEntityAlias.ASSET_ACTIVITY_HOLDER), name);
			}
		} catch (ManagerBeanException e) {
			// nada
		}
		((ActivityBasicController)getController()).setAsset(new Asset());
	}

}
