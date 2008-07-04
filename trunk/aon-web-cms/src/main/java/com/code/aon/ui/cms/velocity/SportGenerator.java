package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.Section;
import com.code.aon.cms.SportCareerPath;
import com.code.aon.cms.SportCategoryDetail;
import com.code.aon.cms.SportClub;
import com.code.aon.cms.SportCoach;
import com.code.aon.cms.SportConfig;
import com.code.aon.cms.SportPlayer;
import com.code.aon.cms.SportPosition;
import com.code.aon.cms.SportPositionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.SportCareerHandler;
import com.code.aon.ui.cms.velocity.attribute.SportCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.SportClubHandler;
import com.code.aon.ui.cms.velocity.attribute.SportCoachHandler;
import com.code.aon.ui.cms.velocity.attribute.SportPlayerHandler;
import com.code.aon.ui.cms.velocity.attribute.SportPositionHandler;

public class SportGenerator extends Generator {

	public static void generate() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		List<ITransferObject> categoryDetailList;
		List<ITransferObject> clubList;
		List<ITransferObject> positionList;
		List<ITransferObject> playerList;
		try {
			IManagerBean scdBean = BeanManager.getManagerBean(SportCategoryDetail.class);
			IManagerBean scBean = BeanManager.getManagerBean(SportClub.class);
			IManagerBean spBean = BeanManager.getManagerBean(SportPosition.class);
			IManagerBean splBean = BeanManager.getManagerBean(SportPlayer.class);
			Criteria scCriteria;
			Criteria spCriteria;
			Criteria splCriteria;
			SportCategoryDetail scd;
			SportClub sc;
			SportPosition sp;
			SportPlayer spl;
			
			List<SportCategoryHandler> sportCategoryHandlerList = new ArrayList<SportCategoryHandler>();
			categoryDetailList = (List<ITransferObject>)scdBean.getList(null);
			for (int i=0; i < categoryDetailList.size(); i++) {
				scd = (SportCategoryDetail)categoryDetailList.get(i);
				SportCategoryHandler sportCategoryHandler = new SportCategoryHandler(scd);

				scCriteria = new Criteria();
				scCriteria.addEqualExpression(scBean.getFieldName(ICMSAlias.SPORT_CLUB_SPORT_CATEGORY_ID), scd.getSportCategory().getId());
				clubList = (List<ITransferObject>)scBean.getList(scCriteria);
				for (int j=0; j < clubList.size(); j++) {
					sc = (SportClub)clubList.get(j);
					SportClubHandler sportClubHandler = new SportClubHandler(sc);
					sportCategoryHandler.addSportClubHandler(sportClubHandler);
					
					IManagerBean bean = BeanManager.getManagerBean(SportCoach.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_COACH_SPORT_CLUB_ID), sc.getId());
					List<ITransferObject> lst = bean.getList(criteria);
					for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
						sportClubHandler.addSportCoachHandler(new SportCoachHandler((SportCoach) iterator.next()));
					}
					
					spCriteria = new Criteria();
					spCriteria.addOrder(spBean.getFieldName(ICMSAlias.SPORT_POSITION_ALIAS));
					positionList = (List<ITransferObject>)spBean.getList(spCriteria);
					for (int k=0; k < positionList.size(); k++) {
						sp = (SportPosition)positionList.get(k);
						SportPositionHandler sportPositionHandler = new SportPositionHandler(sp); 
						
						splCriteria = new Criteria();
						splCriteria.addEqualExpression(splBean.getFieldName(ICMSAlias.SPORT_PLAYER_SPORT_CLUB_ID), sc.getId());
						splCriteria.addEqualExpression(splBean.getFieldName(ICMSAlias.SPORT_PLAYER_SPORT_POSITION_ID), sp.getId());
						splCriteria.addEqualExpression(splBean.getFieldName(ICMSAlias.SPORT_PLAYER_ACTIVE), true);
						splCriteria.addOrder(splBean.getFieldName(ICMSAlias.SPORT_PLAYER_NAME));
						playerList = (List<ITransferObject>)splBean.getList(splCriteria);
						
						for (int l=0; l < playerList.size(); l++) {
							spl = (SportPlayer)playerList.get(l);
							
							SportPlayerHandler sportPlayerHandler = new SportPlayerHandler(spl);
							sportPositionHandler.addSportPlayerHandler(sportPlayerHandler);
						}
						sportClubHandler.addSportPositionHandler(sportPositionHandler);
					}
				}
				sportCategoryHandlerList.add(sportCategoryHandler);
			}
			
			Section configSection = GeneratorConfigController.currentSection(SportConfig.class);
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			for (Iterator iterCat = sportCategoryHandlerList.iterator(); iterCat.hasNext();) {
				SportCategoryHandler categoryHandler = (SportCategoryHandler) iterCat.next();
				vu.put("category", categoryHandler);
				VelocityUtil.addMessage(" Generando category " + categoryHandler.getAlias() + ".", VelocityUtil.INFO);
				generate(vu, Templates.SPORT, CATEGORY + categoryHandler.getAlias());
				
				for (Iterator iterClub = categoryHandler.getClubs().iterator(); iterClub.hasNext();) {
					SportClubHandler clubHandler = (SportClubHandler) iterClub.next();
					vu.put("club", clubHandler);
					VelocityUtil.addMessage(" Generando club " + clubHandler.getAlias() + ".", VelocityUtil.INFO);
					generate(vu, Templates.SPORT, CLUB + clubHandler.getAlias());

					for (Iterator iterPos = clubHandler.getPositions().iterator(); iterPos.hasNext();) {
						SportPositionHandler posHandler = (SportPositionHandler) iterPos.next();
						for (Iterator iterPlayer = posHandler.getPlayers().iterator(); iterPlayer.hasNext();) {
							SportPlayerHandler playerHandler = (SportPlayerHandler) iterPlayer.next();
							vu.put("player", playerHandler);
							VelocityUtil.addMessage(" Generando player " + playerHandler.getAlias() + ".", VelocityUtil.INFO);
							generate(vu, Templates.SPORT, PLAYER + playerHandler.getAlias());
							vu.remove("player");
						}
					}
					vu.remove("club");
				}
				
				
				VelocityUtil.addMessage(" Generando category index.", VelocityUtil.INFO);
				vu.put("categoryList",sportCategoryHandlerList);
				generate(vu, Templates.SPORT, MAIN_PAGE );
				vu.remove("categoryList");
				
				vu.remove("category");
			}
		} catch (Exception e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);
		} finally{
			categoryDetailList = null;
			clubList = null;
			positionList = null;
			playerList = null;
		}
		vu.finalize();
		vu = null;
	}
	
	public static String CATEGORY = "category_";
	
	public static String CLUB = "club_";
	
	public static String PLAYER = "player_";
	
	public static String MAIN_PAGE = "index";
}
