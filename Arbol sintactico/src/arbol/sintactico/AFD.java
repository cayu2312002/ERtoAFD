package arbol.sintactico;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class AFD {
    private ArrayList<Estado> estados_d = null;
    private Set<Character> alfabeto = null;
    private final Hashtable<Character, Integer>[] tranD;
    private final Set<Integer> pposRaiz;
    private final Set<Integer>[] sgtPos;
    private final Hashtable<Character, Set<Integer>> pos;
    private final int cntCaracteres;
    private int cntEstados = 0;

    public AFD(ArbolSintactico st, String er) {
        this.pos = st.getPos();
        this.pposRaiz = st.getRaiz().getPpos();
        this.sgtPos = st.getSgtPos();
        this.cntCaracteres = st.getCntCaracteres();
        this.tranD = new Hashtable[this.cntCaracteres + 1];
        for (int i = 0; i <= this.cntCaracteres; i++) tranD[i] = new Hashtable<>();
        getAlfabeto(er);
    }

    public Set<Integer> getConjEstados(int label) {
        return estados_d.get(label - 1).getConjPos();
    }

    public int getCntEstados() {
        return this.cntEstados;
    }

    public Hashtable<Character, Integer>[] getTranD() {
        return this.tranD;
    }

    public boolean esEstadoFinal(int label) {
        return this.estados_d.get(label - 1).esEstadoFinal();
    }

    private boolean esCaracter(char simbolo) {
        return Character.isLetterOrDigit(simbolo) || simbolo == '#';
    }

    private void getAlfabeto(String er) {
        this.alfabeto = new HashSet<>();
        for (int i = 0; i < er.length(); i++) if (esCaracter(er.charAt(i))) this.alfabeto.add(er.charAt(i));
    }

    public String getAlfaString() {
        StringBuilder alfa = new StringBuilder();
        for (Character simbolo : alfabeto) alfa.append(simbolo).append(",");
        return alfa.substring(0, alfa.length() - 1);
    }

    public Set<Character> getAlfabeto() {
        return this.alfabeto;
    }

    public void crearAFD() {
        estados_d = new ArrayList<>();
        Estado inicial = new Estado(++cntEstados);
        inicial.setConjPos(pposRaiz);
        estados_d.add(inicial);
        while (true) {
            Estado actual = null;
            boolean finalizado = true;
            for (Estado estado : estados_d) {
                if (!estado.estaMarcado()) {
                    actual = estado;
                    finalizado = false;
                }
            }
            if (finalizado) break;
            actual.setMarcado(true);
            actual.setEstadoFinal(cntCaracteres);
            for (Character simbolo : alfabeto) {
                Set<Integer> u = new HashSet<>();
                for (Integer posicion : actual.getConjPos()) {
                    if (pos.get(simbolo).contains(posicion)) {
                        u.addAll(sgtPos[posicion]);
                    }
                }
                if (!u.isEmpty()) {
                    boolean existe = false;
                    Estado estado_vist = null;
                    for (Estado estado : estados_d) {
                        if (u.equals(estado.getConjPos())) {
                            existe = true;
                            estado_vist = estado;
                            break;
                        }
                    }
                    if (!existe) {
                        Estado estadoU = new Estado(++cntEstados);
                        estadoU.setConjPos(u);
                        tranD[actual.getLabel()].put(simbolo, estadoU.getLabel());
                        estados_d.add(estadoU);
                    } else tranD[actual.getLabel()].put(simbolo, estado_vist.getLabel());
                }
            }
        }
    }

    private boolean DFS(String cadena, int i, int estado) {
        if (i == cadena.length() && estados_d.get(estado - 1).esEstadoFinal()) return true;
        else if (i < cadena.length()) {
            char simbolo = cadena.charAt(i);
            if (!tranD[estado].isEmpty() && tranD[estado].get(simbolo) != null) {
                return DFS(cadena, i + 1, tranD[estado].get(simbolo));
            }
        }
        return false;
    }

    public boolean reconoceCadena(String cadena) {
        cadena = cadena.replace("&", "");
        return DFS(cadena, 0, 1);
    }
}