package solutions.aon.seg.social.object;

import java.util.Date;

public class PaternityDetail {
	public PaternityDetail() {}

	private Date date;
	private Float baseCC;
	private Float baseCP;
	private Integer days;
	
	public Date getDate() {
		return date;
	}
	public PaternityDetail setDate(Date date) {
		this.date = date;
		return this;
	}
	public Float getBaseCC() {
		return baseCC;
	}
	public PaternityDetail setBaseCC(Float baseCC) {
		this.baseCC = baseCC;
		return this;
	}
	public Float getBaseCP() {
		return baseCP;
	}
	public PaternityDetail setBaseCP(Float baseCP) {
		this.baseCP = baseCP;
		return this;
	}
	public Integer getDays() {
		return days;
	}
	public PaternityDetail setDays(Integer days) {
		this.days = days;
		return this;
	}
	
	
	@Override
    public String toString() {
        return "PaternityDetail{"
        		+ "date=" + date +","
        		+ "baseCC=" + baseCC +","
        		+ "baseCP=" + baseCP +","
        		+ "days=" + days +","
        		+  "}";
    }

}
