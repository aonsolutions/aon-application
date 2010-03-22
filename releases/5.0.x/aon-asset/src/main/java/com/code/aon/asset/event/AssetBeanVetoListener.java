package com.code.aon.asset.event;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.dao.IAssetAlias;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;

public class AssetBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AssetBeanVetoListener.class.getName());

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		System.out.println("vetoableBeanInserted");
		checkDates((AssetActivity) evt.getTo());
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		System.out.println("vetoableBeanUpdated");
		checkDates((AssetActivity) evt.getTo());
	}
	
	

	private void checkDates(AssetActivity act)
			throws ManagerBeanVetoListenerException {
		if (hasOverlap(act)) {
    		throw new ManagerBeanVetoListenerException(
					"Existe solape de horarios en la fecha y hora indicadas.");
		}
	}

	public boolean hasOverlap(AssetActivity act) {
		Date fromTime = act.getFromTime();
		Date toTime = act.getToTime();
		Integer id = act.getId();
		boolean overlapped = false;

		try {
			IManagerBean bean = BeanManager.getManagerBean(AssetActivity.class);
			Criteria criteria = new Criteria();

			String alias = bean.getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_ID);
			criteria.addEqualExpression(alias, act.getAsset().getId());

			alias = bean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
			criteria.addEqualExpression(alias, act.getDate());

			List<ITransferObject> lista = bean.getList(criteria);
			Iterator<ITransferObject> iter = lista.iterator();

			while (iter.hasNext() && !overlapped) {
				AssetActivity aa = (AssetActivity) iter.next();
				if(aa.getId().equals(id) || !aa.getStatus().equals(ActivityStatus.ACCEPTED)) {
					overlapped = false;
//				} else if ((aa.getFromTime().before(toTime) || aa.getFromTime().equals(toTime))
//						|| (aa.getToTime().after(fromTime) || aa.getToTime().equals(fromTime))) {
				} else if (aa.getFromTime().before(toTime) && aa.getFromTime().after(fromTime)) {
					overlapped = true;
				} else if (aa.getToTime().after(fromTime) && aa.getToTime().before(toTime)) {
					overlapped = true;
				} else if (aa.getToTime().equals(toTime) && aa.getFromTime().equals(fromTime)) {
					overlapped = true;
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> hasOverlap " + e.getMessage());
		}
		return overlapped;
	}
}
