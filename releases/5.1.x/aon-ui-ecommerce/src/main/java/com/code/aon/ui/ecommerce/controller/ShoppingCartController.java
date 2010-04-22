package com.code.aon.ui.ecommerce.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.Ectarget;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class ShoppingCartController extends EmailParentController{
	
	private static final String ECOMMERCE_BUNDLE = "ecommerceBundle";
	
	private List<CartItem> list;
	private DataModel model;
	private boolean registered;
	private ShoppingCartMap cart;
	private Double total;
	private CartTarget cartTarget;
	private String newPasswd;
	
	public ShoppingCartController() {
		ServletContext sc = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext());
		setCart(ShoppingCartMap.getInstance(sc));
	}

	public DataModel getModel() {
		model = new ListDataModel(getList());
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public List<CartItem> getList() {
		if (list == null) {
			ServletContext sc = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext());
			list = ShoppingCartMap.getInstance(sc).getList();
			setTotal(0.0);
		}
		return list;
	}

	public void setList(List<CartItem> list) {
		this.list = list;
	}
	
	public String getNewPasswd() {
		return newPasswd;
	}
	public void setNewPasswd(String newPasswd) {
		this.newPasswd = newPasswd;
	}

	public ShoppingCartMap getCart() {
		return cart;
	}

	public void setCart(ShoppingCartMap cart) {
		this.cart = cart;
	}
	
	public String budgetRequest() {
		if (!isRegistered()) {
			return IECommerceConstants.REGISTRY_ACTION;
		}
		((CartOfferController)AonUtil.getRegisteredBean(IECommerceConstants.OFFER_CONTROLLER)).initialize();
		return IECommerceConstants.PAYMETHOD_ACTION;
	}

	public void budgetRequest(ActionEvent event) {
		checkItemList();
		((CartOfferController)AonUtil.getRegisteredBean(IECommerceConstants.OFFER_CONTROLLER)).initialize();
		
		/*
		 * se crea un carrito con referencias debiles en memoria y 
		 * se guarda el carrito en el contexto de aplicacion para que sea accesible desde el servlet 
		 */
		ServletContext sc = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext());
		ShoppingCartMap cart = ShoppingCartMap.getInstance(sc);
		for(CartItem ci:getList()){
			cart.put(ci);
		}
		cart.setTotal(getTotal());
		sc.setAttribute("map", cart);
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

	public void addToCart(ShopItem item) {
		CartItem ci = new CartItem();
		ci.setItem(item);
		
		int index = getItemIndex(item);
		if (index == -1) {
			ci.setQuantity(1);
			ci.setTotal(ci.getItem().getPrice());
			getList().add(ci);
			setTotal(getTotal() + ci.getTotal());
		} else {
			CartItem c = getList().get(index);
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
	private int getItemIndex(ShopItem item) {
		int index = -1;
		boolean found = false;
		Iterator<CartItem> iterator = getList().iterator();
		while (iterator.hasNext() && !found) {
			CartItem listItem = iterator.next();
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
	}

	public void refreshTotal() {
		setTotal(getCart().getTotal());
	}
	
	public void onSelect(ActionEvent event) {
		ShopItemController sic = (ShopItemController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_ITEM_CONTROLLER);
		CartItem ci =  (CartItem) getModel().getRowData();
		sic.setItem(ci.getItem());
		ShopController sc = (ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView( ViewEnum.SHOPPING_CART );
		sc.setContentView( ViewEnum.ITEM_DETAIL );
	}
	
	public void onResetTarget(ActionEvent event) {
		setCartTarget(new CartTarget());
		getCartTarget().setEcTarget(new Ectarget());
		getCartTarget().getEcTarget().setTarget(new Target());
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
		getCartTarget().getWeb().setMediaType(MediaType.FAX);	
		
		getCartTarget().getEcTarget().getTarget().setAdvertising(Advertising.ALLOWED);
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

	private void addTarget() throws ManagerBeanException {
		IManagerBean tagetBean = BeanManager.getManagerBean(Target.class);
		IManagerBean ecTagetBean = BeanManager.getManagerBean(Ectarget.class);
		
		Ectarget to = getCartTarget().getEcTarget();
		to.getTarget().setAdvertising(getCartTarget().getEcTarget().getTarget().getAdvertising());
		to.getTarget().setRegistry(getCartTarget().getEcTarget().getTarget().getRegistry());
		
		try{
			tagetBean.insertOrUpdate(to.getTarget());
			ecTagetBean.insertOrUpdate(to);
		} catch (Exception e){
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
		
		String message = "target guardado";
		System.out.println(message);
		sendEmail();
		afterBeanAdded();
	}
	
	public void onAddTarget(ActionEvent event) {
		checkUserPasswd(event);
		
		try {
			addTarget();
			CartOfferController offerController = (CartOfferController)AonUtil.getRegisteredBean(IECommerceConstants.OFFER_CONTROLLER);
			offerController.initialize();
			offerController.getOffer().setTarget(getCartTarget().getEcTarget().getTarget());
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException( e.getMessage(), e );
		}		
	}
	
	public void afterBeanAdded() {
		setNewPasswd(null);
		try {
			((ShopController)AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER)).setLogged(true);
			((LoginController)AonUtil.getRegisteredBean(IECommerceConstants.LOGIN_CONTROLLER)).setLogin(getCartTarget().getEcTarget().getLogin());
			
			updateRegistryLines( getCartTarget().getEcTarget().getTarget().getRegistry() );
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException( e.getMessage(), e );
		}
	}

	public void afterBeanUpdated() {
		try {
			updateRegistryLines( getCartTarget().getEcTarget().getTarget().getRegistry() );
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException( e.getMessage(), e );
		}
	}
	
	private void checkItemList(){
		if(getList().size()<=0){
			String message = "Imposible realizar el pedido. Ningun articulo seleccionado.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}
	
	public void checkUserPasswd(ActionEvent event){
		if(!getCartTarget().getEcTarget().getPassword().equals(this.getNewPasswd())){
			String msg = "Contraseña incorrecta.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void sendEmail(){
		String from=null;
		try {
			from = ((ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER)).getCompany().getEmail().getValue();
		} catch (ManagerBeanException e1) {
			String msg = "En los Datos de la Empresa no esta indicado el email";
			AonUtil.addErrorMessage(msg);
			new AbortProcessingException(msg,e1);
		}
		String to = getCartTarget().getEcTarget().getLogin();
		String subject = "AON-ECOMMERCE - datos de registro.";
		StringBuffer content = new StringBuffer();
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_user_registry")).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_user_name")).append(": ");
		content.append( getCartTarget().getEcTarget().getLogin() ).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_user_passwd")).append(": ");
		content.append( getCartTarget().getEcTarget().getPassword() ).append(SystemUtils.LINE_SEPARATOR);
		super.email(subject, from, to, content.toString());
	}
	
	public void onCartClean(ActionEvent event){
		ShopController sc = (ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		setList(null);
		setModel(null);
		sc.setContentView( ViewEnum.ITEM_LIST );
	}


}