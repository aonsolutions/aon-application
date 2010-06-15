package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Image;
import com.code.aon.cms.SportClub;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;


public class SportClubController extends BasicController implements ICMSConstants {
	
	public void onDelImage(ActionEvent event) {
		SportClub current = (SportClub)getTo();
		current.setImage(null);
	}

	public void onDelLogo(ActionEvent event) {
		SportClub current = (SportClub)getTo();
		current.setLogo(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		SportClub current = (SportClub)getTo();
		current.setImage(image);
	}

	public void onSelectThumbnail(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		SportClub current = (SportClub)getTo();
		current.setLogo(image);
	}

	public void defaultChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		SportClub row = (SportClub) model.getRowData();
		updateDefault_(row);
		row.setDefault_(true);
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefault_(SportClub default_) throws ManagerBeanException, ExpressionException {

		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			SportClub row = (SportClub)list.get(i);
			row.setDefault_(false);
		}
		
		IManagerBean bean = BeanManager.getManagerBean(SportClub.class);
		list = (List<ITransferObject>)bean.getList(null);
		for (int i = 0; i < list.size(); i++) {
			SportClub row = (SportClub)list.get(i);
			row.setDefault_(false);
			bean.update(row);
		}

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_CLUB_ID), default_.getId());
		list = bean.getList(criteria);
		if (list.size() > 0) {
			SportClub row = (SportClub) list.get(0);
			row.setDefault_(true);
			bean.update(row);
		}
	}
}