/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.scene.media.AudioClip;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Gonzalo Palacios
 */
public class VentanaJuego extends javax.swing.JFrame {
    
    
    static int ANCHOPANTALLA = 600;
    static int ALTOPANTALLA = 480;
    
    //numero de marcianos que van a aparecer
    int filas = 5;
    int columnas = 7;
    int contadorTiempo = 0;
    int a;
    int b;
    int p = 3;
    // p=1 Bulbasur, 3 charmander, 5 squirtle
    public static Label label1 = new Label();
    Image fondo;
    int puntuacion = 0;
    
    
    
    BufferedImage buffer = null;
    
    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();
    //Marciano miMarciano = new Marciano();
    Marciano [][] listaMarcianos = new Marciano[filas][columnas];
    ArrayList <Explosion> listaExplosiones = new ArrayList();
    
    boolean direccionMarcianos = false;
    
    // el contador sirve para decidir que imgen del marciano toca poner
    int contador = 0;
    // imagen para cargar el spritesheet con todos los sprites del juego
    BufferedImage plantilla = null;
    Image [][] imagenes;
    Image [][] imagenesNave;
    Image [][]imagenesDisparo;
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
    public VentanaJuego() { //"/imagenes/angryBirds.png", 3, 3, 129, 124, 4
        initComponents();
        imagenes = cargaImagenes("/imagenes/angryBirds.png", 3, 3, 129, 124, 4);
                                //(nombredearchivo, filas, columnas, ancho , alto, escala)
                                // ancho y alto de cada sprite dentro del spritesheet
                                //escala para cambiar el tamaño
        imagenesNave =  cargaImagenes("/imagenes/pokemonNave.png", 6, 4, 89, 87, 1);  
        imagenesDisparo = cargaImagenes("/imagenes/explosion.png", 7, 6, 130, 130, 5);
                                

        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        buffer = (BufferedImage) jPanel1.createImage(ANCHOPANTALLA, ALTOPANTALLA);
        buffer.createGraphics();
        
        temporizador.start();
        
        try {
            fondo = ImageIO.read(getClass().getResource("/imagenes/campoBatalla.png"));
        } catch (IOException ex) {
        }
        
        reproduce("/sonidos/pokemonmusica.wav");
        
        setLocationRelativeTo(null);
        Font font1;
        font1 = new Font("Calibri",Font.BOLD, 30);
        
        label1.setFont(font1);
        label1.setForeground(Color.red);
        label1.setBackground(Color.BLACK);
        label1.setBounds(500, 400, 80, 30);
        label1.setText("0");
        jPanel1.add(label1);
        
        //Cambio la imagen de la nave dentro del sprite de imagenes
        miNave.imagen = imagenesNave[p][0];
        //Cambio la imagen del disparo dentro del sprite de imagenes
        miDisparo.imagen = imagenesDisparo [3][3];
        //inicializo la posicion inicial de la nave
        miNave.x = ANCHOPANTALLA/2 - miNave.imagen.getWidth(this)/2;
        miNave.y = ALTOPANTALLA - miNave.imagen.getHeight(this)-30;
        // inicializo el array de marcianos
        
       //1º numero de filas de marcianos que estoy creando
       //2º parametro fila dentro del spritesheet
       //3º parametro columna dentro del spritesheet para elegir el marciano
        
        creaFilaDeMarcianos(0, 0, 0);
        creaFilaDeMarcianos(1, 0, 1);
        creaFilaDeMarcianos(2, 0, 0);
        creaFilaDeMarcianos(3, 0, 1);
        creaFilaDeMarcianos(4, 1, 1);

    }
    
    
    private void creaFilaDeMarcianos (int numFila, int spriteFila, int spriteColumna){
        
        for(int j=0; j<columnas; j++){
                listaMarcianos[numFila][j] = new Marciano();
                listaMarcianos[numFila][j].imagen1 = imagenes[spriteFila][spriteColumna];
                listaMarcianos[numFila][j].imagen2 = imagenes[spriteFila][spriteColumna+1];
                listaMarcianos[numFila][j].x = j*(15+listaMarcianos[numFila][j].imagen1.getWidth(null));
                listaMarcianos[numFila][j].y = numFila*(15+listaMarcianos[numFila][j].imagen1.getHeight(null));
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
    private void pintaExplosiones (Graphics2D g2){
       //Pinto las explosiones
       for(int i=0; i<listaExplosiones.size();i++){
           Explosion e = listaExplosiones.get(i);
           e.setTiempoDeVida(e.getTiempoDeVida()-1);
           if(e.getTiempoDeVida()>25){
               g2.drawImage(e.imagenExplosion, listaMarcianos[a][b].x, listaMarcianos[a][b].y, null);
           }else{
               g2.drawImage(e.imagenExplosion2, listaMarcianos[a][b].x, listaMarcianos[a][b].y, null);
           }
           // si el tiempo de vida de la explosion es menor que 0 la elimino
           if(e.getTiempoDeVida()<=0){
               listaExplosiones.remove(i);
           }
       }
    }
    private void actualizaContadorTiempo(){
        contadorTiempo ++;
        if(contadorTiempo>100){
            contadorTiempo = 0;
        }
    }
    private void finDePartida (Graphics2D muerto) throws IOException{
        try{
            Image gameOver1 = ImageIO.read((getClass().getResource("/imagenes/gameOver.jpeg")));
            muerto.drawImage(gameOver1, 0, 0,ANCHOPANTALLA,ALTOPANTALLA ,null);
        }catch (IOException ex){   
        }
    }
    private void ganaPartida (Graphics2D win) throws IOException{
        try{
            Image ganador = ImageIO.read((getClass().getResource("/imagenes/ashwin.jpg")));
            win.drawImage(ganador, 0, 0,ANCHOPANTALLA,ALTOPANTALLA ,null);
        }catch (IOException ex){   
        }
    }
    private void reproduce (String cancion){
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream( getClass().getResource(cancion) ));
            clip.loop(0);
            Thread one = new Thread() {
                    public void run() {
                            while(clip.getFramePosition()<clip.getFrameLength())
                                Thread.yield();
                    }  
                };
            one.start();
        } catch (Exception e) {      
        } 
    }
    
    private void bucleDelJuego(){
        //se encarga del redibujado de los objetos en el jPanel1
        //primero borro todo lo que hay en el buffer
        
        contador++;
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        
        if(!gameOver){
            
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);
            g2.drawImage(fondo,0,0, null);


            ///////////////////////////////////////////////////////////////////////
            //redibujamos aquí cada elemento
            if(puntuacion ==800){
                miNave.imagen = imagenesNave[p][1];
            }
            if(puntuacion == 2000){
                miNave.imagen = imagenesNave[p][2];
            }
            g2.drawImage(miDisparo.imagen, miDisparo.x, miDisparo.y , null);
            g2.drawImage(miNave.imagen, miNave.x, miNave.y , null);
            //g2.drawImage(miMarciano.imagen1, miMarciano.x, miMarciano.y , null);
            pintaMarcianos(g2);
            pintaExplosiones(g2);
            actualizaContadorTiempo();
            chequeaColision();
            miNave.mueve();
            miDisparo.mueve();
            
            if(puntuacion == 3500){
                try {
                    ganaPartida(g2);
                } catch (IOException ex) {
                }
            }

        }

        else{
            try {
                finDePartida(g2);
            } catch (IOException ex) {
            }
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
                                miNave.y+50, 
                                miNave.imagen.getWidth(null),
                                miNave.imagen.getHeight(null));
        
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
                        reproduce("/sonidos/explosion.wav");
                        puntuacion += 100;
                        label1.setText(""+puntuacion);
                        Explosion e = new Explosion();
                        a = i;
                        b = j;
                        listaExplosiones.add(e);
                        e.sonidoExplosion.start();

                        
                    }
                    if(rectanguloMarciano.intersects(rectanguloNave)){
                        GameOver obj = new GameOver();
                        obj.setVisible(true);
                        dispose();
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
            reproduce ("/sonidos/laser.wav");
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
