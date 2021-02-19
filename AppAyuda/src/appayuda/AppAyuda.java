
package appayuda;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;



/**
 *
 * @author crist
 */
public class AppAyuda extends Application {
    
    private Scene scene;
        
    @Override 
    public void start(Stage stage) { 

        // Crea la escena
        stage.setTitle("AppAyuda"); 
        scene = new Scene(new Browser(),750,500, Color.web("#666970")); 
        stage.setScene(scene); 
        //scene.getStylesheets().add( AppAyuda.class.getResource("BrowserToolbar.css").toExternalForm()); 
        stage.show();
    
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
    
    class Browser extends Region {
        
        //agrega barra de herramientas con 4 objetos Hyperlink
        private HBox toolBar; 
        private static String[] imageFiles = new String[]{ 
            "img/moodle.jpg", 
            "img/facebook.jpg", 
            "img/documentation.png", 
            "img/twitter.jpg",
            "img/help_1.png"
        }; 
        private static String[] captions = new String[]{ 
            "Moodle", 
            "Facebook", 
            "Documentation", 
            "Twitter",
            "Ayuda"
        }; 
        private static String[] urls = new String[]{ 
            "https://moodle.org/?lang=es", 
            "https://es-es.facebook.com/",
            "http://docs.oracle.com/javase/index.html", 
            "https://twitter.com/?lang=es",
            AppAyuda.class.getResource("/help/help.html").toExternalForm()
        };
        
        final ImageView selectedImage = new ImageView(); 
        final Hyperlink[] hpls = new Hyperlink[captions.length]; 
        final Image[] images = new Image[imageFiles.length]; 
        
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        
        //boton adicional para ocultar y mostrar la documentacionde Java SE para las versiones anteriores
        final Button showPrevDoc = new Button("Toggle Previous Docs");
        //Ventanas emergentes
        final WebView smallView = new WebView();
        private boolean needDocumentationButton = false;
                

        public Browser() { 
        // aplica estilos
        getStyleClass().add("browser");
        
        //Para tratar lo tres enlaces 
        //crea los hipervinculos
        for (int i = 0; i < captions.length; i++) { 
            Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
            Image image = images[i] = new Image(getClass().getResourceAsStream(imageFiles[i])); 
            hpl.setGraphic(new ImageView(image)); 
            final String url = urls[i];
            //el boton de agrega a la barra de herramientas solo cuando se selecciona la pagina documentacion
            final boolean addButton = (hpl.getText().equals("Documentation"));

            //proccess event 
            //setOnAction define el comportamiento de los hipervínculos
            hpl.setOnAction(new EventHandler<ActionEvent>() {
                @Override 
                public void handle(ActionEvent e) {
                    
                    needDocumentationButton = addButton;
                    //Cuando un usuario hace clic en un enlace, el valor de la URL correspondiente se pasa al método de carga de webEngine
                    webEngine.load(url); 
                } 
            }); 
        }
        
        // el motor web carga la pagina 
        webEngine.load("http://www.ieslosmontecillos.es/wp/");
        
        // create the toolbar 
        toolBar = new HBox(); 
        toolBar.setAlignment(Pos.CENTER); 
        toolBar.getStyleClass().add("browser-toolbar"); 
        toolBar.getChildren().addAll(hpls); 
        toolBar.getChildren().add(createSpacer());
        
        //establecer la accion del boton 
        showPrevDoc.setOnAction(new EventHandler() { 
            @Override 
            public void handle(Event t) { 
                //el metodo executeScript para ejecutar un comando de javaScript para un documento cargado en el navegador
                //Cuando el usuario hace clic en el botón Toggle Previous Doc, 
                //el método executeScript ejecuta la función JavaScript toggleDisplay para la página de documentación 
                //y aparecen los documentos para las versiones Java anteriores
                webEngine.executeScript("toggleDisplay('PrevRel')"); 
            } 
        });
        
        //nueva ventana del navegador para el documento de destino
        //El usuario selecciona abrir de la ventana emergente
        //el navegador smallView se agrega a la barra de herramientas definido en el metodo setCreatePopupHandler
        smallView.setPrefSize(120, 80); 
        //handle popup windows 
        webEngine.setCreatePopupHandler( new Callback<PopupFeatures, WebEngine>() { 
            @Override 
            public WebEngine call(PopupFeatures config) { 
                smallView.setFontScale(0.8); 
                if (!toolBar.getChildren().contains(smallView)) { 
                    toolBar.getChildren().add(smallView); 
                } return smallView.getEngine(); 
            } 
        });
        
        // habrá que definir la combo como propiedad de la clase Brower 
        final ComboBox comboBox = new ComboBox();
        
        //En el constructor de la clase Browser damos formato al combobox y lo 
        //incluimos en la toolbar 
        comboBox.setPrefWidth(60); 
        toolBar.getChildren().add(comboBox);
        
        //también el constructor de la clase Browser declaramos el manejador 
        //del histórico 
        //WebHistory representa un historial de sesion asocioado con un objeto WebEngine
        //Utiliza el método WebEngine.getHistory() para obtener la instancia de WebHistory para un motor web particular
        //La historia es una lista de entrada, cada entrada es una pagina visitada su URL, titulo fcha visita
        final WebHistory history = webEngine.getHistory(); 
        history.getEntries().addListener(new ListChangeListener<WebHistory.Entry>(){ 
            @Override 
            public void onChanged(Change<? extends Entry> c) { 
                c.next(); 
                for (Entry e : c.getRemoved()) { 
                    comboBox.getItems().remove(e.getUrl()); 
                } 
                for (Entry e : c.getAddedSubList()) { 
                    comboBox.getItems().add(e.getUrl()); 
                } 
            } 
        }); 
        //Se define el comportamiento del combobox 
        comboBox.setOnAction(new EventHandler<ActionEvent>() { 
            @Override 
            public void handle(ActionEvent ev) { 
                int offset = comboBox.getSelectionModel().getSelectedIndex() - history.getCurrentIndex(); 
                history.go(offset); 
            } 
        });

        // process page loading
        // el método getLoadWorker() proporciona una instancia de la interfaz Worker para seguir el progreso de la carga
        webEngine.getLoadWorker().stateProperty().addListener( new ChangeListener<State>() { 
            @Override 
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) { 
                toolBar.getChildren().remove(showPrevDoc);
                
                // Si el estado de progreso de la página de Documentación es SUCCEEDED, 
                //se agrega el botón Toggle Previous Docs a la barra de herramientas
                if (newState == State.SUCCEEDED) {
                    JSObject win = (JSObject) webEngine.executeScript("window"); 
                    win.setMember("app", new JavaApp());
                    if (needDocumentationButton) { 
                        toolBar.getChildren().add(showPrevDoc); 
                    } 
                } 
            } 
          } 
        );
        
        // load the web page 
        //webEngine.load("http://www.oracle.com/products/index.html");
            
        //add components 
        getChildren().add(toolBar);
        //el objeto WebView que contiene el motor web se agrega a la escena
        getChildren().add(browser); 
        }
        
        // JavaScript interface object 
        public class JavaApp { 
            
            //metodo publico al que se puede acceder externamente, hace que la app JavaFX finalice
            public void exit() { 
            Platform.exit(); 
            } 
        }
        
        
        //createSpacer, layoutChildren, computePrefWidth y computePrefHeight metodos de diseñodel objeto WebView
        //y elementos de control de la barra de herramientas
        private Node createSpacer() { 
            Region spacer = new Region(); 
            HBox.setHgrow(spacer, Priority.ALWAYS); 
            return spacer; 
        }
        
        @Override 
        protected void layoutChildren() { 
            double w = getWidth(); 
            double h = getHeight(); 
            double tbHeight = toolBar.prefHeight(w); 
            layoutInArea(browser,0,0,w,h-tbHeight,0, HPos.CENTER, VPos.CENTER); 
            layoutInArea(toolBar,0,h- tbHeight,w,tbHeight,0,HPos.CENTER,VPos.CENTER); 
        }
        
        @Override 
        protected double computePrefWidth(double height) { 
            return 750; 
        } @Override 
        protected double computePrefHeight(double width) { 
            return 500; 
        }
        
    }
   
      
        
        
       

    
    

