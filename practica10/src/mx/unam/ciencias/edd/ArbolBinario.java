package mx.unam.ciencias.edd;

import java.util.NoSuchElementException;

/**
 * <p>Clase abstracta para árboles binarios genéricos.</p>
 * <p>
 * <p>La clase proporciona las operaciones básicas para árboles binarios, pero
 * deja la implementación de varias en manos de las subclases concretas.</p>
 */
public abstract class ArbolBinario<T> implements Coleccion<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class Vertice implements VerticeArbolBinario<T> {

        /**
         * El elemento del vértice.
         */
        public T elemento;
        /**
         * El padre del vértice.
         */
        public Vertice padre;
        /**
         * El izquierdo del vértice.
         */
        public Vertice izquierdo;
        /**
         * El derecho del vértice.
         */
        public Vertice derecho;

        /**
         * Constructor único que recibe un elemento.
         *
         * @param elemento el elemento del vértice.
         */
        public Vertice(T elemento) {
            this.elemento = elemento;
        }

        /**
         * Regresa una representación en cadena del vértice.
         *
         * @return una representación en cadena del vértice.
         */
        public String toString() {
            return elemento.toString();
        }

        /**
         * Nos dice si el vértice tiene un padre.
         *
         * @return <tt>true</tt> si el vértice tiene padre,
         * <tt>false</tt> en otro caso.
         */
        @Override public boolean hayPadre() {
            return padre != null;
        }

        /**
         * Nos dice si el vértice tiene un izquierdo.
         *
         * @return <tt>true</tt> si el vértice tiene izquierdo,
         * <tt>false</tt> en otro caso.
         */
        @Override public boolean hayIzquierdo() {
            return izquierdo != null;
        }

        /**
         * Nos dice si el vértice tiene un derecho.
         *
         * @return <tt>true</tt> si el vértice tiene derecho,
         * <tt>false</tt> en otro caso.
         */
        @Override public boolean hayDerecho() {
            return derecho != null;
        }

        /**
         * Regresa el padre del vértice.
         *
         * @return el padre del vértice.
         * @throws NoSuchElementException si el vértice no tiene padre.
         */
        @Override public VerticeArbolBinario<T> getPadre() {
            if (padre == null)
                throw new NoSuchElementException();
            return padre;
        }

        /**
         * Regresa el izquierdo del vértice.
         *
         * @return el izquierdo del vértice.
         * @throws NoSuchElementException si el vértice no tiene izquierdo.
         */
        @Override public VerticeArbolBinario<T> getIzquierdo() {
            if (izquierdo == null)
                throw new NoSuchElementException();
            return izquierdo;
        }

        /**
         * Regresa el derecho del vértice.
         *
         * @return el derecho del vértice.
         * @throws NoSuchElementException si el vértice no tiene derecho.
         */
        @Override public VerticeArbolBinario<T> getDerecho() {
            if (derecho == null)
                throw new NoSuchElementException();
            return derecho;
        }

        /**
         * Regresa el elemento al que apunta el vértice.
         *
         * @return el elemento al que apunta el vértice.
         */
        @Override public T get() {
            return elemento;
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>. Las clases que extiendan {@link Vertice} deben
         * sobrecargar el método {@link Vertice#equals}.
         *
         * @param o el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         * {@link Vertice}, su elemento es igual al elemento de éste
         * vértice, y los descendientes de ambos son recursivamente
         * iguales; <code>false</code> en otro caso.
         */
        @Override public boolean equals(Object o) {
            if (o == null || raiz == null || getClass() != o.getClass())
                return false;
            @SuppressWarnings("unchecked") Vertice vertice = (Vertice) o;
            return raiz.get().equals(vertice.get()) && equals(raiz.izquierdo, vertice.izquierdo)
                    && equals(raiz.derecho, vertice.derecho);
        }

        private boolean equals(Vertice i, Vertice d) {
            //En el caso de que vertices de un nodo y ambos no tengas hijos.
            if (i == null && d == null)
                return true;
                //Si los vertices hijos son diferentes.
            else if (i != null && d == null || i == null && d != null)
                return false;
            //Compara el elemento y despues a sus hijos por izquierda y
            return i.get().equals(d.get()) && equals(i.izquierdo, d.izquierdo)
                    && equals(i.derecho, d.derecho);
        }
    }

    /**
     * La raíz del árbol.
     */
    protected Vertice raiz;
    /**
     * El número de elementos
     */
    protected int elementos;
    /**
     * El vértice del último elemento agegado.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros.
     */
    public ArbolBinario() {
    }

    /**
     * Construye un árbol binario a partir de una colección. El árbol binario
     * tiene los mismos elementos que la colección recibida.
     *
     * @param coleccion la colección a partir de la cual creamos el árbol
     *                  binario.
     */
    public ArbolBinario(Coleccion<T> coleccion) {
        for (T e : coleccion)
            agrega(e);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link Vertice}. Para
     * crear vértices se debe utilizar este método en lugar del operador
     * <code>new</code>, para que las clases herederas de ésta puedan
     * sobrecargarlo y permitir que cada estructura de árbol binario utilice
     * distintos tipos de vértices.
     *
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    protected Vertice nuevoVertice(T elemento) {
        return new Vertice(elemento);
    }

    /**
     * Regresa la profundidad del árbol. La profundidad de un árbol es la
     * longitud de la ruta más larga entre la raíz y una hoja.
     *
     * @return la profundidad del árbol.
     */
    public int profundidad() {
        return profundidad(raiz);
    }

    private int profundidad(Vertice vertice) {
        if (vertice == null)
            return -1;
        return 1 + Math.max(profundidad(vertice.izquierdo), profundidad(vertice.derecho));
    }

    /**
     * Regresa el número de elementos que se han agregado al árbol.
     *
     * @return el número de elementos en el árbol.
     */
    public int getElementos() {
        return elementos;
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al árbol.
     *
     * @return el vértice que contiene el último elemento agregado al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Nos dice si un elemento está en el árbol binario.
     *
     * @param elemento el elemento que queremos comprobar si está en el árbol.
     * @return <code>true</code> si el elemento está en el árbol;
     * <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return busca(elemento) != null;
    }

    /**
     * Busca un elemento en el árbol. Si lo encuentra, regresa el vértice que lo
     * contiene; si no, regresa <tt>null</tt>.
     *
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene el elemento buscado si lo encuentra;
     * <tt>null</tt> en otro caso.
     */
    public VerticeArbolBinario<T> busca(T elemento) {
        /* Busca recursivamente. */
        return busca(raiz, elemento);
    }

    /**
     * Busca recursivamente un elemento, a partir del vértice recibido.
     *
     * @param vertice  el vértice a partir del cuál comenzar la búsqueda. Puede
     *                 ser <code>null</code>.
     * @param elemento el elemento a buscar a partir del vértice.
     * @return el vértice que contiene el elemento a buscar, si se encuentra en
     * el árbol; <code>null</code> en otro caso.
     */
    protected Vertice busca(Vertice vertice, T elemento) {
        if (vertice == null)
            return null;
        if (vertice.get().equals(elemento))
            return vertice;
        Vertice mi = busca(vertice.izquierdo, elemento);
        Vertice md = busca(vertice.derecho, elemento);
        return mi != null ? mi : md;
    }

    /**
     * Regresa el vértice que contiene la raíz del árbol.
     *
     * @return el vértice que contiene la raíz del árbol.
     * @throws NoSuchElementException si el árbol es vacío.
     */
    public VerticeArbolBinario<T> raiz() {
        if (raiz == null)
            throw new NoSuchElementException();
        return raiz;
    }

    /**
     * Regresa el número de elementos en el árbol.
     *
     * @return el número de elementos en el árbol.
     */
    @Override public boolean esVacio() {
        return raiz == null;
    }

    /**
     * Compara el árbol con un objeto.
     *
     * @param o el objeto con el que queremos comparar el árbol.
     * @return <code>true</code> si el objeto recibido es un árbol binario y los
     * árboles son iguales; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked") ArbolBinario<T> arbol = (ArbolBinario<T>) o;
        return esVacio() || raiz.equals(arbol.raiz);
    }

    /**
     * Regresa una representación en cadena del árbol.
     *
     * @return una representación en cadena del árbol.
     */
    @Override public String toString() {
        if (elementos == 0)
            return "";
        /* Necesitamos la profundidad para saber cuántas ramas puede haber. */
        int p = profundidad() + 1;
        /* true == dibuja rama, false == dibuja espacio. */
        boolean[] rama = new boolean[p];
        for (int i = 0; i < p; i++)
            /* Al inicio, no dibujamos ninguna rama. */
            rama[i] = false;
        String s = aCadena(raiz, 0, rama);
        return s.substring(0, s.length() - 1);
    }

    /* Método auxiliar recursivo que hace todo el trabajo. */
    private String aCadena(Vertice vertice, int nivel, boolean[] rama) {
        /* Primero que nada agregamos el vertice a la cadena. */
        String s = vertice + "\n";
        /* A partir de aquí, dibujamos rama en este nivel. */
        rama[nivel] = true;
        if (vertice.izquierdo != null && vertice.derecho != null) {
            /* Si hay vertice izquierdo Y derecho, dibujamos ramas o
             * espacios. */
            s += espacios(nivel, rama);
            /* Dibujamos el conector al hijo izquierdo. */
            s += "├─›";
            /* Recursivamente dibujamos el hijo izquierdo y sus
               descendientes. */
            s += aCadena(vertice.izquierdo, nivel + 1, rama);
            /* Dibujamos ramas o espacios. */
            s += espacios(nivel, rama);
            /* Dibujamos el conector al hijo derecho. */
            s += "└─»";
            /* Como ya dibujamos el último hijo, ya no hay rama en este
               nivel. */
            rama[nivel] = false;
            /* Recursivamente dibujamos el hijo derecho y sus descendientes. */
            s += aCadena(vertice.derecho, nivel + 1, rama);
        } else if (vertice.izquierdo != null) {
            /* Dibujamos ramas o espacios. */
            s += espacios(nivel, rama);
            /* Dibujamos el conector al hijo izquierdo. */
            s += "└─›";
            /* Como ya dibujamos el último hijo, ya no hay rama en este
               nivel. */
            rama[nivel] = false;
            /* Recursivamente dibujamos el hijo izquierdo y sus
               descendientes. */
            s += aCadena(vertice.izquierdo, nivel + 1, rama);
        } else if (vertice.derecho != null) {
            /* Dibujamos ramas o espacios. */
            s += espacios(nivel, rama);
            /* Dibujamos el conector al hijo derecho. */
            s += "└─»";
            /* Como ya dibujamos el último hijo, ya no hay rama en este
               nivel. */
            rama[nivel] = false;
            /* Recursivamente dibujamos el hijo derecho y sus descendientes. */
            s += aCadena(vertice.derecho, nivel + 1, rama);
        }
        return s;
    }

    /* Dibuja los espacios (incluidas las ramas, de ser necesarias) que van
       antes de un vértice. */
    private String espacios(int n, boolean[] rama) {
        String s = "";
        for (int i = 0; i < n; i++)
            if (rama[i])
                /* Rama: dibújala. */
                s += "│  ";
            else
                /* No rama: dibuja espacio. */
                s += "   ";
        return s;
    }

    /**
     * Convierte el vértice (visto como instancia de {@link
     * VerticeArbolBinario}) en vértice (visto como instancia de {@link
     * Vertice}). Método auxiliar para hacer esta audición en un único lugar.
     *
     * @param vertice el vértice de árbol binario que queremos como vértice.
     * @return el vértice recibido visto como vértice.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *                            Vertice}.
     */
    protected Vertice vertice(VerticeArbolBinario<T> vertice) {
        /* No necesitamos suprimir advertencias porque Vertice no es
         * genérica. */
        Vertice v = (Vertice) vertice;
        return v;
    }

}
