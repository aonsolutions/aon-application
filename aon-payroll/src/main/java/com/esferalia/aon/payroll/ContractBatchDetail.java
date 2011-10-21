package com.esferalia.aon.payroll;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;


@Entity
@Table(name="contract_batch_detail")
public class ContractBatchDetail implements ITransferObject {

	private static final long serialVersionUID = -3926954764830338061L;

	private Integer id;
	private ContractBatch contractBatch;
	private Contract contract;

	@Id     
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, length=10)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity = ContractBatch.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "contract_batch", nullable = false)
	@ForeignKey(name = "FK_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH")
    @Index(name = "IDX_CONTRACT_BATCH_DETAIL_CONTRACT_BATCH")
	public ContractBatch getContractBatch() {
		return contractBatch;
	}
	public void setContractBatch(ContractBatch contractBatch) {
		this.contractBatch = contractBatch;
	}
	
	@ManyToOne(targetEntity = Contract.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "contract", nullable = false)
	@ForeignKey(name = "FK_CONTRACT_BATCH_DETAIL_CONTRACT")
	@Index(name = "IDX_CONTRACT_BATCH_DETAIL_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	
}


