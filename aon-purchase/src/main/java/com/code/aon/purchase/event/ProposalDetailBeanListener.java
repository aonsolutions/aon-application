package com.code.aon.purchase.event;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.purchase.Proposal;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.enumeration.ProposalDetailStatus;
import com.code.aon.purchase.enumeration.ProposalStatus;

public class ProposalDetailBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		ProposalDetail detail = (ProposalDetail)event.getTo();
		if(!detail.isSkipProposalUpdating()){
			manageProposalStatus(detail);
		}
	}
	
	private void manageProposalStatus(ProposalDetail detail) throws ManagerBeanException{
		Integer detailPendingCount = getProposalDetailPendingCount(detail);
		Integer detailTotalCount = getProposalDetailTotalCount(detail.getProposal());
		if(detailPendingCount == 0){
			updateProposal(detail.getProposal(), ProposalStatus.PROCESSED);
		} else if(detailPendingCount > 0 && detailPendingCount < detailTotalCount){
			updateProposal(detail.getProposal(), ProposalStatus.PARTIAL_PROCESSED);
		} else if(detailPendingCount==detailTotalCount){
			updateProposal(detail.getProposal(), ProposalStatus.PENDING);
		}
	}
	
	private Integer getProposalDetailPendingCount(ProposalDetail detail) {
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		String select = "SELECT ProposalDetail" 
				+ " FROM ProposalDetail as ProposalDetail" 
				+ " WHERE ProposalDetail.proposal = :proposalId"
				+ " AND ProposalDetail.status = :pendingDetail";
		Query query = session.createQuery(select);
		query.setInteger("proposalId", detail.getProposal().getId());
		query.setInteger("pendingDetail", ProposalDetailStatus.PENDING.ordinal());
		return query.list().size();
	}

	private Integer getProposalDetailTotalCount(Proposal proposal){
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		String select = "SELECT ProposalDetail" 
				+ " FROM ProposalDetail as ProposalDetail" 
				+ " WHERE ProposalDetail.proposal = :proposalId";
		Query query = session.createQuery(select);
		query.setInteger("proposalId", proposal.getId());
		return query.list().size();
	}
	
	private void updateProposal(Proposal proposal, ProposalStatus status) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Proposal.class);
		proposal.setStatus(status);
		bean.update(proposal);
	}

}
