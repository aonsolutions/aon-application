package es.translogia.tedi;

public enum TediPGC {

	ACCOUNT_700("700.0", "Ventas de mercaderías","700. Ventas de mercaderías"),
	ACCOUNT_705("705.0", "Prestación de servicios", "705. Prestación de servicios"),
	ACCOUNT_600("600.0", "Compras de mercaderías", "600. Compras de mercaderías"),	
	ACCOUNT_607("607.0", "Trabajos realizados por otras empresas", "607. Trabajos realizados por otras empresas"),
	ACCOUNT_621("621.0", "Arrendamiento y cánones", "621. Arrendamiento y cánones"),
	ACCOUNT_622("622.0", "Reparaciones y convervación", "622. Reparaciones y conservación"),
	ACCOUNT_623("623.0", "Servicios de profesionales independientes","623. Servicios de profesionales independientes"),
	ACCOUNT_624("624.0", "Transportes", "624. Transportes"),
	ACCOUNT_625("625.0", "Primas de seguros", "625. Primas de seguros"),
	ACCOUNT_626("626.0", "Servicios bancarios y similares", "626. Servicios bancarios y similares"),
	ACCOUNT_627("627.0", "Publicidad, propaganda y relaciones públicas", "627. Publicidad, propaganda y relaciones públicas"),
	ACCOUNT_628("628.0", "Suministros", "628. Suministros"),
	ACCOUNT_629("629.0", "Otros gastos", "629. Otros gastos"),
	ACCOUNT_6291("629.1", "Alojamiento", "629.1 Alojamiento"),
	ACCOUNT_6292("629.2", "Aparcamiento", "629.2. Aparcamiento"),
	ACCOUNT_6293("629.3", "Combustible", "629.3. Combustible"),
	ACCOUNT_6294("629.4", "Desplazamientos", "629.4 Desplazamientos"),
	ACCOUNT_6295("629.5", "Dietas", "629.5 Dietas"),
	ACCOUNT_6296("629.6", "Peaje", "629.6 Peaje"),
	ACCOUNT_6297("629.7", "Kilometraje", "629.7. Kilometraje"),
	ACCOUNT_6298("629.8", "Multas y sanciones", "629.8. Multas y sanciones"),
	ACCOUNT_6299("629.9", "Tasas y tributos", "629.9. Tasas y tributos");
	
	String category;
	String name;
	String description;
	
	private TediPGC(String category, String name, String description) {
		this.category = category;
		this.name = name;
		this.description = description;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static TediPGC getValue(String code){
		if("700.0" == code) return ACCOUNT_700; 
		else if("705.0" == code) return ACCOUNT_705;
		else if("600.0" == code) return ACCOUNT_600;
		else if("607.0" == code) return ACCOUNT_607;
		else if("621.0" == code) return ACCOUNT_621;
		else if("622.0" == code) return ACCOUNT_622;
		else if("623.0" == code) return ACCOUNT_623;
		else if("624.0" == code) return ACCOUNT_624;
		else if("625.0" == code) return ACCOUNT_625;
		else if("626.0" == code) return ACCOUNT_626;
		else if("627.0" == code) return ACCOUNT_627;
		else if("628.0" == code) return ACCOUNT_628;
		else if("629.0" == code) return ACCOUNT_629;
		else if("629.1" == code) return ACCOUNT_6291;
		else if("629.2" == code) return ACCOUNT_6292;
		else if("629.3" == code) return ACCOUNT_6293;
		else if("629.4" == code) return ACCOUNT_6294;
		else if("629.5" == code) return ACCOUNT_6295;
		else if("629.6" == code) return ACCOUNT_6296;
		else if("629.7" == code) return ACCOUNT_6297;
		else if("629.8" == code) return ACCOUNT_6298;
		else return ACCOUNT_6299;
	}
}
