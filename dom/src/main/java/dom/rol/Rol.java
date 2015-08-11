package dom.rol;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;

import dom.permisos.Permisos;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@DomainObject(objectType = "Rol")
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Rol {
	public String title() {
		return this.getNombre();
	}

	private String nombre;

	@MemberOrder(sequence = "1")
	@Column(allowsNull = "false")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(final String nombre) {
		this.nombre = nombre;
	}
	
	// {{ Permisos (Collection)
	private List<Permisos> permisos = new ArrayList<Permisos>();

	@MemberOrder(sequence = "1")
	public List<Permisos> getPermisos() {
		return permisos;
	}

	public void setPermisos(final List<Permisos> permisos) {
		this.permisos = permisos;
	}
	// }}


	

//	@Join
//	@Element(dependent = "false")
//	private SortedSet<Permisos> listaPermisos = new TreeSet<Permisos>();
//
//	@MemberOrder(name = "lista de permisos", sequence = "3")
//	// @Render(org.apache.isis.applib.annotation.Render.Type.EAGERLY)
//	public SortedSet<Permisos> getListaPermisos() {
//		return listaPermisos;
//	}
//
//	public void setListaPermisos(final SortedSet<Permisos> listaPermisos) {
//		this.listaPermisos = listaPermisos;
//	}

	

}
