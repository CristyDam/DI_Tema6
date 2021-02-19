
package lanzaayuda;

import java.awt.Menu;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import java.net.URL; 
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.*;
import javax.swing.JButton;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


/**
 *
 * @author crist
 */
public class LanzaAyuda {
    
    /**
     * @param args the command line arguments
     */
    private JMenuBar menuBar;
    private JMenu menu;
    private static JMenuItem menuItem1, menuItem2;
    private static JButton botonGuardar;
    private static JButton botonAbrir;
    
    public JMenuBar createMenuBar() {
        
        menuBar = new JMenuBar();
        menu = new JMenu("Ayuda");
        menuItem1 = new JMenuItem("Contenido de la ayuda");
        menuItem2 = new JMenuItem("About");
        
        menuBar.add(menu);
        menu.add(menuItem1);
        menu.add(menuItem2);
        
        return menuBar;
        
    }
    

    public static HelpSet obtenFicAyuda(){ 
        try { 
            ClassLoader cl = LanzaAyuda.class.getClassLoader();
            
            URL url = new URL("jar:file:LanzaAyuda.jar!/lanzaayuda/help/helpset.hs");
            //File file = new File(LanzaAyuda.class.getResource("help/helpset.hs").getFile()); 
            //URL url = file.toURI().toURL();
            //System.out.println("Etiqueta url: "+ url.toString());
            
            // crea un objeto Helpset 
            HelpSet hs = new HelpSet(null,url); 
            return hs; 
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(null,"Fichero HelpSet no encontrado"); 
            return null; 
        } 
    }
    
    public static void main(String[] args) {
        
        //ventana
        JFrame frame = new JFrame("Aplicacion Swing con Ayuda");
        
        //panel
        JPanel panel = new JPanel();
        panel.setSize(500, 500);
        
        LanzaAyuda lanza = new  LanzaAyuda();
        
        frame.setJMenuBar(lanza.createMenuBar());
        
        frame.add(panel);
        frame.setVisible(true);
        frame.setSize(500, 500);
        
        HelpSet hs = obtenFicAyuda(); 
        HelpBroker hb = hs.createHelpBroker();
        hb.enableHelpOnButton(menuItem1,"Contenido de la ayuda",hs); 
        
        
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)); 
        hb.enableHelpKey(menuItem1, "Contenido de la ayuda", hs);
        
        hb.enableHelp(botonGuardar, "guardar", hs); 
        hb.enableHelp(botonAbrir, "abrir",hs);
        
        
    }

   
    
}
