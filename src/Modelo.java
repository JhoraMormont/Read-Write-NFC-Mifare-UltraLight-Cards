import java.util.ArrayList;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;




public class Modelo {
	
	TerminalFactory factory;
        List<CardTerminal> terminals;
        CardTerminal lector;
        String rut;
    
	
    public Modelo(){
        
        try{
        	factory = TerminalFactory.getDefault();
                terminals = factory.terminals().list();
                lector = terminals.get(0);
                rut = "0";
            }
        catch(Exception e){
        	
        } 
 
    }
    
    public void bytesAhexa (byte [] lista){
        ArrayList  cadena2 = new ArrayList();
        
                for(int i=0;i<lista.length;i++){
                    //Imprimo los valores como entero positivos, por problemas de algunos bytes que tenian signo -
                    //System.out.println(lista[i]);
                        int a = Integer.valueOf(lista[i] & 0xFF);
                        //formateo del string, con %02 le indico que si viene un numero de 1 digito, le agrege un 0 al principio
                        cadena2.add(String.format("%02X", a));     
                } 
                for(int i=0;i<cadena2.size();i++){
                       System.out.print(cadena2.get(i)+" ");
                }
                System.out.println();
                hexaAascii(cadena2);
    }
     
    public void hexaAascii(ArrayList cadena) {   
    	rut = ""; 
        
        for(int i=0;i<cadena.size();i++){
           // System.out.print((char)Integer.parseInt((String)(cadena.get(i)), 16));  
            rut += (char)Integer.parseInt((String)(cadena.get(i)), 16);
            
        }
    }
    
    public String getRut(){
    	return this.rut;
    	}
    
    /*Convertir ascii a hexadecimal, con una salida de un array de bytes*/
    public byte[] asciiAhexa(String rut){
       byte[] data=rut.getBytes();
            for(int i=0;i<data.length;i++){
              String a=String.format("%02X",data[i]);
              int b=Integer.valueOf(a,16);
              data[i]=(byte)b;
            }     
    return data;
    }
    
    /*creo un commandAPDU a partir de la informacion que se quiera escribir*/
     public CommandAPDU[] crearCommandApduEscritura(String contenido){
      
       byte[] data = asciiAhexa(contenido);
       byte[] bloque1 = new byte[4];
       byte[] bloque2 = new byte[4];
       byte[] bloque3 = new byte[4];
       int j=0,x=0;
       
            for(int i=0;i<12;i++){
                if(i<4){
                    bloque1[i]=data[i];
                //    System.out.print("Bloque 1 "+data[i]);
                }
                else if(i>3 & i<8){
                    bloque2[j]=data[i];
                    j++;
                //      System.out.print("\nBloque 2 "+data[i]);
                }
                else if(i==8 & data.length==9){
                    bloque3[x]=data[i];
                    x++;
                //    System.out.print("\nBloque 3 "+data[i]);
                }
                else{
                    bloque3[x]=(byte)0x2E;
                //    System.out.print("\nBloque 3 "+bloque3[x]);
                    x++;
                }
            }
       CommandAPDU [] mensajes = new CommandAPDU[3];    
       CommandAPDU envio = new CommandAPDU((byte)0xFF,(byte)0xD6,(byte)0x00,(byte)0x04,bloque1);
       mensajes[0]=envio;
       CommandAPDU envio2 = new CommandAPDU((byte)0xFF,(byte)0xD6,(byte)0x00,(byte)0x05,bloque2);
       mensajes[1]=envio2;
       
     //  if(data.length==9){
            CommandAPDU envio3 = new CommandAPDU((byte)0xFF,(byte)0xD6,(byte)0x00,(byte)0x06,bloque3);
            mensajes[2]=envio3;
     //  }
       return  mensajes;
    }
    
    
    public void leerTarjeta(CardTerminal lector){
        
        byte read[] = {(byte) 0xFF, //CLA
                       (byte) 0xB0, //INS -> Instruccion a realizar
                       (byte) 0x00, //P1
                       (byte) 0x04, //P2 -> sector del cual ejecuto el comando
                       (byte) 0x0C};//LC field -> cantidad de bytes de la operacion 
        
        try{
         if(lector.waitForCardPresent(0)){
                        try{
                            Card tarjeta= lector.connect("*");
                            CardChannel canal = tarjeta.getBasicChannel();
                            CommandAPDU envio =new CommandAPDU(read);
                            ResponseAPDU respuesta=canal.transmit(envio);
                            bytesAhexa(respuesta.getData());
                            tarjeta.disconnect(false);
                            
                        }catch(CardNotPresentException e){
                            System.out.println("Error con tarjeta: "+e);
                        }
                    }
        }catch(CardException e){
              System.out.println("Error en metodo leerTarjeta: "+e);
        }
    }
    
    public void escribirTarjeta(CardTerminal lector,String dato){
            
        byte update[] = {(byte) 0xFF, //CLA
                         (byte) 0xD6, //INS -> Instruccion a realizar
                         (byte) 0x00, //P1
                         (byte) 0x04, //P2 -> sector del cual ejecuto el comando
                         (byte) 0x05,//LC field -> cantidad de bytes de la operacion
                         (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6F,(byte) 0x6F };
                         //informacion a escribir de acuerdo al
                         //tama�o escrito en lc
        
        try{
         if(lector.waitForCardPresent(0)){
                        try{
                            Card tarjeta= lector.connect("*");
                            CardChannel canal = tarjeta.getBasicChannel();
                            CommandAPDU[] envio =crearCommandApduEscritura(dato);
                                for(int i=0;i<3;i++){
                                    if(envio[i]!=null){
                                    ResponseAPDU respuesta=canal.transmit(envio[i]);
                                    System.out.println("\nSW1: "+Integer.toHexString(respuesta.getSW1())+
                                                        " SW2: "+String.format("%02x", respuesta.getSW2()));
                                    }
                                }
                            tarjeta.disconnect(false);
                            
                        }catch(CardNotPresentException e){
                            System.out.println("Error con tarjeta: "+e);
                        }
                    }
        }catch(CardException e){
              System.out.println("Error en metodo Update tarjeta: "+e);
        }
    }
}
