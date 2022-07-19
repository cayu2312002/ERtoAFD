package arbol.sintactico;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InterfazGrafica extends javax.swing.JFrame {

    private ArbolSintactico st = null;
    private AFD afd = null;
    /*
        Ejemplos de expresiones regulares aceptadas

        (a|bb*)+aa(ab)?gb+
        (a|b(c|d)*)+
        a+bc?aa*b(((d|e)|&)+)?
        (a|b)*abb
        (((af|bc)+abb)*fd)+ab
        (ab|cd)*d*c?
        ((a|b(dcab*|b+)?abbf+)*)?a+c
        (a|b|c|d)*(hola)?
    */

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new InterfazGrafica().setVisible(true));
    }

    public InterfazGrafica() {
        establecerComponentes();
    }

    private void DFS(Nodo actual, DefaultTableModel tableModel) {
        if (actual == null) return;
        DFS(actual.getHijoIzq(), tableModel);
        DFS(actual.getHijoDer(), tableModel);
        String ppos = String.join(", ", actual.getPpos().toString());
        String upos = String.join(", ", actual.getUpos().toString());
        tableModel.addRow(new Object[]{actual.getLabel(), ppos, upos, actual.getIndice()});
    }

    private void paintTree(Nodo actual, Graphics g, int depth, int x, int y) {
        if (actual == null) return;
        Nodo hijoIzq = actual.getHijoIzq();
        Nodo hijoDer = actual.getHijoDer();
        int offsetX = 30;
        int offsetY = 25;
        int hijosIzq;
        int hijosDer;
        if (hijoIzq != null && hijoDer != null) {
            hijosIzq = hijoIzq.getNumHijos();
            hijosIzq = (hijosIzq == 1) ? 0 : hijosIzq * 5;
            hijosDer = hijoDer.getNumHijos();
            hijosDer = (hijosDer == 1) ? 0 : hijosDer * 5;
            if (hijosIzq > hijosDer && hijosDer > 2) hijosIzq -= 10;
            else if (hijosDer > hijosIzq && hijosIzq > 2) hijosDer += 10;
            g.drawLine(x, y, x - offsetX - hijosIzq + 10, y + offsetY);
            paintTree(hijoIzq, g, depth + 1, x - offsetX - hijosIzq + 10, y + offsetY);
            g.drawLine(x, y, x + offsetX + hijosDer - 10, y + offsetY);
            paintTree(hijoDer, g, depth + 1, x + offsetX + hijosDer - 10, y + offsetY);
            String indiceS = actual.getIndice() + "";
            g.drawString(indiceS, x - 3, y + 20);
        } else if (hijoIzq != null) {
            g.drawLine(x, y, x, y + offsetY + 10);
            paintTree(hijoIzq, g, depth + 1, x, y + offsetY + 10);
            String indiceS = actual.getIndice() + "";
            g.drawString(indiceS, x + 1, y + 20);
        } else {
            String indiceS = actual.getIndice() + "";
            g.drawString(indiceS, x - 3, y + 20);
        }
        g.setColor(Color.YELLOW);
        g.fillOval(x - 10, y - 10, 20, 20);
        String label = actual.getLabel() + "";

        g.setColor(Color.BLACK);
        g.drawString(label, x - 3, y + 5);

    }

    public void paintTree() {
        int x = lienzoJSP.getWidth() / 2 + 150;
        int y = 10;
        paintTree(st.getRaiz(), lienzoJSP.getGraphics(), 1, x, y);
    }

    private void reiniciarValores() {
        st = null;
        afd = null;
        stLabel.setText("Arbol sintactico de:");
        Graphics g = lienzoJSP.getGraphics();
        g.clearRect(0, 0, lienzoJSP.getWidth(), lienzoJSP.getHeight());
        alfaLabel.setText("Vacio");
        regexTF.setText("");
        cadenaTF.setText("");
        DefaultTableModel tableModel = (DefaultTableModel) puTable.getModel();
        tableModel.setRowCount(0);
        tableModel = (DefaultTableModel) spTable.getModel();
        tableModel.setRowCount(0);
        tableModel = (DefaultTableModel) trandTable.getModel();
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        tableModel = (DefaultTableModel) ceTable.getModel();
        tableModel.setRowCount(0);
    }

    private void establecerValores() {
        String labelText = stLabel.getText();
        stLabel.setText(labelText + " " + regexTF.getText());
        DefaultTableModel tableModel = (DefaultTableModel) puTable.getModel();
        alfaLabel.setText(afd.getAlfaString());
        DFS(st.getRaiz(), tableModel);
        Set<Integer>[] sgtPos = st.getSgtPos();
        tableModel = (DefaultTableModel) spTable.getModel();
        for (int i = 1; i <= st.getCntCaracteres(); i++) {
            String conjPos = sgtPos[i].toString();
            tableModel.addRow(new Object[]{i, conjPos});
        }
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableModel.addColumn("Estado");
        Set<Character> alfabeto = afd.getAlfabeto();
        for (Character simbolo : alfabeto) tableModel.addColumn(simbolo);
        Hashtable<Character, Integer>[] tranD = afd.getTranD();
        for (int i = 1; i <= afd.getCntEstados(); i++) {
            ArrayList<Object> row = new ArrayList<>();
            String estadoFinal = "";
            if (afd.esEstadoFinal(i)) estadoFinal = "*";
            row.add(i + estadoFinal);
            for (Character simbolo : alfabeto) row.add(tranD[i].get(simbolo));
            tableModel.addRow(row.toArray());
        }
        trandTable.setModel(tableModel);
        paintTree();
        tableModel = (DefaultTableModel) ceTable.getModel();
        for (int i = 1; i <= afd.getCntEstados(); i++) {
            tableModel.addRow(new Object[]{i, afd.getConjEstados(i).toString()});
        }
    }
    

    private void establecerComponentes() {

        javax.swing.JTabbedPane panelPestañas = new javax.swing.JTabbedPane();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        puTable = new javax.swing.JTable();
        JScrollPane jScrollPane2 = new JScrollPane();
        spTable = new javax.swing.JTable();
        JScrollPane jScrollPane3 = new JScrollPane();
        trandTable = new javax.swing.JTable();
        JScrollPane jScrollPane4 = new JScrollPane();
        ceTable = new javax.swing.JTable();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        regexTF = new javax.swing.JTextField();
        cadenaTF = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JButton arbolButton = new javax.swing.JButton();
        JButton verficarButton = new JButton();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        alfaLabel = new javax.swing.JLabel();
        JButton reiniciarButton = new JButton();
        lienzoJSP = new javax.swing.JPanel();
        stLabel = new javax.swing.JLabel();
        JButton repaintBT = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        puTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Nodo", "Ppos", "Upos", "Indice"
                }
        ) {
            final boolean[] canEdit = new boolean[]{
                    false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane1.setViewportView(puTable);

        //panelPestañas.addTab("ppos_upos", jScrollPane1);

        spTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Posicion", "SgtPos"
                }
        ) {
            final boolean[] canEdit = new boolean[]{
                    false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane2.setViewportView(spTable);

        //panelPestañas.addTab("sgtpos", jScrollPane2);

        trandTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{

                }
        ));
        jScrollPane3.setViewportView(trandTable);

        panelPestañas.addTab("TranD", jScrollPane3);

        ceTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Estado", "ConjEstados"
                }
        ) {
            final boolean[] canEdit = new boolean[]{
                    false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane4.setViewportView(ceTable);

        //panelPestañas.addTab("ConjEstados", jScrollPane4);

        jLabel1.setText("Expresion regular:");

        jLabel2.setText("Cadena a verificar:");

        arbolButton.setText("Arbol Sintactico");
        arbolButton.addActionListener(this::arbolButtonActionPerformed);
        arbolButton.addActionListener(evt -> repaintBTActionPerformed());

        verficarButton.setText("Verificar");
        verficarButton.addActionListener(this::verficarButtonActionPerformed);

        jLabel3.setText("Alfabeto:");

        alfaLabel.setText("Vacio");

        reiniciarButton.setText("Reiniciar");
        reiniciarButton.addActionListener(evt -> reiniciarButtonActionPerformed());

        javax.swing.GroupLayout lienzoJSPLayout = new javax.swing.GroupLayout(lienzoJSP);
        lienzoJSP.setLayout(lienzoJSPLayout);
        lienzoJSPLayout.setHorizontalGroup(
                lienzoJSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 580, Short.MAX_VALUE)
        );
        lienzoJSPLayout.setVerticalGroup(
                lienzoJSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );

        stLabel.setText("Arbol sintactico de:");

        repaintBT.setText("Repintar");
        repaintBT.addActionListener(evt -> repaintBTActionPerformed());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel3))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(regexTF, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(arbolButton))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addComponent(cadenaTF)
                                                                        .addComponent(alfaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(reiniciarButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(verficarButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                                .addGap(32, 32, 32))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(repaintBT, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(panelPestañas, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lienzoJSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(229, 229, 229)
                                                .addComponent(stLabel)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(regexTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(arbolButton))
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(alfaLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cadenaTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(verficarButton))
                                .addGap(37, 37, 37)
                                .addComponent(reiniciarButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panelPestañas, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(repaintBT)
                                .addContainerGap(75, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(stLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lienzoJSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(16, 16, 16))
        );

        pack();
    }

    private void arbolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arbolButtonActionPerformed
        String er = regexTF.getText();
        if (!er.isEmpty()) {
            reiniciarValores();
            regexTF.setText(er);
            st = new ArbolSintactico(er);
            st.crearArbol();
            st.calculoPosiciones();
            afd = new AFD(st, er);
            afd.crearAFD();
            establecerValores();
        }
    }

    private void verficarButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String cadena = cadenaTF.getText();
        if (!cadena.isEmpty() && afd != null) {
            if (afd.reconoceCadena(cadena)) JOptionPane.showMessageDialog(null, "Se ha reconocido la cadena.");
            else JOptionPane.showMessageDialog(null, "No se ha reconocido la cadena.");
        }
    }

    private void reiniciarButtonActionPerformed() {
        reiniciarValores();
    }

    private void repaintBTActionPerformed() {
        if (st != null) paintTree();
    }


    
    // Variables declaration - do not modify
    private javax.swing.JLabel alfaLabel;
    private javax.swing.JTextField cadenaTF;
    private javax.swing.JTable ceTable;
    private javax.swing.JPanel lienzoJSP;
    private javax.swing.JTable puTable;
    private javax.swing.JTextField regexTF;
    private javax.swing.JTable spTable;
    private javax.swing.JLabel stLabel;
    private javax.swing.JTable trandTable;
    // End of variables declaration
}
