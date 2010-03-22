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
			for (int i = 0; i < albumImageList.size(); i++) {
				albumImage = (AlbumImage)albumImageList.get(i);
				criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(albumImageDetailBean.getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_ALBUM_IMAGE_ID), albumImage.getId());
				criteria_detail.addEqualExpression(albumImageDetailBean.getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				albumImageDetailList = (List<ITransferObject>)albumImageDetailBean.getList(criteria_detail);
				if (albumImageDetailList.size() > 0) {
					albumImageDetail = (AlbumImageDetail)albumImageDetailList.get(0);
					AlbumImageHandler handler = new AlbumImageHandler(albumImageDetail);
					list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}finally{
			albumImageList = null;
			albumImageDetailList = null;
		}
		return list;
	}
	
	public static void generate() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Buscando plantilla seleccionada '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando album imagenes... ", VelocityUtil.INFO);
		
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
			albumCategoryCriteria.addEqualExpression(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ACTIVE), true);
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
				if (albumCategoryDetailList.size() > 0) {
					albumCategoryDetail = (AlbumCategoryDetail)albumCategoryDetailList.get(0);
					albumCriteria = new Criteria();
					albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), albumCategory.getId());
					albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ACTIVE), true);
					albumList = (List<ITransferObject>)albumBean.getList(albumCriteria);
					ArrayList<AlbumHandler> ahlist = new ArrayList<AlbumHandler>(); 
					
					if (albumList.size()>0){
						AlbumCategoryHandler achandler = new AlbumCategoryHandler(albumCategoryDetail,ahlist);
						for (int i=0; i < albumList.size(); i++) {
							album = (Album)albumList.get(i);
							albumDetailCriteria = new Criteria();
							albumDetailCriteria.addEqualExpression(albumDetailBean.getFieldName(ICMSAlias.ALBUM_DETAIL_ALBUM_ID), album.getId());
							albumDetailCriteria.addEqualExpression(albumDetailBean.getFieldName(ICMSAlias.ALBUM_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
							albumDetailList = (List<ITransferObject>)albumDetailBean.getList(albumDetailCriteria);
							if (albumDetailList.size() > 0) {
								albumDetail = (AlbumDetail)albumDetailList.get(0);
								ArrayList<AlbumImageHandler> accessList = getAlbumImageList(albumDetail);
								if (accessList != null && accessList.size() > 0) {
									AlbumHandler ahandler = new AlbumHandler(albumDetail);
									ahlist.add(ahandler);
									for (int k=0;k<accessList.size();k++){
										if (Math.abs(k / album.getItemsPerPage())==0){
											vu.put("back_url", ahandler.getUrl());
										}else if (Math.abs(k / album.getItemsPerPage())>0){
											vu.put("back_url", Templates.ALBUM_IMAGES.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(Math.abs(k / album.getItemsPerPage())+1)));
										}
										vu.put("album_image", accessList.get(k));
										if (k>0)
											vu.put("album_image_previous", accessList.get(k-1).getUrl());
										if (k+1<accessList.size())
											vu.put("album_image_next", accessList.get(k+1).getUrl());
										vu.addMessage(" Generando imagen.", VelocityUtil.INFO);
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
												vu.put("album_previous", Templates.ALBUM_IMAGES.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(page-1)));
											}
											if (iter.hasNext()){
												vu.put("album_next", Templates.ALBUM_IMAGES.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(page+1)));
											}
											vu.put("album_image_list", partialLst);
											vu.addMessage(" Generando album de imagenes " + album.getAlias() + ".", VelocityUtil.INFO);
											generate(vu, Templates.ALBUM_IMAGES, album.getAlias()+(page==1?"":"_"+page));
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
						vu.put("back_url", Templates.ALBUM_IMAGES.getHtmlName().replaceAll("%NAME%", ALBUM_LIST_PAGE));
						vu.put("album_category", achandler);
						vu.put("album_list", ahlist);
						vu.addMessage(" Generando list de album.", VelocityUtil.INFO);
						generate(vu, Templates.ALBUM_IMAGES, albumCategory.getAlias());
						vu.remove("back_url");
						vu.remove("album_category");
						vu.remove("album_list");

						achlist.add(achandler);
					}
					ahlist = null;
				}
			}
			vu.put("album_category_list", achlist);
			vu.addMessage(" Generando categorias de album.", VelocityUtil.INFO);
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			generate(vu, Templates.ALBUM_IMAGES, ALBUM_LIST_PAGE);
			vu.remove("album_category_list");
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}finally{
			albumCategoryList = null;
			albumCategoryDetailList = null;
			albumList = null;
			albumDetailList = null;
		}
		vu.finalize();
		vu = null;
	}

	public static String ALBUM_LIST_PAGE = "album_categories";
}
