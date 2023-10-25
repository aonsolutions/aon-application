package net.aonsolutions.aon.bank.nordigen;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;

class RequisitionParams {
	private String redirect;
	private String institutionId;
	private String agreement;
	private String reference;
	private AonLanguage userLanguage;
	private String ssn;
	private Boolean accountSelection;
	private Boolean redirectImmediate;
	
	public String getRedirect() {
		return redirect;
	}
	public RequisitionParams setRedirect(String redirect) {
		this.redirect = redirect;
		return this;
	}
	public String getInstitutionId() {
		return institutionId;
	}
	public RequisitionParams setInstitutionId(String institutionId) {
		this.institutionId = institutionId;
		return this;
	}
	public String getAgreement() {
		return agreement;
	}
	public RequisitionParams setAgreement(String agreement) {
		this.agreement = agreement;
		return this;
	}
	public String getReference() {
		return reference;
	}
	public RequisitionParams setReference(String reference) {
		this.reference = reference;
		return this;
	}
	public AonLanguage getUserLanguage() {
		return userLanguage;
	}
	public RequisitionParams setUserLanguage(AonLanguage userLanguage) {
		this.userLanguage = userLanguage;
		return this;
	}
	public String getSsn() {
		return ssn;
	}
	public RequisitionParams setSsn(String ssn) {
		this.ssn = ssn;
		return this;
	}
	public Boolean getAccountSelection() {
		return accountSelection;
	}
	public RequisitionParams setAccountSelection(Boolean accountSelection) {
		this.accountSelection = accountSelection;
		return this;
	}
	public Boolean getRedirectImmediate() {
		return redirectImmediate;
	}
	public RequisitionParams setRedirectImmediate(Boolean redirectImmediate) {
		this.redirectImmediate = redirectImmediate;
		return this;
	}
	
	public JSONObject toJSON() {
		return new JSONObject()
			.put("redirect", this.redirect)
			.put("institution_id", this.institutionId)
			.put("agreement", this.agreement)
			.put("reference", this.reference)
			.put("user_language", this.userLanguage != null ? this.userLanguage.getLanguage() : null)
			.put("ssn", this.ssn)
			.put("account_selection", this.accountSelection)
			.put("redirect_immediate", this.redirectImmediate);
	}
	
}
