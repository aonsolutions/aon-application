package com.code.aon.ui.registry.controller.event;

import static com.code.aon.ui.registry.controller.IRegistryConstants.CORPORATE_IDENTITY_SEARCH_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryAttachmentTag;
import com.code.aon.registry.Tag;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.CorporateIdentityController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CorporateIdentityControllerListener extends RegistryAttachControllerListener {

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
			getSearch().setTags( new LinkedList<Tag>() );			
		}
	}	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanSelected(event);
		RegistryAttachment attachment = (RegistryAttachment) event.getController().getTo();
		try {
			List<Tag> tags = getTagList( attachment );
			getSearch().setTags( tags );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanAdded(event);
		RegistryAttachment attachment = (RegistryAttachment) event.getController().getTo();
		try {
			List<Tag> tags = getSearch().getTags();
			updateTagList( attachment, tags, true );
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
			List<Tag> tags = getSearch().getTags();
			updateTagList( attachment, tags, false );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<RegistryAttachmentTag> getRegistryAttachmentTags( RegistryAttachment attachment ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachmentTag.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_TAG_ATTACHMENT_ID);
		criteria.addEqualExpression(alias, attachment.getId());
		criteria.addOrder("RegistryAttachmentTag.tag.name");
		return (List) bean.getList(criteria);
	}	
	
	public List<Tag> getTagList( RegistryAttachment attachment ) throws ManagerBeanException {
		List<Tag> list = new LinkedList<Tag>();
		for( RegistryAttachmentTag rat : getRegistryAttachmentTags(attachment) ) {
			if (! list.contains(rat.getTag()) ) {
				list.add(rat.getTag());
			}
		}
		return list;
	}	

	@Transient
	public void updateTagList( RegistryAttachment attachment, List<Tag> tags, boolean _new ) throws ManagerBeanException {
		List<Tag> _tags = new LinkedList<Tag>(tags);
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
