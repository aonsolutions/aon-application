package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.util.AonUtil;

public class AlbumController extends BasicI18nController implements IOrderedControllerListener {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.ALBUM_POSITION);

	private AlbumCategory currentAlbumCategory;
	
	public AlbumCategory getCurrentAlbumCategory() {
		return currentAlbumCategory;
	}

	public void setCurrentAlbumCategory(AlbumCategory currentAlbumCategory) {
		this.currentAlbumCategory = currentAlbumCategory;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Album a = (Album)this.model.getRowData();
		a.setActive(active);
		getManagerBean().update(a);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = "- NO VALUE -";
		AlbumDetail ad = (AlbumDetail)getModelRowdataI18n();
		if (ad != null) title = ad.getTitle();
		return title;
	}


	public void onSelectAlbumImages(ActionEvent event) throws ManagerBeanException, ExpressionException {
		AlbumImageController aic = (AlbumImageController)AonUtil.getController("albumImage");
		IManagerBean albumImageBean = BeanManager.getManagerBean(AlbumImage.class);
		Album album = (Album) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID), "" + album.getId());
		criteria.addOrder(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_POSITION));
		aic.setCurrentAlbum(album);
		aic.setCriteria(criteria);
		aic.onSearch(event);
	}

	
	public void onDelImage(ActionEvent event) {
		Album current = (Album)getTo();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Album current = (Album)getTo();
		current.setImage(image);
	}

    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public void fireBeforeUseCriteria(Criteria criteria) {
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), "" + getCurrentAlbumCategory().getId());
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}

}