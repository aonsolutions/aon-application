package com.code.aon.ui.ecommerce.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.geozone.GeoZone;
import com.code.aon.product.Item;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ShoppingCartController {
	private List<CartItem> list;
	private DataModel model;
	private boolean registered;
	private Double total;
	private CartTarget cartTarget;

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel(getList());
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public List<CartItem> getList() {
		if (list == null) {
			list = new ArrayList<CartItem>();
			setTotal(0.0);
		}
		return list;
	}

	public void setList(List<CartItem> list) {
		this.list = list;
	}
	
	public String budgetRequest() {
		if (!isRegistered()) {
			return IECommerceConstants.REGISTRY_ACTION;
		}
		initializeOffer();
		return IECommerceConstants.OFFER_ACTION;
	}

	public boolean isRegistered() {
		registered = ((ShopController)AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER)).isLogged();
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
	
	public CartTarget getCartTarget() {
		return cartTarget;
	}
	
	public void setCartTarget(CartTarget cartTarget) {
		this.cartTarget = cartTarget;
	}
	
	public Integer getQuantity() {
		return getList().size();
	}

	public void addToCart(Item item) {
		CartItem ci = new CartItem();
		ci.setItem(item);
		ci.setQuantity(1);
		// ci.setDiscount(0);
		// ci.setPrice(item.getPrice());

		int index = getItemIndex(item);
		if (index == -1) {
			ci.setTotal(ci.getItem().getPrice());
			getList().add(ci);
			setTotal(getTotal() + ci.getTotal());
		} else {
			CartItem c = (CartItem) getList().get(index);
			c.setQuantity(c.getQuantity() + 1);
			c.setTotal(c.getItem().getPrice() * c.getQuantity());
			setTotal(getTotal() + c.getItem().getPrice());
		}
	}

	/**
	 * Devuelve el index del item en la lista o -1 si no existe
	 * 
	 * @param item
	 * @return index
	 */
	private int getItemIndex(Item item) {
		int index = -1;
		boolean found = false;
		Iterator<CartItem> iterator = getList().iterator();
		while (iterator.hasNext() && !found) {
			CartItem listItem = ((CartItem) iterator.next());
			if (listItem.getItem().getId().equals(item.getId())) {
				index = getList().indexOf(listItem);
				found = true;
			}
		}
		return index;
	}

	public void removeFromCart(ActionEvent event) {
		CartItem ci = (CartItem) getModel().getRowData();
		getList().remove(ci);
		setTotal(getTotal() - ci.getTotal());
	}

	public void buy(ActionEvent event) {
		String msg = "Factura";
		AonUtil.addInfoMessage(msg);
	}

	public void refreshPrice(ActionEvent event) {
		CartItem ci = new CartItem();
		ci = (CartItem) getModel().getRowData();
		Double totalPrice = ci.getQuantity() * ci.getItem().getPrice();
		setTotal(getTotal() - ci.getTotal());
		ci.setTotal(totalPrice);

		setTotal(getTotal() + totalPrice);

		// refreshTotal();
	}

	public void refreshTotal() {
		setTotal(getTotal() + total);

	}
	
	public void onSelect(ActionEvent event) {
		((ShopItemController) FormUtil
			.getController(IECommerceConstants.SHOP_ITEM_CONTROLLER))
			.setItem(((CartItem) getModel().getRowData()).getItem());
		((ShopController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
				.setDetail(true);
	}
	
	public void onResetTarget(ActionEvent event) {
		setCartTarget(new CartTarget());
		getCartTarget().setTarget(new Target());
		getCartTarget().getTarget().setRegistry(new Registry());
		getCartTarget().setMainAddress(new RegistryAddress());
		getCartTarget().getMainAddress().setAddressType( AddressType.MAIN );
		getCartTarget().getMainAddress().setGeozone( new GeoZone() );
		getCartTarget().setPhone(new RegistryMedia());
		getCartTarget().getPhone().setMediaType(MediaType.FIXED_PHONE);
		getCartTarget().setFax(new RegistryMedia());
		getCartTarget().getFax().setMediaType(MediaType.FAX);
		getCartTarget().setEmail(new RegistryMedia());
		getCartTarget().getEmail().setMediaType(MediaType.FAX);
		getCartTarget().setWeb(new RegistryMedia());	
		getCartTarget().getWeb().setMediaType(MediaType.FAX);	
	}
	
	private boolean isEmpty( RegistryAddress address ) {
		return StringUtils.isEmpty(address.getAddress()) && StringUtils.isEmpty(address.getAddress2()) &&
			StringUtils.isEmpty(address.getAddress3());
	}
	
	private boolean isEmpty( RegistryMedia media ) {
		return StringUtils.isEmpty(media.getValue());
	}

	private void updateRegistryMedia( Registry registry, RegistryMedia media ) throws ManagerBeanException {
		IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		if (! isEmpty(media) ) {
			media.setRegistry( registry );
			registryMediaBean.insertOrUpdate( media );
		} else if ( media.getId() != null ) {
			registryMediaBean.remove(media);
		}		
	}
	
	private void updateRegistryLines( Registry registry ) throws ManagerBeanException {
		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		if (! isEmpty(getCartTarget().getMainAddress()) ) {
			getCartTarget().getMainAddress().setRegistry(registry);
			registryAddressBean.insertOrUpdate(getCartTarget().getMainAddress());
		} else if ( getCartTarget().getMainAddress().getId() != null ) {
			registryAddressBean.remove(getCartTarget().getMainAddress());
		}
		updateRegistryMedia(registry, getCartTarget().getPhone());
		updateRegistryMedia(registry, getCartTarget().getFax());
		updateRegistryMedia(registry, getCartTarget().getEmail());
		updateRegistryMedia(registry, getCartTarget().getWeb());
	}

	private void acceptTarget() throws ManagerBeanException {
		IManagerBean tagetBean = BeanManager.getManagerBean(Target.class);
		
		
		Target to = new Target();
		to.setAdvertising(getCartTarget().getTarget().getAdvertising());
		to.setRegistry(getCartTarget().getTarget().getRegistry());
		
		tagetBean.insertOrUpdate(to);
		String message = "target guardado \n oo";
		System.out.println(message);
		//AonUtil.addInfoMessage(message);
		
		
//		if (! isEmpty(getTarget()) ) {
//			getTarget().getMainAddress().setRegistry(registry);
//			registryAddressBean.insertOrUpdate(getTarget().getMainAddress());
//		} else if ( getTarget().getMainAddress().getId() != null ) {
//			registryAddressBean.remove(getTarget().getMainAddress());
//		}
		
		afterBeanAdded();
	}
	
	public void onAcceptTarget(ActionEvent event) {
		try {
			acceptTarget();
			initializeOffer();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException( e.getMessage(), e );
		}		
	}
	
	/**
	 * creacion del presupuesto 
	 */
	public void initializeOffer() {
		OfferController offerController = (OfferController)FormUtil.getController(IECommerceConstants.OFFER_CONTROLLER);
		OfferDetailController offerDetailController = (OfferDetailController)FormUtil.getController(IECommerceConstants.OFFER_DETAIL_CONTROLLER);
		if(offerController.exist(getCartTarget().getTarget())){
			// ********************************************************
			// ********************************************************
			//  EN CASO DE EXISTIR AINADIR EL TARGET CORRESPONDIENTE AL OFFER
			// ********************************************************
			// ********************************************************
			 
		} else {
			offerController.onReset(null);
			Offer to = (Offer)offerController.getTo();
			getCartTarget().getTarget().setId(738);
			to.setTarget(getCartTarget().getTarget());
			to.setStatus(OfferStatus.PENDING);
			try {
				// ********************************************************
				// ********************************************************
				//  REPASAR EL WORKPLACE QUE HAY QUE AINADIR POR DEFECTO
				// ********************************************************
				// ********************************************************
				to.setWorkPlace((WorkPlace)((CompanyCollectionsController)AonUtil.getRegisteredBean("companyCollections")).getWorkPlaces().get(0).getValue());
				//to.setWorkPlace((WorkPlace)ECommerceUtil.getWorkPlaces().get(0));
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offerController.accept(null);
			for(CartItem ci:getList()){
				offerDetailController.onReset(null);
				OfferDetail tod = (OfferDetail)offerDetailController.getTo();
				tod.setOffer(to);
				tod.setItem(ci.getItem());
				tod.setDescription(ci.getItem().getProduct().getName());
				tod.setQuantity(ci.getQuantity());
				tod.setPrice(ci.getItem().getPrice());
				tod.setDiscountExpression(null);
				offerDetailController.onAccept(null);
			}
		}
		
	}

		
	public void afterBeanAdded() {
//		IController controller = event.getController();
//		Registry registry = ((IRegistry) controller.getTo()).getRegistry();
		try {
			updateRegistryLines( getCartTarget().getTarget().getRegistry() );
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException( e.getMessage(), e );
		}
	}

	public void afterBeanUpdated() {
//		IController controller = event.getController();
//		Registry registry = ((IRegistry) controller.getTo()).getRegistry();
		try {
			updateRegistryLines( getCartTarget().getTarget().getRegistry() );
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException( e.getMessage(), e );
		}
	}


	

	

}