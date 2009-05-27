package com.code.aon.ui.cms.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.AlbumImageDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class AlbumController extends BasicI18nController implements ICMSConstants, Constants {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Album a = (Album)this.model.getRowData();
		a.setActive(active);
		getManagerBean().update(a);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = NO_VALUE_LABEL;
		AlbumDetail ad = (AlbumDetail)getModelRowdataI18n();
		if (ad != null) title = ad.getTitle();
		return title;
	}


	public void onDelImage(ActionEvent event) {
		Album current = (Album)getTo();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Album current = (Album)getTo();
		current.setImage(image);
	}

	public String getBack(){
		if (FormUtil.getController(ALBUM_IMAGE).getTo()==null)
			return HOME;
		return ALBUM_IMAGE_FORM;
	}

	
	public void onAlbumImageCriteria(ActionEvent event) throws ManagerBeanException {
		AlbumImageController albumImageController = (AlbumImageController)AonUtil.getRegisteredBean(ALBUM_IMAGE);
		AlbumImage albumImageTo = (AlbumImage)albumImageController.getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(this.getFieldName(ICMSAlias.ALBUM_ID),albumImageTo.getAlbum().getId());
		setCriteria(criteria);
	}
	
	// ***********************************************
	// GALLERY GENERATOR AND LOG
	// ***********************************************

	private boolean activeLog = false;

	private List<String> status = new ArrayList<String>();

	public boolean isActiveLog() {
		return activeLog;
	}

	public List<String> getStatus() {
		return status;
	}

	public void onInitGenerateFromGallery(ActionEvent event) throws ManagerBeanException {
		this.activeLog = false;
		this.status = new ArrayList<String>();
	}

	public void onGenerateFromGallery(ActionEvent event) throws ManagerBeanException {
		this.status.add(0,GregorianCalendar.getInstance().getTime()+": Init generator");

		String albumAlias = "AUTO_GENERATED";
		
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY); 
		List<Image> list = (List<Image>) controller.getModel().getWrappedData();
		
		this.status.add(0,GregorianCalendar.getInstance().getTime()+": Searching category...");
		
		IManagerBean albumCategoryBean = BeanManager.getManagerBean(AlbumCategory.class);
		Criteria albumCategoryCriteria = new Criteria();
		albumCategoryCriteria.addEqualExpression(albumCategoryBean.getFieldName(ICMSAlias.ALBUM_CATEGORY_ALIAS), albumAlias);
		List<ITransferObject> albumCategoryList = (List<ITransferObject>)albumCategoryBean.getList(albumCategoryCriteria);
		AlbumCategory albumCategory;
		if (albumCategoryList.isEmpty()){
			albumCategory = new AlbumCategory();
			albumCategory.setAlias(albumAlias);
			albumCategory.setPosition(0);
			albumCategory = (AlbumCategory)albumCategoryBean.insert(albumCategory);
			
			AlbumCategoryDetail albumCategoryDetail = new AlbumCategoryDetail();
			albumCategoryDetail.setAlbumCategory(albumCategory);
			albumCategoryDetail.setLanguage(ControllerUtil.getCurrentLanguage());
			albumCategoryDetail.setLabel(albumAlias);
			IManagerBean albumCategoryDetailBean = BeanManager.getManagerBean(AlbumCategoryDetail.class);
			albumCategoryDetail = (AlbumCategoryDetail)albumCategoryDetailBean.insert(albumCategoryDetail);
			
			this.status.add(0,GregorianCalendar.getInstance().getTime()+": Not exist. Category created.");
		}else
			albumCategory = (AlbumCategory)albumCategoryList.get(0); 

		this.status.add(0,GregorianCalendar.getInstance().getTime()+": Creating album...");

		IManagerBean albumBean = BeanManager.getManagerBean(Album.class);
		IManagerBean albumImageBean = BeanManager.getManagerBean(AlbumImage.class);
		IManagerBean albumImageDetailBean = BeanManager.getManagerBean(AlbumImageDetail.class);
		Criteria albumCriteria = new Criteria();
		String alias = controller.getCurrentRelativePath();
		alias = alias.replaceAll("[^A-Za-z0-9._-]+", "");
		if (alias.length()>32)
			alias = alias.substring(0, 32);
		
		this.status.add(0,GregorianCalendar.getInstance().getTime()+": Album name "+alias+".");
		
		albumCriteria.addEqualExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ALIAS), alias);
		List<ITransferObject> albumList = (List<ITransferObject>)albumBean.getList(albumCriteria);
		Album album;
		if (albumList.isEmpty()){
			album = new Album();
			album.setAlbumCategory(albumCategory);
			album.setAlias(alias);
			album.setItemsPerPage(10);
			album.setPosition(0);
			album.setPublishDate(new Date());
			album = (Album)albumBean.insert(album);
			
			AlbumDetail albumDetail = new AlbumDetail();
			albumDetail.setAlbum(album);
			albumDetail.setTitle(alias);
			albumDetail.setLanguage(ControllerUtil.getCurrentLanguage());
			IManagerBean albumDetailBean = BeanManager.getManagerBean(AlbumDetail.class);
			albumDetail = (AlbumDetail)albumDetailBean.insert(albumDetail);

			this.status.add(0,GregorianCalendar.getInstance().getTime()+": Not exits, created "+alias+".");
		}else{
			album = (Album)albumList.get(0); 

			this.status.add(0,GregorianCalendar.getInstance().getTime()+": Exits, removing content.");

			Criteria albumImageCriteria = new Criteria();
			albumImageCriteria.addEqualExpression(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID), album.getId());
			List<ITransferObject> albumImageList = (List<ITransferObject>)albumImageBean.getList(albumImageCriteria);
			for (ITransferObject transferObject : albumImageList) {
				albumImageBean.remove(transferObject);
			}
			
			this.status.add(0,GregorianCalendar.getInstance().getTime()+": Removed.");
		}

		this.status.add(0,GregorianCalendar.getInstance().getTime()+": Creating images.");

		AlbumImage albumImage;
		AlbumImageDetail albumImageDetail;
		int i = 0;
		for (Image image : list) {
			this.status.add(0,GregorianCalendar.getInstance().getTime()+": Image "+image.getRelativePath()+".");
			
			if (album.getImage() == null){
				album.setImage(image.getRelativePath());
				album = (Album)albumBean.update(album);
			}
			albumImage = new AlbumImage();
			albumImage.setAlbum(album);
			albumImage.setImage(image.getRelativePath());
			albumImage.setPosition(i);
			File file = ControllerUtil.getImagePath(image.getRelativePath());
			File thumb = ImageUtil.resize(file,ImageUtil.DEF_MAX_SIZE);
			String path = ControllerUtil.getRelativePath(ControllerUtil.getImagesPath(), thumb);
			albumImage.setThumbnail(path);
			albumImage = (AlbumImage) albumImageBean.insert(albumImage);
			
			albumImageDetail = new AlbumImageDetail();
			albumImageDetail.setAlbumImage(albumImage);
			albumImageDetail.setTitle(image.getName());
			albumImageDetail.setLanguage(ControllerUtil.getCurrentLanguage());
			albumImageDetailBean.insert(albumImageDetail);
			
			++i;
		}

		this.status.add(0,GregorianCalendar.getInstance().getTime()+": Finished generation.");

		this.activeLog = true;
	}

}