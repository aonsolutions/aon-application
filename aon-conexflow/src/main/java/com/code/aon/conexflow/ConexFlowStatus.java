package com.code.aon.conexflow;

public enum ConexFlowStatus {

	//-------------------- PREAUTHORIZATION
	PREAUTHORIZATION("P"),
	PREAUTHORIZATION_FAIL("P-FAIL"),
	PREAUTHORIZATION_CANCEL("P-CANCEL"),
	PREAUTHORIZATION_PAID("P-PAID"),
	PREAUTHORIZATION_CHECK("P-CHECK"),
	PREAUTHORIZATION_CHECK_FAIL("P-CHECK-FAIL"),

	//-------------------- CONFIRM PREAUTHORIZATION
	CONFIRM_PREAUTHORIZATION("C"),
	CONFIRM_PREAUTHORIZATION_FAIL("C-FAIL"),
	CONFIRM_PREAUTHORIZATION_CANCEL("C-CANCEL"),
	CONFIRM_PREAUTHORIZATION_REFUND("C-REFUND"),

	//-------------------- SALE
	SALE("V"),
	SALE_FAIL("V-FAIL"),
	SALE_CANCEL("V-CANCEL"),
	SALE_REFUND("V-REFUND"),
	SALE_CHECK("V-CHECK"),
	SALE_CHECK_FAIL("V-CHECK-FAIL"),
	
	
	//-------------------- REFUND
	REFUND("D"),
	REFUND_FAIL("D-FAIL"),
	REFUND_CANCEL("D-CANCEL"),
	
	//-------------------- CANCEL
	CANCEL("A"),
	CANCEL_FAIL("A-FAIL"),
	
	//-------------------- REDEMPTION
	REDEMPTION("R"),
	REDEMPTION_FAIL("R-FAIL"),
	REDEMPTION_CANCEL("R-CANCEL"),
	
	//-------------------- ISSUE
	ISSUE("E"),
	ISSUE_FAIL("E-FAIL"),
	ISSUE_CANCEL("E-CANCEL"),
	
	//-------------------- CREATE TOKEN
	CREATE_TOKEN("T"),
	CREATE_TOKEN_FAIL("T-FAIL"),
	
	//-------------------- CREATE TOKEN
	DELETE_TOKEN("B"),
	DELETE_TOKEN_FAIL("B-FAIL"),

	//-------------------- VALIDATE CARD
	VALIDATE_CARD("N"),
	VALIDATE_CARD_FAIL("N-FAIL"),
	
	//-------------------- TRANSACTION INFO
	TRANSACTION_INFO("S"),		
	TRANSACTION_INFO_FAIL("S-FAIL"),
	
	//-------------------- PAYSLIP
	PAYSLIP("PAYSLIP"),
	PAYSLIP_CANCEL("PAYSLIP-CANCEL"),
	PAYSLIP_REFUND("PAYSLIP-REFUND")
	;
	
	private String name;

	private ConexFlowStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public Byte value(){
		return (byte) this.ordinal();
	}
	
	public ConexFlowStatus fail() {
		if(this.equals(PREAUTHORIZATION)){return PREAUTHORIZATION_FAIL;}
		if(this.equals(PREAUTHORIZATION_CHECK)){return PREAUTHORIZATION_CHECK_FAIL;}
		if(this.equals(CONFIRM_PREAUTHORIZATION)){return CONFIRM_PREAUTHORIZATION_FAIL;}
		if(this.equals(SALE)){return SALE_FAIL;}
		if(this.equals(REFUND)){return REFUND_FAIL;}
		if(this.equals(CANCEL)){return CANCEL_FAIL;}
		if(this.equals(REDEMPTION)){return REDEMPTION_FAIL;}
		if(this.equals(ISSUE)){return ISSUE_FAIL;}
		if(this.equals(CREATE_TOKEN)){return CREATE_TOKEN_FAIL;}
		if(this.equals(DELETE_TOKEN)){return DELETE_TOKEN_FAIL;}
		if(this.equals(VALIDATE_CARD)){return VALIDATE_CARD_FAIL;}
		if(this.equals(TRANSACTION_INFO)){return TRANSACTION_INFO_FAIL;}
		return this;
	}
	
	public ConexFlowStatus cancel() {
		if(this.equals(PREAUTHORIZATION)){return PREAUTHORIZATION_CANCEL;}
		if(this.equals(CONFIRM_PREAUTHORIZATION)){return CONFIRM_PREAUTHORIZATION_CANCEL;}
		if(this.equals(SALE)){return SALE_CANCEL;}
		if(this.equals(REFUND)){return REFUND_CANCEL;}
		if(this.equals(REDEMPTION)){return REDEMPTION_CANCEL;}
		if(this.equals(ISSUE)){return ISSUE_CANCEL;}
		if(this.equals(PAYSLIP)){return PAYSLIP_CANCEL;}
		return this;
	}
	
	public static ConexFlowStatus valueOfName(String name){
		if(PREAUTHORIZATION.getName().equalsIgnoreCase(name)){return PREAUTHORIZATION;}
		if(CONFIRM_PREAUTHORIZATION.getName().equalsIgnoreCase(name)){return CONFIRM_PREAUTHORIZATION;}
		if(SALE.getName().equalsIgnoreCase(name)){return SALE;}
		if(REFUND.getName().equalsIgnoreCase(name)){return REFUND;}
		if(CANCEL.getName().equalsIgnoreCase(name)){return CANCEL;}
		if(REDEMPTION.getName().equalsIgnoreCase(name)){return REDEMPTION;}
		if(ISSUE.getName().equalsIgnoreCase(name)){return ISSUE;}
		if(CREATE_TOKEN.getName().equalsIgnoreCase(name)){return CREATE_TOKEN;}
		if(DELETE_TOKEN.getName().equalsIgnoreCase(name)){return DELETE_TOKEN;}
		if(VALIDATE_CARD.getName().equalsIgnoreCase(name)){return VALIDATE_CARD;}
		if(TRANSACTION_INFO.getName().equalsIgnoreCase(name)){return TRANSACTION_INFO;}
		if(PAYSLIP.getName().equalsIgnoreCase(name)){return PAYSLIP;}
		return null;
	}
	
	public static ConexFlowStatus valueOfDescriptionName(String name){
		if(name.contains(PREAUTHORIZATION_CANCEL.getName()+"#")){return PREAUTHORIZATION_CANCEL;}
		if(name.contains(PREAUTHORIZATION_CHECK_FAIL.getName()+"#")){return PREAUTHORIZATION_CHECK_FAIL;}
		if(name.contains(PREAUTHORIZATION_CHECK.getName()+"#")){return PREAUTHORIZATION_CHECK;}
		if(name.contains(PREAUTHORIZATION_FAIL.getName()+"#")){return PREAUTHORIZATION_FAIL;}
		if(name.contains(PREAUTHORIZATION_PAID.getName()+"#")){return PREAUTHORIZATION_PAID;}
		if(name.contains(PREAUTHORIZATION.getName()+"#")){return PREAUTHORIZATION;}

		if(name.contains(CONFIRM_PREAUTHORIZATION_FAIL.getName()+"#")){return CONFIRM_PREAUTHORIZATION_FAIL;}
		if(name.contains(CONFIRM_PREAUTHORIZATION_CANCEL.getName()+"#")){return CONFIRM_PREAUTHORIZATION_CANCEL;}
		if(name.contains(CONFIRM_PREAUTHORIZATION_REFUND.getName()+"#")){return CONFIRM_PREAUTHORIZATION_REFUND;}
		if(name.contains(CONFIRM_PREAUTHORIZATION.getName()+"#")){return CONFIRM_PREAUTHORIZATION;}

		if(name.contains(SALE_CHECK_FAIL.getName()+"#")){return SALE_CHECK_FAIL;}
		if(name.contains(SALE_CHECK.getName()+"#")){return SALE_CHECK;}
		if(name.contains(SALE_FAIL.getName()+"#")){return SALE_FAIL;}
		if(name.contains(SALE_CANCEL.getName()+"#")){return SALE_CANCEL;}
		if(name.contains(SALE_REFUND.getName()+"#")){return SALE_REFUND;}
		if(name.contains(SALE.getName()+"#")){return SALE;}

		if(name.contains(REFUND_FAIL.getName()+"#")){return REFUND_FAIL;}
		if(name.contains(REFUND_CANCEL.getName()+"#")){return REFUND_CANCEL;}
		if(name.contains(REFUND.getName()+"#")){return REFUND;}

		if(name.contains(CANCEL_FAIL.getName()+"#")){return CANCEL_FAIL;}
		if(name.contains(CANCEL.getName()+"#")){return CANCEL;}
		
		if(name.contains(REDEMPTION_FAIL.getName()+"#")){return REDEMPTION_FAIL;}
		if(name.contains(REDEMPTION_CANCEL.getName()+"#")){return REDEMPTION_CANCEL;}
		if(name.contains(REDEMPTION.getName()+"#")){return REDEMPTION;}
		
		if(name.contains(ISSUE_FAIL.getName()+"#")){return ISSUE_FAIL;}
		if(name.contains(ISSUE_CANCEL.getName()+"#")){return ISSUE_CANCEL;}
		if(name.contains(ISSUE.getName()+"#")){return ISSUE;}
		
		if(name.contains(CREATE_TOKEN_FAIL.getName()+"#")){return CREATE_TOKEN_FAIL;}
		if(name.contains(CREATE_TOKEN.getName()+"#")){return CREATE_TOKEN;}
		
		if(name.contains(DELETE_TOKEN_FAIL.getName()+"#")){return DELETE_TOKEN_FAIL;}
		if(name.contains(DELETE_TOKEN.getName()+"#")){return DELETE_TOKEN;}
		
		if(name.contains(VALIDATE_CARD_FAIL.getName()+"#")){return VALIDATE_CARD_FAIL;}
		if(name.contains(VALIDATE_CARD.getName()+"#")){return VALIDATE_CARD;}
		
		if(name.contains(TRANSACTION_INFO_FAIL.getName()+"#")){return TRANSACTION_INFO_FAIL;}
		if(name.contains(TRANSACTION_INFO.getName()+"#")){return TRANSACTION_INFO;}
		
		if(name.contains(PAYSLIP.getName()+"#")){return PAYSLIP;}
		if(name.contains(PAYSLIP_CANCEL.getName()+"#")){return PAYSLIP_CANCEL;}
		if(name.contains(PAYSLIP_REFUND.getName()+"#")){return PAYSLIP_REFUND;}

		return null;
	}
	
	
	
}
