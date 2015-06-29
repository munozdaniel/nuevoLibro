package dom.documento;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class Documento implements Comparable<Documento>{
	
	private LocalDateTime time;

	@Property(hidden=Where.EVERYWHERE)
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(name="system_time",sequence = "100")
	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	private LocalDate fecha;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(name="Fecha",sequence = "1")
	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	/*
	 * tipo - 1:Nota - 2:Memo - 3:Resoluciones - 4:Disposiciones - 5:Expedientes
	 */
	private int tipo;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@Property(hidden=Where.EVERYWHERE, editing=Editing.DISABLED)
	//@DescribedAs("Tipo de Documento")
	@MemberOrder(sequence = "30")
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	private String descripcion;

	@MemberOrder(name = "Descripcion", sequence = "1")
	@javax.jdo.annotations.Column(allowsNull = "false", length=250)
	//@MultiLine
	//@MaxLength(255)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	private Boolean habilitado;

	@Property(hidden=Where.EVERYWHERE, editing=Editing.DISABLED)
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "60")
	public Boolean getHabilitado() {
		return habilitado;
	}

	public void setHabilitado(Boolean habilitado) {
		this.habilitado = habilitado;
	}

	private Blob adjuntar;

	@MemberOrder(name = "Adjuntar", sequence = "5")
	@javax.jdo.annotations.Persistent(defaultFetchGroup = "false")
	@javax.jdo.annotations.Column(allowsNull = "true", name = "adjunto")
	@Property(hidden=Where.EVERYWHERE, editing=Editing.DISABLED)
	public Blob getAdjuntar() {
		return adjuntar;
	}

	public void setAdjuntar(final Blob adjunto) {
		this.adjuntar = adjunto;
	}

	// //////////////////////////////////////
	// creadoPor
	// //////////////////////////////////////

	private String creadoPor;

	@Property(hidden=Where.EVERYWHERE, editing=Editing.DISABLED)
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}
	
	private Sector sector;

	@MemberOrder(name="Origen",sequence = "2")
	@Column(allowsNull = "True")
	//@Mandatory
	public Sector getSector() {
		return sector;
	}

	public void setSector(final Sector sector) {
		this.sector = sector;
	}

	public void clearSector() {
		if (this.getSector() != null)
			this.setSector(null);
	}

	public List<Sector> choicesSector() {
		return this.sectorRepositorio.listar();
	}
	
	private Boolean ultimo;

	@Property(hidden = Where.EVERYWHERE)
	@MemberOrder(sequence = "100")
	@javax.jdo.annotations.Column(allowsNull = "true")
	public boolean getUltimo() {
		return ultimo;
	}

	public void setUltimo(final boolean ultimo) {
		this.ultimo = ultimo;
	}

	
	private boolean ultimoDelAnio;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "1")
	@Property(hidden = Where.EVERYWHERE)
	public boolean getUltimoDelAnio() {
		return ultimoDelAnio;
	}

	public void setUltimoDelAnio(final boolean ultimoDelAnio) {
		this.ultimoDelAnio = ultimoDelAnio;
	}
	
	@Override
	public int compareTo(Documento documento) {
		return ObjectContracts.compare(this, documento, "time,descripcion");
	}

	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
}
