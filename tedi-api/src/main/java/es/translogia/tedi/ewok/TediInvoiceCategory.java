package es.translogia.tedi.ewok;

import java.io.Serializable;

public enum TediInvoiceCategory implements Serializable {

	C7000("700.0","Ventas de mercaderías"), 
	C7050("705.0","Prestaciones de servicios"), 
	C6000("600.0","Compras de mercaderías"),
	C6070("607.0","Trabajos realizados por otras empresas"),
	C6210("621.0","Arrendamientos y cánones"),
	C6220("622.0","Reparaciones y conservación"),
	C6230("623.0","Servicios de profesionales independientes"),
	C6240("624.0","Transportes"),
	C6250("625.0","Primas de seguros"),
	C6260("626.0","Servicios bancarios y similares"),
	C6270("627.0","Publicidad, propaganda y relaciones públicas"),
	C6280("628.0","Suministros"),
	C6290("629.0","Otros gastos"),
	C6291("629.1","Alojamiento"),
	C6292("629.2","Aparcamiento"),
	C6293("629.3","Combustible"),
	C6294("629.4","Desplazamientos"),
	C6295("629.5","Dietas"),
	C6296("629.6","Peaje"),
	C6297("629.7","Kilometraje"),
	C6298("629.8","Multas y sanciones"),
	C6299("629.9","Tasas y tributos");

	private String category;
	private String description;

	private TediInvoiceCategory(String category, String description) {
		this.category = category;
		this.description = description;
	}

	public String getType() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public static TediInvoiceCategory safeValueOf(String category) {
		if (category != null && category.trim() != "") {
			try {
				return TediInvoiceCategory.valueOf(category.trim());
			} catch (IllegalArgumentException e) {
			}
		}
		return null;
	}

}
