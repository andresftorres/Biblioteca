package persistencia.sistema;

import javax.persistence.EntityManager;

import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.conexion.ConexionJPA;
import persistencia.repositorio.RepositorioLibroPersistente;
import persistencia.repositorio.RepositorioPrestamoPersistente;

public class SistemaDePersistencia {

	private EntityManager entityManager;

	public SistemaDePersistencia() {
		this.entityManager = new ConexionJPA().createEntityManager();
	}

	public RepositorioLibro obtenerRepositorioLibros() {
		return new RepositorioLibroPersistente(entityManager);
	}
	
	public RepositorioPrestamo obtenerRepositorioPrestamos() {
		return new RepositorioPrestamoPersistente(entityManager, this.obtenerRepositorioLibros());
	}

	public void iniciar() {
		entityManager.getTransaction().begin();
	}

	public void terminar() {
		entityManager.getTransaction().commit();
	}
}
