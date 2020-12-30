package mx.unam.ciencias.edd.proyecto1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

import mx.unam.ciencias.edd.Lista;

/**
 * <p>Proyecto 1: Ordenador lexicografico.</p>
 
 * <p>Programa ordenador lexicográfico que funciona con uno o más archivos de texto
 * o la entrada estándar, y que imprime su salida en la salida estándar.</p>
 *
 */
public class Proyecto1 {

	static final String BANDERA_REVERSA = "-r";
	static final String MENSAJE_ERROR_ENTRADA_ESTANDAR = "Hubo un error con la entrada :(";
	static final String MENSAJE_ERROR_LECTURA_ARCHIVOS = "Hubo un error el leer archivo(s) :(";

	/** Si se reciben archivos como argumentos se agrgaran en esta lista. */
	static Lista<String> archivosLista = new Lista<>();
	/** Las lineas del parrafo recibidas. */
	static Lista<Cadena> parrafoLista = new Lista<>();
	/** Booleano que nos indicara si la bandera se activo. */
	static boolean esReversa = false;

	public static void main(String[] args) {

		checaArgumentos(args);

		boolean esEntradaEstandar = entradaEstandar(args, esReversa);
		String input;

		if (esEntradaEstandar)
			try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
				while ((input = br.readLine()) != null)
					parrafoLista.agrega(new Cadena(input));
			} catch (IOException io) {
				System.out.println(MENSAJE_ERROR_ENTRADA_ESTANDAR);
				System.exit(-1);
			}
		else
			for (String archivos : archivosLista)
				try (BufferedReader br = new BufferedReader(new FileReader(archivos))) {
					while ((input = br.readLine()) != null)
						parrafoLista.agrega(new Cadena(input));
				} catch (IOException io) {
					System.out.println(MENSAJE_ERROR_LECTURA_ARCHIVOS);
					System.exit(-1);
				}

		for (Cadena cad : esReversa ?
		        Lista.mergeSort(parrafoLista).reversa() : Lista.mergeSort(parrafoLista))
			System.out.println(cad);
	}

	/**
	 * Metodo que analiza los argumentos recibidos y nos indicara si tiene alguna bandera,
	 * y en caso de que en los argumento tuviera archivos, los guardaria en una lista.
	 * @param args Argumentos recibidos de la consola.
	 */
	private static void checaArgumentos(String[] args) {
		for (String str : args) {
			if (str.equals(BANDERA_REVERSA))
				esReversa = true;
			if (!str.equals(BANDERA_REVERSA))
				archivosLista.agrega(str);
		}
	}

	/**
	 * Metodo que nos dice como se comportara nuestro prgrama, ya sea leyendo un
	 * archivo o leyendo el texto con la entrada estandar.
	 * @param args Argumentos recibidos de la consola.
	 * @param esReversa Booleano que nos indica si tiene activada la bandera.
	 * @return <tt>true</tt> Si es entrada estandar,
	 *         <tt>false</tt> si leera un archivo(s).
	 */
	private static boolean entradaEstandar(String[] args, boolean esReversa) {
		if (args.length == 0 || (args.length == 1 && esReversa))
			return true;
		return false;
	}

}