package dom.expediente;

import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.validation.constraints.Size;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import dom.documento.Documento;
import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id_documento")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Uniques({ @javax.jdo.annotations.Unique(name = "nroExpediente_must_be_unique", members = { "id_documento" }) })
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(name = "autoCompletarDestino", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE destinoSector.indexOf(:destinoSector) >= 0"),
		@javax.jdo.annotations.Query(name = "buscarUltimoExpedienteTrue", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE habilitado == true"),
		@javax.jdo.annotations.Query(name = "buscarUltimoExpedienteFalse", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE habilitado == false"),
		@javax.jdo.annotations.Query(name = "buscarPorNroExpediente", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE  "
				+ "nro_expediente.indexOf(:nro_expediente) >= 0 ORDER BY fecha DESC, nro_expediente DESC"),
		@javax.jdo.annotations.Query(name = "listarHabilitados", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE  habilitado == true ORDER BY fecha DESC, nro_expediente DESC"),
		@javax.jdo.annotations.Query(name = "listar", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente ORDER BY fecha DESC, nro_expediente DESC"),
		@javax.jdo.annotations.Query(name = "recuperarUltimo", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente " + "WHERE  (ultimo == true)"),
		@javax.jdo.annotations.Query(name = "filtrarCompleto", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE  :desde <= fecha && fecha<=:hasta && sector==:sector ORDER BY fecha DESC, nro_expediente DESC "),
		@javax.jdo.annotations.Query(name = "filtrarPorFechas", language = "JDOQL", value = "SELECT "
				+ "FROM dom.expediente.Expediente "
				+ "WHERE  :desde <= fecha && fecha<=:hasta ORDER BY fecha DESC, nro_expediente DESC ") })
@DomainObject(objectType = "EXPEDIENTE")
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Expediente extends Documento {

	public String title() {
		return "Expediente Nº "
				+ String.format("%03d", this.getNro_expediente());
	}

	public String iconName() {
		if (this.getHabilitado())
			return "expediente";
		else
			return "delete";
	}

	private int nro_expediente;

	@MemberOrder(sequence = "10")
	@javax.jdo.annotations.Column(allowsNull = "false", name = "Nro")
	@Property(hidden = Where.EVERYWHERE, editing = Editing.DISABLED)
	public int getNro_expediente() {
		return nro_expediente;
	}

	public void setNro_expediente(int nro_expediente) {
		this.nro_expediente = nro_expediente;
	}

	/**
	 * Metodo de solo lectura, no se persiste. Su funcion es la de mostrar
	 * nro_nota con tres digitos.
	 */
	@SuppressWarnings("unused")
	private String nro;

	@MemberOrder(sequence = "0")
	@Property(editing = Editing.DISABLED)
	public String getNro() {
		return String.format("%03d", this.getNro_expediente());
	}

	private String expte_cod_empresa;

	@Property(hidden = Where.EVERYWHERE, editing = Editing.DISABLED)
	@MemberOrder(sequence = "20")
	@javax.jdo.annotations.Column(allowsNull = "false", name = "Empresa")
	public String getExpte_cod_empresa() {
		return expte_cod_empresa;
	}

	public void setExpte_cod_empresa(String expte_cod_empresa) {
		this.expte_cod_empresa = expte_cod_empresa;
	}

	private int expte_cod_numero;

	@MemberOrder(sequence = "30")
	@javax.jdo.annotations.Column(allowsNull = "false",name="Numero")
	@Property(hidden=Where.EVERYWHERE, editing=Editing.DISABLED)
	public int getExpte_cod_numero() {
		return expte_cod_numero;
	}

	public void setExpte_cod_numero(int expte_cod_numero) {
		this.expte_cod_numero = expte_cod_numero;
	}

	public enum Letras {
		A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
	}

	private String expte_cod_letra;

	@MemberOrder(sequence = "40")
	@javax.jdo.annotations.Column(allowsNull = "false",name="Inicial")
	@Property(hidden=Where.EVERYWHERE, editing=Editing.DISABLED,maxLength=100)
	@Size(max = 100)
	public String getExpte_cod_letra() {
		return expte_cod_letra;
	}

	public void setExpte_cod_letra(String expte_cod_letra) {
		this.expte_cod_letra = expte_cod_letra;
	}

	private int expte_cod_anio;
	@Property(hidden=Where.EVERYWHERE, editing=Editing.DISABLED)
	@MemberOrder(sequence = "50")
	@javax.jdo.annotations.Column(allowsNull = "false",name="Año")
	public int getExpte_cod_anio() {
		return expte_cod_anio;
	}

	public void setExpte_cod_anio(int expte_cod_anio) {
		this.expte_cod_anio = expte_cod_anio;
	}

	@Override
	public List<Sector> choicesSector() {
		return this.sectorRepositorio.listarExpediente();
	}

	public List<Expediente> eliminar() {
		this.setHabilitado(false);
		return expedienteRepositorio.listar();
	}

	public boolean hideEliminar() {
		if (this.container.getUser().isCurrentUser("root"))
			return false;
		else
			return true;
	}

	@javax.inject.Inject
	private SectorRepositorio sectorRepositorio;
	@javax.inject.Inject
	private DomainObjectContainer container;
	@javax.inject.Inject
	private ExpedienteRepositorio expedienteRepositorio;
}
