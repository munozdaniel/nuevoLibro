package dom.expediente;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;

@DomainService(repositoryFor = Expediente.class)
@DomainServiceLayout(menuOrder = "60", named = "Expediente")
public class ExpedienteRepositorio {
	// public final Lock monitor = new ReentrantLock();
		public boolean ocupado = false;

		public ExpedienteRepositorio() {

		}

		// //////////////////////////////////////
		// Identification in the UI
		// //////////////////////////////////////

		public String getId() {
			return "expediente";
		}

		public String iconName() {
			return "expediente";
		}

		@MemberOrder(sequence = "10")
		public Expediente addExpediente(
				final @ParameterLayout(named = "Inicia") Sector sector,
				final @ParameterLayout(named = "Letra Inicial") @Parameter(maxLength=1,regexPattern="^[a-zA-Z]") String expte_cod_letra,
				final @ParameterLayout(named = "Motivo", multiLine=2) @Parameter(maxLength=255)  String descripcion,
				final @ParameterLayout(named = "Adjunto") @Parameter(optionality=Optionality.OPTIONAL)  Blob adjunto) {
			Expediente expediente = this.nuevoExpediente(expte_cod_letra, sector,
					descripcion, this.currentUserName(), adjunto);
			if (expediente != null) {
				this.container
						.informUser("El Expediente ha sido guardado correctamente.");
				return expediente;
			}
			this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
			return null;
		}

		private Expediente nuevoExpediente(final String expte_cod_letra,
				final Sector sector, final String descripcion,
				final String creadoPor, final Blob adjunto) {
			try {
				final Expediente unExpediente = this.container
						.newTransientInstance(Expediente.class);
				Expediente anterior = recuperarUltimo();
				int nro = 1;
				if (anterior != null) {
					if (!anterior.getUltimoDelAnio()) {
						if (!anterior.getHabilitado())
							nro = anterior.getNro_expediente();
						else
							nro = anterior.getNro_expediente() + 1;
					} else
						anterior.setUltimoDelAnio(false);

					anterior.setUltimo(false);
				}
				unExpediente.setNro_expediente(nro);
				unExpediente.setUltimo(true);

				unExpediente.setExpte_cod_letra(expte_cod_letra.toUpperCase());
				unExpediente.setFecha(LocalDate.now());
				unExpediente.setTipo(5);
				unExpediente.setDescripcion(descripcion.toUpperCase().trim());
				unExpediente.setHabilitado(true);
				unExpediente.setCreadoPor(creadoPor);
				unExpediente.setExpte_cod_anio(LocalDate.now().getYear());
				unExpediente.setExpte_cod_empresa("IMPS");
				int anio = LocalDate.now().getYear();
				unExpediente.setExpte_cod_numero((anio - 2010));

				unExpediente.setTime(LocalDateTime.now().withMillisOfSecond(3));
				unExpediente.setAdjuntar(adjunto);
				unExpediente.setSector(sector);

				container.persistIfNotAlready(unExpediente);
				container.flush();
				return unExpediente;
			} catch (Exception e) {
				container
						.warnUser("Por favor, verifique que la informacion se ha guardado correctamente. En caso contrario informar a Sistemas.");
			} finally {
				this.ocupado = false;
			}
			return null;
		}

		public String validateAddExpediente(final Sector sector,
				final String expte_cod_letra, final String descripcion,
				final Blob adjunto) {
			if (!this.ocupado) {
				this.ocupado = true;
				return null;
			} else
				return "Sistema ocupado, intente nuevamente.";
		}

		@Programmatic
		private Expediente recuperarUltimo() {
			final Expediente doc = this.container
					.firstMatch(new QueryDefault<Expediente>(Expediente.class,
							"recuperarUltimo"));
			if (doc == null)
				return null;
			return doc;
		}

		@Programmatic
		private int recuperarNroResolucion() {
			final List<Expediente> expedientes = this.container
					.allMatches(new QueryDefault<Expediente>(Expediente.class,
							"listarHabilitados"));

			if (expedientes.isEmpty())
				return 0;
			else
				return expedientes.get(expedientes.size() - 1).getNro_expediente();
		}

		public List<Sector> choices0AddExpediente() {
			return sectorRepositorio.listarExpediente();
		}

		@MemberOrder(sequence = "20")
		public List<Expediente> listar() {
			String criterio = "listarHabilitados";
			if (this.container.getUser().isCurrentUser("root"))
				criterio = "listar";
			final List<Expediente> listaExpedientes = this.container
					.allMatches(new QueryDefault<Expediente>(Expediente.class,
							criterio));
			if (listaExpedientes.isEmpty()) {
				this.container
						.warnUser("No hay Expedientes cargados en el sistema");
			}
			return listaExpedientes;

		}

		@Programmatic
		public List<Expediente> autoComplete(final String destino) {
			return container.allMatches(new QueryDefault<Expediente>(
					Expediente.class, "autoCompletarDestino", "destinoSector",
					destino));
		}

		public List<Expediente> filtrarPorDescripcion(
				final @ParameterLayout(named = "Descripcion",multiLine=2) @Parameter(maxLength=255) String descripcion) {

			List<Expediente> lista = this.listar();
			Expediente expediente = new Expediente();
			List<Expediente> listaRetorno = new ArrayList<Expediente>();
			for (int i = 0; i < lista.size(); i++) {
				expediente = new Expediente();
				expediente = lista.get(i);
				if (expediente.getDescripcion().contains(descripcion.toUpperCase()))
					listaRetorno.add(expediente);
			}
			if (listaRetorno.isEmpty())
				this.container.warnUser("No se encotraron Registros.");
			return listaRetorno;
		}

		/**
		 * Filtrar por fecha
		 * 
		 * @param sector
		 * @param fecha
		 * @return
		 */
		@MemberOrder(sequence = "30")
		private List<Expediente> filtrarPorFecha(
				final @ParameterLayout(named = "Desde",multiLine=2) LocalDate desde,
				final @ParameterLayout(named = "Hasta",multiLine=2) LocalDate hasta) {

			final List<Expediente> lista = this.container
					.allMatches(new QueryDefault<Expediente>(Expediente.class,
							"filtrarPorFechas", "desde", desde, "hasta", hasta));
			if (lista.isEmpty()) {
				this.container.warnUser("No se encontraron Registros.");
			}
			return lista;
		}

		// //////////////////////////////////////
		// CurrentUserName
		// //////////////////////////////////////

		private String currentUserName() {
			return container.getUser().getName();
		}

		// //////////////////////////////////////
		// Injected Services
		// //////////////////////////////////////

		@javax.inject.Inject
		private DomainObjectContainer container;
		@javax.inject.Inject
		private SectorRepositorio sectorRepositorio;
}
