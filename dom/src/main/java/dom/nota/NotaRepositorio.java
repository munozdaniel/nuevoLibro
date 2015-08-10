package dom.nota;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.value.Blob;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import dom.sector.Sector;
import dom.sector.SectorRepositorio;
@DomainService(repositoryFor = Nota.class)
@DomainServiceLayout(menuOrder = "10", named = "NOTA")
public class NotaRepositorio {
		public boolean ocupado = false;

		public NotaRepositorio() {

		}

		public String getId() {
			return "nota";
		}

		public String iconName() {
			return "nota";
		}


		//@NotContributed
		//@Named("Enviar")
		@MemberOrder(sequence = "10")
		public Nota addNota(
				final @ParameterLayout(named = "De:") Sector sector,
				@ParameterLayout(named = "Para") final String destino,
				@ParameterLayout(named = "Descripción:")  @Parameter(maxLength = 254)  final  String descripcion,
				@ParameterLayout(named = "Adjuntar:") final Blob adjunto) {
			Nota nota = nuevaNota(sector, destino, descripcion,
					this.currentUserName(), adjunto);
			if (nota != null) {
				this.container
						.informUser("La Nota ha sido guardada correctamente.");
				return nota;
			}
			this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
			return null;

		}

		public String validateAddNota(final Sector sector, final String destino,
				final String descripcion, final Blob adjunto) {
			if (!this.ocupado) {
				this.ocupado = true;
				return null;
			} else
				return "Sistema ocupado, intente nuevamente.";
		}

		@Programmatic
		private Nota nuevaNota(final Sector sector, final String destino,
				final String descripcion, final String creadoPor, final Blob adjunto) {
			// try {
			// if (monitor.tryLock(1, TimeUnit.MILLISECONDS)) {
			try {
				final Nota unaNota = this.container
						.newTransientInstance(Nota.class);
				Integer nro = Integer.valueOf(1);

				Nota notaAnterior = recuperarElUltimo();
				/*
				 * Si es nulo => cero Si no es nulo, si no es el ulitmoDelAnio y si
				 * no esta habilitado => igual Si no es nulo y si no es el
				 * ultimoDelAnio y esta Habilitado => suma Si no es nulo y si es el
				 * ulitmo del Anio => cero
				 */
				if (notaAnterior != null) {
					if (!notaAnterior.getUltimoDelAnio()) {
						if (!notaAnterior.getHabilitado())
							nro = notaAnterior.getNro_nota();
						else
							nro = notaAnterior.getNro_nota() + 1;
					} else
						notaAnterior.setUltimoDelAnio(false);
					notaAnterior.setUltimo(false);
				}
				// if (unaNota.getDescripcion().equalsIgnoreCase("ALGO")) {
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
				unaNota.setDescripcion(descripcion.toUpperCase().trim());
				unaNota.setUltimo(true);
				unaNota.setNro_nota(nro);
				unaNota.setFecha(LocalDate.now());
				unaNota.setTipo(1);
				unaNota.setCreadoPor(creadoPor);
				unaNota.setDestino(destino);
				unaNota.setTime(LocalDateTime.now().withMillisOfSecond(3));
				unaNota.setAdjuntar(adjunto);
				unaNota.setSector(sector);
				unaNota.setHabilitado(true);

				container.persistIfNotAlready(unaNota);
				container.flush();

				return unaNota;
			} catch (Exception e) {
				container
						.warnUser("Por favor, verifique que la informacion se ha guardado correctamente. En caso contrario informar a Sistemas.");
			} finally {
				// monitor.unlock();
				this.ocupado = false;
			}
			// }
			// } catch (InterruptedException e) {
			// this.container
			// .informUser("Verifique que los datos se hayan almacenado");
			return null;

			// e.printStackTrace();
			// }
			// return null;
		}

		@Programmatic
//		@NotInServiceMenu
		private Nota recuperarElUltimo() {

			final Nota nota = this.container.firstMatch(new QueryDefault<Nota>(
					Nota.class, "recuperarUltimo"));
			if (nota == null)
				return null;
			return nota;

		}

//		@Named("Sector")
		public List<Sector> choices0AddNota() {
			List<Sector> lista = sectorRepositorio.listar();
			if (!lista.isEmpty())
				lista.remove(0);// Elimino el primer elemento: OTRO SECTOR
			return lista;
		}

		@Programmatic
		public List<Nota> autoComplete(final String destino) {
			String criterio = "autoCompletarDestino";
			if (this.container.getUser().isCurrentUser("root"))
				criterio = "autoComplete";
			return container.allMatches(new QueryDefault<Nota>(Nota.class,
					criterio, "destino", destino));
		}

		/**
		 * Listar todas las notas, dependera del usuario y sus roles. Optimizar las
		 * busquedas por usuario D:
		 * 
		 * @return
		 */
//		@Paged(12)
		@MemberOrder(sequence = "20")
//		@Named("Lista de Notas")
		public List<Nota> listar() {
			String criterio = "listarHabilitados";
			if (this.container.getUser().isCurrentUser("root"))
				criterio = "listar";
			final List<Nota> listaNotas = this.container
					.allMatches(new QueryDefault<Nota>(Nota.class, criterio));
			if (listaNotas.isEmpty()) {
				this.container.warnUser("No hay Notas cargados en el sistema");
			}
			return listaNotas;

		}

		public List<Nota> filtrarPorDescripcion(
				final @ParameterLayout(named = "Descripción")  @Parameter(maxLength = 254) String descripcion) {

			List<Nota> lista = this.container.allMatches(new QueryDefault<Nota>(
					Nota.class, "filtrarPorDescripcion", "descripcion", descripcion
							.toUpperCase()));
			if (lista.isEmpty()) {
				this.container.warnUser("No se encontraron Registros.");
			}
			return lista;
		}

		public String validateFiltrarPorDescripcion(final String descripcion) {
			if (descripcion.trim() == "" || descripcion == null)
				return "Por favor, ingrese una descripción.";
			return null;
		}

		/**
		 * Filtrar por fecha
		 * 
		 * @param sector
		 * @param fecha
		 * @return
		 */
		@MemberOrder(sequence = "30")
//		@Named("Filtro por Fecha")
//		@DescribedAs("Seleccione una fecha de inicio y una fecha final.")
//		@Hidden
		public List<Nota> filtrarPorFecha(final @ParameterLayout(named = "Desde")  LocalDate desde,
				final @ParameterLayout(named = "Hasta")  LocalDate hasta) {

			final List<Nota> notas = this.container
					.allMatches(new QueryDefault<Nota>(Nota.class,
							"filtrarPorFechas", "desde", desde, "hasta", hasta));
			if (notas.isEmpty()) {
				this.container.warnUser("No se encontraron Registros.");
			}
			return notas;
		}

		private String currentUserName() {
			return container.getUser().getName();
		}

//		@Hidden
		public enum TipoSector {
			ORIGEN("ORIGEN"), DESTINO("DESTINO");

			private final String text;

			/**
			 * @param text
			 */
			private TipoSector(final String text) {
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

		/*
		 * @Hidden public List<Nota> filtrarPorSector(final @Named("Tipo")
		 * TipoSector tipo, final @Named("Sector") String sector) { List<Nota> lista
		 * = new ArrayList<Nota>(); if (tipo.name().equalsIgnoreCase("ORIGEN")) { //
		 * this.container.informUser("BUSCANDO "+tipo.name()); Sector sectorOrigen =
		 * sectorRepositorio.buscarPorNombre(sector); lista =
		 * this.container.allMatches(new QueryDefault<Nota>( Nota.class,
		 * "filtrarPorOrigen", "sector", sectorOrigen)); } else { //
		 * this.container.informUser("BUSCANDO "+tipo.name()); lista =
		 * this.container.allMatches(new QueryDefault<Nota>( Nota.class,
		 * "filtrarPorDestino", "sector", sector .toUpperCase())); } if
		 * (lista.isEmpty())
		 * this.container.informUser("NO SE ENCONTRARON RESULTADOS.");
		 * 
		 * return lista; }
		 */

		@MemberOrder(sequence = "30")
//		@Named("Filtro por Sector y Fecha")
		public List<Nota> filtrarCompleto(
				final @ParameterLayout(named="Sector Origen") Sector origen,
				final @ParameterLayout(named="Sector Destino") String destino,
				final @ParameterLayout(named="Desde:") LocalDate desde,
				final @ParameterLayout(named="Hasta:") LocalDate hasta) {
			List<Nota> lista = new ArrayList<Nota>();
			// TODOS
			// ================================================================
			if (origen != null && destino != null && desde != null && hasta != null)
				lista = this.container.allMatches(new QueryDefault<Nota>(
						Nota.class, "filtrarCompleto", "origen", origen, "destino",
						destino.toUpperCase(), "desde", desde, "hasta", hasta));
			else {
				// SOLO FECHAS
				// =======================================================
				if (origen == null && destino == null && desde != null
						&& hasta != null)
					lista = this.filtrarPorFecha(desde, hasta);
				else {
					// SOLO ORIGEN
					// =======================================================
					if (origen != null && destino == null && desde == null
							&& hasta == null)
						lista = this.container.allMatches(new QueryDefault<Nota>(
								Nota.class, "filtrarOrigen", "origen", origen));
					else {
						// SOLO DESTINO
						// =======================================================
						if (origen == null && destino != null && desde == null
								&& hasta == null) {
							lista = this.container
									.allMatches(new QueryDefault<Nota>(Nota.class,
											"filtrarDestino", "destino", destino
													.toUpperCase()));
							this.container.warnUser("DESTINO"
									+ destino.toUpperCase());

						} else {
							// FECHAS Y ORIGEN
							// =======================================================
							if (origen != null && destino == null && desde != null
									&& hasta != null)
								lista = this.container
										.allMatches(new QueryDefault<Nota>(
												Nota.class, "filtrarFechaYOrigen",
												"origen", origen, "desde", desde,
												"hasta", hasta));
							else {
								// FECHAS Y DESTINO
								// =======================================================
								if (origen == null && destino != null
										&& desde != null && hasta != null)
									lista = this.container
											.allMatches(new QueryDefault<Nota>(
													Nota.class,
													"filtrarFechaYDestino",
													"destino", destino
															.toUpperCase(),
													"desde", desde, "hasta", hasta));
								else {
									// ORIGEN Y DESTINO
									// =======================================================
									if (origen != null && destino != null
											&& desde == null && hasta == null)
										lista = this.container
												.allMatches(new QueryDefault<Nota>(
														Nota.class,
														"filtrarOrigenYDestino",
														"origen", origen,
														"destino", destino
																.toUpperCase()));
								}

							}
						}
					}
				}
			}

			if (lista.isEmpty())
				this.container.informUser("NO SE ENCONTRARON RESULTADOS.");

			return lista;
		}

		public String validateFiltrarCompleto(final Sector origen,
				final String destino, final LocalDate desde, final LocalDate hasta) {
			if ((desde != null && hasta == null))
				return "Por favor, ingrese una fecha final estimativa.";
			else if (desde == null && hasta != null)
				return "Por favor, ingrese una fecha inicial estimativa.";
			else if (origen == null && destino == null && desde == null
					&& hasta == null)
				return "Por favor, ingrese datos para realizar la busqueda.";
			return null;
		}

		public List<Sector> choices0FiltrarCompleto() {
			List<Sector> lista = sectorRepositorio.listar();
			return lista;
		}

		/*********************************************************************************
		 * PARA MIGRAR
		 */
		@Programmatic
		public Nota insertar(final int nro, final Sector sector,
				final String destino, final String descripcion, final int ultimo,
				final String fecha, final int habilitado,
				final LocalDate fechacompleta) {

			final Nota unaNota = this.container.newTransientInstance(Nota.class);
			unaNota.setNro_nota(nro);
			unaNota.setSector(sector);
			unaNota.setDestino(destino);
			unaNota.setDescripcion(descripcion.toUpperCase().trim());
			unaNota.setCreadoPor("root");
			unaNota.setAdjuntar(null);
			if (ultimo == 0)
				unaNota.setUltimo(false);
			else
				unaNota.setUltimo(true);

			unaNota.setUltimoDelAnio(false);
			unaNota.setFecha(fechacompleta);

			if (habilitado == 0)
				unaNota.setHabilitado(true);
			else
				unaNota.setHabilitado(false);
			unaNota.setTipo(1);
			unaNota.setTime(LocalDateTime.now().withMillisOfSecond(3));
			container.persistIfNotAlready(unaNota);
			container.flush();

			return unaNota;
		}

		// //////////////////////////////////////
		// Injected Services
		// //////////////////////////////////////
		@javax.inject.Inject
		private DomainObjectContainer container;
		@javax.inject.Inject
		private SectorRepositorio sectorRepositorio;
}
