package persistencia.repositorio.jpa;

import persistencia.entitad.LibroEntity;

public interface RepositorioLibroJPA {

	/**
	 * Permite obtener un libro entity por un isbn
	 * @param isbn
	 * @return
	 */
	LibroEntity obtenerLibroEntityPorIsbn(String isbn);

}
