
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class Vista extends Application{
    
    Button buttonLeer;
    Button buttonEscribir;
    private Scene escena;
    private GridPane grilla;
    private TextField escribirCampo;
    private Text leerText;
    Modelo modelo;
    
    public Vista(){
        setButtonLeer();
        setButtonEscribir();
        setGrilla();
        setTextLeer();
        setTextBoxEscribir();
    } 
    
    public void Empezar(String[]argumentos){
         launch(argumentos);//empieza a ejecutarce como javafx y va al metodo "start"
    }
    
    @Override
    public void start(Stage Escenario) throws Exception{
        Escenario.setTitle("Titulo");
        
        iniciarModelo();
        iniciarGrilla();//Pre-configuracion
        configurarGrilla();//Añadir o eliminar elementos
        setEscena();
        acciones();
        
        Escenario.setScene(getScena());//Le entregamos la escena al escenario
        Escenario.show();
    }
    
    public void configurarGrilla(){     
          Label escribir = new Label("Escribir");
          Label leer = new Label("Leer");
          
          grilla.add(leer, 0, 0);//Ultimos dos parametros, posicion en la grilla
          grilla.add(leerText, 1, 0);
          grilla.add(escribir, 0, 1);
          grilla.add(escribirCampo, 1, 1);
          grilla.add(buttonLeer, 0, 2);
          grilla.add(buttonEscribir, 1, 2);
          
    }
    
    public void iniciarGrilla(){ 
          grilla.setAlignment(Pos.CENTER);
          grilla.setHgap(10);//Espacio entre columnas y filas
          grilla.setVgap(10);
    }
    
    public GridPane getGrilla(){
        return this.grilla;
    }
    
     public void setGrilla(){
        grilla = new GridPane();
    }
    
    public void setButtonLeer(){
        buttonLeer = new Button("Leer");
    }
    
    public void setButtonEscribir(){
        buttonEscribir = new Button("Escribir");
    }
    
    public void setEscena(){
        escena = new Scene(getGrilla(),250,250);//Tamaño de pantalla
    }
    public Scene getScena(){
        return this.escena;
    }
    
    public void setTextLeer(){
        leerText = new Text("Lectura desde tarjeta...");
    }
    
     public void setTextLeer(String palabra){
        leerText.setText(palabra);
    }
    
    public void setTextBoxEscribir(){
        escribirCampo = new TextField("Ingresa el rut");
    }
    
    public void iniciarModelo(){
        modelo = new Modelo();
    }
    
    
  
    public void accionLeer(){
         buttonLeer.setOnAction(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent event){
                    try{
                            
                            modelo.leerTarjeta(modelo.lector);
                            setTextLeer(modelo.getRut());
                            leerText.setFill(Color.BLACK);
                       
                    }
                    catch(Exception e) {
                        leerText.setFill(Color.RED);  
                        leerText.setText("Error Lector no detectado");
                    }
                }
        });              
    }
    
    public void accionEscribir(){
        buttonEscribir.setOnAction(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent event){
                    try{
                       
                        if( !StringUtils.isNumeric(StringUtils.chop(escribirCampo.getText()))){
                            setTextLeer("Formato Incorrecto");
                            leerText.setFill(Color.RED);
                        }else if(escribirCampo.getText().length()!=9 && escribirCampo.getText().length()!=8){
                            leerText.setFill(Color.RED);  
                            setTextLeer("Rut Fuera de Rango");
                        }else {
                            modelo.escribirTarjeta(modelo.lector, escribirCampo.getText());
                            leerText.setFill(Color.BLACK);
                        }
                        }catch(Exception e){
                             leerText.setFill(Color.RED);  
                             leerText.setText("Error ");
                        }
             
                        
                }
        });   
    }
    
    public void acciones(){
        accionLeer();
        accionEscribir();
    }
}
