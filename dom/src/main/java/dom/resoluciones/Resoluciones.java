package dom.resoluciones;

import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.joda.time.LocalDate;

import dom.documento.Documento;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

public class Resoluciones extends Documento {

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String title() {
		return "Resolución Nº " + String.format("%03d",this.getNro_resolucion());
	}

	public String iconName() {
		if (this.getHabilitado())
			return "resolucion";
		else
			return "delete";
	}

	private int nro_resolucion;

	@MemberOrder(sequence = "0")
	@javax.jdo.annotations.Column(allowsNull = "false",name="Nro")
	@Property(editing=Editing.DISABLED,hidden=Where.ALL_TABLES)
	public int getNro_resolucion() {
		return nro_resolucion;
	}

	public void setNro_resolucion(int nro_resolucion) {
		this.nro_resolucion = nro_resolucion;
	}
	
	/**
	 * Metodo de solo lectura, no se persiste. Su funcion es la de mostrar
	 * nro_nota con tres digitos.
	 */
	@SuppressWarnings("unused")
	private String nro;
	@MemberOrder(sequence = "0")
	@Property(editing=Editing.DISABLED,notPersisted=true)
	public String getNro() {
		return 	String.format("%03d",this.getNro_resolucion());
	}

	public void setNro(String nro) {
		this.nro = nro;
	}
	private LocalDate fechaCreacion;

	@javax.jdo.annotations.Column(allowsNull = "true", name="Fecha de Creacion")
	@Property(editing=Editing.DISABLED,hidden=Where.EVERYWHERE)
	@MemberOrder(sequence = "1")
	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	
	public List<Resoluciones> eliminar() {
		this.setHabilitado(false);
		return resolucionRepositorio.listar();
	}

	public boolean hideEliminar() {
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	@Override
	public List<Sector> choicesSector() {
		return this.sectorRepositorio.listarResoluciones();
	}

	@javax.inject.Inject
	private ResolucionesRepositorio resolucionRepositorio;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;
}
