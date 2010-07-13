package com.code.aon.ui.ebackoffice.event;

import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.Eccatalogue;
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;
import com.code.aon.product.Catalogue;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ebackoffice.controller.EccatalogueController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;

public class CatalogueOptionalControllerListener extends ControllerAdapter implements
		IItemConstants {

	private static final Logger LOGGER = Logger
			.getLogger(CatalogueOptionalControllerListener.class.getName());

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
	
	/*	try {
			Integer id =((Catalogue) this.getController().getTo()).getId();		
			IManagerBean bean = BeanManager.getManagerBean(Eccatalogue.class);
			String idCatalog = bean.getFieldName(IEbackofficeAlias.ECCATALOGUE_CATALOGUE_ID);
			Criteria cri1 = new Criteria();
			cri1.addEqualExpression(idCatalog, id);			
			EccatalogueController con = (EccatalogueController)FormUtil.getController("eccatalogue");
			con.setCriteria(cri1);
			if(con.getModel()!=null){
			con.onSelectFirst(null);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		*/		
	}
	

	
	
	

}