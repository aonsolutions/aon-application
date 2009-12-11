package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.util.AonUtil;


public class ModularPageOptionController extends BasicI18nController implements IOrderedControllerListener {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.MODULAR_PAGE_OPTION_POSITION);

	private ModularPage currentModularPage;
	
	public ModularPage getCurrentModularPage() {
		return currentModularPage;
	}

	public void setCurrentModularPage(ModularPage currentModularPage) {
		this.currentModularPage = currentModularPage;
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		ModularPageOption mp = (ModularPageOption)this.model.getRowData();
		mp.setActive(active);
		getManagerBean().update(mp);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		ModularPageOptionDetail mpd = (ModularPageOptionDetail)getModelRowdataI18n();
		if (mpd != null) label = mpd.getLabel();
		return label;
	}


	public boolean isVisibleIdent() {
		ModularPageOption to = (ModularPageOption)getTo();
		if (to != null && 
				to.getType() != null && 
				!to.getType().equals(ModularPageOptionType.BULLETIN_SUSCRIBE) &&
				!to.getType().equals(ModularPageOptionType.NEXT_ARTICLES)) {
			return true;
		}
		return false;
	}

	public List<SelectItem> getIdents() throws ManagerBeanException, ExpressionException {
		ModularPageOption mo = (ModularPageOption)getTo();
		List<SelectItem> idents = new LinkedList<SelectItem>();
		if (mo.getType().equals(ModularPageOptionType.GENERIC)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getGenericPageList();
		else if (mo.getType().equals(ModularPageOptionType.BANNER)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getBannerList();
		else if (mo.getType().equals(ModularPageOptionType.BANNER_GROUP)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getBannerGroupList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE_NEWS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE_EVENTS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE_SERVICES)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.ARTICLE_OTHER)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getArticleCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.DIRECT_ACCESS_GROUP)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getDirectAccessGroupList();
		else if (mo.getType().equals(ModularPageOptionType.DOWNLOADS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getDownloadCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.DIRECT_ACCESS)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getDirectAccessList();
		else if (mo.getType().equals(ModularPageOptionType.LINK_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getLinkCategoryList();
		else if (mo.getType().equals(ModularPageOptionType.ACTIVITY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getActivityList();
		else if (mo.getType().equals(ModularPageOptionType.ALBUM_CATEGORY)) idents = ((CollectionsController)AonUtil.getRegisteredBean("collections")).getAlbumCategoryList();
		return idents;
	}

    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	public void fireBeforeUseCriteria(Criteria criteria) {
		try {
			criteria.addEqualExpression(getManagerBean().getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_MODULAR_PAGE_ID),currentModularPage.getId());
		} catch (ManagerBeanException e) {
		}
	}

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

}