package com.esferalia.aon.occam.api.model.finance.nordigen;

import com.esferalia.aon.occam.api.model.finance.BankStatement;

public class NordigenBankStatement extends BankStatement {
	private static final long serialVersionUID = 497558091818899419L;	
		
		private Integer nordigenMovementId;
		private Double currentBalance;
		
		public Double getCurrentBalance() {
			return this.currentBalance;
		}
		public NordigenBankStatement setCurrentBalance(Double currentBalance) {
			this.currentBalance = currentBalance;
			return this;
		}
		
		public Integer getNordigenMovementId() {
			return this.nordigenMovementId;
		}
		public NordigenBankStatement setNordigenMovementId(Integer nordigenMovementId) {
			this.nordigenMovementId = nordigenMovementId;
			return this;
		}
}
