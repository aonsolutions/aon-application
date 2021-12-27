package com.esferalia.aon.occam.api.model.finance.checkit;

import com.esferalia.aon.occam.api.model.finance.BankStatement;

public class CheckItBankStatement extends BankStatement {
	private static final long serialVersionUID = 1990271339014485685L;
		
		private Integer checkitMovementId;
		private Double currentBalance;
		
		public Double getCurrentBalance() {
			return this.currentBalance;
		}
		public CheckItBankStatement setCurrentBalance(Double currentBalance) {
			this.currentBalance = currentBalance;
			return this;
		}
		
		public Integer getCheckitMovementId() {
			return this.checkitMovementId;
		}
		public CheckItBankStatement setCheckitMovementId(Integer checkitMovementId) {
			this.checkitMovementId = checkitMovementId;
			return this;
		}
}
