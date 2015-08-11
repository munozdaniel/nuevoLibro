package dom.rol;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import dom.permisos.Permisos;

@DomainService(repositoryFor = Rol.class)
@DomainServiceLayout(menuOrder = "110", named = "Rol")
public class RolRepositorio {
	public String getId() {
		return "rol";
	}

	public String iconName() {
		return "Tecnico";
	}

//	@ActionSemantics(Of.SAFE)
	@MemberOrder(sequence = "2")
	public List<Rol> listarTodos() {
		return container.allInstances(Rol.class);
	}

	@MemberOrder(sequence = "1")
	@Property(hidden=Where.OBJECT_FORMS)
	public Rol crearRol(final @ParameterLayout(named = "Nombre:")  String nombre,
			final @ParameterLayout(named = "Permiso:")  Permisos permiso) {
		final Rol rol = container.newTransientInstance(Rol.class);
		final List<Permisos> permissionsList = new ArrayList<Permisos>();
		if (permiso != null) {
			permissionsList.add(permiso);
			rol.setPermisos(permissionsList);
		}
		rol.setNombre(nombre.toUpperCase().trim());
		container.persistIfNotAlready(rol);
		return rol;
	}

	@Programmatic
	public Rol addRol(final @ParameterLayout(named = "Nombre:") String nombre,
			final @ParameterLayout(named = "Permisos:")  List<Permisos> permisos) {
		final Rol rol = container.newTransientInstance(Rol.class);
		if (permisos != null) {
			List<Permisos> listaPermisos = new ArrayList<Permisos>(permisos);
			rol.setPermisos(listaPermisos);
		}
		rol.setNombre(nombre);
		container.persistIfNotAlready(rol);
		return rol;
	}

//	@ActionSemantics(Of.NON_IDEMPOTENT)
	@MemberOrder(sequence = "4")
	@Programmatic
	public void eliminarRol(@ParameterLayout(named = "Rol:") Rol rol) {
		if (!rol.getNombre().equalsIgnoreCase("SUPERUSUARIO")) {
			String roleName = rol.getNombre();
			container.remove(rol);
			this.container.warnUser("El Rol " + roleName + " se ha eliminado correctamente.");
		}
		else
			this.container.warnUser("El Rol SUPERUSUARIO NO PUEDE SER ELIMINADO.");
	}

	@javax.inject.Inject
	DomainObjectContainer container;
}
