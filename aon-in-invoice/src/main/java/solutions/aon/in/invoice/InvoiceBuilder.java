package solutions.aon.in.invoice;

import java.util.Date;
import java.util.Locale;

public interface InvoiceBuilder<T extends Object>  {
	
	void setDate(Date date);
	
	void setSenderName(String name);
	void setSenderCity(String city);
	void setSenderAddress(String address);
	void setSenderProvince(String province);
	void setSenderPostalCode(String postalCode);
	void setSenderCountry(String isoCountryCode);

	void setSenderDocument(String document);
	void setSenderDocumentCountry(String isoCountryCode);
	
	

}
