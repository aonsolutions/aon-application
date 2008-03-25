package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;

public class DownloadController extends BasicI18nController {

	private DownloadCategory currentDownloadCategory;
	
	public DownloadCategory getCurrentDownloadCategory() {
		return currentDownloadCategory;
	}

	public void setCurrentDownloadCategory(DownloadCategory currentDownloadCategory) {
		this.currentDownloadCategory = currentDownloadCategory;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(new ActionEvent(event.getComponent()));
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Download d = (Download)this.model.getRowData();
		d.setActive(active);
		getManagerBean().update(d);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = "- NO VALUE -";
		DownloadDetail downloadDetail = (DownloadDetail)getModelRowdataI18n();
		if (downloadDetail != null) title = downloadDetail.getTitle();
		return title;
	}

	@SuppressWarnings("unchecked")
	private void move( Download d, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = d.getPosition();
		int newPosition = oldPosition + movement;
		d.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_ID), ""+d.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Download download = (Download)list.get(0);
			download.setPosition(newPosition);
			getManagerBean().update(download);
		}
    	List<Download> listObjects = (List<Download>) this.model.getWrappedData();
    	Download dMoved = listObjects.get( newPosition );
		dMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_ID), ""+dMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Download download = (Download)list.get(0);
			download.setPosition(oldPosition);
			getManagerBean().update(download);
		}
		listObjects.set( newPosition, d);
		listObjects.set( oldPosition, dMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((Download) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((Download) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException{
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_DOWNLOAD_CATEGORY_ID), "" + getCurrentDownloadCategory().getId());
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Download d = (Download)list.get(i);
			int oldPosition = d.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				d.setPosition(newPosition);
				getManagerBean().update(d);
			}
		}
	}
	
	public void onDelImage(ActionEvent event) {
		DownloadDetail current = (DownloadDetail)getToI18n();
		current.setFile(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("document");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DownloadDetail current = (DownloadDetail)getToI18n();
		current.setFile(image);
	}
}