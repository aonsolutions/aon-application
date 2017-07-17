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
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.StopSales;
import com.esferalia.aon.pms.StopSalesItem;
import com.esferalia.aon.pms.StopSalesTariff;
import com.esferalia.aon.pms.sql.SQLStopSales;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.StopSalesController;

public class StopSalesControllerListener extends ControllerAdapter implements IPmsConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		StopSalesController controller = (StopSalesController)event.getController();
		controller.setItems(null);
		controller.setItem(null);
		controller.setTariffs(null);
		controller.setTariff(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		StopSalesController controller = (StopSalesController)event.getController();
		StopSales stopSales = (StopSales)controller.getTo();
		try {
			controller.setItems(obtainStopSalesItems(stopSales));
			controller.setItem(null);
			controller.setTariffs(obtainStopSalesTariffs(stopSales));
			controller.setTariff(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		StopSalesController controller = (StopSalesController)event.getController();
		StopSales stopSales = (StopSales)controller.getTo();
		if (stopSales.getEndDate().before(stopSales.getStartDate())) {
			throw new ControllerListenerException("Las fechas de Inicio y Fin del Periodo son incorrectas.");
		}
		controller.setItems(obtainSelectedItems(controller.getItems(), controller.getItem()));
		controller.setTariffs(obtainsSelectedTariffs(controller.getTariffs(), controller.getTariff()));

		if (stopSales.isActive()) {
			verifyStopSalesOverlap(stopSales, StringUtils.join(controller.getItemsIds(), ","), StringUtils.join(controller.getTariffsIds(), ","));
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		StopSalesController controller = (StopSalesController)event.getController();
		StopSales stopSales = (StopSales)controller.getTo();
		try {
			insertStopSalesItems(stopSales, controller.getItems());
			insertStopSalesTariffs(stopSales, controller.getTariffs());
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		StopSalesController controller = (StopSalesController)event.getController();
		StopSales stopSales = (StopSales)controller.getTo();
		if (stopSales.getEndDate().before(stopSales.getStartDate())) {
			throw new ControllerListenerException("Las fechas de Inicio y Fin del Periodo son incorrectas.");
		}
		controller.setItems(obtainSelectedItems(controller.getItems(), controller.getItem()));
		controller.setTariffs(obtainsSelectedTariffs(controller.getTariffs(), controller.getTariff()));

		if (stopSales.isActive()) {
			verifyStopSalesOverlap(stopSales, StringUtils.join(controller.getItemsIds(), ","), StringUtils.join(controller.getTariffsIds(), ","));
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		StopSalesController controller = (StopSalesController)event.getController();
		StopSales stopSales = (StopSales)controller.getTo();
		try {
			removeStopSalesItems(stopSales);
			insertStopSalesItems(stopSales, controller.getItems());
			removeStopSalesTariffs(stopSales);
			insertStopSalesTariffs(stopSales, controller.getTariffs());
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		StopSales stopSales = (StopSales)event.getController().getTo();
		try {
			removeStopSalesItems(stopSales);
			removeStopSalesTariffs(stopSales);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private Item[] obtainStopSalesItems(StopSales stopSales) throws ManagerBeanException {
		Item[] items = null;
		for (ITransferObject ito : stopSales.getStopSalesItems()) {
			StopSalesItem stopSalesItem = (StopSalesItem)ito;
			items = (Item[])ArrayUtils.add(items, stopSalesItem.getItem());
		}
		return items;
	}

	private Tariff[] obtainStopSalesTariffs(StopSales stopSales) throws ManagerBeanException {
		Tariff[] tariffs = null;
		for (ITransferObject ito : stopSales.getStopSalesTariffs()) {
			StopSalesTariff stopSalesTariff = (StopSalesTariff)ito;
			tariffs = (Tariff[])ArrayUtils.add(tariffs, stopSalesTariff.getTariff());
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

	private void verifyStopSalesOverlap(StopSales stopSales, String items, String tariffs) throws ControllerListenerException {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			if (SQLStopSales.isStopSalesDefined(connection, stopSales, items, tariffs)) {
				throw new ControllerListenerException("Ya existe un Paro de Ventas definido con esas condiciones.");
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

	private void insertStopSalesItems(StopSales stopSales, Item[] items) throws ManagerBeanException {
		if (ArrayUtils.getLength(items) > 0) {
			IManagerBean stopSalesItemBean = BeanManager.getManagerBean(StopSalesItem.class);
			for (Item item : items) {
				StopSalesItem stopSalesItem = new StopSalesItem();
				stopSalesItem.setStopSales(stopSales);
				stopSalesItem.setItem(item);
				stopSalesItemBean.insert(stopSalesItem);
			}
		}
	}

	private void removeStopSalesItems(StopSales stopSales) throws ManagerBeanException {
		IManagerBean stopSalesItemBean = BeanManager.getManagerBean(StopSalesItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stopSalesItemBean.getFieldName(IEntityAlias.STOP_SALES_ITEM_STOP_SALES_ID), stopSales.getId());
		for (ITransferObject ito : stopSalesItemBean.getList(criteria)) {
			stopSalesItemBean.remove(ito);
		}
	}

	private void insertStopSalesTariffs(StopSales stopSales, Tariff[] tariffs) throws ManagerBeanException {
		if (ArrayUtils.getLength(tariffs) > 0) {
			IManagerBean stopSalesTariffBean = BeanManager.getManagerBean(StopSalesTariff.class);
			for (Tariff tariff : tariffs) {
				StopSalesTariff stopSalesTariff = new StopSalesTariff();
				stopSalesTariff.setStopSales(stopSales);
				stopSalesTariff.setTariff(tariff);
				stopSalesTariffBean.insert(stopSalesTariff);
			}
		}
	}

	private void removeStopSalesTariffs(StopSales stopSales) throws ManagerBeanException {
		IManagerBean stopSalesTariffBean = BeanManager.getManagerBean(StopSalesTariff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stopSalesTariffBean.getFieldName(IEntityAlias.STOP_SALES_TARIFF_STOP_SALES_ID), stopSales.getId());
		for (ITransferObject ito : stopSalesTariffBean.getList(criteria)) {
			stopSalesTariffBean.remove(ito);
		}
	}

}