package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumCategoryDetail;
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

	public static final String ALBUM_LIST_PAGE = "album_categories";
	
	public void generate() {
		generate(null);
	}

	public void generate(AlbumCategory selectedCategory) {
		VelocityUtil vu = context.initVelocityUtil();
			
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
			Criteria albumCategoryCriteria = new Criteria();
			if (selectedCategory!=null) {
				albumCategoryCriteria.addEqualExpression(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ID), selectedCategory.getId());
			}
			albumCategoryCriteria.addEqualExpression(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ACTIVE), true);
			albumCategoryCriteria.addOrder(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_POSITION));
			albumCategoryList = (List<ITransferObject>)albumCategoryBean.getList(albumCategoryCriteria);
			ArrayList<AlbumCategoryHandler> achlist = new ArrayList<AlbumCategoryHandler>();
			for (int j=0; j < albumCategoryList.size(); j++) {
				AlbumCategory albumCategory = (AlbumCategory)albumCategoryList.get(j);
				if (albumCategory.getSection()!=null) {
					context.changeSection(vu, albumCategory.getSection());
				} else {
					context.changeSection(vu, configSection);
				}
				Criteria albumCategoryDetailCriteria = new Criteria();
				albumCategoryDetailCriteria.addEqualExpression(albumCategoryDetailBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_ALBUM_CATEGORY_ID), albumCategory.getId());
				albumCategoryDetailCriteria.addEqualExpression(albumCategoryDetailBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				albumCategoryDetailList = (List<ITransferObject>)albumCategoryDetailBean.getList(albumCategoryDetailCriteria);
				if (albumCategoryDetailList.isEmpty()) {
					logger.warning("La categoria de albumes "+albumCategory.getAlias()+" no esta internacionalizada.");
				}else{
					AlbumCategoryDetail albumCategoryDetail = (AlbumCategoryDetail)albumCategoryDetailList.get(0);
					Criteria albumCriteria = new Criteria();
					albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), albumCategory.getId());
					albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ACTIVE), true);
					albumCriteria.addOrder(albumBean.getFieldName(ICMSAlias.ALBUM_POSITION));
					albumList = (List<ITransferObject>)albumBean.getList(albumCriteria);
					ArrayList<AlbumHandler> ahlist = new ArrayList<AlbumHandler>(); 
					if (albumList.isEmpty()){
						logger.warning("La categoria de albumes "+albumCategory.getAlias()+" no tiene albumes.");
					}else{
						AlbumCategoryHandler achandler = new AlbumCategoryHandler(albumCategoryDetail,ahlist);
						for (int i=0; i < albumList.size(); i++) {
							Album  album = (Album)albumList.get(i);
							Criteria albumDetailCriteria = new Criteria();
							albumDetailCriteria.addEqualExpression(albumDetailBean.getFieldName(ICMSAlias.ALBUM_DETAIL_ALBUM_ID), album.getId());
							albumDetailCriteria.addEqualExpression(albumDetailBean.getFieldName(ICMSAlias.ALBUM_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
							albumDetailList = (List<ITransferObject>)albumDetailBean.getList(albumDetailCriteria);
							if (albumDetailList.isEmpty()) {
								logger.warning("El albumes "+album.getAlias()+" no esta internacionalizada.");
							}else{
								AlbumDetail albumDetail = (AlbumDetail)albumDetailList.get(0);
								ArrayList<AlbumImageHandler> accessList = getAlbumImageList(albumDetail);
								if (accessList != null && accessList.size() > 0) {
									AlbumHandler ahandler = new AlbumHandler(albumDetail);
									ahlist.add(ahandler);
									for (int k=0;k<accessList.size();k++){
										if (Math.abs(k / album.getItemsPerPage())==0){
											vu.put(BACK_URL_KEY, ahandler.getUrl());
										}else if (Math.abs(k / album.getItemsPerPage())>0){
											vu.put(BACK_URL_KEY, Templates.ALBUM.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(Math.abs(k / album.getItemsPerPage())+1)));
										}
										vu.put(ALBUM_IMAGE_KEY, accessList.get(k));
										if (k>0)
											vu.put(ALBUM_IMAGE_PREVIOUS_KEY, accessList.get(k-1).getUrl());
										if (k+1<accessList.size())
											vu.put(ALBUM_IMAGE_NEXT_KEY, accessList.get(k+1).getUrl());
										logger.info(" Generando imagen.");
										generate(vu, Templates.ALBUM_IMAGES, "ALBUM_IMAGE_"+accessList.get(k).getId());
										vu.remove(BACK_URL_KEY);
										vu.remove(ALBUM_IMAGE_KEY);
										vu.remove(ALBUM_IMAGE_PREVIOUS_KEY);
										vu.remove(ALBUM_IMAGE_NEXT_KEY);
									}
									int page = 0;
									Iterator<AlbumImageHandler> iter = accessList.iterator();
									List<AlbumImageHandler> partialLst = new ArrayList<AlbumImageHandler>(); 
									while (iter.hasNext()){
										partialLst.add(iter.next());
										if (partialLst.size() == album.getItemsPerPage()
												|| !iter.hasNext()){
											page++;
											vu.put(ALBUM_KEY, ahandler);
											vu.put(BACK_URL_KEY, achandler.getUrl() );
											if (page==2){
												vu.put(ALBUM_PREVIOUS_KEY, ahandler.getUrl());
											}else if (page>2){
												vu.put(ALBUM_PREVIOUS_KEY, Templates.ALBUM.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(page-1)));
											}
											if (iter.hasNext()){
												vu.put(ALBUM_NEXT_KEY, Templates.ALBUM.getHtmlName().replaceAll("%NAME%", ahandler.getAlias()+"_"+(page+1)));
											}
											vu.put(ALBUM_IMAGE_LIST_KEY, partialLst);
											logger.info(" Generando album de imagenes " + album.getAlias() + ".");
											generate(vu, Templates.ALBUM, album.getAlias()+(page==1?"":"_"+page));
											vu.remove(BACK_URL_KEY);
											vu.remove(ALBUM_PREVIOUS_KEY);
											vu.remove(ALBUM_NEXT_KEY);
											vu.remove(ALBUM_KEY);
											vu.remove(ALBUM_IMAGE_LIST_KEY);
											partialLst = new ArrayList<AlbumImageHandler>();
										}
									}
									partialLst = null;
									iter = null;
								}
							}
						}
						vu.put(BACK_URL_KEY, Templates.ALBUM_CATEGORY.getHtmlName().replaceAll("%NAME%", ALBUM_LIST_PAGE));
						vu.put(ALBUM_CATEGORY_KEY, achandler);
						vu.put(ALBUM_LIST_KEY, ahlist);
						logger.info(" Generando list de album.");
						generate(vu, Templates.ALBUM_CATEGORY, albumCategory.getAlias());
						vu.remove(BACK_URL_KEY);
						vu.remove(ALBUM_CATEGORY_KEY);
						vu.remove(ALBUM_LIST_KEY);

						achlist.add(achandler);
					}
					ahlist = null;
				}
			}
			vu.put(ALBUM_CATEGORY_LIST_KEY, achlist);
			logger.info(" Generando categorias de album.");
			context.changeSection(vu, configSection);
			generate(vu, Templates.ALBUM_CATEGORY, ALBUM_LIST_PAGE);
			vu.remove(ALBUM_CATEGORY_LIST_KEY);
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}finally{
			albumCategoryList = null;
			albumCategoryDetailList = null;
			albumList = null;
			albumDetailList = null;
		}
	}

	public ArrayList<AlbumImageHandler> getAlbumImageList(AlbumDetail albumDetail) {
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
				logger.warning("El album "+albumDetail.getAlbum().getAlias()+" no tiene imagenes.");
			for (int i = 0; i < albumImageList.size(); i++) {
				albumImage = (AlbumImage)albumImageList.get(i);
				criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(albumImageDetailBean.getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_ALBUM_IMAGE_ID), albumImage.getId());
				criteria_detail.addEqualExpression(albumImageDetailBean.getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				albumImageDetailList = (List<ITransferObject>)albumImageDetailBean.getList(criteria_detail);
				if (albumImageDetailList.isEmpty()) {
					logger.warning("La imagen "+albumImage.getImage()+" no esta internacionalizada.");
				}else{
					albumImageDetail = (AlbumImageDetail)albumImageDetailList.get(0);
					AlbumImageHandler handler = new AlbumImageHandler(albumImageDetail);
					list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}finally{
			albumImageList = null;
			albumImageDetailList = null;
		}
		return list;
	}
	
	public static Object getAlbumCategoryHandler(Integer ident, String message) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(AlbumCategory.class);
			AlbumCategory albumCategory = (AlbumCategory) bean.get(ident);
			if ( albumCategory == null ){
				getLogger().error( message + " REFERENCIA UNA CATEGORIA DE ALBUM ("+ident+") INEXISTENTE");
				return null;
			}
			
			bean = BeanManager.getManagerBean(AlbumCategoryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_ALBUM_CATEGORY_ID), ident);
			List<ITransferObject> lcd = (List<ITransferObject>)bean.getList(criteria);
			if (lcd.isEmpty()){
				getLogger().warning("La categoria de albumes "+albumCategory.getAlias()+" no esta internacionalizado.");
				return null;
			}
			AlbumCategoryDetail acd = (AlbumCategoryDetail) lcd.get(0);
			
			bean = BeanManager.getManagerBean(Album.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), ident);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.ALBUM_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty())
				getLogger().warning("La categoria de albumes "+albumCategory.getAlias()+" no tiene albumes.");
			Iterator<ITransferObject> iter = l.iterator();
			ArrayList<AlbumHandler> ahlist = new ArrayList<AlbumHandler>();
			while (iter.hasNext()){
				Album album = (Album)iter.next();
				bean = BeanManager.getManagerBean(AlbumDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ALBUM_DETAIL_ALBUM_ID), album.getId());
				List<ITransferObject> ld = (List<ITransferObject>)bean.getList(criteria);
				if (ld.isEmpty()){
					getLogger().warning("El album "+album.getAlias()+" no esta internacionalizado.");
				}else{
					AlbumDetail ad = (AlbumDetail)ld.get(0);
					AlbumHandler ah = new AlbumHandler(ad);
					ahlist.add(ah);
				}
			}
			AlbumCategoryHandler ach = new AlbumCategoryHandler(acd,ahlist);
			return ach;
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}

}
