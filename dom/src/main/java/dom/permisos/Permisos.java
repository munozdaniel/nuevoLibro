package dom.permisos;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@DomainObject(objectType = "Permisos")
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Permisos {
	public String title() {
		return nombre;
	}

	private String nombre;

	@MemberOrder(sequence = "1")
	@Column(allowsNull = "false")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(final String detalles) {
		this.nombre = detalles;
	}

	private String path;

	@MemberOrder(sequence = "1")
	@Column(allowsNull = "false")
	public String getPath() {
		return path;
	}

	public void setPath(final String text) {
		this.path = text;
	}
}
