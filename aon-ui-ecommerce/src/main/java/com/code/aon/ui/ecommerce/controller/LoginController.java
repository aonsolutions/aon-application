package com.code.aon.ui.ecommerce.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.Ectarget;
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;



public class LoginController {
	
	private String login;
	private String password;
	
	public void onLogin(ActionEvent event){
		System.out.println("USER LOGIN event");
				
		try {
			Criteria criteria = new Criteria();
			String alias = IEbackofficeAlias.ECTARGET_LOGIN;
			String identifier = AonUtil.getManagerBean(Ectarget.class).getFieldName(alias); 
			criteria.addEqualExpression(identifier, getLogin());
			alias = IEbackofficeAlias.ECTARGET_PASSWORD;
			identifier = AonUtil.getManagerBean(Ectarget.class).getFieldName(alias);
			criteria.addEqualExpression(identifier, getPassword());

			IManagerBean bean = BeanManager.getManagerBean(Ectarget.class);
			List<ITransferObject> list = bean.getList(criteria);
			if(list.isEmpty()){
				//System.out.println("LOGIN INCORRECTO");
				//AonUtil.addErrorMessage("LOGIN INCORRECTO");
				String msg = "Nombre o contraseña incorrectos.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else {
				//Ectarget ect = (Ectarget)list.iterator().next();
				//System.out.println("LOGIADOOO");
				//AonUtil.addErrorMessage("LOGIADOOO");
				ShopController shopController = ((ShopController)AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER));
				
				shopController.setLogged(true);
				((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).onResetTarget(null);
				Ectarget ecTarget = (Ectarget)list.get(0); 
				((ShoppingCartController)AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).getCartTarget().setEcTarget(ecTarget);

				CartOfferController cartOfferController = ((CartOfferController)AonUtil.getRegisteredBean(IECommerceConstants.OFFER_CONTROLLER));
				
				cartOfferController.initialize();
				cartOfferController.getOffer().setTarget(ecTarget.getTarget());
				
			}
			
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda de usuario falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e); 
		}
	}
	
	public void onLogout(ActionEvent event){
		setLogin(null);
		setPassword(null);
		((ShopController)AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER)).setLogged(false);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
