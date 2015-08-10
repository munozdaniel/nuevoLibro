package dom.disposiciones;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

@DomainService(repositoryFor = Disposicion.class)
@DomainServiceLayout(menuOrder = "40", named = "DISPOSICION")
public class DisposicionRepositorio {
	public final Lock monitor = new ReentrantLock();

	public DisposicionRepositorio() {

	}

	// //////////////////////////////////////
	// Identification in the UI
	// //////////////////////////////////////

	public String getId() {
		return "disposicion";
	}

	public String iconName() {
		return "disposicion";
	}

	@MemberOrder(sequence = "10")
	public Disposicion addDisposicion(
			final @ParameterLayout(named = "Sector") Sector sector,
			final @ParameterLayout(named = "Descripción", multiLine=2) @Parameter(maxLength=255)String descripcion,
			final @ParameterLayout(named = "Adjuntar") @Parameter(optionality=Optionality.OPTIONAL) Blob adjunto) {
		Disposicion disposicion = this.nuevaDisposicion(sector, descripcion,
				this.currentUserName(), adjunto);
		if (disposicion != null){
			this.container.informUser("La Disposicion ha sido guardada correctamente.");
			return disposicion;
		}
		this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
		return null;
	}

	@Programmatic
	private Disposicion nuevaDisposicion(final Sector sector,
			final String descripcion, final String creadoPor, final Blob adjunto) {
		final Disposicion unaDisposicion = this.container
				.newTransientInstance(Disposicion.class);
		try {
			if (monitor.tryLock(25, TimeUnit.MILLISECONDS)) {
				try {
					Disposicion anterior = recuperarUltimo();
					Integer nro = Integer.valueOf(1);
					if (anterior != null) {
						if (!anterior.getUltimoDelAnio()) {
							if (!anterior.getHabilitado())
								nro = anterior.getNro_Disposicion();
							else
								nro = anterior.getNro_Disposicion() + 1;
						} else
							anterior.setUltimoDelAnio(false);

						anterior.setUltimo(false);
					}

					unaDisposicion.setNro_Disposicion(nro);
					unaDisposicion.setUltimo(true);
					unaDisposicion.setFecha(LocalDate.now());
					unaDisposicion.setTipo(4);
					unaDisposicion.setAdjuntar(adjunto);
					unaDisposicion.setDescripcion(descripcion.toUpperCase()
							.trim());
					unaDisposicion.setHabilitado(true);
					unaDisposicion.setCreadoPor(creadoPor);

					unaDisposicion.setTime(LocalDateTime.now()
							.withMillisOfSecond(3));
					unaDisposicion.setSector(sector);

					container.persistIfNotAlready(unaDisposicion);
					container.flush();
					return unaDisposicion;
				} finally {
					monitor.unlock();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Programmatic
	private Disposicion recuperarUltimo() {
		final Disposicion doc = this.container
				.firstMatch(new QueryDefault<Disposicion>(Disposicion.class,
						"recuperarUltimo"));
		if (doc == null)
			return null;
		return doc;
	}

	public List<Sector> choices0AddDisposicion() {
		return sectorRepositorio.listarDisposiciones(); // TODO: return list of
														// choices for
		// property
	}

	@Programmatic
	private int recuperarNroDisposicion() {
		final List<Disposicion> disposiciones = this.container
				.allMatches(new QueryDefault<Disposicion>(Disposicion.class,
						"listarHabilitados"));

		if (disposiciones.isEmpty())
			return 0;
		else
			return disposiciones.get(disposiciones.size() - 1)
					.getNro_Disposicion();
	}

	@Programmatic
	public List<Disposicion> autoComplete(final String destino) {
		return container.allMatches(new QueryDefault<Disposicion>(
				Disposicion.class, "autoCompletarDestino", "destinoSector",
				destino));
	}

	// //////////////////////////////////////
	// Listar Memos
	// //////////////////////////////////////
	@MemberOrder(sequence = "20")
	public List<Disposicion> listar() {
		String criterio = "listarHabilitados";
		if (this.container.getUser().isCurrentUser("root"))
			criterio = "listar";
		final List<Disposicion> listaMemo = this.container
				.allMatches(new QueryDefault<Disposicion>(Disposicion.class,
						criterio));
		if (listaMemo.isEmpty()) {
			this.container
					.warnUser("No hay Disposiciones cargados en el sistema");
		}
		return listaMemo;

	}

	public List<Disposicion> filtrarPorDescripcion(
			final @ParameterLayout(named = "Descripción", multiLine=2) @Parameter(maxLength=255)
			String descripcion) {

		List<Disposicion> lista = this.listar();
		Disposicion disposicion = new Disposicion();
		List<Disposicion> listaRetorno = new ArrayList<Disposicion>();
		for (int i = 0; i < lista.size(); i++) {
			disposicion = new Disposicion();
			disposicion = lista.get(i);
			if (disposicion.getDescripcion()
					.contains(descripcion.toUpperCase()))
				listaRetorno.add(disposicion);
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
	public List<Disposicion> filtrarPorFecha(
			final @ParameterLayout(named = "Desde") LocalDate desde,
			final @ParameterLayout(named = "Hasta") LocalDate hasta) {

		final List<Disposicion> lista = this.container
				.allMatches(new QueryDefault<Disposicion>(Disposicion.class,
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
