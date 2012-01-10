package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.SIGNATURE_DUPLICATED;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.SIGNATURE_USED;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.db.MailAccount;
import com.code.aon.webmail.db.Signature;
import com.esferalia.aon.entity.IEntityAlias;

public class SignatureDBController extends MailDBController implements ISignatureController {

	private final static Logger LOGGER = LoggerFactory.getLogger(SignatureDBController.class);
	
	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, SIGNATURE_DUPLICATED, name);
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
	
	private boolean checkRemovable( Signature signature ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(MailAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MAIL_ACCOUNT_SOURCE), getSource());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MAIL_ACCOUNT_SIGNATURE_ID), signature.getId());
			if ( bean.getCount(criteria) > 0 ) {
				AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, SIGNATURE_USED, signature.getName() );
				return false;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("checkRemovable for " + signature, e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return true;		
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		if ( checkRemovable((Signature) getTo() ) ) {
			super.onRemove(event);			
		}
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		Signature signature = (Signature) getTo();
		signature.setSource(getSource());
		signature.setSourceId(getSourceId());
	}
	
}
