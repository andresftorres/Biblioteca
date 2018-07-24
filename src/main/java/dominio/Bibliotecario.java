package dominio;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;

public class Bibliotecario {
	
	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	public static final String EL_ISBN_ES_PALINDROMO = "Los libros palíndromos solo se pueden utilizar en la biblioteca";
	public static final String EL_LIBRO_NO_EXISTE = "El libro no se encuentra registrado en la biblioteca.";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}
	
	/*	Realiza el prestamo de un libro con las verificaciones pertinentes:
	 *	¿El libro esta prestado? : No esta disponible
	 *	¿El ISBN es palindrome? : No se presta
	 *	¿La suma del ISBN es mayor a 30? : Se calcula fecha maxima de entrega    
	 */
	public void prestar(String isbn, String nombreUsuario, Date fechaPrestamo) {
		
		Libro	libroaPrestar;
		
		//El libro esta prestado? Lanza excepción cuando esta prestado.
		if(esPrestado(isbn))
			throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		
		//El libro permite prestamos? Es palindromo su ISBN? Lanza excepción si no permite prestamos.
		if(esPalindromo(isbn))
			throw new PrestamoException(EL_ISBN_ES_PALINDROMO);
			
		//Obtiene el libro del repositorio
		try{
			libroaPrestar = repositorioLibro.obtenerPorIsbn(isbn);
		}catch (NoResultException e) {
			throw new PrestamoException(EL_LIBRO_NO_EXISTE);
		}
		
		Date	fechaEntregaMaxima;
		
		//Si la suma de los números de ISBN supero el número 30,
		//se debe calcular una fecha maxia de entrega.
		if( sumaIsbn(isbn) ){
			
			//Calcula la fecha maxia a partir de la fecha de solicitud.			
			fechaEntregaMaxima = calcularFechaMaxima(fechaPrestamo);
		}else{
			fechaEntregaMaxima = null;
		}
		
		//Registra el prestamo en la persistencia
		Prestamo prestamoActual = new Prestamo(fechaPrestamo, libroaPrestar, fechaEntregaMaxima, nombreUsuario);
		//Guarda el prestamo 
		repositorioPrestamo.agregar(prestamoActual);
				
	}

	/*
	 * Identifica si el ISBN dado corresponde a un libro prestado.
	 * True: El libro esta prestado.
	 * False: El libro esta disponible.
	 */
	public boolean esPrestado(String isbn) {		
		//Obtiene el libro prestado que tenga el ISBN dado
		Libro LibroActual = repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn) ;
		
		//EL libro es prestado?
		if(LibroActual != null)
			return true;
			
		return false;		
	}
	
	/*
	 * Identifica si el ISBN dado es palindromo.
	 * True: El isbn es palindromo.
	 * False: El isbn no es palindromo.
	 */
	public boolean esPalindromo(String isbn) {
		//Longitud total
		int len = isbn.length();
		//Mitad del ISBN
		int mitad = len / 2;
		
		//Se recorre la sección desde el inicio de la palabra hasta la mitad.
		for (int i = 0; i < mitad; i++) {
			//Se valida que la palabra de un extremo coincida con la del extremo contrario.
			//Si no cumple alguno, ya no es un palindromo
			if (isbn.charAt(i) != isbn.charAt(len - 1 - i))
				return false;			
		}
		return true;
	}
	
	/*
	 * Valida si la suma de los caracteres numericos del ISBN de un libro suma
	 * mas de 30
	 * True: El isbn suma mas de 30.
	 * False: El isbn no suma mas de 30.
	 */
	public boolean sumaIsbn(String isbn) {
		
		int suma = 0;
		int len = isbn.length();
		char currentChar;
		
		//Recorre cada caracter del ISBN
		for (int i = 0; i < len; i++) {
			currentChar = isbn.charAt(i);
			
			//Valida si el caracter es un digito intentado convertirlo en un numero entero.					
			try{
				suma += Integer.parseInt(""+currentChar);
			}catch (NumberFormatException e) {
				//Caso cuando se intenta convertir un caracter a un número
			}			
		}
		//Valor acumulado mayor a 30.
		if(suma > 30){
			return true;
		};
		
		return false;		
	}
	
	/*
	 * Calcula la fecha maxima de entrega a patir de la fecha de prestamo
	 * teniendo en cuenta 15 días a partir de la misma y sin contar domingos
	 * 
	 * Date: Fecha maxima de entrega
	 */
	public Date calcularFechaMaxima(Date fechaPrestamo) {
		//Se crea objeto Calendar, este facilita la adición de dias.
		Calendar calculoFechaEntrega = Calendar.getInstance();
		//Se establece la fecha actual como la fecha de solicitud
		calculoFechaEntrega.setTime(fechaPrestamo);
		
		//Se valida que dias es hoy
		int day = calculoFechaEntrega.get(Calendar.DAY_OF_WEEK);
		
		/*
		 * El proceso es el siguiente:
		 * Los dias domingo se deben saltar. Se sabe la cantidad de saltos que alcanza a hacer
		 * depende de que día se solicito el prestamo.
		 * Apartir de esto se sabe que desde los días viernes y sabado se harán 3 saltos y el 
		 * resto de días solo 2 saldos.
		 * 
		 */
		if(day == Calendar.FRIDAY || day == Calendar.SATURDAY){
			calculoFechaEntrega.add(Calendar.DAY_OF_YEAR, 17);
		}else{
			calculoFechaEntrega.add(Calendar.DAY_OF_YEAR, 16);
		}
		
		//Se extrae la fecha maxima de prestamo y se retorna.
		return calculoFechaEntrega.getTime();		
	}
	
}
