package com.code.aon.ui.marketing.event;

import com.code.aon.registry.Category;
import com.code.aon.registry.enumeration.CategoryType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ChannelControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Category category = (Category) event.getController().getTo();
		category.setType(CategoryType.ARTICLE);
	}

}
