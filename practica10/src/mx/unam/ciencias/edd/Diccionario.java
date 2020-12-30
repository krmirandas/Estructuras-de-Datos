package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para diccionarios (<em>hash tables</em>). Un diccionario generaliza el
 * concepto de arreglo, permitiendo (en general, dependiendo de qué tan bueno
 * sea su método para generar picadillos) agregar, eliminar, y buscar valores en
 * tiempo <i>O</i>(1) (amortizado) en cada uno de estos casos.
 */
public class Diccionario<K, V> implements Iterable<V> {

    /** Máxima carga permitida por el diccionario. */
    public static final double MAXIMA_CARGA = 0.72;

    /* Clase para las entradas del diccionario. */
    private class Entrada {

        /* La llave. */
        public K llave;
        /* El valor. */
        public V valor;

        /* Construye una nueva entrada. */
        public Entrada(K llave, V valor) {
            this.llave = llave;
            this.valor = valor;
        }
    }

    /* Clase privada para iteradores de diccionarios. */
    private class Iterador implements Iterator<V> {

        /* En qué lista estamos. */
        private int indice;
        /* Iterador auxiliar. */
        private Iterator<Diccionario<K, V>.Entrada> iterador;

        /* Construye un nuevo iterador, auxiliándose de las listas del
         * diccionario. */
        public Iterador() {
            Lista<Entrada> l = new Lista<>();

            for (int i = 0; i < entradas.length; i++)
                if (entradas[i] != null)
                    for (Entrada e : entradas[i])
                        l.agrega(e);

            this.iterador = l.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        public V next() {
            return iterador.next().valor;
        }

        /* No lo implementamos: siempre lanza una excepción. */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* Tamaño mínimo; decidido arbitrariamente a 2^6. */
    private static final int MIN_N = 64;

    /* Máscara para no usar módulo. */
    private int mascara;
    /* Picadillo. */
    private Picadillo<K> picadillo;
    /* Nuestro diccionario. */
    private Lista<Entrada>[] entradas;
    /* Número de valores*/
    private int elementos;

    /* Truco para crear un arreglo genérico. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private Lista<Entrada>[] nuevoArreglo(int n) {
        Lista[] arreglo = new Lista[n];
        return (Lista<Entrada>[])arreglo;
    }

    /**
     * Construye un diccionario con un tamaño inicial y picadillo
     * predeterminados.
     */
    public Diccionario() {
        this(MIN_N, (K o) -> o.hashCode());
    }

    /**
     * Construye un diccionario con un tamaño inicial definido por el usuario, y
     * un picadillo predeterminado.
     * @param tam el tamaño a utilizar.
     */
    public Diccionario(int tam) {
        this(tam, (K o) -> o.hashCode());
    }

    /**
     * Construye un diccionario con un tamaño inicial predeterminado, y un
     * picadillo definido por el usuario.
     * @param picadillo el picadillo a utilizar.
     */
    public Diccionario(Picadillo<K> picadillo) {
        this(MIN_N, picadillo);
    }

    /**
     * Construye un diccionario con un tamaño inicial, y un método de picadillo
     * definidos por el usuario.
     * @param tam el tamaño del diccionario.
     * @param picadillo el picadillo a utilizar.
     */
    public Diccionario(int tam, Picadillo<K> picadillo) {
        this.picadillo = picadillo;
        this.mascara = tam < MIN_N ? mascara(MIN_N) : mascara(tam);
        this.entradas = nuevoArreglo(mascara + 1);
    }

    private int mascara(int n) {
        int m = 1;
        while (m <= n)
            m = (m << 1) | 1;
        m = (m << 1) | 1;
        return m;
    }

    /**
     * Agrega un nuevo valor al diccionario, usando la llave proporcionada. Si
     * la llave ya había sido utilizada antes para agregar un valor, el
     * diccionario reemplaza ese valor con el recibido aquí.
     * @param llave la llave para agregar el valor.
     * @param valor el valor a agregar.
     * @throws IllegalArgumentException assertFalsesi la llave o el valor son nulos.
     */
    public void agrega(K llave, V valor) {
        if (llave == null || valor == null)
            throw new IllegalArgumentException();

        int indice = calculaIndice(llave);
        Lista<Entrada> l = obtenerLista(indice);
        Entrada e = buscaEntradaConLlave(l, llave);
        if (e == null) {
            e = new Entrada(llave, valor);
            entradas[indice].agrega(e);
            elementos++;
        } else {
            e.valor = valor;
        }
        if (carga() >= MAXIMA_CARGA)
            duplicaArreglo();
    }

    private int calculaIndice(K llave) {
        return picadillo.picadillo(llave) & mascara;
    }

    private Lista<Entrada> obtenerLista(int i) {
        if (entradas[i] == null)
            entradas[i] = new Lista<>();
        return entradas[i];
    }

    private Entrada buscaEntradaConLlave(Lista<Entrada> l, K llave) {
        for (Entrada e : l)
            if (e.llave.equals(llave))
                return e;
        return null;
    }

    private void duplicaArreglo() {
        mascara = mascara(entradas.length * 2);
        Lista<Entrada>[] nuevaEntrada = nuevoArreglo(mascara + 1);
        for (int i = 0; i < entradas.length; i++) {
            if (entradas[i] != null) {
                for (Entrada e : entradas[i]) {
                    int indice = calculaIndice(e.llave);
                    nuevaEntrada[indice] = nuevaEntrada(nuevaEntrada[indice], e);
                }
            }
        }
        entradas = nuevaEntrada;
    }

    private Lista<Entrada> nuevaEntrada(Lista<Entrada> le, Entrada e) {
        Lista<Entrada> l;
        if (le == null)
            l = new Lista<Entrada>();
        else
            l = le;
        l.agrega(e);
        return l;
    }

    /**
     * Regresa el valor del diccionario asociado a la llave proporcionada.
     * @param llave la llave para buscar el valor.
     * @return el valor correspondiente a la llave.
     * @throws NoSuchElementException si la llave no está en el diccionario.
     */
    public V get(K llave) {
        int i = calculaIndice(llave);
        if (entradas[i] == null)
            throw new NoSuchElementException();
        for (Entrada e : entradas[i]) {
            if (e.llave.equals(llave))
                return e.valor;
        }
        throw new NoSuchElementException();
    }

    /**
     * Nos dice si una llave se encuentra en el diccionario.
     * @param llave la llave que queremos ver si está en el diccionario.
     * @return <tt>true</tt> si la llave está en el diccionario,
     *         <tt>false</tt> en otro caso.
     */
    public boolean contiene(K llave) {
        int i = calculaIndice(llave);
        if (entradas[i] == null)
            return false;
        for (Entrada e : entradas[i]) {
            if (e.llave.equals(llave))
                return true;
        }
        return false;
    }

    /**
     * Elimina el valor del diccionario asociado a la llave proporcionada.
     * @param llave la llave para buscar el valor a eliminar.
     * @throws NoSuchElementException si la llave no se encuentra en
     *         el diccionario.
     */
    public void elimina(K llave) {
        int i = calculaIndice(llave);
        if (entradas[i] == null)
            throw new NoSuchElementException();
        Entrada e = buscaEntradaConLlave(entradas[i], llave);
        entradas[i].elimina(e);
        elementos--;
    }

    /**
     * Regresa una lista con todas las llaves con valores asociados en el
     * diccionario. La lista no tiene ningún tipo de orden.
     * @return una lista con todas las llaves.
     */
    public Lista<K> llaves() {
        Lista<K> ll = new Lista<>();
        for (int i = 0; i < entradas.length; i++) {
            if (entradas[i] != null) {
                for (Entrada e : entradas[i]) {
                    ll.agrega(e.llave);
                }
            }
        }
        return ll;
    }

    /**
     * Regresa una lista con todos los valores en el diccionario. La lista no
     * tiene ningún tipo de orden.
     * @return una lista con todos los valores.
     */
    public Lista<V> valores() {
        Lista<V> lv = new Lista<>();
        for (int i = 0; i < entradas.length; i++) {
            if (entradas[i] != null) {
                for (Entrada e : entradas[i]) {
                    lv.agrega(e.valor);
                }
            }
        }
        return lv;
    }

    /**
     * Nos dice cuántas colisiones hay en el diccionario.
     * @return cuántas colisiones hay en el diccionario.
     */
    public int colisiones() {
        int colisiones = 0;
        for (int i = 0; i < entradas.length; i++) {
            if (entradas[i] != null) {
                colisiones += entradas[i].getElementos() - 1;
            }
        }
        return colisiones;
    }

    /**
     * Nos dice el máximo número de colisiones para una misma llave que tenemos
     * en el diccionario.
     * @return el máximo número de colisiones para una misma llave.
     */
    public int colisionMaxima() {
        int colision = 0;
        int colisionMaxima = 0;
        for (int i = 0; i < entradas.length; i++) {
            if (entradas[i] != null) {
                colision = entradas[i].getElementos() - 1;
                if (colision > colisionMaxima)
                    colisionMaxima = colision;
            }
        }
        return colisionMaxima;
    }

    /**
     * Nos dice la carga del diccionario.
     * @return la carga del diccionario.
     */
    public double carga() {
        double carga = (elementos + 0.0) / entradas.length;
        return carga;
    }

    /**
     * Regresa el número de entradas en el diccionario.
     * @return el número de entradas en el diccionario.
     */
    public int getElementos() {
        return elementos;
    }

    /**
     * Nos dice si el diccionario es vacío.
     * @return <code>true</code> si el diccionario es vacío, <code>false</code>
     *         en otro caso.
     */
    public boolean esVacio() {
        return elementos == 0;
    }

    /**
     * Nos dice si el diccionario es igual al objeto recibido.
     * @param o el objeto que queremos saber si es igual al diccionario.
     * @return <code>true</code> si el objeto recibido es instancia de
     *         Diccionario, y tiene las mismas llaves asociadas a los mismos
     *         valores.
     */
    @Override public boolean equals(Object o) {
        if (!(o instanceof Diccionario))
            return false;
        @SuppressWarnings("unchecked") Diccionario<K, V> d = (Diccionario<K, V>)o;
        if (getElementos() != d.getElementos())
            return false;
        for (K k : llaves())
            if (!d.contiene(k) && !d.get(k).equals(get(k))) {
                return false;
            }
        return true;
    }

    /**
     * Regresa un iterador para iterar los valores del diccionario. El
     * diccionario se itera sin ningún orden específico.
     * @return un iterador para iterar el diccionario.
     */
    @Override public Iterator<V> iterator() {
        return new Iterador();
    }
}