package com.code.aon.purchase;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.purchase.enumeration.ProposalDetailStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProposalDetailDB;

@Entity
@Table(name="proposal_detail")
public class ProposalDetail extends ProposalDetailDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private PurchaseDetail purchaseDetail;
	
	private boolean skipProposalUpdating;
	
	@Transient
	public boolean isSkipProposalUpdating() {
		return skipProposalUpdating;
	}

	public void setSkipProposalUpdating(boolean skipProposalUpdating) {
		this.skipProposalUpdating = skipProposalUpdating;
	}

	@Transient
	public boolean isPending(){
		return getStatus()==ProposalDetailStatus.PENDING;
	}

	@Transient
	public PurchaseDetail getPurchaseDetail() {
		if(purchaseDetail==null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(PurchaseDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PROPOSAL_DETAIL_ID), this.getId());
				purchaseDetail = (PurchaseDetail) bean.getList(criteria).get(0);
			} catch (Exception e) {
				// NADA
			}
		}
		return purchaseDetail;
	}

}
