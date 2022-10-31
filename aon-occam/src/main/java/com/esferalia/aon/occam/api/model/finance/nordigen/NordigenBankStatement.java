package com.esferalia.aon.occam.api.model.finance.nordigen;

import com.esferalia.aon.occam.api.model.finance.BankStatement;

public class NordigenBankStatement extends BankStatement {
	private static final long serialVersionUID = 497558091818899419L;	
		
		private Long nordigenMovementId;
		private Double currentBalance;
		private boolean isPending;
		
		public Double getCurrentBalance() {
			return this.currentBalance;
		}
		public NordigenBankStatement setCurrentBalance(Double currentBalance) {
			this.currentBalance = currentBalance;
			return this;
		}
		
		public Long getNordigenMovementId() {
			return this.nordigenMovementId;
		}
		public NordigenBankStatement setNordigenMovementId(Long nordigenMovementId) {
			this.nordigenMovementId = nordigenMovementId;
			return this;
		}
		
		public boolean isPending() {
			return isPending;
		}
		public NordigenBankStatement setPending(boolean isPending) {
			this.isPending = isPending;
			return this;
		}
}
