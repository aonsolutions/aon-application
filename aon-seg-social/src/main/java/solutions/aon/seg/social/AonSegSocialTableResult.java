package solutions.aon.seg.social;

public class AonSegSocialTableResult {

	String totalCccs;
	String totalLiquidations;
	String amount;
	String stretches;
	String calculatedStretches;
	String noCalculatedStretches;
	

	public AonSegSocialTableResult(String totalCccs, String totalLiquidations, String amount, String stretches,
			String calculatedStretches, String noCalculatedStretches) {
		super();
		this.totalCccs = totalCccs;
		this.totalLiquidations = totalLiquidations;
		this.amount = amount;
		this.stretches = stretches;
		this.calculatedStretches = calculatedStretches;
		this.noCalculatedStretches = noCalculatedStretches;
	}

	public AonSegSocialTableResult() {
		super();
	}

	public String getTotalCccs() {
		return totalCccs;
	}

	public void setTotalCccs(String totalCccs) {
		this.totalCccs = totalCccs;
	}

	public String getTotalLiquidations() {
		return totalLiquidations;
	}

	public void setTotalLiquidations(String totalLiquidations) {
		this.totalLiquidations = totalLiquidations;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getStretches() {
		return stretches;
	}

	public void setStretches(String stretches) {
		this.stretches = stretches;
	}

	public String getCalculatedStretches() {
		return calculatedStretches;
	}

	public void setCalculatedStretches(String calculatedStretches) {
		this.calculatedStretches = calculatedStretches;
	}

	public String getNoCalculatedStretches() {
		return noCalculatedStretches;
	}

	public void setNoCalculatedStretches(String noCalculatedStretches) {
		this.noCalculatedStretches = noCalculatedStretches;
	}

}
