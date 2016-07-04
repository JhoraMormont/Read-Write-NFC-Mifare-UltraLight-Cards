import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Controlador { 
    
    Vista vista;
    
    public Controlador(Vista vista,String[] arg){
        vista.Empezar(arg);
    }
 
   
}