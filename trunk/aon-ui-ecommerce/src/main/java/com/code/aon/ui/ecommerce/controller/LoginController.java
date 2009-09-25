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
import com.code.aon.ebackoffice.util.EmailUtils;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;



public class LoginController {
	
	private String login;
	private String password;
	Ectarget ecTarget;
	
	public String getLogin() {
		if(login == ""){
			login = null;
		}
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		if(password == ""){
			password = null;
		}
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Ectarget getEcTarget() {
		return ecTarget;
	}
	public void setEcTarget(Ectarget ecTarget) {
		this.ecTarget = ecTarget;
	}
	
	public void onLogin(ActionEvent event) {
		if (getLogin() == null || getPassword() == null ) {
			String msg = "Debe indicar el login y la contraseña..";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} else{
			loadTarget(true);
		}
		if (getEcTarget() == null) {
			String msg = "Nombre o contraseña incorrectos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} else {
			ShopController shopController = ((ShopController) AonUtil
					.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER));
			shopController.setLogged(true);
			((ShoppingCartController) AonUtil
					.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER))
					.onResetTarget(null);
			((ShoppingCartController) AonUtil
					.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER))
					.getCartTarget().setEcTarget(getEcTarget());
			CartOfferController cartOfferController = ((CartOfferController) AonUtil
					.getRegisteredBean(IECommerceConstants.OFFER_CONTROLLER));
			cartOfferController.initialize();
			cartOfferController.getOffer().setTarget(ecTarget.getTarget());
		}
	}
	
	private void loadTarget(boolean withPasswd) {
		try {
			Criteria criteria = new Criteria();
			String alias = IEbackofficeAlias.ECTARGET_LOGIN;
			String identifier;
			identifier = AonUtil.getManagerBean(Ectarget.class).getFieldName(
					alias);
			criteria.addEqualExpression(identifier, getLogin());
			if (withPasswd) {
				alias = IEbackofficeAlias.ECTARGET_PASSWORD;
				identifier = AonUtil.getManagerBean(Ectarget.class)
						.getFieldName(alias);
				criteria.addEqualExpression(identifier, getPassword());
			}
			IManagerBean bean = BeanManager.getManagerBean(Ectarget.class);
			List<ITransferObject> list = bean.getList(criteria);
			if (list.isEmpty()) {
				setEcTarget(null);
//				String msg = "No existe ningun usuario asociado a esa cuenta de correo.";
//				AonUtil.addErrorMessage(msg);
//				throw new AbortProcessingException(msg);
			} else {
				setEcTarget((Ectarget) list.get(0));
			}
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda de usuario falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onLogout(ActionEvent event){
		setLogin(null);
		setPassword(null);
		((ShopController)AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER)).setLogged(false);
	}

	public void onLoginRequest(ActionEvent event){
		if(getLogin()==null || getLogin()=="" ){
			AonUtil.addInfoMessage("Debe indicar su email.");
			throw new AbortProcessingException();
		}
		if(!EmailUtils.validateEmailAddress(getLogin())){
			AonUtil.addInfoMessage("No es una cuenta de correo valida.");
			throw new AbortProcessingException();
		}
		
		loadTarget(false);
		
		if(getEcTarget()==null){
			String msg = "No existe ningun usuario asociado a esa cuenta de correo.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		/*
		 * ENVIAR UN MAIL AL TARGET DEL EMAIL INDICADO
		 */
		// sendEmail();
		String msg = "Se le enviara un email con sus datos de acceso a la direccion ";
		msg += getLogin();
		msg += ". proximamente....";
		msg += "Sus datos: "+getEcTarget().getLogin()+" "+getEcTarget().getPassword();
		AonUtil.addInfoMessage(msg);
	}
	
}
