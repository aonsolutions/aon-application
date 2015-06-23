package com.esferalia.aon.occam.api.model.fiscal.d2_deposit;

public enum D2DepositFooterKey {
	// balance de situacion
	A(4,""),D(2,"");
	
	
		private String code;
		private String name;
		private D2DepositFooterKey(Integer code, String name) {
			this(Integer.toString(code));
			this.name = name;
		}
		
		private D2DepositFooterKey(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public String getDescription() {
			return D2DepositDescription.DESCRIPTION_MAP.get(this);
		}

		public String getName(){
			return this.name;
		}
		
		public static void main(String[] args) {
			
		}
	
}
