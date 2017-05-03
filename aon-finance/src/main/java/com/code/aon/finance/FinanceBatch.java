package com.code.aon.finance;

import java.util.HashSet;
import java.util.List;
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
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.FinanceBatchDB;

@Entity
@Table(name = "fbatch")
public class FinanceBatch extends FinanceBatchDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceBatch.class.getName());

	private Set<FinanceBatchDetail> lines = new HashSet<FinanceBatchDetail>();

    @OneToMany(mappedBy = "financeBatch", cascade={CascadeType.REMOVE})
	public Set<FinanceBatchDetail> getLines() {
		return this.lines;
	}
	public void setLines( Set<FinanceBatchDetail> lines ) {
		this.lines = lines;
	}

	@Transient
	public List<ITransferObject> getDetailList() {
		try {
			IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), getId());
	        criteria.addOrder(financeBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_DUE_DATE));
	        criteria.addOrder(financeBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_CONCEPT));
			return financeBatchDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining financeBatchDetail list", e);
		}
		return null;
	}

	@Transient
	public Integer getFinanceBatchTotalDetails(){
		try {
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), getId());
			return fBatchDetailBean.getCount(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining fbatch total details", e);
		}
		return new Integer(0);
	}

	@Transient
	public Double getFinanceBatchTotalAmount(){
		try {
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), getId());
			Projection projection = Projection.sum(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_AMOUNT));
			Object value = fBatchDetailBean.getUniqueResult(projection, criteria);
			if (value != null) {
				return (Double)value;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining fbatch total amount", e);
		}
		return new Double(0);
	}

	@Transient
    public boolean isTodo() {
        return FinanceBatchStatus.TODO == getFinanceBatchStatus();
    }
	@Transient
    public boolean isDone() {
        return FinanceBatchStatus.DONE == getFinanceBatchStatus();
    }
	@Transient
    public boolean isRecorded() {
        return FinanceBatchStatus.RECORDED == getFinanceBatchStatus();
    }

	@Transient
    public boolean isDiskMode() {
        return FinanceBatchType.NONE != getFinanceBatchType();
    }

}