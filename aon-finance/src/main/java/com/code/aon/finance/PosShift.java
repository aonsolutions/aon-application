package com.code.aon.finance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.PosShiftDB;

@Entity
@Table(name="pos_shift")
public class PosShift extends PosShiftDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Finance.class.getName());
	
	private Set<PosShiftCount> posShiftCount = new HashSet<PosShiftCount>();
	private boolean skipCheckPosShift;
	private Map<PayMethod, double[]> totalShiftCountMap;
	private Double amount;

	public PosShift() {
		setSkipCheckPosShift(false);
	}

	@OneToMany(mappedBy = "posShift", cascade={CascadeType.REMOVE})
	public Set<PosShiftCount> getPosShiftCount() {
		return posShiftCount;
	}
	public void setPosShiftCount(Set<PosShiftCount> posShiftCount) {
		this.posShiftCount = posShiftCount;
	}
	
	@Transient
	public boolean isSkipCheckPosShift() {
		return skipCheckPosShift;
	}
	public void setSkipCheckPosShift(boolean skipCheckPosShift) {
		this.skipCheckPosShift = skipCheckPosShift;
	}

	@Transient
	public Map<PayMethod, double[]> getTotalShiftCountMap() {
		if (totalShiftCountMap == null || totalShiftCountMap.size() == 0) {
			totalShiftCountMap = calculateTotalShiftCountMap();
		}
		return totalShiftCountMap;
	}

	public void setTotalShiftCountMap(Map<PayMethod,double[]> totalShiftCountMap) {
		this.totalShiftCountMap = totalShiftCountMap;
	}

	@Transient
	private Map<PayMethod,double[]> calculateTotalShiftCountMap() {
		Map<PayMethod,double[]> totalShiftCountMap = new HashMap<PayMethod, double[]>();
		try {
			IManagerBean posShiftCountBean = BeanManager.getManagerBean(PosShiftCount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posShiftCountBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), getId());
			criteria.addOrder(posShiftCountBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_PAY_METHOD_NAME));
			for (ITransferObject ito : posShiftCountBean.getList(criteria)) {
				PosShiftCount posShiftCount = (PosShiftCount)ito;
				double[] totals = (totalShiftCountMap.containsKey(posShiftCount.getPayMethod())) ? totalShiftCountMap.get(posShiftCount.getPayMethod()) : new double[2];
				totals[0] = CommonUtil.round(totals[0] + posShiftCount.getAmount());
				totalShiftCountMap.put(posShiftCount.getPayMethod(), totals);
			}

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), new Boolean(false));
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_ID), getId());
			criteria.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_TYPE), PayMethodType.NEGOTIABLE_DOCUMENT);
			criteria.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_TYPE), PayMethodType.OTHER);
			criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_NAME));
			Projection projection = Projection.group(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD));
			for (Object obj : financeBean.getList(new ProjectionList(projection), criteria)) {
				PayMethod payMethod = (PayMethod)obj;
				Criteria sumCriteria = new Criteria();
				sumCriteria.addExpression(criteria.getExpression());
				sumCriteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), payMethod.getId());
				projection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
				Object value = financeBean.getUniqueResult(projection, sumCriteria);

				double[] totals = (totalShiftCountMap.containsKey(payMethod)) ? totalShiftCountMap.get(payMethod) : new double[2];
				totals[1] = CommonUtil.round(totals[1] + ((value == null) ? 0 : ((Double)value).doubleValue()));
				totalShiftCountMap.put(payMethod, totals);
			}

			for (PayMethod payMethod : totalShiftCountMap.keySet()) {
				if (payMethod.getType() == PayMethodType.CASH_BASIS) {
					double[] totals = (totalShiftCountMap.containsKey(payMethod)) ? totalShiftCountMap.get(payMethod) : new double[2];
					totals[0] = CommonUtil.round(totals[0] - getInitialAmount());
					totalShiftCountMap.put(payMethod, totals);
					break;
				}
			}
		} catch (ManagerBeanException ex) {
			LOGGER.error("Error obtaining totalShiftCountMap.", ex);
		}
		return totalShiftCountMap;
	}

	@Transient
	public Double getAmount() {
		return amount;
	}

	@Transient
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Transient
	public Double getTotalCountAmount() throws ManagerBeanException {
		return getPosShiftCountAmount(null);
	}

	@Transient
	public Double getTotalCountCashAmount() throws ManagerBeanException {
		return getPosShiftCountAmount(PayMethodType.CASH_BASIS);
	}

	@Transient
	private double getPosShiftCountAmount(PayMethodType type) throws ManagerBeanException {
		double value = 0;
		for (PayMethod payMethod : getTotalShiftCountMap().keySet()) {
			if (type == null || type == payMethod.getType()) {
				value = CommonUtil.round(value + getTotalShiftCountMap().get(payMethod)[0]);
			}
		}
		return value;
	}

	@Transient
	public boolean isClosed() {
		return getEndTime() != null;
	}

}
