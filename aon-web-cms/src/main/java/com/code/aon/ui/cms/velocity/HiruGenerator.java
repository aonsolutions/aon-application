package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.HiruConfig;
import com.code.aon.cms.HiruOrganizerCentre;
import com.code.aon.cms.HiruCourse;
import com.code.aon.cms.HiruCourseDetail;
import com.code.aon.cms.Section;
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
import com.code.aon.ui.cms.velocity.attribute.HiruCenterHandler;
import com.code.aon.ui.cms.velocity.attribute.HiruCourseHandler;

public class HiruGenerator extends Generator {
	
	public static void generate() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		List<ITransferObject> center_list = null;
		ArrayList<HiruCenterHandler> center_handler_list = null;
		ArrayList<HiruCourseHandler> course_handler_list = null;
		Criteria criteria;
		Criteria criteria_detail;
		try {
			IManagerBean centerbean = BeanManager.getManagerBean(HiruOrganizerCentre.class);
			IManagerBean courseBean = BeanManager.getManagerBean(HiruCourse.class);
			IManagerBean courseDetailBean = BeanManager.getManagerBean(HiruCourseDetail.class);
			center_list = (List<ITransferObject>)centerbean.getList(null);
			center_handler_list = new ArrayList<HiruCenterHandler>();
			for (int i=0; i < center_list.size(); i++) {
				HiruOrganizerCentre hoc = (HiruOrganizerCentre)center_list.get(i);
				
				course_handler_list = new ArrayList<HiruCourseHandler>();
				
				criteria = new Criteria();
				criteria.addEqualExpression(courseBean.getFieldName(ICMSAlias.HIRU_COURSE_HIRU_ORGANIZER_CENTRE_ID), hoc.getId());
				criteria.addEqualExpression(courseBean.getFieldName(ICMSAlias.HIRU_COURSE_ACTIVE), true);
				List<ITransferObject> l = (List<ITransferObject>)courseBean.getList(criteria);
				if (l.isEmpty())
					VelocityUtil.addMessage("El centro "+hoc.getName()+" no tiene cursos", VelocityUtil.WARN);
				for (int j = 0; j < l.size(); j++) {
					HiruCourse hc = (HiruCourse)l.get(j);
					criteria_detail = new Criteria();
					criteria_detail.addEqualExpression(courseDetailBean.getFieldName(ICMSAlias.HIRU_COURSE_DETAIL_HIRU_COURSE_ID), hc.getId());
					criteria_detail.addEqualExpression(courseDetailBean.getFieldName(ICMSAlias.HIRU_COURSE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
					List<ITransferObject> ld = (List<ITransferObject>)courseDetailBean.getList(criteria_detail);
					if (ld.isEmpty()) {
						VelocityUtil.addMessage("La curso "+hc.getAlias()+" no esta internacionalizada", VelocityUtil.WARN);
					}else{
						HiruCourseDetail hcd = (HiruCourseDetail)ld.get(0);
						HiruCourseHandler hcoh = new HiruCourseHandler(hcd);
						course_handler_list.add(hcoh);
					}
				}

				HiruCenterHandler hch = new HiruCenterHandler(hoc,course_handler_list);
				center_handler_list.add(hch);
			}
			if (center_handler_list != null && center_handler_list.size() > 0) {  
				Section configSection = GeneratorConfigController.currentSection(HiruConfig.class);
				CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
				vu.put("center_list", center_handler_list);
				VelocityUtil.addMessage(" Generando hiru.", VelocityUtil.INFO);
				generate(vu, Templates.HIRU_COURSES, "hiru");
				vu.remove("center_list");
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);
		} finally {
			vu.finalize();
			vu = null;
			center_list = null;
			course_handler_list = null;
			center_handler_list = null;
		}
	}

}
