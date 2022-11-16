package com.esferalia.aon.occam.api.model.seres;

public class EdiCodes {

	String department;
	String customerEdiCode;
	String deliveryPointEdiCode;
	String customerPackage;
	String companyEdiCode;
	
	String customerEdiHeader;
	String customerEdiPoint;
	String customerEdiInvoice;
	
	String mscode;
	String mrcode;
	String sucode;
	String pwcode;
	String dpcode;
	String uccode;
	String bycode;
	String shcode;
	String ivcode;

	public String getDepartment() {
		return department;
	}

	public EdiCodes setDepartment(String department) {
		this.department = department;
		return this;
	}

	public String getCustomerEdiCode() {
		return customerEdiCode;
	}

	public EdiCodes setCustomerEdiCode(String customerEdiCode) {
		this.customerEdiCode = customerEdiCode;
		return this;
	}

	public String getDeliveryPointEdiCode() {
		return deliveryPointEdiCode;
	}

	public EdiCodes setDeliveryPointEdiCode(String deliveryPointEdiCode) {
		this.deliveryPointEdiCode = deliveryPointEdiCode;
		return this;
	}

	public String getCustomerPackage() {
		return customerPackage;
	}

	public EdiCodes setCustomerPackage(String customerPackage) {
		this.customerPackage = customerPackage;
		return this;
	}

	public String getCompanyEdiCode() {
		return companyEdiCode;
	}

	public EdiCodes setCompanyEdiCode(String companyEdiCode) {
		this.companyEdiCode = companyEdiCode;
		return this;
	}

	public String getCustomerEdiHeader() {
        return customerEdiHeader;
    }
	
	public EdiCodes setCustomerEdiHeader(String customerEdiHeader) {
        this.customerEdiHeader = customerEdiHeader;
        return this;
    }
	
	public String getCustomerEdiPoint() {
        return customerEdiPoint;
    }
	
	public EdiCodes setCustomerEdiPoint(String customerEdiPoint) {
        this.customerEdiPoint = customerEdiPoint;
        return this;
    }
	
	public String getCustomerEdiInvoice() {
        return customerEdiInvoice;
    }
	
	public EdiCodes setCustomerEdiInvoice(String customerEdiInvoice) {
        this.customerEdiInvoice = customerEdiInvoice;
        return this;
    }
	
	
	public String getMscode() {
		if(mscode == null) {
			mscode = getCompanyEdiCode();
		}
		return mscode;
	}

	public EdiCodes setMscode(String mscode) {
		this.mscode = mscode;
		return this;
	}

	public String getMrcode() {
		if(mrcode == null) {
			mrcode = getCustomerEdiCode();
		}
		return mrcode;
	}

	public EdiCodes setMrcode(String mrcode) {
		this.mrcode = mrcode;
		return this;
	}

	public String getSucode() {
		if(sucode == null) {
			sucode = getCompanyEdiCode();
		}
		return sucode;
	}

	public EdiCodes setSucode(String sucode) {
		this.sucode = sucode;
		return this;
	}

	public String getPwcode() {
		if(pwcode == null) {
			pwcode = getCompanyEdiCode();
		}
		return pwcode;
	}

	public EdiCodes setPwcode(String pwcode) {
		this.pwcode = pwcode;
		return this;
	}

	public String getDpcode() {
		if(dpcode == null) {
			dpcode = getDeliveryPointEdiCode();
		}
		return dpcode;
	}

	public EdiCodes setDpcode(String dpcode) {
		this.dpcode = dpcode;
		return this;
	}

	public String getUccode() {
		return uccode;
	}

	public EdiCodes setUccode(String uccode) {
		this.uccode = uccode;
		return this;
	}

	public String getBycode() {
		if(bycode == null) {
			bycode = getCustomerEdiCode();
		}
		return bycode;
	}

	public EdiCodes setBycode(String bycode) {
		this.bycode = bycode;
		return this;
	}

	public String getShcode() {
		if(shcode == null) {
			shcode = getCustomerEdiCode();
		}
		return shcode;
	}

	public EdiCodes setShcode(String shcode) {
		this.shcode = shcode;
		return this;
	}

	public String getIvcode() {
		if(ivcode == null) {
			ivcode = getCustomerEdiCode();
		}
		return ivcode;
	}

	public EdiCodes setIvcode(String ivcode) {
		this.ivcode = ivcode;
		return this;
	}
	
}
