package dom.documento;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class Documento implements Comparable<Documento>{

	
	private boolean ultimoDelAnio;

	@javax.jdo.annotations.Column(allowsNull = "false")
	@MemberOrder(sequence = "1")
	@Property(hidden = Where.EVERYWHERE)
	public boolean getUltimoDelAnio() {
		return ultimoDelAnio;
	}

	public void setUltimoDelAnio(final boolean ultimoDelAnio) {
		this.ultimoDelAnio = ultimoDelAnio;
	}

	@Override
	public int compareTo(Documento documento) {
		return ObjectContracts.compare(this, documento, "time,descripcion");
	}

}
