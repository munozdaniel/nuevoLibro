package dom.memo;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MaxLength;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MultiLine;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Optional;
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

@DomainService(repositoryFor = Memo.class)
@DomainServiceLayout(menuOrder = "20", named = "MEMO")
public class MemoRepositorio {
	public boolean ocupado = false;

	public MemoRepositorio() {

	}
	public String getId() {
		return "memo";
	}

	public String iconName() {
		return "memo";
	}
	//FIXME: HABRIA QUE QUITAR OTRO SECTOR. CHEQUEAR QUE NO HAYA PROBLEMAS AL HACERLO. EN NOTA TMB.
		@MemberOrder(sequence = "10")
		public Memo addMemo(
				final @ParameterLayout(named = "De:")  Sector sector,
				final @ParameterLayout(named = "Sector Destino:") @Parameter(optionality=Optionality.OPTIONAL) Sector destinoSector,
				final @ParameterLayout(named = "otro Sector? ") boolean otro,
				 @ParameterLayout(named = "Destino:") @Parameter(optionality=Optionality.OPTIONAL)  String otroSector,
				final @ParameterLayout(named = "Descripción:",multiLine=2) @Parameter(maxLength=250) String descripcion,
				final @ParameterLayout(named = "Adjuntar:") @Parameter(optionality=Optionality.OPTIONAL)  Blob adjunto) {
			// if (!destinoSector.getNombre_sector().contentEquals("OTRO SECTOR"))
			// otroSector = "";
			if (!otro)
				otroSector = "";
			Memo memo = this.nuevoMemo(sector, destinoSector, otroSector,
					descripcion, this.currentUserName(), adjunto);
			if (memo != null) {
				this.container
						.informUser("El Memo ha sido guardado correctamente.");
				return memo;
			}
			this.container.informUser("SISTEMA OCUPADO, INTENTELO NUEVAMENTE.");
			return null;
		}
		
		public List<Sector> choices1AddMemo(Sector sector, Sector destinoSector,
				boolean otro, String otroSector) {
			if (otro)
				return null;
			else {
				otroSector = "";
				return sectorRepositorio.listar();
			}
		}
		
		@Programmatic
		private Memo nuevoMemo(final Sector sector, final Sector destinoSector,
				final String otroSector, final String descripcion,
				final String creadoPor, final Blob adjunto) {
			// try {
			// if (monitor.tryLock(4, TimeUnit.MILLISECONDS)) {
			try {
				final Memo unMemo = this.container.newTransientInstance(Memo.class);
				Memo anterior = recuperarUltimo();
				Integer nro = Integer.valueOf(1);
				if (anterior != null) {
					if (!anterior.getUltimoDelAnio()) {
						if (!anterior.getHabilitado())
							nro = anterior.getNro_memo();
						else
							nro = anterior.getNro_memo() + 1;
					} else
						anterior.setUltimoDelAnio(false);

					anterior.setUltimo(false);
				}

				unMemo.setNro_memo(nro);
				unMemo.setUltimo(true);
				unMemo.setFecha(LocalDate.now());
				unMemo.setAdjuntar(adjunto);
				unMemo.setTipo(2);
				unMemo.setDescripcion(descripcion.toUpperCase().trim());
				unMemo.setHabilitado(true);
				unMemo.setCreadoPor(creadoPor);
				unMemo.setTime(LocalDateTime.now().withMillisOfSecond(3));

				unMemo.setDestinoSector(destinoSector);
				unMemo.setOtroDestino(otroSector);
				unMemo.setSector(sector);

				container.persistIfNotAlready(unMemo);
				container.flush();
				return unMemo;
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
			// e.printStackTrace();
			// }
			return null;
		}

		@Programmatic
		private Memo recuperarUltimo() {
			final Memo doc = this.container.firstMatch(new QueryDefault<Memo>(
					Memo.class, "recuperarUltimo"));
			if (doc == null)
				return null;
			return doc;
		}

		// @Named("Sector")
		// public List<Sector> choices1AddMemo() {
		// return sectorRepositorio.listar();
		//
		// }

		public List<Sector> choices0AddMemo() {
			List<Sector> lista = sectorRepositorio.listar();
			if (!lista.isEmpty())
				lista.remove(0);// Elimino el primer elemento: OTRO SECTOR
			return lista;
		}

		//
		// public Sector default1AddMemo() {
		// List<Sector> lista = this.sectorRepositorio.listar();
		// if (!lista.isEmpty())
		// return lista.get(0);
		// return null;
		// }

		// //////////////////////////////////////
		// Buscar Tecnico
		// //////////////////////////////////////

		@Programmatic
		public List<Memo> autoComplete(final String destino) {
			return container.allMatches(new QueryDefault<Memo>(Memo.class,
					"autoCompletarDestino", "destinoSector", destino));
		}

		// //////////////////////////////////////
		// Listar Memos
		// //////////////////////////////////////
		@MemberOrder(sequence = "20")
		public List<Memo> listar() {
			String criterio = "listarHabilitados";
			if (this.container.getUser().isCurrentUser("root"))
				criterio = "listar";
			final List<Memo> listaMemo = this.container
					.allMatches(new QueryDefault<Memo>(Memo.class, criterio));
			if (listaMemo.isEmpty()) {
				this.container.warnUser("No hay Memos cargados en el sistema");
			}
			return listaMemo;

		}

		public List<Memo> filtrarPorDescripcion(
				final @ParameterLayout(named = "Descripcion", multiLine=2) @Parameter(maxLength=255) String descripcion) {

			List<Memo> lista = this.container.allMatches(new QueryDefault<Memo>(
					Memo.class, "filtrarPorDescripcion", "descripcion", descripcion
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
		 **/
		@MemberOrder(sequence = "30")
		private List<Memo> filtrarPorFecha(final LocalDate desde,
				final  LocalDate hasta) {

			final List<Memo> lista = this.container
					.allMatches(new QueryDefault<Memo>(Memo.class,
							"filtrarPorFechas", "desde", desde, "hasta", hasta));
			if (lista.isEmpty()) {
				this.container.warnUser("No se encontraron Registros.");
			}
			return lista;
		}
		 
		@MemberOrder(sequence = "30")
		public List<Memo> filtrarCompleto(
				final @ParameterLayout(named = "Sector Origen") @Parameter(optionality=Optionality.OPTIONAL) Sector origen,
				final @ParameterLayout(named = "Sector Destino") @Parameter(optionality=Optionality.OPTIONAL) String destino,
				final @ParameterLayout(named = "Desde") @Parameter(optionality=Optionality.OPTIONAL) LocalDate desde,
				final @ParameterLayout(named = "Hasta") @Parameter(optionality=Optionality.OPTIONAL)  LocalDate hasta) {
			List<Memo> lista = new ArrayList<Memo>();

			Sector sectorDestino = sectorRepositorio.buscarPorNombre(destino);

			// Todos ===========================================================
			if (origen != null && destino != null && desde != null && hasta != null)
				if (sectorDestino != null)
					lista = this.container
							.allMatches(new QueryDefault<Memo>(Memo.class,
									"filtrarCompleto", "origen", origen,
									"sectorDestino", sectorDestino, "otroDestino",
									destino.toUpperCase(), "desde", desde, "hasta",
									hasta));
				else
					lista = this.container.allMatches(new QueryDefault<Memo>(
							Memo.class, "filtrarCompletoOtroDestino", "origen",
							origen, "otroDestino", destino.toUpperCase(), "desde",
							desde, "hasta", hasta));

			else {
				// solo las fechas ============================================
				if (origen == null && destino == null && desde != null
						&& hasta != null)
					lista = this.filtrarPorFecha(desde, hasta);
				else {
					// solo origen =============================================
					if (origen != null && destino == null && desde == null
							&& hasta == null)
						lista = this.container.allMatches(new QueryDefault<Memo>(
								Memo.class, "filtrarOrigen", "origen", origen));
					else {
						// solo destino ========================================
						if (origen == null && destino != null && desde == null
								&& hasta == null) {
							if (sectorDestino != null)
								lista = this.container
										.allMatches(new QueryDefault<Memo>(
												Memo.class, "filtrarDestino",
												"sectorDestino", sectorDestino,
												"otroDestino", destino
														.toUpperCase()));
							else
								lista = this.container
										.allMatches(new QueryDefault<Memo>(
												Memo.class, "filtrarOtroDestino",
												"otroDestino", destino
														.toUpperCase()));

						} else {
							// fecha y Origen ===================================
							if (origen != null && destino == null && desde != null
									&& hasta != null)
								lista = this.container
										.allMatches(new QueryDefault<Memo>(
												Memo.class, "filtrarFechaYOrigen",
												"origen", origen, "desde", desde,
												"hasta", hasta));
							else {
								// fecha y Destino ==============================
								if (origen == null && destino != null
										&& desde != null && hasta != null)
									if (sectorDestino != null)
										lista = this.container
												.allMatches(new QueryDefault<Memo>(
														Memo.class,
														"filtrarFechaYDestino",
														"sectorDestino",
														sectorDestino,
														"otroDestino", destino
																.toUpperCase(),
														"desde", desde, "hasta",
														hasta));
									else
										lista = this.container
												.allMatches(new QueryDefault<Memo>(
														Memo.class,
														"filtrarFechaYOtroDestino",
														"otroDestino", destino
																.toUpperCase(),
														"desde", desde, "hasta",
														hasta));

								else {
									// Origen y Destino ============================
									if (origen != null && destino != null
											&& desde == null && hasta == null)
										if (sectorDestino != null)
											lista = this.container
													.allMatches(new QueryDefault<Memo>(
															Memo.class,
															"filtrarOrigenYDestino",
															"origen", origen,
															"sectorDestino",
															sectorDestino,
															"otroDestino", destino
																	.toUpperCase()));

										else
											lista = this.container
													.allMatches(new QueryDefault<Memo>(
															Memo.class,
															"filtrarOrigenYOtroDestino",
															"origen", origen,
															"otroDestino", destino
																	.toUpperCase()));
								}

							}
						}
					}
				}
			}

			if (lista.isEmpty())
				this.container.informUser("NO SE ENCONTRARON REGISTROS.");

			return lista;

		}

		public String validateFiltrarCompleto(Sector origen, String destino,
				LocalDate desde, LocalDate hasta) {
			if ((desde != null && hasta == null))
				return "Por favor, ingrese una fecha final estimativa.";
			else if (desde == null && hasta != null)
				return "Por favor, ingrese una fecha inicial estimativa.";
			else if (origen == null && destino == null && desde == null
					&& hasta == null)
				return "Por favor, ingrese datos para realizar la busqueda.";
			return null;
		}

		public List<Sector> choices0FiltrarCompleto() {//Habria que eliminar el primer elemento? OTRO SECTOR? o eliminarlo de la BD
			return sectorRepositorio.listar();
		}
		private String currentUserName() {
			return container.getUser().getName();
		}
		@javax.inject.Inject
		private DomainObjectContainer container;

		@javax.inject.Inject
		private SectorRepositorio sectorRepositorio;

}
