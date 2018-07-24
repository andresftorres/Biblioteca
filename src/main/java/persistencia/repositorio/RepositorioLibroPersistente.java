package persistencia.repositorio;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import dominio.Libro;
import dominio.repositorio.RepositorioLibro;
import persistencia.builder.LibroBuilder;
import persistencia.entitad.LibroEntity;
import persistencia.repositorio.jpa.RepositorioLibroJPA;

public class RepositorioLibroPersistente implements RepositorioLibro, RepositorioLibroJPA {

	private static final String ISBN = "isbn";
	private static final String LIBRO_FIND_BY_ISBN = "Libro.findByIsbn";
	
	private EntityManager entityManager;

	public RepositorioLibroPersistente(EntityManager entityManager) {
		
		this.entityManager = entityManager;
	}	

	@Override
	public Libro obtenerPorIsbn(String isbn) {
		
		LibroEntity libroEntity = obtenerLibroEntityPorIsbn(isbn);

		return LibroBuilder.convertirADominio(libroEntity);
	}

	@Override
	public void agregar(Libro libro) {
		
		entityManager.persist(LibroBuilder.convertirAEntity(libro));
	}

	@Override
	public LibroEntity obtenerLibroEntityPorIsbn(String isbn) {
		
		Query query = entityManager.createNamedQuery(LIBRO_FIND_BY_ISBN);
		query.setParameter(ISBN, isbn);

		return (LibroEntity) query.getSingleResult();
	}

}
