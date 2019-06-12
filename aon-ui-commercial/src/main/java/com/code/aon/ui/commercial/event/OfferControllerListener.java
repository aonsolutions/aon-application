package com.code.aon.ui.commercial.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class OfferControllerListener extends ControllerAdapter implements ICommercialConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String ATTACH_VARIABLE_NAME = "PPTO_ADJUNTO";

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		controller.setListTotal(null);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		OfferController controller = (OfferController)event.getController();
		Offer offer = (Offer) controller.getTo();
		try {
			List<SelectItem> workPlaces = companyColls.getCurrentUserWorkPlaces();
			if (workPlaces.size() > 0) {
				offer.setWorkPlace((WorkPlace)workPlaces.get(0).getValue());
			} else {
				throw new ControllerListenerException("No hay un Centro de Trabajo definido.");
			}
			offer.setSecurityLevel(SecurityLevel.OFFICIAL);
			offer.setStatus(OfferStatus.PENDING);
			offer.setScope(offer.getWorkPlace().getScope());
			offer.setType(OfferType.NORMAL);
			controller.setAddresses(null);
			controller.setProjects(null);
			controller.setDefaultPayMethod(null);
			controller.resetOfferPayMethod();
			controller.initSeries();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		try {
			controller.loadAddresses(((Offer)controller.getTo()).getTarget().getRegistry().getId());
			controller.loadProjects(((Offer)controller.getTo()).getTarget().getRegistry().getId());
			controller.loadDefaultPayMethod(((Offer)controller.getTo()).getTarget().getRegistry(), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Offer offer = (Offer)event.getController().getTo();
		try {
			IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
			IManagerBean commercialTermBean = BeanManager.getManagerBean(CommercialTerm.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(commercialTermBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_GENERAL), true);
			criteria.addOrder(commercialTermBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE));
			int line = 0;
			for (ITransferObject to : commercialTermBean.getList(criteria)) {
				CommercialTerm commercialTerm = (CommercialTerm)to;

				OfferTerm offerTerm = new OfferTerm();
				offerTerm.setOffer(offer);
				offerTerm.setLine(++line);
				offerTerm.setName(commercialTerm.getName());
				offerTerm.setDescription(commercialTerm.getDescription());
				offerTerm.setGeneral(commercialTerm.isGeneral());
				offerTermBean.insert(offerTerm);
			}
			
			if(OfferType.DEALERSHIP == offer.getType()) {
				Supplier supplier = offer.getSupplier();
				List<ITransferObject> list = obtainAttachList(supplier);
				if(list!=null && !list.isEmpty()) {
					IManagerBean offerAttach = BeanManager.getManagerBean(OfferAttachment.class);
					for(int i=0; i<list.size(); i++) {
						RegistryAttachment ra = (RegistryAttachment) list.get(i);
						OfferAttachment oa = (OfferAttachment) offerAttach.createNewTo(); 
						oa.setOffer(offer);
						oa.setMimeType(ra.getMimeType());
						oa.setDescription("Adjunto proveedor " + (i+1));
						oa.setData(ra.getData());
						offerAttach.insert(oa);
					}
				}
			}

			IController offerDetailController = FormUtil.getController(OFFER_DETAIL_CONTROLLER_NAME);
			offerDetailController.onReset(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		controller.setListTotal(null);
	}

	private List<ITransferObject> obtainAttachList( Supplier supplier ) throws ManagerBeanException {
		IManagerBean attach = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(attach.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), supplier.getId());
		criteria.addEqualExpression(attach.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), ATTACH_VARIABLE_NAME);
		return attach.getList(criteria);
	}
	
}