package aon.sepe.objects;

import java.util.Optional;

/**
 * QUOTE_DATA CERTIFIC@
 */
public class QuoteData {
	private Integer anio;
	private Integer month;
	private Integer days; //DAYS CTZ
	private Double bccc;
	private Double bcd;
	
	public Integer getAnio() {
		return anio;
	}
	
	public QuoteData setAnio(Integer anio) {
		this.anio = anio;
		return this;
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public QuoteData setMonth(Integer month) {
		this.month = month;
		return this;
	}
	
	public Integer getDays() {
		return days;
	}
	
	public QuoteData setDays(Integer days) {
		this.days = days;
		return this;
	}
	
	public Optional<String> getBccc() {
		return Optional.ofNullable(bccc.toString().replace(".", ","));
	}
	
	public QuoteData setBccc(Double bccc) {
		this.bccc = bccc;
		return this;
	}
	
	public Optional<String> getBcd() {
		return Optional.ofNullable(bcd.toString().replace(".", ","));
	}
	
	public QuoteData setBcd(Double bcd) {
		this.bcd = bcd;
		return this;
	}
	
	@Override
	public String toString() {
		return "QuoteData [anio=" + anio + ", month=" + month + ", days=" + days + ", bccc="
				+ getBccc().get() + ", bcd=" + getBcd().get() + "]";
	}
	
}
