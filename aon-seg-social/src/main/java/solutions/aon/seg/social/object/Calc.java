package solutions.aon.seg.social.object;

public class Calc {

	private Double base;
	private Double total;
	private Double employee;
	private Double enterprise;
	private Double employeePercent;
	private Double enterprisePercent;
	
	public Double getBase() {
		return base;
	}
	public Calc setBase(Double base) {
		this.base = base;
		return this;
	}
	public Double getEnterprise() {
		return enterprise;
	}
	public Calc setEnterprise(Double enterprise) {
		this.enterprise = enterprise;
		return this;
	}
	public Double getEmployee() {
		return employee;
	}
	public Calc setEmployee(Double employee) {
		this.employee = employee;
		return this;
	}
	public Double getTotal() {
		return total;
	}
	public Calc setTotal(Double total) {
		this.total = total;
		return this;
	}
	
	public Double getEnterprisePercent() {
		return enterprisePercent;
	}
	
	public Calc setEnterprisePercent(Double enterprisePercent) {
		this.enterprisePercent = enterprisePercent;
		return this;
	}
	
	public Double getEmployeePercent() {
		return employeePercent;
	}
	
	public Calc setEmployeePercent(Double employeePercent) {
		this.employeePercent = employeePercent;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + ((employee == null) ? 0 : employee.hashCode());
		result = prime * result + ((enterprise == null) ? 0 : enterprise.hashCode());
		result = prime * result + ((total == null) ? 0 : total.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Calc other = (Calc) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		if (enterprise == null) {
			if (other.enterprise != null)
				return false;
		} else if (!enterprise.equals(other.enterprise))
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Calc [base=" + base + ", total=" + total + ", employee=" + employee + ", enterprise=" + enterprise
				+ ", employeePercent=" + employeePercent + ", enterprisePercent=" + enterprisePercent + "]";
	}
	
	
}
