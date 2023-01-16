package solutions.aon.seg.social.object;

import java.util.Date;
import java.util.Objects;

public class Idc {
	private String descripcion;
	private Date fecha;
	public Idc(String descripcion, Date fecha) {
		super();
		this.descripcion = descripcion;
		this.fecha = fecha;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	@Override
	public String toString() {
		return "Idc [descripcion=" + descripcion + ", fecha=" + fecha + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(descripcion, fecha);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Idc ) )
			return false;
		
		Idc idc = (Idc) obj;
		
		return Objects.equals(descripcion, idc.descripcion) && Objects.equals(fecha, idc.fecha);
	}
}