package com.code.aon.ui.ecommerce.controller;

import java.util.Calendar;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.SystemUtils;

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

public class LoginController extends EmailParentController{
	private static final String ECOMMERCE_BUNDLE = "ecommerceBundle";
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
			try {
				Ectarget ect = getEcTarget();
				ect.setLastAccess(Calendar.getInstance().getTime());
				// se actualiza la fecha del ultimo acceso
				IManagerBean ectBean = BeanManager.getManagerBean(Ectarget.class);
				ectBean.update(ect);
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			AonUtil.addInfoMessage("Debe indicar su usuario (debe ser un email válido).");
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
		sendEmail();
		String msg = "Se le ha enviado un email con sus datos de acceso a la direccion ";
		msg += getLogin();
		AonUtil.addInfoMessage(msg);
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
		String to = getLogin();
		
		String subject = "AON-ECOMMERCE - datos de acceso.";
		StringBuffer content = new StringBuffer();
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_user_login")).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_user_name")).append(": ");
		content.append( getEcTarget().getLogin() ).append(SystemUtils.LINE_SEPARATOR);
		content.append(	AonUtil.getMessage(ECOMMERCE_BUNDLE,"aon_ecommerce_user_passwd")).append(": ");
		content.append( getEcTarget().getPassword() ).append(SystemUtils.LINE_SEPARATOR);
		
		super.email(subject, from, to, content.toString());
		AonUtil.addInfoMessage("Mensaje enviado correctamente.");
	}
	
}
