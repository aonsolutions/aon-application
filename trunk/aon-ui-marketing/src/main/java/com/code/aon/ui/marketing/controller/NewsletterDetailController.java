package com.code.aon.ui.marketing.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.NewsletterDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class NewsletterDetailController extends LinesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterDetailController.class.getName());
	
	public Integer getNextPosition() {
		Integer position = null;
		try{
			String positionAlias = getFieldName(IEntityAlias.NEWSLETTER_DETAIL_POSITION);
			position = (Integer) getManagerBean().getUniqueResult(Projection.max(positionAlias), getCriteria());
		}catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return (position != null ? position+1 : 0);
	}
	
	public void reorderObjects() throws ManagerBeanException {
		List<ITransferObject> list = getManagerBean().getList( getCriteria() );
		for (int i = 0; i < list.size(); i++) {
			NewsletterDetail nd = (NewsletterDetail) list.get(i);
			if (nd.getPosition() != i) {
				nd.setPosition(i);
				getManagerBean().update( nd );
			}
		}
		initializeModel();
	}	
	
	private void move( int movement ) {
		try {		
			NewsletterDetail nd = (NewsletterDetail) getSelectedTO();
			int oldPosition = nd.getPosition();
			int newPosition = oldPosition + movement;
			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IEntityAlias.NEWSLETTER_DETAIL_POSITION), newPosition);
			List<ITransferObject> list = getManagerBean().getList(criteria);
			if (!list.isEmpty()) {
				NewsletterDetail otherPage = (NewsletterDetail) list.get(0);
				otherPage.setPosition(oldPosition);
				getManagerBean().update(otherPage);
			}
			nd.setPosition(newPosition);
			getManagerBean().update(nd);
		
			initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error("move", e);
			throw new AbortProcessingException(e.getMessage(), e);
		}			
	}
	
    public void onMoveUp(ActionEvent event) {
    	move(-1);
    }

    public void onMoveDown(ActionEvent event) {
    	move(1);    	
    }

}
