package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumConfig;
import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.AlbumImageDetail;
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
import com.code.aon.ui.cms.velocity.attribute.AlbumCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.AlbumHandler;
import com.code.aon.ui.cms.velocity.attribute.AlbumImageHandler;

public class AlbumGenerator extends Generator {

	public static void generate() {
		AlbumGenerator.generate(null);
	}

	public static void generate(AlbumCategory selectedCategory) {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		
		List<ITransferObject> albumCategoryList;
		List<ITransferObject> albumCategoryDetailList;
		List<ITransferObject> albumList;
		List<ITransferObject> albumDetailList;
		try {
			Section configSection = GeneratorConfigController.currentSection(AlbumConfig.class);
			IManagerBean albumCategoryBean = BeanManager.getManagerBean(AlbumCategory.class);
			IManagerBean albumCategoryDetailBean = BeanManager.getManagerBean(AlbumCategoryDetail.class);
			IManagerBean albumBean = BeanManager.getManagerBean(Album.class);
			IManagerBean albumDetailBean = BeanManager.getManagerBean(AlbumDetail.class);
			Criteria albumCategoryDetailCriteria;
			Criteria albumCriteria;
			Criteria albumDetailCriteria;
			Criteria albumCategoryCriteria = new Criteria();
			if (selectedCategory!=null)
				albumCategoryCriteria.addEqualExpression(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ID), selectedCategory.getId());
			albumCategoryCriteria.addEqualExpression(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ACTIVE), true);
			albumCategoryCriteria.addOrder(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_POSITION));
			albumCategoryList = (List<ITransferObject>)albumCategoryBean.getList(albumCategoryCriteria);
			ArrayList<AlbumCategoryHandler> achlist = new ArrayList<AlbumCategoryHandler>();
			AlbumCategory albumCategory;
			AlbumCategoryDetail albumCategoryDetail;
			Album album;
			AlbumDetail albumDetail;
			for (int j=0; j < albumCategoryList.size(); j++) {
				albumCategory = (AlbumCategory)albumCategoryList.get(j);
				if (albumCategory.getSection()!=null)
					CommonGenerator.getCommonGenerator().chargeContext(vu, albumCategory.getSection());
				else
					CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
				albumCategoryDetailCriteria = new Criteria();
				albumCategoryDetailCriteria.addEqualExpression(albumCategoryDetailBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_ALBUM_CATEGORY_ID), albumCategory.getId());
				albumCategoryDetailCriteria.addEqualExpression(albumCategoryDetailBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				albumCategoryDetailList = (List<ITransferObject>)albumCategoryDetailBean.getList(albumCategoryDetailCriteria);
				if (albumCategoryDetailList.isEmpty()) {
					VelocityUtil.addMessage("La categoria de albumes "+albumCategory.getAlias()+" no esta internacionalizada.", VelocityUtil.WARN);
				}else{
					albumCategoryDetail = (AlbumCategoryDetail)albumCategoryDetailList.get(0);
					albumCriteria = new Criteria();
					albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), albumCategory.getId());
					albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ACTIVE), true);
					albumCriteria.addOrder(albumBean.getFieldName(ICMSAlias.ALBUM_POSITION));
					albumList = (List<ITransferObject>)albumBean.getList(albumCriteria);
					ArrayList<AlbumHandler> ahlist = new ArrayList<AlbumHandler>(); 
					if (albumList.isEmpty()){
						VelocityUtil.addMessage("La categoria de albumes "+albumCategory.getAlias()+" no tiene albumes.", VelocityUtil.WARN);
					}else{
						AlbumCategoryHandler achandler = new AlbumCategoryHandler(albumCategoryDetail,ahlist);
						for (int i=0; i < albumList.size(); i++) {
							album = (Album)albumList.get(i);
							albumDetailCriteria = new Criteria();
							albumDetailCriteria.addEqualExpression(albumDetailBean.getFieldName(ICMSAlias.ALBUM_DETAIL_ALBUM_ID), album.getId());
							albumDetailCriteria.addEqualExpression(albumDetailBean.getFieldName(ICMSAlias.ALBUM_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
							albumDetailList = (List<ITransferObject>)albumDetailBean.getList(albumDetailCriteria);
							if (albumDetailList.isEmpty()) {
								VelocityUtil.addMessage("El albumes "+album.getAlias()+" no esta internacionalizada.", VelocityUtil.WARN);
							}else{
								albumDetail = (AlbumDetail)albumDetailList.get(0);
								ArrayList<AlbumImageHandler> accessList = getAlbumImageList(albumDetail);
								if (accessList != null && accessList.size() > 0) {
									AlbumHandler ahandler = new AlbumHandler(albumDetail);
									ahlist.add(ahandler);
									for (int k=0;k<accessList.size();k++){
										if (Math.abs(k / album.getItemsPerPage())==0){
											vu.put("back_url", ahandler.getUrl());
										}else if (Math.abs(k / album.getItemsPerPage())>0){
											vu.put("back_url", Templates.ALBUM.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(Math.abs(k / album.getItemsPerPage())+1)));
										}
										vu.put("album_image", accessList.get(k));
										if (k>0)
											vu.put("album_image_previous", accessList.get(k-1).getUrl());
										if (k+1<accessList.size())
											vu.put("album_image_next", accessList.get(k+1).getUrl());
										VelocityUtil.addMessage(" Generando imagen.", VelocityUtil.INFO);
										generate(vu, Templates.ALBUM_IMAGES, "ALBUM_IMAGE_"+accessList.get(k).getId());
										vu.remove("back_url");
										vu.remove("album_image");
										vu.remove("album_image_previous");
										vu.remove("album_image_next");
									}
									int page = 0;
									Iterator<AlbumImageHandler> iter = accessList.iterator();
									List<AlbumImageHandler> partialLst = new ArrayList<AlbumImageHandler>(); 
									while (iter.hasNext()){
										partialLst.add(iter.next());
										if (partialLst.size() == album.getItemsPerPage()
												|| !iter.hasNext()){
											page++;
											vu.put("album", ahandler);
											vu.put("back_url", achandler.getUrl() );
											if (page==2){
												vu.put("album_previous", ahandler.getUrl());
											}else if (page>2){
												vu.put("album_previous", Templates.ALBUM.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(page-1)));
											}
											if (iter.hasNext()){
												vu.put("album_next", Templates.ALBUM.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(page+1)));
											}
											vu.put("album_image_list", partialLst);
											VelocityUtil.addMessage(" Generando album de imagenes " + album.getAlias() + ".", VelocityUtil.INFO);
											generate(vu, Templates.ALBUM, album.getAlias()+(page==1?"":"_"+page));
											vu.remove("back_url");
											vu.remove("album_previous");
											vu.remove("album_next");
											vu.remove("album");
											vu.remove("album_image_list");
											partialLst = new ArrayList<AlbumImageHandler>();
										}
									}
									partialLst = null;
									iter = null;
								}
							}
						}
						vu.put("back_url", Templates.ALBUM_CATEGORY.getHtmlName().replaceAll("%NAME%", ALBUM_LIST_PAGE));
						vu.put("album_category", achandler);
						vu.put("album_list", ahlist);
						VelocityUtil.addMessage(" Generando list de album.", VelocityUtil.INFO);
						generate(vu, Templates.ALBUM_CATEGORY, albumCategory.getAlias());
						vu.remove("back_url");
						vu.remove("album_category");
						vu.remove("album_list");

						achlist.add(achandler);
					}
					ahlist = null;
				}
			}
			vu.put("album_category_list", achlist);
			VelocityUtil.addMessage(" Generando categorias de album.", VelocityUtil.INFO);
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			generate(vu, Templates.ALBUM_CATEGORY, ALBUM_LIST_PAGE);
			vu.remove("album_category_list");
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		}finally{
			albumCategoryList = null;
			albumCategoryDetailList = null;
			albumList = null;
			albumDetailList = null;
		}
		vu.finalize();
		vu = null;
	}

	public static ArrayList<AlbumImageHandler> getAlbumImageList(AlbumDetail albumDetail) {
		ArrayList<AlbumImageHandler> list = new ArrayList<AlbumImageHandler>();
		List<ITransferObject> albumImageList;
		List<ITransferObject> albumImageDetailList;
		try {
			IManagerBean albumImageBean = BeanManager.getManagerBean(AlbumImage.class);
			IManagerBean albumImageDetailBean = BeanManager.getManagerBean(AlbumImageDetail.class);
			Criteria criteria_detail;
			AlbumImage albumImage;
			AlbumImageDetail albumImageDetail;
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID), albumDetail.getAlbum().getId());
			criteria.addEqualExpression(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_ACTIVE), true);
			criteria.addOrder(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_POSITION));
			albumImageList = (List<ITransferObject>)albumImageBean.getList(criteria);
			if(albumImageList.isEmpty())
				VelocityUtil.addMessage("El album "+albumDetail.getAlbum().getAlias()+" no tiene imagenes.", VelocityUtil.WARN);
			for (int i = 0; i < albumImageList.size(); i++) {
				albumImage = (AlbumImage)albumImageList.get(i);
				criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(albumImageDetailBean.getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_ALBUM_IMAGE_ID), albumImage.getId());
				criteria_detail.addEqualExpression(albumImageDetailBean.getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				albumImageDetailList = (List<ITransferObject>)albumImageDetailBean.getList(criteria_detail);
				if (albumImageDetailList.isEmpty()) {
					VelocityUtil.addMessage("La imagen "+albumImage.getImage()+" no esta internacionalizada.", VelocityUtil.WARN);
				}else{
					albumImageDetail = (AlbumImageDetail)albumImageDetailList.get(0);
					AlbumImageHandler handler = new AlbumImageHandler(albumImageDetail);
					list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		}finally{
			albumImageList = null;
			albumImageDetailList = null;
		}
		return list;
	}
	
	public static Object getAlbumCategoryHandler(Integer ident) {
		List<ITransferObject> l;
		List<ITransferObject> ld;
		List<ITransferObject> lc;
		List<ITransferObject> lcd;
		Iterator<ITransferObject> iter;
		try {
			IManagerBean bean = BeanManager.getManagerBean(AlbumCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ID), ident);
			lc = bean.getList(criteria); 
			if (lc.isEmpty()){
				VelocityUtil.addMessage("CATEGORIA DE ALBUM "+ident+" REFERENCIADA NO EXISTE !!!", VelocityUtil.WARN);
				return null;
			}
			
			AlbumCategory albumCategory = (AlbumCategory) lc.get(0);
			
			bean = BeanManager.getManagerBean(AlbumCategoryDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_ALBUM_CATEGORY_ID), ident);
			lcd = (List<ITransferObject>)bean.getList(criteria);
			if (lcd.isEmpty()){
				VelocityUtil.addMessage("La categoria de albumes "+albumCategory.getAlias()+" no esta internacionalizado.", VelocityUtil.WARN);
				return null;
			}
			AlbumCategoryDetail acd = (AlbumCategoryDetail) lcd.get(0);
			
			bean = BeanManager.getManagerBean(Album.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), ident);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.ALBUM_POSITION));
			l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty())
				VelocityUtil.addMessage("La categoria de albumes "+albumCategory.getAlias()+" no tiene albumes.", VelocityUtil.WARN);
			iter = l.iterator();
			ArrayList<AlbumHandler> ahlist = new ArrayList<AlbumHandler>();
			Album album;
			while (iter.hasNext()){
				album = (Album)iter.next();
				bean = BeanManager.getManagerBean(AlbumDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_DETAIL_ALBUM_ID), album.getId());
				ld = (List<ITransferObject>)bean.getList(criteria);
				if (ld.isEmpty()){
					VelocityUtil.addMessage("El album "+album.getAlias()+" no esta internacionalizado.", VelocityUtil.WARN);
				}else{
					AlbumDetail ad = (AlbumDetail)ld.get(0);
					AlbumHandler ah = new AlbumHandler(ad);
					ahlist.add(ah);
				}
			}
			AlbumCategoryHandler ach = new AlbumCategoryHandler(acd,ahlist);
			return ach;
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
			l = null;
			ld = null;
			lcd = null;
			iter = null;
		}
		return null;
	}

	public static String ALBUM_LIST_PAGE = "album_categories";
}
