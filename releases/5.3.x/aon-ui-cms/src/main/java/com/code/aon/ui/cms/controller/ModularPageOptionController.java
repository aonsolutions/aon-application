package com.code.aon.ui.cms.controller;

import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.util.AonUtil;


public class ModularPageOptionController extends BasicI18nController implements IOrderedControllerListener, ICMSConstants, Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(ModularPageOptionController.class);
	
	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.MODULAR_PAGE_OPTION_POSITION);

	private ModularPage currentModularPage;
	
	public ModularPage getCurrentModularPage() {
		return currentModularPage;
	}

	public void setCurrentModularPage(ModularPage currentModularPage) {
		this.currentModularPage = currentModularPage;
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
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
		List<SelectItem> idents;
		CollectionsController collections = (CollectionsController) AonUtil.getRegisteredBean(COLLECTIONS);
		switch ( mo.getType() ) {
			case ACTIVITY:
				idents = collections.getActivityList();
				break;
			case ALBUM_CATEGORY:
				idents = collections.getAlbumCategoryList();
				break;				
			case ARTICLE:
				idents = collections.getArticleList();
				break;
			case ARTICLE_EVENTS:
			case ARTICLE_NEWS:
			case ARTICLE_OTHER:
			case ARTICLE_SERVICES:
				idents = collections.getArticleCategoryList();
				break; 
			case BANNER:
				idents = collections.getBannerList();
				break;
			case BANNER_GROUP:
				idents = collections.getBannerGroupList();
				break;
			case GENERIC:
				idents = collections.getGenericPageList();
				break;
			case DIRECT_ACCESS:
				idents = collections.getDirectAccessList();
				break;
			case DIRECT_ACCESS_GROUP:
				idents = collections.getDirectAccessGroupList();
				break;
			case DOWNLOADS:
				idents = collections.getDownloadCategoryList();
				break;				
			case LINK_CATEGORY:
				idents = collections.getLinkCategoryList();
				break;
			default:
				idents = Collections.emptyList();
		}
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
			LOGGER.error(e.getMessage(), e);
		}
	}

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}