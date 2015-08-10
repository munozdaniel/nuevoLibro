package dom.resoluciones;

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

@DomainService(repositoryFor = Resoluciones.class)
@DomainServiceLayout(menuOrder = "30", named = "Resolución")
public class ResolucionesRepositorio {
	public boolean ocupado = false;

	public ResolucionesRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "resoluciones";
	}

	public String iconName() {
		return "resolucion";
	}

	@MemberOrder(sequence = "10")
	public Resoluciones addResoluciones(
			final @ParameterLayout(named = "Fecha:")  LocalDate fecha,
			final @ParameterLayout(named = "De:")  Sector sector,
			final @ParameterLayout(named = "Descripción:",multiLine=2)  @Parameter(maxLength=255) String descripcion,
		    final @ParameterLayout(named = "Adjuntar:") @Parameter(optionality=Optionality.OPTIONAL) Blob adjunto) {
		return this.nuevaResolucion(fecha, sector, descripcion,
				this.currentUserName(), adjunto);

	}

	public String validateAddResoluciones(final LocalDate fecha,
			final Sector sector, final String descripcion, final Blob adjunto) {
		if (!this.ocupado) {
			this.ocupado = true;
			return null;
		} else
			return "Sistema ocupado, intente nuevamente.";
	}

	@Programmatic
	private Resoluciones nuevaResolucion(final LocalDate fecha,
			final Sector sector, final String descripcion,
			final String creadoPor, final Blob adjunto) {

		try {
			final Resoluciones unaResolucion = this.container
					.newTransientInstance(Resoluciones.class);
			Integer nro = Integer.valueOf(1);

			Resoluciones resolucionAnterior = recuperarElUltimo();

			if (resolucionAnterior != null) {
				if (!resolucionAnterior.getUltimoDelAnio()) {
					if (!resolucionAnterior.getHabilitado())
						nro = resolucionAnterior.getNro_resolucion();
					else
						nro = resolucionAnterior.getNro_resolucion() + 1;
				} else
					resolucionAnterior.setUltimoDelAnio(false);
				resolucionAnterior.setUltimo(false);
			}
			unaResolucion.setDescripcion(descripcion.toUpperCase().trim());
			// if (unaResolucion.getDescripcion().equalsIgnoreCase("ALGO")) {
			// try {
			// Thread.sleep(11000);
			// } catch (InterruptedException e) {
			//
			// }
			//
			// }
			// Si no habian nota, o si es el ultimo del año, el proximo
			// nro
			// comienza en 1.
			unaResolucion.setNro_resolucion(nro);
			unaResolucion.setUltimo(true);
			unaResolucion.setUltimoDelAnio(false);
			unaResolucion.setFecha(fecha);
			unaResolucion.setTipo(3);
			unaResolucion.setHabilitado(true);
			unaResolucion.setCreadoPor(creadoPor);
			unaResolucion.setAdjuntar(adjunto);
			unaResolucion.setTime(LocalDateTime.now().withMillisOfSecond(3));
			unaResolucion.setSector(sector);
			unaResolucion.setFechaCreacion(LocalDate.now());
			container.persistIfNotAlready(unaResolucion);
			container.flush();
			return unaResolucion;
		} catch (Exception e) {
			container
					.warnUser("Por favor, verifique que la informacion se ha guardado correctamente. En caso contrario informar a Sistemas.");
		} finally {
			// monitor.unlock();
			this.ocupado = false;
		}
		return null;

	}

	private Resoluciones recuperarElUltimo() {
		final Resoluciones resoluciones = this.container
				.firstMatch(new QueryDefault<Resoluciones>(Resoluciones.class,
						"recuperarUltimo"));
		if (resoluciones == null)
			return null;
		return resoluciones;
	}

	@Programmatic
	public List<Resoluciones> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Resoluciones>(
				Resoluciones.class, "autoCompletarDestino", "nombreSector",
				destino));
	}

	public List<Sector> choices1AddResoluciones() {
		return sectorRepositorio.listarResoluciones();
	}

	// //////////////////////////////////////
	// Listar Memos
	// //////////////////////////////////////

	@MemberOrder(sequence = "20")
	public List<Resoluciones> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Resoluciones> listaMemo = this.container
				.allMatches(new QueryDefault<Resoluciones>(Resoluciones.class,
						criterio));
		if (listaMemo.isEmpty()) {
			this.container
					.warnUser("No hay Resoluciones cargados en el sistema");
		}
		return listaMemo;

	}

	public List<Resoluciones> filtrarPorDescripcion(
			final @ParameterLayout(named = "Descripción:",multiLine=2)  @Parameter(maxLength=255) String descripcion) {

		List<Resoluciones> lista = this.listar();
		Resoluciones unaResolucion = new Resoluciones();
		List<Resoluciones> listaRetorno = new ArrayList<Resoluciones>();
		for (int i = 0; i < lista.size(); i++) {
			unaResolucion = new Resoluciones();
			unaResolucion = lista.get(i);
			if (unaResolucion.getDescripcion().contains(
					descripcion.toUpperCase()))
				listaRetorno.add(unaResolucion);
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
	@SuppressWarnings("unused")
	private List<Resoluciones> filtrarPorFecha(
			final @ParameterLayout(named = "Desde") LocalDate desde,
			final @ParameterLayout(named = "Hasta")LocalDate hasta) {

		final List<Resoluciones> lista = this.container
				.allMatches(new QueryDefault<Resoluciones>(Resoluciones.class,
						"filtrarPorFechas", "desde", desde, "hasta", hasta));
		if (lista.isEmpty()) {
			this.container.warnUser("No se encontraron Registros.");
		}
		return lista;
	}

	@MemberOrder(sequence = "30")
	public List<Resoluciones> filtrarCompleto(
			final @ParameterLayout(named = "Sector:") @Parameter(optionality=Optionality.OPTIONAL) Sector sector,
			final @ParameterLayout(named = "Desde:")  LocalDate desde,
			final @ParameterLayout(named = "Hasta:") LocalDate hasta) {

		if (sector == null) {
			final List<Resoluciones> lista = this.container
					.allMatches(new QueryDefault<Resoluciones>(
							Resoluciones.class, "filtrarPorFechas", "desde",
							desde, "hasta", hasta));
			if (lista.isEmpty())
				this.container.informUser("NO SE ENCONTRARON REGISTROS.");

			return lista;
		} else {
			List<Resoluciones> lista = new ArrayList<Resoluciones>();
			lista = this.container.allMatches(new QueryDefault<Resoluciones>(
					Resoluciones.class, "filtrarCompleto", "sector", sector,
					"desde", desde, "hasta", hasta));
			if (lista.isEmpty())
				this.container.informUser("NO SE ENCONTRARON REGISTROS.");

			return lista;
		}

	}

	public List<Sector> choices0FiltrarCompleto() {
		return sectorRepositorio.listarResoluciones();
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
