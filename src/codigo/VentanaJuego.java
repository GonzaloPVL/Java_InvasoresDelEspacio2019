/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Gonzalo Palacios
 */
public class VentanaJuego extends javax.swing.JFrame {
    
    
    static int ANCHOPANTALLA = 600;
    static int ALTOPANTALLA = 450;
    
    //numero de marcianos que van a aparecer
    int filas = 5;
    int columnas = 10;
    int marcador = 50;
    
    
    
    BufferedImage buffer = null;
    
    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();
    //Marciano miMarciano = new Marciano();
    Marciano [][] listaMarcianos = new Marciano[filas][columnas];
    
    boolean direccionMarcianos = false;
    boolean explosion = false;
    
    // el contador sirve para decidir que imgen del marciano toca poner
    int contador = 0;
    // imagen para cargar el spritesheet con todos los sprites del juego
    BufferedImage plantilla = null;
    Image [][] imagenes;
    //booleana para saber si hemos perdido;
    boolean gameOver = false;
    
    
    
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            bucleDelJuego();
        }
    });

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
        imagenes = cargaImagenes("/imagenes/invaders2.png", 5, 4, 64, 64, 2);
                                //(nombredearchivo, filas, columnas, ancho , alto, escala)
                                // ancho y alto de cada sprite dentro del spritesheet
                                //escala para cambiar el tamaño
        
        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        buffer = (BufferedImage) jPanel1.createImage(ANCHOPANTALLA, ALTOPANTALLA);
        buffer.createGraphics();
        
        temporizador.start();
        
        //Cambio la imagen de la nave dentro del sprite de imagenes
        miNave.imagen = imagenes[4][2];
        //Cambio la imagen del disparo dentro del sprite de imagenes
        miDisparo.imagen = imagenes [3][2];
        //inicializo la posicion inicial de la nave
        miNave.x = ANCHOPANTALLA/2 - miNave.imagen.getWidth(this)/2;
        miNave.y = ALTOPANTALLA - miNave.imagen.getHeight(this)-30;
        
        
        
        // inicializo el array de marcianos
        for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++){
                listaMarcianos[i][j] = new Marciano();
                
                listaMarcianos[i][j].imagen1 = imagenes[0][0];
                listaMarcianos[i][j].imagen2 = imagenes[0][1];
                listaMarcianos[i][j].x = j*(15+listaMarcianos[i][j].imagen1.getWidth(null));
                listaMarcianos[i][j].y = i*(15+listaMarcianos[i][j].imagen1.getHeight(null));
            }
        }

    }
    //este metodo va a servir para crear el array de imagenes con tdaslas imagenes
    // del sprite . Devolverá un array de dos dimensiones con la imagenes colocadas
    private Image[][] cargaImagenes(String nombreArchivoImagen, 
                                    int numFilas, int numColumnas,
                                    int ancho, int alto, int escala){
        
         try {
            plantilla = ImageIO.read(getClass().getResource(nombreArchivoImagen));
        } catch (IOException ex) { }
        Image[][] arrayImagenes = new Image[numFilas][numColumnas];

        // Cargo las imagenes de forma individual en cada imagen del array de imagenes
        // Convertimos lo que sería un array de dos dimensiones en uno array de una dimension
        for(int i = 0; i<numFilas; i++){
            for(int j = 0; j<numColumnas; j++){
                arrayImagenes[i][j] = plantilla.getSubimage(j*ancho,i*alto , ancho, alto);
                arrayImagenes[i][j] = arrayImagenes[i][j].getScaledInstance(ancho/escala, alto/escala, Image.SCALE_SMOOTH);
            }
        }
        
        return arrayImagenes;
//        // la ultima fila del spritesheet solo mide 32 de alto, asique hay que hacerlo aparte
//        for(int j = 0; j<4 ; j++){
//            imagenes[20+j] = plantilla.getSubimage(j*64, 5*64, 64, 32);
//        }
        
        
    }
    
    private void bucleDelJuego(){
        //se encarga del redibujado de los objetos en el jPanel1
        //primero borro todo lo que hay en el buffer
        
        contador++;
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        
        if(!gameOver){
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);
        
        
        ///////////////////////////////////////////////////////////////////////
        //redibujamos aquí cada elemento
        g2.drawImage(miDisparo.imagen, miDisparo.x, miDisparo.y , null);
        g2.drawImage(miNave.imagen, miNave.x, miNave.y , null);
        //g2.drawImage(miMarciano.imagen1, miMarciano.x, miMarciano.y , null);
        pintaMarcianos(g2);
        chequeaColision();
        miNave.mueve();
        miDisparo.mueve();
        }
        else{
            g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);
        }
        
        
        
        ///////////////////////////////////////////////////////////////////////
        //      fase final, se dibuja el buffer sobre el jPanel              //
        
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);
        
    }
    private void chequeaColision(){
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloNave = new Rectangle2D.Double();
        
        rectanguloDisparo.setFrame(miDisparo.x,
                                    miDisparo.y,
                                    miDisparo.imagen.getWidth(null),
                                    miDisparo.imagen.getHeight(null));
        rectanguloNave.setFrame(miNave.x,
                                miNave.y, 
                                miNave.imagen.getWidth(null)-20,
                                miNave.imagen.getHeight(null)-20);
        
        for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++){
                if(listaMarcianos[i][j].vivo){
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].x,
                                                listaMarcianos[i][j].y,
                                                listaMarcianos[i][j].imagen1.getWidth(null),
                                                listaMarcianos[i][j].imagen1.getHeight(null));
                    if(rectanguloDisparo.intersects(rectanguloMarciano)){
                        listaMarcianos[i][j].vivo= false;
                        miDisparo.y = 30000;
                        miDisparo.disparado = false;
                        explosion = true;
                        marcador -=100;
                        
                    }
                    if(rectanguloMarciano.intersects(rectanguloNave)){
                        gameOver = true;
                    }
                }
            }
        }
    }
    

    private void cambiaDireccionMarciano(){
        
        for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++){
                listaMarcianos[i][j].y +=10;
                listaMarcianos[i][j].setvX(listaMarcianos[i][j].getvX()*-1);
                
            }
        }
        
    }   
    private void pintaMarcianos(Graphics2D _g2){
        
        int anchoMarciano = listaMarcianos[0][0].imagen1.getWidth(null);
        for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++){
                if(listaMarcianos[i][j].vivo){
                listaMarcianos[i][j].mueve();
                // chequeo si el marciano ha chocado contra la pared para cambiar la direccion
                //de todos los marcianos
                if(listaMarcianos[i][j].x + anchoMarciano == ANCHOPANTALLA){
                    direccionMarcianos = true; 
                }
                if(listaMarcianos[i][j].x  == 0){
                   direccionMarcianos = true;
                }
                if(contador < 50){
                   _g2.drawImage(listaMarcianos[i][j].imagen1, 
                              listaMarcianos[i][j].x, 
                              listaMarcianos[i][j].y, 
                              null); 
                }else if(contador < 100){
                   _g2.drawImage(listaMarcianos[i][j].imagen2, 
                              listaMarcianos[i][j].x, 
                              listaMarcianos[i][j].y, 
                              null); 
                }
                else contador = 0; 
              }
//                if(!listaMarcianos[i][j].vivo){  
//                    _g2.drawImage(imagenes[23], listaMarcianos[i][j].x, listaMarcianos[i][j].y,null);
//                    listaMarcianos[i][j].mueve();
//                }
            }
        }
        if(direccionMarcianos){
            cambiaDireccionMarciano();
            direccionMarcianos = false;
        }
    }
    
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()){
            case KeyEvent.VK_LEFT: miNave.setPulsadoIzquierda(true) ; 
            break;
            case KeyEvent.VK_RIGHT: miNave.setPulsadoDerecha(true) ; 
            break;
            case KeyEvent.VK_SPACE: miDisparo.posicionaDisparo(miNave);
            miDisparo.disparado = true;
            break;
            
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()){  
            case KeyEvent.VK_LEFT: miNave.setPulsadoIzquierda(false) ; 
            break;
            case KeyEvent.VK_RIGHT: miNave.setPulsadoDerecha(false) ; 
            break;
        }
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
