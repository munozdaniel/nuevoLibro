package dom.sector;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;

@DomainService(repositoryFor = Sector.class)
public class SectorRepositorio {
	public SectorRepositorio() {

	}

	public String getId() {
		return "sector";
	}

	public String iconName() {
		return "Tecnico";
	}

	@Programmatic
	@PostConstruct
	public void init() {
		List<Sector> lista = this.listar();
		if (lista.isEmpty()) {
			this.nuevoSector("OTRO SECTOR", "None", false, false, false,
					"Admin");
		}
	}

	/**
	 * Insertar un Sector.
	 * 
	 * @param nombre_sector
	 * @param responsable
	 * @param disposicion
	 * @param expediente
	 * @param resolucion
	 * @return
	 */

	@MemberOrder(name = "Agregar", sequence = "10")
	public Sector agregar(
			@ParameterLayout(named = "Nombre") final String nombre_sector,
			@ParameterLayout(named = "Responsable") final String responsable,
			@ParameterLayout(named = "Disposicion") final Boolean disposicion,
			@ParameterLayout(named = "Expediente") final Boolean expediente,
			@ParameterLayout(named = "Resolucion") final Boolean resolucion) {
		return nuevoSector(nombre_sector, responsable, disposicion, expediente,
				resolucion, this.currentUserName());
	}

	@Programmatic
	private Sector nuevoSector(final String nombre_sector,
			final String responsable, final Boolean disposicion,
			final Boolean expediente, final Boolean resolucion,
			final String creadoPor) {
		final Sector unSector = this.container
				.newTransientInstance(Sector.class);
		unSector.setNombre_sector(nombre_sector.toUpperCase().trim());
		unSector.setHabilitado(true);
		unSector.setCreadoPor(creadoPor);
		unSector.setResponsable(responsable);
		if (resolucion != null)
			unSector.setResolucion(resolucion);
		else
			unSector.setResolucion(false);
		if (disposicion != null)
			unSector.setDisposicion(disposicion);
		else
			unSector.setDisposicion(false);
		if (expediente != null)
			unSector.setExpediente(expediente);
		else
			unSector.setExpediente(false);
		this.container.persistIfNotAlready(unSector);
		this.container.flush();
		return unSector;
	}

	/**
	 * listar Devuelve todos los sectores. Hay que chequear aquellos sectores
	 * que corresponden solo a Resolucion o Disposicion, etc.
	 * 
	 * @return
	 */
	@MemberOrder(sequence = "20")
	public List<Sector> listar() {
		final List<Sector> listarSectores = this.container
				.allMatches(new QueryDefault<Sector>(Sector.class,
						"todosLosSectores"));
		if (listarSectores.isEmpty())
			this.container
					.warnUser("No se encontraron sectores cargados en el sistema.");
		return listarSectores;
	}

	/**
	 * Buscar
	 * 
	 * @param nombreSector
	 * @return
	 */
	//@Action(hidden = Where.EVERYWHERE)
	@MemberOrder(sequence = "21")
	public List<Sector> buscar(
			@ParameterLayout(named = "Nombre", typicalLength = 100) final String nombreSector) {
		final List<Sector> listarSectores = this.container
				.allMatches(new QueryDefault<Sector>(Sector.class,
						"buscarPorNombre", "nombre_sector", nombreSector
								.toUpperCase().trim()));
		if (listarSectores.isEmpty())
			this.container.warnUser("No se encontraron sectores.");
		return listarSectores;
	}

	/**
	 * Buscar Sector por id para migrar los datos.
	 * 
	 * @param nombreSector
	 * @return
	 */
	@Programmatic
	@MemberOrder(sequence = "21")
	public Sector buscarPorNombre(final String nombre) {
		Sector sector = null;
		if (nombre != null && nombre != "")
			sector = this.container.uniqueMatch(new QueryDefault<Sector>(
					Sector.class, "buscarNombre", "nombre", nombre
							.toUpperCase()));
		if (sector == null)
			this.container.warnUser("No se encontraron sectores.");
		return sector;
	}

	/**
	 * autoComplete
	 * 
	 * @param buscarNombreSector
	 * @return
	 */
	@Programmatic
	public List<Sector> autoComplete(final String buscarNombreSector) {
		return container.allMatches(new QueryDefault<Sector>(Sector.class,
				"autoCompletePorNombreSector", "nombre_sector",
				buscarNombreSector.toUpperCase().trim()));
	}

	/**
	 * listarDisposiciones
	 * 
	 * @return
	 */
	@Programmatic
	@MemberOrder(sequence = "20")
	public List<Sector> listarDisposiciones() {
		final List<Sector> listarSectores = this.container
				.allMatches(new QueryDefault<Sector>(Sector.class,
						"sectoresDisposiciones"));
		if (listarSectores.isEmpty())
			this.container
					.warnUser("No se encontraron sectores cargados en el sistema.");
		return listarSectores;
	}

	/**
	 * listarResoluciones
	 * 
	 * @return
	 */
	@Programmatic
	@MemberOrder(sequence = "20")
	public List<Sector> listarResoluciones() {
		final List<Sector> listarSectores = this.container
				.allMatches(new QueryDefault<Sector>(Sector.class,
						"sectoresResoluciones"));
		if (listarSectores.isEmpty())
			this.container
					.warnUser("No se encontraron sectores cargados en el sistema.");
		return listarSectores;
	}

	/**
	 * listarExpediente
	 * 
	 * @return
	 */
	@Programmatic
	@MemberOrder(sequence = "20")
	public List<Sector> listarExpediente() {
		final List<Sector> listarSectores = this.container
				.allMatches(new QueryDefault<Sector>(Sector.class,
						"sectoresExpediente"));
		if (listarSectores.isEmpty())
			this.container
					.warnUser("No se encontraron sectores cargados en el sistema.");
		return listarSectores;
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
}
