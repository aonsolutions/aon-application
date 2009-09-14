package com.code.aon.ui.ebackoffice.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.ebackoffice.enumeration.LoginType;
import com.code.aon.ebackoffice.enumeration.OriginalPrice;
import com.code.aon.ebackoffice.enumeration.PriceType;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ebackoffice.enumeration.TaxType;
import com.code.aon.ebackoffice.enumeration.WishList;
import com.code.aon.ui.form.BasicController;


public class EcconfigController extends BasicController {

	private List<SelectItem> skins;
	private List<SelectItem> loginTypes;
	private List<SelectItem> priceTypes;
	private List<SelectItem> taxPriceTypes;
	private List<SelectItem> originalPriceTypes;
	private List<SelectItem> wishListTypes;
	
	
	public List<SelectItem> getSkins() {
		if(skins==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			skins = new LinkedList<SelectItem>();
			for (SkinType e : SkinType.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				skins.add(item);
				
			}
		}
		
		return skins;
	}
	
	public List<SelectItem> getLoginTypes() {
		if(loginTypes==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			loginTypes = new LinkedList<SelectItem>();
			for (LoginType e : LoginType.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				loginTypes.add(item);
				
			}
		}
		
		return loginTypes;
	}
	

	public List<SelectItem> getPriceTypes() {
		if(priceTypes==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			priceTypes = new LinkedList<SelectItem>();
			for (PriceType e : PriceType.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				priceTypes.add(item);
				
			}
		}
		
		return priceTypes;
	}
	
	
	public List<SelectItem> getTaxPriceTypes() {
		if(taxPriceTypes==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			taxPriceTypes = new LinkedList<SelectItem>();
			for (TaxType e : TaxType.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				taxPriceTypes.add(item);
				
			}
		}
		
		return taxPriceTypes;
	}
	
	public List<SelectItem> getOriginalPriceTypes() {
		if(originalPriceTypes==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			originalPriceTypes = new LinkedList<SelectItem>();
			for (OriginalPrice e : OriginalPrice.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				originalPriceTypes.add(item);
				
			}
		}
		
		return originalPriceTypes;
	}
	
	public List<SelectItem> getWishListTypes() {
		if(wishListTypes==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			wishListTypes = new LinkedList<SelectItem>();
			for (WishList e : WishList.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				wishListTypes.add(item);
				
			}
		}
		
		return wishListTypes;
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		// TODO Auto-generated method stub
		
		super.onAccept(event);
		
		this.setModel(null);
	}
	
	
	
	
	
}
