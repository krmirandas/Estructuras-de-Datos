package mx.unam.ciencias.edd;

/**
 * <p>Clase para árboles AVL.</p>
 *
 * <p>Un árbol AVL cumple que para cada uno de sus vértices, la diferencia entre
 * la áltura de sus subárboles izquierdo y derecho está entre -1 y 1.</p>
 */
public class ArbolAVL<T extends Comparable<T>> extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices de árboles AVL. La única diferencia
     * con los vértices de árbol binario, es que tienen una variable de clase
     * para la altura del vértice.
     */
    protected class VerticeAVL extends ArbolBinario<T>.Vertice {

        /** La altura del vértice. */
        public int altura;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeAVL(T elemento) {
            super(elemento);
            altura = 0;
        }

        /**
         * Regresa una representación en cadena del vértice AVL.
         * @return una representación en cadena del vértice AVL.
         */
        public String toString() {
            return String.format("%s %d/%d", elemento.toString(), altura, balance(this));
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param o el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeAVL}, su elemento es igual al elemento de éste
         *         vértice, los descendientes de ambos son recursivamente
         *         iguales, y las alturas son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object o) {
            if (o == null || raiz == null || getClass() != o.getClass())
                return false;
            @SuppressWarnings("unchecked") VerticeAVL vertice = (VerticeAVL)o;
            return raiz.get().equals(vertice.get()) && verticeAVL(raiz).altura == vertice.altura
                   && equals(verticeAVL(raiz.izquierdo), verticeAVL(vertice.izquierdo))
                   && equals(verticeAVL(raiz.derecho), verticeAVL(vertice.derecho));
        }

        private boolean equals(VerticeAVL a, VerticeAVL b) {
            //En el caso de que vertices de un nodo y ambos no tengas hijos.
            if (a == null && b == null)
                return true;
            //Si los vertices hijos son diferentes.
            else if (a != null && b == null || a == null && b != null)
                return false;
            //Compara el elemento y despues a sus hijos por izquierda y
            return a.get().equals(b.get()) && verticeAVL(a).altura == b.altura
                   && equals(verticeAVL(a.izquierdo), verticeAVL(b.izquierdo))
                   && equals(verticeAVL(a.derecho), verticeAVL(b.derecho));
        }
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol girándolo como
     * sea necesario. La complejidad en tiempo del método es <i>O</i>(log
     * <i>n</i>) garantizado.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        rebalanceo(verticeAVL(ultimoAgregado));
    }

    private void rebalanceo(VerticeAVL vertice) {
        if (vertice == null)
            return;

        cambiaAltura(vertice);

        if (balance(vertice) == -2) {
            if (balance(verticeAVL(vertice.derecho)) == 1)
                giraDerechaAVL(verticeAVL(vertice.derecho));

            giraIzquierdaAVL(vertice);
        } else if (balance(vertice) == 2) {
            if (balance(verticeAVL(vertice.izquierdo)) == -1)
                giraIzquierdaAVL(verticeAVL(vertice.izquierdo));

            giraDerechaAVL(vertice);
        }
        
        rebalanceo(verticeAVL(vertice.padre));
    }

    private void cambiaAltura(VerticeAVL vertice) {
        vertice.altura = getAlturaCalculada(vertice);
    }

    /**
     * Regresa la altura del vértice AVL.
     * @param vertice el vértice del que queremos la altura.
     * @return la altura del vértice AVL.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeAVL}.
     */
    public int getAltura(VerticeArbolBinario<T> vertice) {
        return vertice == null ? -1 : verticeAVL(vertice).altura;
    }

    private int getAlturaCalculada(VerticeAVL vertice) {
        return 1 + Math.max(getAltura(verticeAVL(vertice.izquierdo)), getAltura(verticeAVL(vertice.derecho)));
    }

    private int balance(VerticeAVL vertice) {
        return getAltura(verticeAVL(vertice.izquierdo)) - getAltura(verticeAVL(vertice.derecho));
    }

    private void giraIzquierdaAVL(VerticeAVL vertice){
        super.giraIzquierda(vertice);
        cambiaAltura(vertice);
        cambiaAltura(verticeAVL(vertice.padre));
    }

    private void giraDerechaAVL(VerticeAVL vertice){
        super.giraDerecha(vertice);
        cambiaAltura(vertice);
        cambiaAltura(verticeAVL(vertice.padre));
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y gira el árbol como sea necesario para rebalancearlo. La
     * complejidad en tiempo del método es <i>O</i>(log <i>n</i>) garantizado.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeAVL vertice = verticeAVL(busca(raiz, elemento));

        if (vertice == null)
            return;
        //Cuando es una arbol que tiene izquierda, me tomo el maximo subarbol
        //izquierdo para intercambiar los contenidos con el maximo y el
        //elemento a eliminar, para no lidiar con las referencias. Y de esta
        //forma cuando el vertice que quiero eliminar o es hoja o es esta en un "chorizo".
        if (vertice.hayIzquierdo()) {
            //Vertice auxiliar que apunte a el elemento a eliminar.
            VerticeAVL aux = vertice;
            //Vertice a eliminar igual al maximo subarbol.
            vertice = verticeAVL(maximoEnSubarbol(vertice.izquierdo));
            //Intercambio elementos
            aux.elemento = vertice.elemento;
        }
        if (esHoja(vertice))
            eliminaHoja(vertice);
        else
            subirHijo(vertice);
        
        rebalanceo(verticeAVL(vertice.padre));
        elementos--;
    }

    private void eliminaHoja(VerticeAVL vertice) {
        //Si es la raiz, pone todo en null.
        if (vertice == raiz)
            raiz = ultimoAgregado = null;
        //En otro caso solomante corta la conexion con las hojas.
        else if (esHijoIzquierdo(vertice))
            vertice.padre.izquierdo = null;
        else
            vertice.padre.derecho = null;
    }

    private void subirHijo(VerticeAVL vertice) {
        if (vertice.hayIzquierdo())
            //En este caso solamente sube y elimina el elemento que esta en la raiz.
            if (vertice == raiz) {
                raiz = vertice.izquierdo;
                raiz.padre = null;
            } else {
                //El que se quiere elimiar esta entre vertices.
                vertice.izquierdo.padre = vertice.padre;
                if (esHijoIzquierdo(vertice))
                    vertice.padre.izquierdo = vertice.izquierdo;
                else
                    vertice.padre.derecho = vertice.izquierdo;
            }
        //En el caso de que se todo un "chorizo" con hijo(s) derechos.
        else
            //En este caso solamente sube y elimina el elemento que esta en la raiz.
            if (vertice == raiz) {
                raiz = raiz.derecho;
                raiz.padre = null;
            } else {
                //El que se quiere elimiar esta entre vertices.
                vertice.derecho.padre = vertice.padre;
                if (esHijoIzquierdo(vertice))
                    vertice.padre.izquierdo = vertice.derecho;
                else
                    vertice.padre.derecho = vertice.derecho;
            }
    }


    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la derecha por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la izquierda por el " +
                                                "usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL
     * no pueden ser girados a la izquierda por los usuarios de la clase, porque
     * se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles AVL no  pueden " +
                                                "girar a la derecha por el " +
                                                "usuario.");
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link VerticeAVL}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeAVL(elemento);
    }

    /**
     * Convierte el vértice (visto como instancia de {@link
     * VerticeArbolBinario}) en vértice (visto como instancia de {@link
     * VerticeAVL}). Método auxililar para hacer esta audición en un único
     * lugar.
     * @param vertice el vértice de árbol binario que queremos como vértice AVL.
     * @return el vértice recibido visto como vértice AVL.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeAVL}.
     */
    protected VerticeAVL verticeAVL(VerticeArbolBinario<T> vertice) {
        return (VerticeAVL)vertice;
    }

}
