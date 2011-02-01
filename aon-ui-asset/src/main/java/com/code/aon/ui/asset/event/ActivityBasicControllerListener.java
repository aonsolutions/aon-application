package com.code.aon.ui.asset.event;

import com.code.aon.asset.Asset;
import com.code.aon.asset.dao.IAssetAlias;
import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.asset.controller.ActivityBasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class ActivityBasicControllerListener extends ControllerAdapter{
	
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
				LoggedUser logged = (LoggedUser)AonUtil.getRegisteredBean("loggedUser");
				String name = logged.getLoggedUserName();
				criteria.addEqualExpression(getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO), name);			
			} else if(!who.equals("none")){
				criteria = this.getController().getCriteria();
				LoggedUser logged = (LoggedUser)AonUtil.getRegisteredBean("loggedUser");
				String name = logged.getLoggedUserName();
				criteria.addEqualExpression(getController().getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO), name);
			}
		} catch (ManagerBeanException e) {
			// nada
		}
		((ActivityBasicController)getController()).setAsset(new Asset());
	}

}
