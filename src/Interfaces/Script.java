/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Clases.ConsultasQuery;
import java.io.File;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

/**
 *
 * @author User
 */
public class Script extends javax.swing.JFrame {
    File carpeta;
    /**
     * Creates new form Script
     */
    public Script(File file) {
        initComponents();
        this.setLocationRelativeTo(null);
        carpeta=file;
        jLabel2.setText(carpeta.getName());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        textosql = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textosql.setColumns(20);
        textosql.setRows(5);
        textosql.setText("select * from institucion as inst inner join autores\n as aut on inst.id_inst = aut.id_inst \ninner join paper_autor as paut on paut.id_autor = aut.id_autor");
        jScrollPane1.setViewportView(textosql);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 530, 190));

        jButton1.setText("Crear consulta");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 270, -1, -1));

        jButton2.setText("Cerrar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, -1, -1));

        jButton3.setText("Atras");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 40, 260, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("NOMBRE DE LA CARPETA SELECCIONADA:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/menu.jpeg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 300));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //hacemos una instancia de la clase que usaremos para los metodos
        ConsultasQuery cq = new ConsultasQuery();
        //ponemos el texto en minusculas
        String consultaTxt =textosql.getText().toLowerCase();
        //hacemos la validacion si la consulta es un insert
        if(consultaTxt.startsWith("insert into ")){
            //le removemos el insert into de la consulta 
            consultaTxt=consultaTxt.replaceFirst("insert into ", "");
            cq.ConsultaInsert(consultaTxt,carpeta);
            //si empieza con delete from
        }else if(consultaTxt.startsWith("delete from ")){
            //le removemos el delete from de la consulta
            consultaTxt=consultaTxt.replaceFirst("delete from ", "");
            //recibira los datos procesados
            cq.ConsultaDelete(consultaTxt,carpeta);
            //para la conslta update si empieza con update
        }else if(consultaTxt.startsWith("update ")){
            //le removemos el update de la consulta
            consultaTxt=consultaTxt.replaceFirst("update ", "");
            cq.ConsultaUpdate(consultaTxt,carpeta);
        }else if(consultaTxt.startsWith("select ")){
            //quitamos los saltos de linea de la consulta
            consultaTxt = cq.quitarSaltosLinea(consultaTxt);
            //le removemos el select de la consulta ejemplo "select * from usuarios" = "* from usuarios"
            consultaTxt=consultaTxt.replaceFirst("select ", "");
            //se va a identificar que tipo de consulta es
            if(consultaTxt.contains(" inner join ")){
                cq.consultaSelectInner(consultaTxt, carpeta);
            }else{
                cq.ConsultaSelectSimple(consultaTxt, carpeta);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Error de sintaxis", "Error", ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    
    //salir de la aplicacion
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Home home = new Home();
        home.setVisible(true);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textosql;
    // End of variables declaration//GEN-END:variables
}
