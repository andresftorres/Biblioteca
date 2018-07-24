package dominio.integracion;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {

	private static final String CRONICA_DE_UNA_MUERTA_ANUNCIADA = "Cronica de una muerta anunciada";
	private static final String PRESTAMISTA = "J.K. ROWLING";
	private static final String ISBN_NO_REGISTRADO = "12ADF87";
	private static final String ISBN_PALINDROME = "1234DD4321";	
	private static final String ISBN_MAYOR_30 = "J79TO49RI39E9F9E9AC9G9";
	private static final String ISBN_MENOR_30 = "HY7E6G4B";	
	private static final String ISBN_NO_PALINDROME = "H74JS12H3G4E9";

	private static final String FECHA_MAXIMA = "2017-06-12";
	private static final String FECHA_SOLICITUD = "2017-05-26";
	
	
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioLibro repositorioLibros;
	private RepositorioPrestamo repositorioPrestamo;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioLibros = sistemaPersistencia.obtenerRepositorioLibros();
		repositorioPrestamo = sistemaPersistencia.obtenerRepositorioPrestamos();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void prestarLibroTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		blibliotecario.prestar(libro.getIsbn(), PRESTAMISTA, new Date());

		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));

	}

	@Test
	public void prestarLibroNoDisponibleTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		
		repositorioLibros.agregar(libro);
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		blibliotecario.prestar(libro.getIsbn(), PRESTAMISTA, new Date() );
		try {
			
			blibliotecario.prestar(libro.getIsbn(), PRESTAMISTA, new Date());
			fail();
			
		} catch (PrestamoException e) {
			// assert
			System.out.println(e.getMessage());
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE, e.getMessage());
		}
	}
	
	@Test
	public void PrestarLibroNoRegistradoTest() {

		// arrange
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		// act
		try{
			blibliotecario.prestar(ISBN_NO_REGISTRADO, PRESTAMISTA, new Date() );
		}catch( Exception ex){
			// assert
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_EXISTE, ex.getMessage());
		}		
	}
	
	@Test
	public void PrestarLibroPalindromeTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conIsbn(ISBN_PALINDROME).build();				
		repositorioLibros.agregar(libro);		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		// act
		try{
			blibliotecario.prestar(ISBN_PALINDROME, PRESTAMISTA, new Date() );
		}catch( Exception ex){
			// assert
			Assert.assertEquals(Bibliotecario.EL_ISBN_ES_PALINDROMO, ex.getMessage());			
		}
	}
	
	@Test
	public void PrestarLibroNoPalindromeTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conIsbn(ISBN_NO_PALINDROME).build();				
		repositorioLibros.agregar(libro);		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		// act
		blibliotecario.prestar(ISBN_NO_PALINDROME, PRESTAMISTA, new Date() );
		
		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(ISBN_NO_PALINDROME));				
		
	}
	
	@Test
	public void PrestarLibroISBNMaximaFechaTest() throws ParseException {

		// arrange
		Libro libro = new LibroTestDataBuilder().conIsbn(ISBN_MAYOR_30).build();				
		repositorioLibros.agregar(libro);		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		// act
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		blibliotecario.prestar(libro.getIsbn(), PRESTAMISTA, formatDate.parse(FECHA_SOLICITUD));
		
		
		
		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertEquals(repositorioPrestamo.obtener(libro.getIsbn()).getFechaEntregaMaxima(),
				formatDate.parse(FECHA_MAXIMA));
		
	}
	
	@Test
	public void PrestarLibroISBNMaximaFechaNullTest() throws ParseException {

		// arrange
		Libro libro = new LibroTestDataBuilder().conIsbn(ISBN_MENOR_30).build();				
		repositorioLibros.agregar(libro);		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		// act
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		blibliotecario.prestar(libro.getIsbn(), PRESTAMISTA, formatDate.parse(FECHA_SOLICITUD));
		
		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNull(repositorioPrestamo.obtener(libro.getIsbn()).getFechaEntregaMaxima());
		
	}
		
}
