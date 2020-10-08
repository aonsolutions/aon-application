package solutions.aon.in.invoice.templates;

public class InvoiceTax {
	
	private TaxType type;
	private double base;
	private double percent;
	private double quota;

	public TaxType getType() {
		return type;
	}

	public InvoiceTax setType(TaxType type) {
		this.type = type;
		return this;
	}

	public double getBase() {
		return base;
	}

	public InvoiceTax setBase(double base) {
		this.base = base;
		return this;
	}

	public double getPercent() {
		return percent;
	}

	public InvoiceTax setPercent(double percent) {
		this.percent = percent;
		return this;
	}

	public double getQuota() {
		return quota;
	}

	public InvoiceTax setQuota(double quota) {
		this.quota = quota;
		return this;
	}
}
