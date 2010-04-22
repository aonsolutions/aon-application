package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.AttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class ShopItemController {
	
	//private static final Logger LOGGER = LoggerFactory
	//.getLogger(ShopItemController.class.getName());

	private ShopItem item;
	private List<ItemAttachment> imageList;
	private List<ItemAttachment> documentList;
	private DataModel imageModel;
	private DataModel documentModel;
	private Integer bigImageId;
	private boolean zoomEnabled;

	public boolean isZoomEnabled() {
		return zoomEnabled;
	}

	public void setZoomEnabled(boolean zoomEnabled) {
		this.zoomEnabled = zoomEnabled;
	}

	public Integer getBigImageId() {
		return bigImageId;
	}

	public void setBigImageId(Integer bigImageId) {
		this.bigImageId = bigImageId;
	}

	public ShopItem getItem() {
		return item;
	}

	public void setItem(ShopItem item) {
		this.item = item;
	}
	
	public List<ItemAttachment> getImageList() {
		if (imageList == null) {
			imageList = new LinkedList<ItemAttachment>();
		}
		return imageList;
	}

	public void setImageList(List<ItemAttachment> imageList) {
		this.imageList = imageList;
	}

	public List<ItemAttachment> getDocumentList() {
		if (documentList == null) {
			documentList = new LinkedList<ItemAttachment>();
		}
		return documentList;
	}

	public void setDocumentList(List<ItemAttachment> documentList) {
		this.documentList = documentList;
	}

	public DataModel getImageModel() {
		if (imageModel == null) {
			imageModel = new ListDataModel(getImageList());
		}
		return imageModel;
	}

	public void setImageModel(DataModel imageModel) {
		this.imageModel = imageModel;
	}

	public DataModel getDocumentModel() {
		if (documentModel == null) {
			documentModel = new ListDataModel(getDocumentList());
		}
		return documentModel;
	}

	public void setDocumentModel(DataModel documentModel) {
		this.documentModel = documentModel;
	}
	public void closeZoom(ActionEvent event) {
		setZoomEnabled(false);
	}
	public void addToCart(ActionEvent event) {
		((ShoppingCartController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER))
				.addToCart(item);
		ShopController sc = (ShopController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView(ViewEnum.ITEM_DETAIL);
		sc.setContentView(ViewEnum.SHOPPING_CART);
	}

	public void paintThumbnail(OutputStream out, Object data) {
		if (getItem().getThumbnail() != null) {
			getItem().paintThumbnail(out, data);
		}
	}

	public void paintImage(OutputStream out, Object data) {
		try {
			out.write(findItem((Integer)data,getImageList()).getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void paintBigImage(OutputStream out, Object data) {
		try {
			out.write(findItem(getBigImageId(),getImageList()).getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onImageSelected(ActionEvent event) {
		setZoomEnabled(true);
		setBigImageId(((ItemAttachment)getImageModel().getRowData()).getId());
	}
	
	public void onLoadAttachment(ActionEvent event) {
		initializeAttachment();
		try {
			IManagerBean iaBean = BeanManager
					.getManagerBean(ItemAttachment.class);
			String identifier = iaBean
					.getFieldName(IProductAlias.ITEM_ATTACHMENT_ITEM_ID);
			Criteria criteria = new Criteria();
			criteria
					.addEqualExpression(identifier, getItem().getItem().getId());

			for (ITransferObject to : iaBean.getList(criteria)) {
				ItemAttachment ia = (ItemAttachment) to;
				ia.getMimeType().getName();
				ia.getMimeType().getExtension();
				if (ia.getType().equals(AttachmentType.IMAGE)) {
					getImageList().add(ia);
				}
				if (ia.getType().equals(AttachmentType.DOCUMENT)) {
					getDocumentList().add(ia);
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al buscar attachment";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void initializeAttachment() {
		setImageModel(null);
		setDocumentModel(null);
		setImageList(null);
		setDocumentList(null);

	}

	public void downloadAttachment(ActionEvent event)
			throws NumberFormatException, ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		String id = context.getExternalContext().getRequestParameterMap().get(
				"index");
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		writeAttachment(findItem(Integer.valueOf(id),getDocumentList()), response);
		context.responseComplete();
		
	}
	
	private void writeAttachment(ItemAttachment attachment,
			HttpServletResponse response) {
		try {
			String filename = attachment.getDescription();
			if (attachment.getMimeType() != null) {
				response.setContentType(attachment.getMimeType().getName());
			}
			response.setHeader("Content-disposition", "attachment;filename=\""
					+ filename + "\"");
			byte[] data = attachment.getData();
			response.setHeader("Content-Length", String.valueOf(data.length));
			ServletOutputStream sos = response.getOutputStream();
			sos.write(data);
			sos.close();
			response.flushBuffer();
		} catch (IOException e) {
			//LOGGER.error(e.getMessage(), e);
		}
	}
	
	/*
	 * Busqueda de un itemAttach en una lista
	 */
	private ItemAttachment findItem(Integer id, List<ItemAttachment> list){
		boolean finded=false;
		Iterator<ItemAttachment> it = list.iterator();
		ItemAttachment attachment=null;
		while (it.hasNext() && !finded) {
			ItemAttachment ia = it.next();
			if (ia.getId().equals(id)) {
				attachment = ia;
				finded=true;
			}
		}
		return attachment;
	}

}
