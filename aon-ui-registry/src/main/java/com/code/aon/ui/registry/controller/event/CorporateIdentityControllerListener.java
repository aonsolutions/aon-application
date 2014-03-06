package com.code.aon.ui.registry.controller.event;

import static com.code.aon.ui.registry.controller.IRegistryConstants.CORPORATE_IDENTITY_SEARCH_CONTROLLER_NAME;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Company;
import com.code.aon.config.Domain;
import com.code.aon.config.Tag;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryAttachmentTag;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.CorporateIdentityController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CorporateIdentityControllerListener extends RegistryAttachControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public CorporateIdentitySearchListener getSearch() {
		return (CorporateIdentitySearchListener) AonUtil.getRegisteredBean(CORPORATE_IDENTITY_SEARCH_CONTROLLER_NAME);
	}
	
	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanCreated(event);
		CorporateIdentityController cic = (CorporateIdentityController) event.getController();
		if ( cic.isMassiveUpload() && (cic.getLastAttachment() != null) ) {
			RegistryAttachment attach = (RegistryAttachment) cic.getTo();
			attach.setConfidential(cic.getLastAttachment().isConfidential());
			attach.setAttachDate(cic.getLastAttachment().getAttachDate());
			attach.setScope(cic.getLastAttachment().getScope());
			attach.setCategory(cic.getLastAttachment().getCategory());
		} else {
			getSearch().setTags( null );			
		}
		try {		
			IManagerBean domainBean = BeanManager.getManagerBean(Domain.class);
			Domain domain = null;
			if ( cic.isMassiveUpload() ) {
				domain = (Domain) domainBean.createNewTo();
			} else {
				domain = (Domain) domainBean.get(DomainManager.getCurrentDomain());
			}
			cic.setDomain( domain );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanSelected(event);
		CorporateIdentityController cic = (CorporateIdentityController) event.getController();
		RegistryAttachment attachment = (RegistryAttachment) cic.getAttachment();
		try {
			getSearch().setTags( getTagList(attachment) );
			cic.setDomain((Domain)BeanManager.getManagerBean(Domain.class).get(attachment.getDomain()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		super.beforeBeanAdded(event);
		CorporateIdentityController cic = (CorporateIdentityController) event.getController();
		Integer domainId = DomainManager.getCurrentDomain();
		if (! domainId.equals(cic.getDomain().getId()) ) {
			try {			
				RegistryAttachment attachment = (RegistryAttachment) cic.getAttachment();
				attachment.setDomain(cic.getDomain().getId());
				IManagerBean bean = BeanManager.getManagerBean(Company.class);
				Criteria criteria = new Criteria();
				criteria.setSkipDomainFilter(true);
				criteria.addEqualExpression("Compay.domain", cic.getDomain().getId());
				List<ITransferObject> list = bean.getList(criteria);
				if (! list.isEmpty() ) {
					Company company = (Company) list.get(0);
					attachment.setRegistry( company.getRegistry() );
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e);
			}
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanAdded(event);
		RegistryAttachment attachment = (RegistryAttachment) event.getController().getTo();
		try {
			updateTagList( attachment, getSearch().getTags(), true );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanUpdated(event);
		RegistryAttachment attachment = (RegistryAttachment) event.getController().getTo();
		try {
			updateTagList( attachment, getSearch().getTags(), false );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<RegistryAttachmentTag> getRegistryAttachmentTags( RegistryAttachment attachment ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachmentTag.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_TAG_ATTACHMENT_ID);
		criteria.addEqualExpression(alias, attachment.getId());
		criteria.addOrder("RegistryAttachmentTag.tag.name");
		return (List) bean.getList(criteria);
	}	
	
	private Tag[] getTagList( RegistryAttachment attachment ) throws ManagerBeanException {
		List<Tag> list = new LinkedList<Tag>();
		for( RegistryAttachmentTag rat : getRegistryAttachmentTags(attachment) ) {
			if (! list.contains(rat.getTag()) ) {
				list.add(rat.getTag());
			}
		}
		return list.toArray(new Tag[list.size()]);
	}	

	private void updateTagList( RegistryAttachment attachment, Tag[] tags, boolean _new ) throws ManagerBeanException {
		List<Tag> _tags = new LinkedList<Tag>(Arrays.asList(tags));
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachmentTag.class);
		if (! _new ) {
			for( RegistryAttachmentTag rat : getRegistryAttachmentTags(attachment) ) {
				if ( _tags.contains(rat.getTag()) ) {
					_tags.remove(rat.getTag());
				} else {
					bean.remove(rat);
				}
			}
		}
		if (! _tags.isEmpty() ) {
			for( Tag tag : _tags ) {
				if ((tag != null) && (tag.getId() != null)) {
					RegistryAttachmentTag rat = new RegistryAttachmentTag();
					rat.setAttachment(attachment);
					rat.setTag(tag);
					bean.insert(rat);
				}
			}
		}
	}		
	
}
