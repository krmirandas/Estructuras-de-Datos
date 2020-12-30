package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<tt>null</tt>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros son autobalanceados, y por lo tanto las operaciones de
 * inserción, eliminación y búsqueda pueden realizarse en <i>O</i>(log
 * <i>n</i>).
 */
public class ArbolRojinegro<T extends Comparable<T>> extends ArbolBinarioOrdenado<T> {

    //////////////////////////////////////////////////////////////////////////////
    //Hay codigo que se podria "reducir" o simplemente hacerlas menos explicitas /
    //pero me gustaría ver mi código y en un futuro entenderle. :) :3            /
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Clase interna protegida para vértices de árboles rojinegros. La única
     * diferencia con los vértices de árbol binario, es que tienen un campo para
     * el color del vértice.
     */
    protected class VerticeRojinegro extends ArbolBinario<T>.Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
            super(elemento);
            color = Color.NINGUNO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        public String toString() {
            return String.format("%s{%s}", color == Color.ROJO ? "R" : "N", elemento.toString());
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param o el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object o) {
            if (o == null || raiz == null || getClass() != o.getClass())
                return false;
            @SuppressWarnings("unchecked") VerticeRojinegro vertice = (VerticeRojinegro) o;
            return raiz.get().equals(vertice.get()) && verticeRojinegro(raiz).color == vertice.color
                   && equals(verticeRojinegro(raiz.izquierdo), verticeRojinegro(vertice.izquierdo))
                   && equals(verticeRojinegro(raiz.derecho), verticeRojinegro(vertice.derecho));
        }

        private boolean equals(VerticeRojinegro a, VerticeRojinegro b) {
            //En el caso de que vertices de un nodo y ambos no tengas hijos.
            if (a == null && b == null)
                return true;
            //Si los vertices hijos son diferentes.
            else if (a != null && b == null || a == null && b != null)
                return false;
            //Compara el elemento y despues a sus hijos por izquierda y
            return a.get().equals(b.get()) && verticeRojinegro(a).color == b.color
                   && equals(verticeRojinegro(a.izquierdo), verticeRojinegro(b.izquierdo))
                   && equals(verticeRojinegro(a.derecho), verticeRojinegro(b.derecho));
        }
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del
     *         mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    /**
     * Convierte el vértice (visto como instancia de {@link
     * VerticeArbolBinario}) en vértice (visto como instancia de {@link
     * VerticeRojinegro}). Método auxililar para hacer esta audición en un único
     * lugar.
     * @param vertice el vértice de árbol binario que queremos como vértice
     *                rojinegro.
     * @return el vértice recibido visto como vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    private VerticeRojinegro verticeRojinegro(VerticeArbolBinario<T> vertice) {
        VerticeRojinegro v = (VerticeRojinegro)vertice;
        return v;
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        return verticeRojinegro(vertice).color;
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        VerticeRojinegro vertice = verticeRojinegro(ultimoAgregado);
        //El algoritmo dice que al agregar simpre seran vertices ROJOS. 
        vertice.color = Color.ROJO;
        rebalanceoAgrega(vertice);
    }

    /**
     * Metodo auxiliar que es llamado cuando se agrega un elemento al arbol
     * para balancear el arbol. 
     */
    private void rebalanceoAgrega(VerticeRojinegro vertice) {
        VerticeRojinegro padre, abuelo, tio;
        /** --Caso 1---
        * El padre del vertice es nulo.
        * Coloreamos el vertice de NEGRO y terminamos. */
        if (!vertice.hayPadre()) {
            raiz = vertice;
            vertice.color = Color.NEGRO;
            return;
        }
        padre = verticeRojinegro(vertice.padre);
        /** --Caso 2---
         * El color del padre es NEGRO.
         * Terminamos. */
        if (getColor(padre) == Color.NEGRO)
            return;
        //Nunca el abuelo sera nulo en este punto, ya cuando se agrege las primeras
        //veces o se llena por los dos lados, o esta en una linea.
        abuelo = verticeRojinegro(padre.padre);
        /** --Caso 3---
         * El tio es ROJO.
         * Coloreamos al padre y al tio de NEGRO, y al abuelo de ROJO y
         * hacemos recursion sobre el abuelo y terminamos. */
        tio = obtenerTio(padre, abuelo);
        if (tio != null && tio.color == Color.ROJO) {
            padre.color = tio.color = Color.NEGRO;
            abuelo.color = Color.ROJO;
            rebalanceoAgrega(abuelo);
            return;
        }
        /** --Caso 4---
         * El vertice y su padre estan cruzados.
         * Giramos sobre el padre en su direccion. 
         * Se cumple unicamente cuando solo uno es hijo izquierdo. */
        if (esHijoIzquierdo(vertice) ^ esHijoIzquierdo(padre)) {
            if (esHijoIzquierdo(padre))
                super.giraIzquierda(padre);
            else
                super.giraDerecha(padre);
            //Intercambiamos el vertice con el padre, por que cuando se hace
            //un giro el vertice es el padre y el padre ahora es el vertice.
            VerticeRojinegro aux = vertice;
            vertice = padre;
            padre = aux;
            //Preparamos para el caso 5.
        }
        /** --Caso 5---
        * Coloreamos al padre de NEGRO y al abuelo de ROJO, giramos sobre el
        * abuelo en direccion contraria del vertice. */
        padre.color = Color.NEGRO;
        abuelo.color = Color.ROJO;
        if (esHijoIzquierdo(vertice))
            super.giraDerecha(abuelo);
        else
            super.giraIzquierda(abuelo);
    }

    private VerticeRojinegro obtenerTio(VerticeRojinegro padre, VerticeRojinegro abuelo) {
        return esHijoIzquierdo(padre) ? verticeRojinegro(abuelo.derecho) :
               verticeRojinegro(abuelo.izquierdo);
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeRojinegro vertice = verticeRojinegro(busca(raiz, elemento));
        VerticeRojinegro hijo, fantasma = null;

        if (vertice == null)
            return;
        //Cuando es una arbol que tiene izquierda, me tomo el maximo subarbol
        //izquierdo para intercambiar los contenidos con el maximo y el
        //elemento a eliminar, para no lidiar con las referencias. Y de esta
        //forma cuando el vertice que quiero eliminar o es hoja o es esta en un "chorizo".
        if (vertice.hayIzquierdo()) {
            //Vertice auxiliar que apunte a el elemento a eliminar.
            VerticeRojinegro aux = vertice;
            //Vertice a eliminar igual al maximo subarbol.
            vertice = verticeRojinegro(maximoEnSubarbol(vertice.izquierdo));
            //Intercambio elementos
            aux.elemento = vertice.elemento;
        }
        //En el caso de que sea hoja, le crearemos un vertice fantasama.
        if (esHoja(vertice)){
            fantasma = verticeRojinegro(nuevoVertice(null));
            fantasma.color = Color.NEGRO;
            fantasma.padre = vertice;
            vertice.izquierdo = fantasma;
        }
        //El hijo tanto puede ser un fantasma como el hijo izquiero o derecho del que se eliminara.
        hijo = obtenerHijo(vertice);
        //Si esta en un "chorizo" unicamente sube el elemento.
        subirHijo(vertice);

        //Existen 3 casos aqui ->
        //1=>)Vertice ROJO,  Hijo NEGRO -> Solamente pintamos de NEGRO el hijo. 
        //2=>)Vertice NEGRO, Hijo ROJO  -> Solamente pintamos de NEGRO el hijo.
        //3=>)Vertice NEGRO, Hijo NEGRO -> Se pinta de negro y se REBALANCEA.
        //1,2=>)Los dos primeros casos se pinta de negro por que existe una combinacion de
        //colores, y entonces en el camino afuerzas debe de existir un NEGRO, por eso pintamos de NEGRO.
        //3->)El motivo del rebalanceo es que como hay dos negros, el nuemero de caminos para
        //todo vertice se ve alterado.
        //Vertice ROJO,  Hijo ROJO -> NO SE TOMA ESTE CASO, ya que lo vertices ROJOS no pueden tener hijos ROJOS.
        if(esNegro(vertice) && esNegro(hijo)){
            hijo.color = Color.NEGRO;
            rebalanceoElimina(hijo);
        }else 
            hijo.color = Color.NEGRO;

        mataFantasma(fantasma);

        elementos--;
    }

    private void subirHijo(VerticeRojinegro vertice){
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

    private void mataFantasma(VerticeRojinegro fantasma){
        if(fantasma != null)
            if(esRaiz(fantasma))
                raiz = ultimoAgregado = fantasma = null;
            else
                if(esHijoIzquierdo(fantasma))
                    fantasma.padre.izquierdo = null;
                else
                    fantasma.padre.derecho = null;
    }

    private void rebalanceoElimina(VerticeRojinegro vertice) {
        VerticeRojinegro padre, hermano, sobrinoIzq, sobrinoDer, aux;
        /** --Caso 1--
         * El padre es null.
         * Terminamos. */
        if (vertice.padre == null){
            vertice.color = Color.NEGRO;
            raiz = vertice;
            return;
        }
        padre = verticeRojinegro(vertice.padre);
        hermano = obtenerHermano(vertice);
        /** --Caso 2--
         * El hermano es rojo.
         * Coloreamos al hermano de NEGRO, al padre de ROJO, y  giramos
         * sobre el padre en la direccion del vertice. */
        if (!esNegro(hermano)) {
            hermano.color = Color.NEGRO;
            padre.color = Color.ROJO;
            
            if (esHijoIzquierdo(vertice))
                super.giraIzquierda(padre);
            else
                super.giraDerecha(padre);
            //Como se hizo un giro se deben de actualizar las referencias.
            padre = verticeRojinegro(vertice.padre);
            hermano = obtenerHermano(vertice);
        }
        sobrinoIzq = verticeRojinegro(hermano.izquierdo);
        sobrinoDer = verticeRojinegro(hermano.derecho);
        /** --Caso 3--
         * El padre, el hermano y los hijos del hermano son negros.
         * Coloreamos al hermano de ROJO, recursamos sobre el padre y terminamos */
        if (esNegro(padre) && esNegro(hermano) && sobrinosNegros(sobrinoIzq, sobrinoDer)) {
            hermano.color = Color.ROJO;
            rebalanceoElimina(padre);
            return;
        }
        /** --Caso 4--
         * El hermano y los sobrinos son negros, y el padre es ROJO.
         * Coloreamos al padre de NEGRO, al hermano de ROJO y terminamos. */
        if (esNegro(hermano) && sobrinosNegros(sobrinoIzq, sobrinoDer) && !esNegro(padre)) {
            padre.color = Color.NEGRO;
            hermano.color = Color.ROJO;
            return;
        }
        /** --Caso 5--
         * Los sobrinos son bicoloreados cruzados.
         * Coloreamos al sobrino ROJO de NEGRO, hermano de ROJO y giramos sobre 
         * el hermano en la direccion contraria al vertice. */
        if (sonVerticesBicoloreados(sobrinoIzq, sobrinoDer) && sonSobrinoCruzados(vertice, sobrinoIzq, sobrinoDer)) {
            if(!esNegro(sobrinoIzq))
                sobrinoIzq.color = Color.NEGRO;
            else
                sobrinoDer.color = Color.NEGRO;

            hermano.color = Color.ROJO;

            if(esHijoIzquierdo(vertice))
                super.giraDerecha(hermano);
            else
                super.giraIzquierda(hermano);
            //Se prepara para el caso 6.
            hermano = obtenerHermano(vertice);
            sobrinoIzq = verticeRojinegro(hermano.izquierdo);
            sobrinoDer = verticeRojinegro(hermano.derecho);
        }
        /** --Caso 6--
         * El sobrino cruzado es ROJO.
         * Coloreamos al hermano de color del padre, al padre de NEGRO,
         * al sobrino cruzado de NEGRO y giramos sobre padre en la
         * direccion del vertice */
        hermano.color = padre.color;
        padre.color = Color.NEGRO;

        if(esHijoIzquierdo(vertice))
            sobrinoDer.color = Color.NEGRO;
        else
            sobrinoIzq.color = Color.NEGRO;

        if(esHijoIzquierdo(vertice))
            super.giraIzquierda(padre);
        else
            super.giraDerecha(padre);
    }

    private VerticeRojinegro obtenerHijo(VerticeRojinegro vertice){
        if(vertice.hayIzquierdo())
            return verticeRojinegro(vertice.izquierdo);
        return verticeRojinegro(vertice.derecho);
    }

    private boolean sonSobrinoCruzados(VerticeRojinegro vertice, VerticeRojinegro sobrinoIzq, VerticeRojinegro sobrinoDer) {
        return esNegro(sobrinoIzq) && esHijoDerecho(vertice) || esNegro(sobrinoDer) && esHijoIzquierdo(vertice);
    }

    /**
     * Se usa un XOR ya que con que uno se cumpla es verdadra.
     * p|⊕|q
     * 0|0|0
     * 0|1|1
     * 1|1|0
     * 1|0|1
     */
    private boolean sonVerticesBicoloreados(VerticeRojinegro a, VerticeRojinegro b) {
        return esNegro(a) ^ esNegro(b);
    }

    /**
     * Se tiene p v q
     * donde  p -> vertice == null
     *        q -> vertice.color == Color.NEGRO
     * Entonces su negación es ¬p ^ ¬q
     * donde ¬p -> vertice != null
     *       ¬q -> vertice.color != Color.NEGRO
     */
    private boolean esNegro(VerticeRojinegro vertice) {
        return vertice == null || vertice.color == Color.NEGRO;
    }

    private boolean sobrinosNegros(VerticeRojinegro sobrinoIzq, VerticeRojinegro sobrinoDer) {
        return esNegro(sobrinoIzq) && esNegro(sobrinoDer);
    }

    private VerticeRojinegro obtenerHermano(VerticeRojinegro vertice) {
        if (esHijoIzquierdo(vertice))
            return verticeRojinegro(vertice.padre.derecho);
        return verticeRojinegro(vertice.padre.izquierdo);
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

}
