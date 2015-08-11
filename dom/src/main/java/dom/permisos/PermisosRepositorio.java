package dom.permisos;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(repositoryFor = Permisos.class)
@DomainServiceLayout(menuOrder = "120", named = "Permisos")
public class PermisosRepositorio {
	public PermisosRepositorio() {

	}

	public String getId() {
		return "Permiso";
	}

	public String iconName() {
		return "Tecnico";
	}

	@Programmatic
	public void addPermiso(final Permisos permiso) {
		container.persistIfNotAlready(permiso);
		container.flush();

	}

	@Programmatic
	@PostConstruct
	public void init() {
		List<Permisos> lista = this.listarPermisos();
		if (lista.isEmpty()) {

			// Se crean todos los permisos que van a ser utilizados (y buscados
			// ) por los roles.
			Permisos permiso = new Permisos();

			// MENU::
			permiso = new Permisos();
			permiso.setNombre("Menu Expedientes");
			permiso.setPath("dom.expediente:ExpedienteRepositorio:*:*");
			this.addPermiso(permiso);

			permiso = new Permisos();
			permiso.setNombre("Menu Memo");
			permiso.setPath("dom.memo:MemoRepositorio:*:*");
			this.addPermiso(permiso);

			permiso = new Permisos();
			permiso.setNombre("Menu Notas");
			permiso.setPath("dom.nota:NotaRepositorio:*:*");
			this.addPermiso(permiso);

			permiso = new Permisos();
			permiso.setNombre("Menu Resoluciones");
			permiso.setPath("dom.resoluciones:ResolucionesRepositorio:*:*");
			this.addPermiso(permiso);

			permiso = new Permisos();
			permiso.setNombre("Menu Disposiciones");
			permiso.setPath("dom.disposiciones:DisposicionRepositorio:*:*");
			this.addPermiso(permiso);

			// BLOQUEAR MENU::

			permiso = new Permisos();
			permiso.setNombre("Bloquear Menu Resoluciones");
			permiso.setPath("dom.disposiciones:DisposicionRepositorio:*:r");
			this.addPermiso(permiso);

			permiso = new Permisos();
			permiso.setNombre("Bloquear Menu Expedientes");
			permiso.setPath("dom.Expediente:ExpedienteRepositorio:*:r");
			this.addPermiso(permiso);

			permiso = new Permisos();
			permiso.setNombre("Bloquear Menu Notas");
			permiso.setPath("dom.nota:NotaRepositorio:*:r");
			this.addPermiso(permiso);

			permiso = new Permisos();
			permiso.setNombre("Bloquear Menu Memo");
			permiso.setPath("dom.Memo:MemoRepositorio:*:r");
			this.addPermiso(permiso);

			permiso = new Permisos();
			permiso.setNombre("Bloquear Menu Disposiciones");
			permiso.setPath("dom.disposiciones:DisposicionRepositorio:*:r");
			this.addPermiso(permiso);
		}
	}

	@MemberOrder(sequence = "1")
	public Permisos agregarPermiso(
			final @ParameterLayout(named = "Nombre") String nombre,
			final @ParameterLayout(named = "Package:")  @Parameter(optionality=Optionality.OPTIONAL) String pack,
			final @ParameterLayout(named = "Otro Package?:")  boolean nuevo,
			final @ParameterLayout(named = "Nuevo Package:")  @Parameter(optionality=Optionality.OPTIONAL) String nuevoPackage,
			@ParameterLayout(named = "Clase:")  @Parameter(optionality=Optionality.OPTIONAL) String clase,
			@ParameterLayout(named = "Metodo/Atributo:")  @Parameter(optionality=Optionality.OPTIONAL)  String campo,
			final @ParameterLayout(named = "Permiso de Escritura?:")  @Parameter(optionality=Optionality.OPTIONAL)boolean escritura) {

		final Permisos permiso = container.newTransientInstance(Permisos.class);

		String paquete = "";
		if (pack == null)
			paquete = nuevoPackage;
		else
			paquete = pack;

		permiso.setNombre(nombre.toUpperCase().trim());
		if (clase == "" || clase == null)
			clase = "*";
		if (campo == "" || campo == null)
			campo = "*";
		String acceso = "*";
		if (!escritura)
			acceso = "r";
		String directorio = paquete + ":" + clase + ":" + campo + ":" + acceso;

		permiso.setPath(directorio);
		permiso.setNombre(nombre);
		container.persistIfNotAlready(permiso);
		return permiso;
	}

	public String validateAgregarPermiso(final String nombre, final String pack,
			final boolean nuevo, final String nuevoPackage, String clase,
			String campo, boolean escritura) {
		if (nuevo && (nuevoPackage == null || nuevoPackage == ""))
			return "Ingrese un nuevo Package.";
		if (!nuevo && (pack == null || pack == ""))
			return "Seleccione un Package.";
		return null;
	}

	public List<String> choices1AgregarPermiso(String nombre, String pack,
			boolean nuevo) {
		if (nuevo)
			return null;
		else {
			List<String> lista = new ArrayList<String>();
			for (Package p : Package.values()) {
				lista.add(p.toString());
			}
			return lista;
		}
	}

	public String default4AgregarPermiso() {
		return "*";
	}

	// public List<String> choices4AddPermiso(String nombre, String pack,
	// boolean nuevo, String nuevoPackage, String clase)
	// throws ClassNotFoundException {
	// String path;
	// if (nuevo)
	// path = nuevoPackage;
	// else
	// path = pack;
	// Class userClass = Class.forName("dom.rol.Rol");
	// if(esCampo)//Agregar campo booleano esCampo
	// Field[] method = aClass.getFields();
	// else
	// Method[] method = userClass.getMethods();
	// List<String> retorno = new ArrayList<String>();
	// retorno.add("*");
	// for (int i = 0; i < method.length; i++) {
	// retorno.add(method[i].getName());
	// }
	// return retorno;
	// }

	public String default5AgregarPermiso() {
		return "*";
	}

	public boolean default6AgregarPermiso() {
		return true;
	}

//	@ActionSemantics(Of.NON_IDEMPOTENT)
	@MemberOrder(sequence = "4")
	public String eliminar(@ParameterLayout(named = "Permiso:")  Permisos permiso) {
		String permissionDescription = permiso.getNombre();
		container.remove(permiso);
		return "El Permiso: " + permissionDescription
				+ " ha sido eliminado correctamente.";
	}

//	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "2")
	public List<Permisos> listarPermisos() {
		return container.allInstances(Permisos.class);
	}

	public enum Package {
		DISPOSICIONES("dom.disposicion"), DOCUMENTOS("dom.documento"), EXPEDIENTES(
				"dom.expediente"), INICIO("dom.inicio"), MEMO("dom.memo"), NOTA(
				"dom.nota"), PERMISO("dom.permiso"), RESOLUCIONES(
				"dom.resoluciones"), ROL("dom.rol"), SECTOR("dom.sector"), USUARIO(
				"dom.usuario"), SERVICES("dom.services"), ;

		private final String text;

		/**
		 * @param text
		 */
		private Package(final String text) {
			this.text = text;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return text;
		}
	}

	@javax.inject.Inject
	DomainObjectContainer container;
}
