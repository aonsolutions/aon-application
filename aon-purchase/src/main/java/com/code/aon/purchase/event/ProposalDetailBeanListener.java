package com.code.aon.purchase.event;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

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

	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		manageProposalStatus((ProposalDetail)event.getTo());
	}
	
	@SuppressWarnings("unchecked")
	private void manageProposalStatus(ProposalDetail detail) throws ManagerBeanException{
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		String select = "SELECT ProposalDetail" 
				+ " FROM ProposalDetail as ProposalDetail" 
				+ " WHERE ProposalDetail.proposal = :proposalId"
				+ " AND ProposalDetail.status = :pendingDetail";
		Query query = session.createQuery(select);
		query.setInteger("proposalId", detail.getProposal().getId());
		query.setInteger("pendingDetail", ProposalDetailStatus.PENDING.ordinal());
		List<ProposalDetail> list = query.list();
		IManagerBean bean = BeanManager.getManagerBean(Proposal.class);
		Proposal proposal = (Proposal) bean.get(detail.getProposal().getId());
		if(list.isEmpty()){
			proposal.setStatus(ProposalStatus.PROCESSED);
		} else if(list.size()>0 && list.size()<getProposalDetailCount(detail.getProposal())){
			proposal.setStatus(ProposalStatus.PARTIAL_PROCESSED);
		} else if(list.size()==getProposalDetailCount(detail.getProposal())){
			proposal.setStatus(ProposalStatus.PENDING);
		}
		bean.update(proposal);
	}
	
	private Integer getProposalDetailCount(Proposal proposal){
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		String select = "SELECT ProposalDetail" 
				+ " FROM ProposalDetail as ProposalDetail" 
				+ " WHERE ProposalDetail.proposal = :proposalId";
		Query query = session.createQuery(select);
		query.setInteger("proposalId", proposal.getId());
		return query.list().size();
	}

}
