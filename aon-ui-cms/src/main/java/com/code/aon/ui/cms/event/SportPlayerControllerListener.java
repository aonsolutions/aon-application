package com.code.aon.ui.cms.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.SportCareerPath;
import com.code.aon.cms.SportPlayer;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.cms.controller.SportCareerPathController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SportPlayerControllerListener extends ControllerAdapter implements ICMSConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(SportPlayerControllerListener.class);
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try{
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(SportPlayer.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_PLAYER_ACTIVE));
			criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_PLAYER_SPORT_CLUB_ID));
			criteria.addOrder(bean.getFieldName(ICMSAlias.SPORT_PLAYER_NAME));
			event.getController().setCriteria(criteria);
		}catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try{
			SportCareerPathController c = (SportCareerPathController)FormUtil.getController(SPORT_CAREER_PATH);
			IManagerBean moBean = BeanManager.getManagerBean(SportCareerPath.class);
			SportPlayer sportPlayer = (SportPlayer) event.getController().getTo();
			Criteria criteria = new Criteria();
			criteria.addExpression(moBean.getFieldName(ICMSAlias.SPORT_CAREER_PATH_SPORT_PLAYER_ID), "" + sportPlayer.getId());
			c.setSportPlayer(sportPlayer);
			c.setCriteria(criteria);
			c.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
