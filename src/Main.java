import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Main {
    public static void main(String [] args){
        Vista vista= new Vista();
        Controlador c = new Controlador(vista,args);
    }
}
