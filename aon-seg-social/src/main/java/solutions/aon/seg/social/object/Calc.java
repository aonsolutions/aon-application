package solutions.aon.seg.social.object;

public class Calc {
	private Double base;
	private Double enterprise;
	private Double employee;
	private Double total;
	
	
	
	public Calc(Double base, Double enterprise, Double employee, Double total) {
		super();
		this.base = base;
		this.enterprise = enterprise;
		this.employee = employee;
		this.total = total;
	}
	public Double getBase() {
		return base;
	}
	public void setBase(Double base) {
		this.base = base;
	}
	public Double getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Double enterprise) {
		this.enterprise = enterprise;
	}
	public Double getEmployee() {
		return employee;
	}
	public void setEmployee(Double employee) {
		this.employee = employee;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	
	
}
