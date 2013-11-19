package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.common.ICommonMessages.SIGNATURE_DUPLICATED;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT_DB;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.db.Signature;
import com.esferalia.aon.entity.IEntityAlias;

public class SignatureDBController extends MailDBController implements ISignatureController {

	private final static Logger LOGGER = LoggerFactory.getLogger(SignatureDBController.class);
	
	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(SIGNATURE_DUPLICATED, name);
	}
	
	@Override
	protected String getToName() {
		ISignature signature = (ISignature) getTo();
		return signature.getName();
	}

	@Override
	public List<SelectItem> getSignatures() {
		List<SelectItem> signatures = new LinkedList<SelectItem>();
		try {		
			for( ITransferObject to : getManagerBean().getList(getCriteria()) ) {
				Signature signature = (Signature) to;
				SelectItem item = new SelectItem(signature, signature.getName());
				signatures.add(item);				
			}
		} catch (ManagerBeanException e) {
            LOGGER.error(">>>> getSignatures", e);
		}		
		return signatures;
	}

	@Override
	public boolean isRemovable( ISignature signature ) {
		try {
			MailAccountDBController controller = (MailAccountDBController) AonUtil.getRegisteredBean(BEAN_MAIL_ACCOUNT_DB);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(controller.getFieldName(IEntityAlias.MAIL_ACCOUNT_SIGNATURE_ID), ((Signature)signature).getId());
			controller.completeCriteria(criteria);
			return controller.getManagerBean().getCount(criteria) == 0; 
		} catch (ManagerBeanException e) {
			LOGGER.error("checkRemovable for " + signature, e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}	
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		Signature signature = (Signature) getTo();
		signature.setUser(getUser());
	}

	@Override
	protected String getUserAlias() throws ManagerBeanException {
		return getFieldName( IEntityAlias.SIGNATURE_USER_ID );
	}

	@Override
	protected String getNameAlias() throws ManagerBeanException {
		return getFieldName( IEntityAlias.SIGNATURE_NAME );
	}
	
}
