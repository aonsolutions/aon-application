package com.code.aon.ebackoffice.event;

import java.util.logging.Logger;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ebackoffice.Eccatalogue;
import com.code.aon.ebackoffice.enumeration.CatalogueType;

public class EccatalogueBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final Logger LOGGER = Logger
			.getLogger(EccatalogueBeanVetoListener.class.getName());

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		if (((Eccatalogue) evt.getTo()).getType()==CatalogueType.MAIN) {
			try {
				checkMainCatalogue((Eccatalogue) evt.getTo());
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {

		if (((Eccatalogue) evt.getTo()).getType()==CatalogueType.MAIN) {
			try {
				checkMainCatalogue((Eccatalogue) evt.getTo());
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void checkMainCatalogue(ITransferObject to)
			throws ManagerBeanException {

/*		IManagerBean eccatalogueBean = BeanManager.getManagerBean(Eccatalogue.class);
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Eccatalogue catalog = (Eccatalogue) to;

		List<ITransferObject> lista;
		lista = eccatalogueBean.getList(null);

		for (ITransferObject rec : lista) {
			Eccatalogue eccat = (Eccatalogue) rec;
			if (catalog.getId() != eccat.getId()) {
				eccat.setType(CatalogueType.STANDARD);
				eccatalogueBean.update(eccat);
				;
			}

		}*/
	}

}
