package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumConfig;
import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.AlbumImageDetail;
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
import com.code.aon.ui.cms.velocity.attribute.AlbumCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.AlbumHandler;
import com.code.aon.ui.cms.velocity.attribute.AlbumImageHandler;

public class AlbumGenerator extends Generator {
	
	public static ArrayList<AlbumImageHandler> getAlbumImageList(AlbumDetail albumDetail) {
		ArrayList<AlbumImageHandler> list = new ArrayList<AlbumImageHandler>();

		try {
			IManagerBean albumImageBean = BeanManager.getManagerBean(AlbumImage.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID), albumDetail.getAlbum().getId());
			criteria.addEqualExpression(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_ACTIVE), true);
			criteria.addOrder(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)albumImageBean.getList(criteria);
			for (int i = 0; i < l.size(); i++) {
				AlbumImage albumImage = (AlbumImage)l.get(i);
				IManagerBean albumImageDetailBean = BeanManager.getManagerBean(AlbumImageDetail.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(albumImageDetailBean.getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_ALBUM_IMAGE_ID), albumImage.getId());
				criteria_detail.addEqualExpression(albumImageDetailBean.getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> ld = (List<ITransferObject>)albumImageDetailBean.getList(criteria_detail);
				if (ld.size() > 0) {
					AlbumImageDetail detail = (AlbumImageDetail)ld.get(0);
					AlbumImageHandler handler = new AlbumImageHandler(detail);
					list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static void generate(VelocityUtil vu) {
		try {
			IManagerBean albumCategoryBean = BeanManager.getManagerBean(AlbumCategory.class);
			Criteria albumCategoryCriteria = new Criteria();
			albumCategoryCriteria.addEqualExpression(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ACTIVE), true);
			List<ITransferObject> albumCategoryList = (List<ITransferObject>)albumCategoryBean.getList(albumCategoryCriteria);
			ArrayList<AlbumCategoryHandler> achlist = new ArrayList<AlbumCategoryHandler>(); 
			for (int j=0; j < albumCategoryList.size(); j++) {
				AlbumCategory albumCategory = (AlbumCategory)albumCategoryList.get(j);
				CommonGenerator.chargeContext(vu, albumCategory.getSection());
				IManagerBean albumCategoryDetailBean = BeanManager.getManagerBean(AlbumCategoryDetail.class);
				Criteria albumCategoryDetailCriteria = new Criteria();
				albumCategoryDetailCriteria.addEqualExpression(albumCategoryDetailBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_ALBUM_CATEGORY_ID), albumCategory.getId());
				albumCategoryDetailCriteria.addEqualExpression(albumCategoryDetailBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> albumCategoryDetailList = (List<ITransferObject>)albumCategoryDetailBean.getList(albumCategoryDetailCriteria);
				if (albumCategoryDetailList.size() > 0) {
					AlbumCategoryDetail albumCategoryDetail = (AlbumCategoryDetail)albumCategoryDetailList.get(0);
					IManagerBean albumBean = BeanManager.getManagerBean(Album.class);
					Criteria albumCriteria = new Criteria();
					albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), albumCategory.getId());
					albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ACTIVE), true);
					List<ITransferObject> albumList = (List<ITransferObject>)albumBean.getList(albumCriteria);
					ArrayList<AlbumHandler> ahlist = new ArrayList<AlbumHandler>(); 
					for (int i=0; i < albumList.size(); i++) {
						Album album = (Album)albumList.get(i);
						IManagerBean albumDetailBean = BeanManager.getManagerBean(AlbumDetail.class);
						Criteria albumDetailCriteria = new Criteria();
						albumDetailCriteria.addEqualExpression(albumDetailBean.getFieldName(ICMSAlias.ALBUM_DETAIL_ALBUM_ID), album.getId());
						albumDetailCriteria.addEqualExpression(albumDetailBean.getFieldName(ICMSAlias.ALBUM_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
						List<ITransferObject> albumDetailList = (List<ITransferObject>)albumDetailBean.getList(albumDetailCriteria);
						if (albumDetailList.size() > 0) {
							AlbumDetail albumDetail = (AlbumDetail)albumDetailList.get(0);
							AlbumHandler ahandler = new AlbumHandler(albumDetail);
							ahlist.add(ahandler);
							ArrayList<AlbumImageHandler> accessList = getAlbumImageList(albumDetail);
							if (accessList != null && accessList.size() > 0) {
								vu.put("album", ahandler);
								vu.put("album_image_list", accessList);
								vu.addMessage(" Generando album de imagenes " + album.getAlias() + ".", VelocityUtil.INFO);
								CommonGenerator.chargeContext(vu, albumCategory.getSection());
								generate(vu, Templates.ALBUM_IMAGES, album.getAlias());
								vu.remove("album");
								vu.remove("album_image_list");
							}
						}
					}
					AlbumCategoryHandler achandler = new AlbumCategoryHandler(albumCategoryDetail,ahlist);
					
					vu.put("album_category", achandler);
					vu.put("album_list", ahlist);
					vu.addMessage(" Generando list de album.", VelocityUtil.INFO);
					CommonGenerator.chargeContext(vu, albumCategory.getSection());
					generate(vu, Templates.ALBUM_IMAGES, albumCategory.getAlias());
					vu.remove("album_category");
					vu.remove("album_list");
					
					achlist.add(achandler);
				}
			}
			vu.put("album_category_list", achlist);
			vu.addMessage(" Generando categorias de album.", VelocityUtil.INFO);
			CommonGenerator.chargeContext(vu, ((AlbumConfig)GeneratorConfigController.currentConfig(AlbumConfig.class)).getSection());
			generate(vu, Templates.ALBUM_IMAGES, ALBUM_LIST_PAGE);
			vu.remove("album_category_list");
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public static String ALBUM_LIST_PAGE = "album_categories";
}
