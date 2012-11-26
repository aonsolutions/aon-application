package com.code.aon.config.util;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class SeriesUtil {

	public static synchronized SecurityLevel getSeriesSecurityLevel(String seriesCode) throws ManagerBeanException {
		Series series = SeriesUtil.getSeries(seriesCode);
		if (series != null && series.getSecurityLevel() != null) {
			return series.getSecurityLevel();
		}
		return null;
	}

	public static synchronized String ensureDeliverySeries(String seriesCode) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(seriesCode)) {
			Series series = SeriesUtil.getSeries(seriesCode);
			if (series != null && series.isDelivery()) {
				return series.getCode();
			}
		}
		return null;
	}

	public static synchronized String ensureSalesSeries(String seriesCode) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(seriesCode)) {
			Series series = SeriesUtil.getSeries(seriesCode);
			if (series != null && series.isSales()) {
				return series.getCode();
			}
		}
		return null;
	}
	
	public static synchronized String ensureInvoiceSeries(String seriesCode) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(seriesCode)) {
			Series series = SeriesUtil.getSeries(seriesCode);
			if (series != null && series.isInvoice()) {
				return series.getCode();
			}
		}
		return null;
	}

	public static synchronized String ensureProjectTasSeries(String seriesCode) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(seriesCode)) {
			Series series = SeriesUtil.getSeries(seriesCode);
			if (series != null && series.isTas()) {
				return series.getCode();
			}
		}
		return null;
	}

	public static synchronized String ensureRectificationSeries(String seriesCode) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(seriesCode)) {
			Series series = SeriesUtil.getSeries(seriesCode);
			if (series != null && series.isRectification()) {
				return series.getCode();
			}
		}
		return null;
	}
	
	public static synchronized String getFirstRectificationSeries() throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_RECTIFICATION), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), new Boolean(true));
		Iterator<ITransferObject> iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return ((Series) iter.next()).getCode();
		}
		return null;
	}

	public static synchronized boolean isSeriesActive(String seriesCode) throws ManagerBeanException {
		Series series = SeriesUtil.getSeries(seriesCode);
		return (series != null && series.isActive());
	}
	
	public static synchronized Series getSeries(String seriesCode) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_CODE), seriesCode);
		Iterator<ITransferObject> iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return (Series) iter.next();
		}
		return null;
	}

}
