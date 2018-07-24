package dominio.repositorio;

import dominio.Libro;

public interface RepositorioLibro {

	/**
	 * Permite obtener un libro dado un isbn
	 * @param isbn
	 * @return
	 */
	Libro obtenerPorIsbn(String isbn);

	/**
	 * Permite agregar un libro al repositorio
	 * @param libro
	 */
	void agregar(Libro libro);

}