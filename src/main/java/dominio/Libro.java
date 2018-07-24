package dominio;

public class Libro {

	private String isbn;
	private String titulo;
	private int anio;

	public Libro(String isbn, String titulo, int anio) {

		this.isbn = isbn;
		this.titulo = titulo;
		this.anio = anio;
	}

	public String getTitulo() {
		return titulo;
	}

	public int getAnio() {
		return anio;
	}

	public String getIsbn() {
		return isbn;
	}

}
