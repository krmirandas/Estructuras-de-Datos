package mx.unam.ciencias.edd.proyecto1;

import java.text.Collator;

/**
 * <p>Clase cadena que recibe un String.</p>
 *
 * <p> La implementacion de esta clase es por que se necesitaba comparar las
 * cadenas de forma diferente, es decir, ignorando si es mayuscula o
 * minuscula e ignorando caracteres especiales. Para ello se hizo que
 * implementara {@link Comparable} para sobreescribir el metodo
 * {@link Comparable#compareTo(Object)}. </p>
 *
 */
public class Cadena implements Comparable<Cadena> {

	String cadena;

	public Cadena(String cadena) {
		this.cadena = cadena;
	}

	@Override public String toString() {
		return cadena;
	}

	@Override public int compareTo(Cadena cad) {
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.PRIMARY);
		return collator.compare(cadena.replaceAll("\\P{L}+", ""),
		                        cad.toString().replaceAll("\\P{L}+", ""));
	}

}