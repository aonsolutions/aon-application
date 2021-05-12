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
	
	
}
