package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.SportPosition;
import com.code.aon.cms.SportPositionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;

public class SportPositionHandler {

	private String description;

	private List<SportPlayerHandler> players;
	
	public SportPositionHandler(SportPosition sportPosition){
		this.description = getPositionString(sportPosition);
		this.players = new ArrayList<SportPlayerHandler>();
	}

	private String getPositionString(SportPosition sp){
		try {
			IManagerBean bean = BeanManager.getManagerBean(SportPositionDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_POSITION_DETAIL_SPORT_POSITION_ID), sp.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_POSITION_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> lst = bean.getList(criteria);
			if (!lst.isEmpty()){
				return ((SportPositionDetail)lst.get(0)).getDescription();
			}
		} catch (ManagerBeanException e) {
		}
		return null;
	}
	
	public void addSportPlayerHandler(SportPlayerHandler sportPlayerHandler){
		this.players.add(sportPlayerHandler);
	}

	public String getDescription() {
		return description;
	}

	public List<SportPlayerHandler> getPlayers() {
		return players;
	}
	
}
