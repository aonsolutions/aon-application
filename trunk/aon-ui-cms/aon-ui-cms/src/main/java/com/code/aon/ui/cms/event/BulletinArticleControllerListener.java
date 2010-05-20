package com.code.aon.ui.cms.event;

import com.code.aon.cms.BulletinArticle;
import com.code.aon.ui.cms.controller.BulletinArticleController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class BulletinArticleControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BulletinArticleController controller = (BulletinArticleController)event.getController();
		BulletinArticle to = (BulletinArticle)event.getController().getTo();
		to.setBulletin(controller.getCurrentBulletin());
	}

}
