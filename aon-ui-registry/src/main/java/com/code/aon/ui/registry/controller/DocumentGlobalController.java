package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.registry.controller.IRegistryConstants.BATCH_DOCUMENT_CONTROLLER_NAME;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class DocumentGlobalController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void clearCriteria() throws ManagerBeanException {
		super.clearCriteria();
		getCriteria().setSkipDomainFilter(true);
	}

	@Override
	public void setCriteria(Criteria criteria) throws ManagerBeanException {
		super.setCriteria(criteria);
		getCriteria().setSkipDomainFilter(true);		
	}
	
	public String getDomainDescription() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			RegistryAttachment attach = (RegistryAttachment) getModel().getRowData();
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(attach.getDomain());
			if ( domain != null ) {
				return domain.getDescription();
			}
		}
		return null;
	}
	
    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("index");
        IAttachment attachment = (IAttachment) getManagerBean().get(Integer.valueOf(id));
        DownloadUtil.downloadAttachment( attachment );    	
    }

	public boolean isCurrentInBatch() throws ManagerBeanException {
		BatchDocument bd = (BatchDocument) AonUtil.getRegisteredBean(BATCH_DOCUMENT_CONTROLLER_NAME);
		return bd.isInBatch( (IAttachment) getSelectedTO() );
	}

	public void onAddCurrentToBatch(ActionEvent event) throws ManagerBeanException {
		BatchDocument bd = (BatchDocument) AonUtil.getRegisteredBean(BATCH_DOCUMENT_CONTROLLER_NAME);
		bd.addToBatch( (IAttachment) getSelectedTO() );
	}
    
}