package solutions.aon.seg.social;

import java.util.Date;

public class ItPartId{
	Date startDate;
	String naf;
	
	public ItPartId(Date startDate,String naf) {
		this.startDate = startDate;
		this.naf = naf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((naf == null) ? 0 : naf.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
		ItPartId other = (ItPartId) obj;
		if (naf == null) {
			if (other.naf != null)
				return false;
		} else if (!naf.equals(other.naf))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}