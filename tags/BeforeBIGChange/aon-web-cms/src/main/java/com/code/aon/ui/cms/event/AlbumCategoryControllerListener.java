package com.code.aon.ui.cms.event;

import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.AlbumCategoryController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlbumCategoryControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		AlbumCategoryController controller = (AlbumCategoryController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(AlbumCategory.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_POSITION));
			event.getController().setCriteria(criteria);
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AlbumCategoryController controller = (AlbumCategoryController) event.getController();
		AlbumCategory albumCategory = (AlbumCategory)controller.getTo();
		albumCategory.setActive(true);
		albumCategory.setPosition(controller.orderedControllerSupport.getLastPosition(controller));
		assignSection(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		assignSection(event);
	}
	
	private void assignSection(ControllerEvent event){
		AlbumCategory to = (AlbumCategory)event.getController().getTo();
		if (to.getSection().getId()==-1){
			to.setSection(null);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try{
			((AlbumCategoryController) event.getController()).onSelectAlbums(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try{
			((AlbumCategoryController) event.getController()).onSelectAlbums(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
}