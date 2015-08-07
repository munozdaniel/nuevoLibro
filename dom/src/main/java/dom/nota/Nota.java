package dom.nota;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import dom.documento.Documento;
import dom.sector.SectorRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Uniques({ @javax.jdo.annotations.Unique(name = "nro_nota_must_be_unique", members = { "id_documento" }) })
@Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE destino.indexOf(:destino) >= 0 && (habilitado==true)"),
		@javax.jdo.annotations.Query(name = "autoComplete", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE destino.indexOf(:destino) >= 0"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  habilitado == true ORDER BY fecha DESC, nro_nota DESC "),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota ORDER BY fecha DESC, nro_nota DESC  "),
		@javax.jdo.annotations.Query(name = "recuperarUltimo", language = "JDOQL", value = " SELECT  "
				+ "FROM dom.nota.Nota " + "WHERE  (ultimo == true)  "),
		@javax.jdo.annotations.Query(name = "esNuevoAnio", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE fecha == :fecha ORDER BY nro_nota DESC, fecha DESC "),

		@javax.jdo.annotations.Query(name = "filtrarPorDescripcion", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE descripcion.toUpperCase().indexOf(:descripcion)>=0 ORDER BY nro_nota DESC, fecha DESC "),

		@javax.jdo.annotations.Query(name = "filtrarCompleto", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota  "
				+ "WHERE sector==:origen && destino.toUpperCase().indexOf(:destino) >= 0 && :desde <= fecha && fecha<=:hasta"
				+ " ORDER BY fecha DESC, nro_nota DESC  "),

		@javax.jdo.annotations.Query(name = "filtrarOrigen", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota  "
				+ "WHERE sector==:origen"
				+ " ORDER BY fecha DESC, nro_nota DESC  "),

		@javax.jdo.annotations.Query(name = "filtrarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota  "
				+ "WHERE  destino.toUpperCase().indexOf(:destino) >= 0"
				+ " ORDER BY fecha DESC, nro_nota DESC  "),

		@javax.jdo.annotations.Query(name = "filtrarFechaYOrigen", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota  "
				+ "WHERE sector==:origen && :desde <= fecha && fecha<=:hasta"
				+ " ORDER BY fecha DESC, nro_nota DESC  "),

		@javax.jdo.annotations.Query(name = "filtrarFechaYDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota  "
				+ "WHERE destino.toUpperCase().indexOf(:destino) >= 0 && :desde <= fecha && fecha<=:hasta"
				+ " ORDER BY fecha DESC, nro_nota DESC  "),

		@javax.jdo.annotations.Query(name = "filtrarOrigenYDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota  "
				+ "WHERE sector==:origen && destino.toUpperCase().indexOf(:destino) >= 0 "
				+ " ORDER BY fecha DESC, nro_nota DESC  "),

		@javax.jdo.annotations.Query(name = "filtrarPorFechas", language = "JDOQL", value = "SELECT "
				+ "FROM dom.nota.Nota "
				+ "WHERE  :desde <= fecha && fecha<=:hasta ORDER BY fecha DESC, nro_nota DESC ") })
@DomainObject(autoCompleteRepository = NotaRepositorio.class, autoCompleteAction = "autoComplete", objectType = "NOTAS")
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Nota extends Documento {
	public Nota()
	{
		super();
	}
	public String title() {
		return "Nota Nº " + String.format("%04d", this.getNro_nota());
	}

	public String iconName() {
		if (this.getHabilitado())
			return "nota";
		else
			return "delete";
	}

	private int nro_nota;

	//@Disabled
	// @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
	//@Named("Nro")
	//@Hidden
	@Persistent
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Property(hidden=Where.EVERYWHERE, editing=Editing.DISABLED)
	@MemberOrder(sequence = "0")
	public int getNro_nota() {
		return nro_nota;
	}

	public void setNro_nota(final int nro_nota) {
		this.nro_nota = nro_nota;
	}

	/**
	 * Metodo de solo lectura, no se persiste. Su funcion es la de mostrar
	 * nro_nota con tres digitos.
	 */
	@SuppressWarnings("unused")
	private String nro;

	//@Named("Nro")
	//@NotPersisted
	@javax.jdo.annotations.Column(allowsNull = "false")
	@Property(editing=Editing.DISABLED)
	@MemberOrder(sequence = "0")
	public String getNro() {
		return String.format("%04d", this.getNro_nota());
	}

	public void setNro(String nro) {
		this.nro = nro;
	}

	private String destino;

	//@Named("Destino")
	@Persistent
	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "3")
	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	//@Named("Eliminar")
	//@DescribedAs("Necesario privilegios de Administrador.")
	public List<Nota> eliminar() {
		this.setHabilitado(false);
		return notaRepositorio.listar();
	}

	public boolean hideEliminar() {
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	@javax.inject.Inject
	private NotaRepositorio notaRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;
}
