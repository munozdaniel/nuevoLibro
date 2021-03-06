package dom.sector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;

import conexion.Conexion;

@DomainService(repositoryFor = Sector.class)
@DomainServiceLayout(menuOrder = "100", named = "Sector")
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
			Connection con = Conexion.GetConnectionGestionUsuarios();
			boolean disposicion = false;
			boolean resolucion = false;
			boolean expediente = false;
			String responsable = "Sin Definir";
			try {
				PreparedStatement stmt = con
						.prepareStatement("Select * from sectores where sector_activo =1");
				ResultSet rs = stmt.executeQuery();

				while (rs.next()) {
					this.agregarDesdeBD(rs.getInt("sector_id"),
							rs.getString("sector_nombre"), responsable,
							disposicion, expediente, resolucion);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else{
			this.updateSector();
		}
	}

	private Sector agregarDesdeBD(final int sector_id,
			final String nombre_sector, final String responsable,
			final Boolean disposicion, final Boolean expediente,
			final Boolean resolucion) {
		final Sector unSector = this.container
				.newTransientInstance(Sector.class);
		unSector.setNombre_sector(nombre_sector.toUpperCase().trim());
		unSector.setSector_id(sector_id);
		unSector.setHabilitado(true);
		unSector.setCreadoPor(this.currentUserName());
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
	 * Actualiza los sectores existentes con los de la bd gestionusuarios. Y
	 * aquellos que no existen los agrega. Se mantiene la logica que los sectores no 
	 * se eliminan fisicamente, sino que pasan de activo a inactivo. 
	 * 
	 * @return
	 */
	public List<Sector> updateSector() {
		Connection con = Conexion.GetConnectionGestionUsuarios();
		try {
			Sector sector = null;
			PreparedStatement stmt = con
					.prepareStatement("Select * from sectores where sector_activo =1");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				sector = this.searchById(rs.getInt("sector_id"));
				if(sector!=null)
				{
					sector.setNombre_sector(rs.getString("sector_nombre"));
					if(rs.getInt("sector_activo")==1)
						sector.setHabilitado(true);
					else
						sector.setHabilitado(false);
					this.container.flush();
				}
				else
				{
					boolean disposicion = false;
					boolean resolucion = false;
					boolean expediente = false;
					String responsable = "Sin Definir";
					this.agregarDesdeBD(rs.getInt("sector_id"),
							rs.getString("sector_nombre"), responsable,
							disposicion, expediente, resolucion);
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this.listar();
	}
	/*Busca en la bd de libro si el sector existe.*/
	@MemberOrder(sequence = "21")
	private Sector searchById(final int id) {
		final Sector sector = this.container
				.firstMatch(new QueryDefault<Sector>(Sector.class,
						"buscarPorId", "id", id));

		return sector;
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
	/*
	 * Deshabilitado porque no deberia poder agregar sectores desde esta
	 * aplicacion.
	 * 
	 * @MemberOrder(sequence = "10") public Sector agregar(
	 * 
	 * @ParameterLayout(named = "Nombre") final String nombre_sector,
	 * 
	 * @ParameterLayout(named = "Responsable") final String responsable,
	 * 
	 * @ParameterLayout(named = "Disposicion") final Boolean disposicion,
	 * 
	 * @ParameterLayout(named = "Expediente") final Boolean expediente,
	 * 
	 * @ParameterLayout(named = "Resolucion") final Boolean resolucion) { return
	 * nuevoSector(nombre_sector, responsable, disposicion, expediente,
	 * resolucion, this.currentUserName()); }
	 * 
	 * @Programmatic private Sector nuevoSector(final String nombre_sector,
	 * final String responsable, final Boolean disposicion, final Boolean
	 * expediente, final Boolean resolucion, final String creadoPor) { final
	 * Sector unSector = this.container .newTransientInstance(Sector.class);
	 * unSector.setNombre_sector(nombre_sector.toUpperCase().trim());
	 * unSector.setHabilitado(true); unSector.setCreadoPor(creadoPor);
	 * unSector.setResponsable(responsable); if (resolucion != null)
	 * unSector.setResolucion(resolucion); else unSector.setResolucion(false);
	 * if (disposicion != null) unSector.setDisposicion(disposicion); else
	 * unSector.setDisposicion(false); if (expediente != null)
	 * unSector.setExpediente(expediente); else unSector.setExpediente(false);
	 * this.container.persistIfNotAlready(unSector); this.container.flush();
	 * return unSector; }
	 */
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
						"todosLosSectoresTrue"));
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
	// @Action(hidden = Where.EVERYWHERE)
	@MemberOrder(sequence = "21")
	private List<Sector> buscar(
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

	private String currentUserName() {
		return container.getUser().getName();
	}

	@javax.inject.Inject
	private DomainObjectContainer container;
}
