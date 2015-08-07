package dom.disposiciones;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;

import dom.documento.Documento;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;


@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nroDisposicion_must_be_unique", members = { "id_documento" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE destinoSector.indexOf(:destinoSector) >= 0"),
		@javax.jdo.annotations.Query(name = "buscarUltimaDisposicionTrue", language = "JDOQL", value = "SELECT MAX(nro_Disposicion) "
				+ "FROM dom.disposiciones.Disposicion "),
		@javax.jdo.annotations.Query(name = "buscarUltimaDisposicionFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE habilitado == false"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE  habilitado == true  ORDER BY fecha DESC, nro_Disposicion DESC"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion ORDER BY fecha DESC, nro_Disposicion DESC"),
		@javax.jdo.annotations.Query(name = "recuperarUltimo", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion " + "WHERE  (ultimo == true)"),
		@javax.jdo.annotations.Query(name = "filtrarPorFechas", language = "JDOQL", value = "SELECT "
				+ "FROM dom.disposiciones.Disposicion "
				+ "WHERE  :desde <= fecha && fecha<=:hasta ORDER BY fecha DESC, nro_Disposicion DESC ") })

@DomainObject(objectType = "DISPOSICION")
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public class Disposicion extends Documento{
	public String title() {
		return "Disposicion Nº " + String.format("%03d",this.getNro_Disposicion());
	}

	public String iconName() {
		if (this.getHabilitado())
			return "disposicion";
		else
			return "delete";
	}
	private int nro_Disposicion;

	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Property(editing=Editing.DISABLED)
	public int getNro_Disposicion() {
		return nro_Disposicion;
	}

	public void setNro_Disposicion(int nro_Disposicion) {
		this.nro_Disposicion = nro_Disposicion;
	}
	
	/**
	 * Metodo de solo lectura, no se persiste. Su funcion es la de mostrar
	 * nro_nota con tres digitos.
	 */
	@SuppressWarnings("unused")
	private String nro;
	@javax.jdo.annotations.Column(allowsNull = "false",name="Nro")
	@Property(editing=Editing.DISABLED,notPersisted=true)
	@MemberOrder(sequence = "0")
	public String getNro() {
		return 	String.format("%03d",this.getNro_Disposicion());
	}

	@Override
	public List<Sector> choicesSector() {
		return this.sectorRepositorio.listarDisposiciones();
	}

	public List<Disposicion> eliminar() {
		this.setHabilitado(false);
		return disposicionRepositorio.listar();
	}

	public boolean hideEliminar() {
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	// @Named("Restaurar")
	// @DescribedAs("Necesario privilegios de Administrador.")
	// public Disposicion restaurar() {
	// this.setHabilitado(true);
	// return this;
	// }
	//
	// public boolean hideRestaurar() {
	// // TODO: return true if action is hidden, false if
	// // visible
	// if (this.container.getUser().isCurrentUser("root"))
	// return false;
	// else
	// return true;
	// }

	@javax.inject.Inject
	private DisposicionRepositorio disposicionRepositorio;
	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;
}
