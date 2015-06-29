package dom.sector;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
// @javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name =
// "nombre_Sector_must_be_unique", members = { "nombre_sector" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletePorNombreSector", language = "JDOQL", value = "SELECT "
				+ "FROM dom.sector.Sector "
				+ "WHERE nombre_sector.indexOf(:nombre_sector) >= 0"),
		@javax.jdo.annotations.Query(name = "todosLosSectoresTrue", language = "JDOQL", value = "SELECT "
				+ " FROM dom.sector.Sector " + " WHERE habilitado == true"),
		@javax.jdo.annotations.Query(name = "todosLosSectores", language = "JDOQL", value = "SELECT "
				+ "FROM dom.sector.Sector "),
		@javax.jdo.annotations.Query(name = "sectoresDisposiciones", language = "JDOQL", value = "SELECT "
				+ " FROM dom.sector.Sector " + " WHERE disposicion==true"),
		@javax.jdo.annotations.Query(name = "sectoresResoluciones", language = "JDOQL", value = "SELECT "
				+ " FROM dom.sector.Sector " + " WHERE resolucion==true"),
		@javax.jdo.annotations.Query(name = "sectoresExpediente", language = "JDOQL", value = "SELECT "
				+ " FROM dom.sector.Sector " + " WHERE expediente==true"),
		@javax.jdo.annotations.Query(name = "buscarNombre", language = "JDOQL", value = "SELECT  "
				+ " FROM dom.sector.Sector " + " WHERE nombre_sector==:nombre"),
		@javax.jdo.annotations.Query(name = "buscarPorNombre", language = "JDOQL", value = "SELECT "
				+ "FROM dom.sector.Sector "
				+ "WHERE "
				+ " nombre_sector.indexOf(:nombre_sector) >= 0") })
@DomainObject(autoCompleteRepository = SectorRepositorio.class, 
autoCompleteAction = "autoComplete", objectType = "SECTORES")
public class Sector implements Comparable<Sector>{
	public String title() {
		return this.getNombre_sector();
	}

	public String iconName() {
		return "sector";
	}

	private String nombre_sector;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(name="Nombre",sequence = "10")	
	public String getNombre_sector() {
		return nombre_sector;
	}

	public void setNombre_sector(String nombre_sector) {
		this.nombre_sector = nombre_sector;
	}

	private String responsable;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(name="Responsable",sequence = "11")
	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	private Boolean resolucion;

	// @Hidden(where = Where.ALL_TABLES)
	@javax.jdo.annotations.Column(allowsNull = "True")
	@MemberOrder(name="resolucion",sequence = "20")
	public Boolean getResolucion() {
		return resolucion;
	}

	public void setResolucion(Boolean resolucion) {
		this.resolucion = resolucion;
	}

	private Boolean disposicion;

	// @Hidden(where = Where.ALL_TABLES)
	@javax.jdo.annotations.Column(allowsNull = "True")
	@MemberOrder(sequence = "40")
	public Boolean getDisposicion() {
		return disposicion;
	}

	public void setDisposicion(Boolean disposicion) {
		this.disposicion = disposicion;
	}


	private Boolean expediente;

	@javax.jdo.annotations.Column(allowsNull = "True")
	@MemberOrder(sequence = "50")
	public Boolean getExpediente() {
		return expediente;
	}

	public void setExpediente(Boolean expediente) {
		this.expediente = expediente;
	}

	
	private String creadoPor;

	@Property(hidden=Where.EVERYWHERE)
	@javax.jdo.annotations.Column(allowsNull = "false")
	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	// //////////////////////////////////////
	// Habilitado (propiedad)
	// //////////////////////////////////////

	public boolean habilitado;

	@Property(hidden=Where.EVERYWHERE)
	@MemberOrder(sequence = "40")
	public boolean getEstaHabilitado() {
		return habilitado;
	}

	public void setHabilitado(final boolean habilitado) {
		this.habilitado = habilitado;
	}

	@Override
	public int compareTo(final Sector sector) {

		return ObjectContracts.compare(this, sector, "nombre_sector");
		// return 1;
	}
}
