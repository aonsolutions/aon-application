package com.esferalia.aon.ui.pms.event;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Allotment;
import com.esferalia.aon.pms.AllotmentItem;
import com.esferalia.aon.pms.AllotmentTariff;
import com.esferalia.aon.pms.reservation.InventoryManager;
import com.esferalia.aon.pms.sql.SQLAllotment;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.controller.AllotmentController;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;

public class AllotmentControllerListener extends ControllerAdapter implements IPmsConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		controller.setRateCode(true);
		controller.setGroup(false);
		controller.setItems(null);
		controller.setItem(null);
		controller.setTariffs(null);
		controller.setTariff(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		Allotment allotment = (Allotment)controller.getTo();
		try {
			controller.setRateCode(StringUtils.isNotEmpty(allotment.getRateCode()));
			controller.setGroup(allotment.isGroup());
			controller.setItems(obtainAllotmentItems(allotment));
			controller.setItem(controller.isRateCode() ? controller.getItems()[0] : null);
			controller.setTariffs(obtainAllotmentTariffs(allotment));
			controller.setTariff(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		Allotment allotment = (Allotment)controller.getTo();
		if (allotment.getEndDate().before(allotment.getStartDate())) {
			throw new ControllerListenerException("Las fechas de Inicio y Fin del Periodo son incorrectas.");
		}
		controller.setItems(obtainSelectedItems(controller.getItems(), controller.getItem()));
		controller.setTariffs(obtainsSelectedTariffs(controller.getTariffs(), controller.getTariff()));

		if (allotment.isActive()) {
			verifyAllotmentOverlap(allotment, StringUtils.join(controller.getItemsIds(), ","), StringUtils.join(controller.getTariffsIds(), ","));
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		Allotment allotment = (Allotment)controller.getTo();
		try {
			insertAllotmentItems(allotment, controller.getItems());
			insertAllotmentTariffs(allotment, controller.getTariffs());

			if (allotment.isActive() && controller.isRateCode()) {
				sendInventoryData(allotment);
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		Allotment allotment = (Allotment)controller.getTo();
		if (allotment.getEndDate().before(allotment.getStartDate())) {
			throw new ControllerListenerException("Las fechas de Inicio y Fin del Periodo son incorrectas.");
		}
		controller.setItems(obtainSelectedItems(controller.getItems(), controller.getItem()));
		controller.setTariffs(obtainsSelectedTariffs(controller.getTariffs(), controller.getTariff()));

		if (allotment.isActive()) {
			verifyAllotmentOverlap(allotment, StringUtils.join(controller.getItemsIds(), ","), StringUtils.join(controller.getTariffsIds(), ","));
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		Allotment allotment = (Allotment)controller.getTo();
		try {
			removeAllotmentItems(allotment);
			insertAllotmentItems(allotment, controller.getItems());
			removeAllotmentTariffs(allotment);
			insertAllotmentTariffs(allotment, controller.getTariffs());

			if (allotment.isActive() && controller.isRateCode()) {
				sendInventoryData(allotment);
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Allotment allotment = (Allotment)event.getController().getTo();
		try {
			removeAllotmentItems(allotment);
			removeAllotmentTariffs(allotment);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private Item[] obtainAllotmentItems(Allotment allotment) throws ManagerBeanException {
		Item[] items = null;
		for (ITransferObject ito : allotment.getAllotmentItems()) {
			AllotmentItem allotmentItem = (AllotmentItem)ito;
			items = (Item[])ArrayUtils.add(items, allotmentItem.getItem());
		}
		return items;
	}

	private Tariff[] obtainAllotmentTariffs(Allotment allotment) throws ManagerBeanException {
		Tariff[] tariffs = null;
		for (ITransferObject ito : allotment.getAllotmentTariffs()) {
			AllotmentTariff allotmentTariff = (AllotmentTariff)ito;
			tariffs = (Tariff[])ArrayUtils.add(tariffs, allotmentTariff.getTariff());
		}
		return tariffs;
	}

	private Item[] obtainSelectedItems(Item[] items, Item item) {
		if (item != null && item.getId() != null && !ArrayUtils.contains(items, item)) {
			items = (Item[])ArrayUtils.add(items, item);
		}
		return (Item[])ArrayUtils.removeElement(items, null);
	}

	private Tariff[] obtainsSelectedTariffs(Tariff[] tariffs, Tariff tariff) {
		if (tariff != null && tariff.getId() != null && !ArrayUtils.contains(tariffs, tariff)) {
			tariffs = (Tariff[])ArrayUtils.add(tariffs, tariff);
		}
		return (Tariff[])ArrayUtils.removeElement(tariffs, null);
	}

	private void verifyAllotmentOverlap(Allotment allotment, String items, String tariffs) throws ControllerListenerException {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			if (SQLAllotment.isAllotmentDefined(connection, allotment, items, tariffs)) {
				throw new ControllerListenerException("Ya existen Cupos definidos para la Agencia con esas condiciones.");
			}
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new ControllerListenerException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(connection);
		}
	}

	private void insertAllotmentItems(Allotment allotment, Item[] items) throws ManagerBeanException {
		if (ArrayUtils.getLength(items) > 0) {
			IManagerBean allotmentItemBean = BeanManager.getManagerBean(AllotmentItem.class);
			for (Item item : items) {
				AllotmentItem allotmentItem = new AllotmentItem();
				allotmentItem.setAllotment(allotment);
				allotmentItem.setItem(item);
				allotmentItemBean.insert(allotmentItem);
			}
		}
	}

	private void removeAllotmentItems(Allotment allotment) throws ManagerBeanException {
		IManagerBean allotmentItemBean = BeanManager.getManagerBean(AllotmentItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_ID), allotment.getId());
		for (ITransferObject ito : allotmentItemBean.getList(criteria)) {
			allotmentItemBean.remove(ito);
		}
	}

	private void insertAllotmentTariffs(Allotment allotment, Tariff[] tariffs) throws ManagerBeanException {
		if (ArrayUtils.getLength(tariffs) > 0) {
			IManagerBean allotmentTariffBean = BeanManager.getManagerBean(AllotmentTariff.class);
			for (Tariff tariff : tariffs) {
				AllotmentTariff allotmentTariff = new AllotmentTariff();
				allotmentTariff.setAllotment(allotment);
				allotmentTariff.setTariff(tariff);
				allotmentTariffBean.insert(allotmentTariff);
			}
		}
	}

	private void removeAllotmentTariffs(Allotment allotment) throws ManagerBeanException {
		IManagerBean allotmentTariffBean = BeanManager.getManagerBean(AllotmentTariff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(allotmentTariffBean.getFieldName(IEntityAlias.ALLOTMENT_TARIFF_ALLOTMENT_ID), allotment.getId());
		for (ITransferObject ito : allotmentTariffBean.getList(criteria)) {
			allotmentTariffBean.remove(ito);
		}
	}

    private void sendInventoryData(Allotment allotment) {
    	InventoryManager manager = new InventoryManager();
    	manager.processInventoryQuery(allotment);
    }

}