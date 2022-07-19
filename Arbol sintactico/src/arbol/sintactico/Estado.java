package arbol.sintactico;

import java.util.HashSet;
import java.util.Set;

public class Estado {
    private final int label;
    private Set<Integer> conjPos;
    private boolean marcado = false;
    private boolean estadoFinal = false;

    public Estado(int label) {
        this.label = label;
        this.conjPos = new HashSet<>();
    }

    public int getLabel() {
        return label;
    }

    public void setConjPos(Set<Integer> conjPos) {
        this.conjPos = conjPos;
    }

    public Set<Integer> getConjPos() {
        return this.conjPos;
    }

    public boolean estaMarcado() {
        return this.marcado;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    public boolean esEstadoFinal() {
        return this.estadoFinal;
    }

    public void setEstadoFinal(int posNumeral) {
        if (conjPos.contains(posNumeral)) this.estadoFinal = true;
    }
}
