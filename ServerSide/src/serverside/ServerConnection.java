/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverside;

import com.sun.corba.se.spi.activation.Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author HP
 */
public class ServerConnection extends Thread {
    
    Socket socket;
    ServerSide server;
    DataInputStream din;
    DataOutputStream dout;
    String name;
    boolean shouldRun=true;
    
    public ServerConnection(Socket socket,ServerSide server){
        super("ServerConnectionTread");
        this.socket=socket;
        this.server=server;
        
    }
    
    public void sendStringToClient(String text){
        try{
            dout.writeUTF(text);
            dout.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void sendStringToAllClients(String text){
        for(int index=0;index<server.connections.size();index++){
            ServerConnection sc=server.connections.get(index);
            sc.sendStringToClient(text);
            
        }
    }
    
    public void run() {
        try {
            din=new DataInputStream(socket.getInputStream());
            dout=new DataOutputStream(socket.getOutputStream());
            
            while(shouldRun){
                while(din.available() == 0){
                    try {
                        Thread.sleep(1);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
                
                String textIn = din.readUTF();
                sendStringToAllClients(textIn);
                if(textIn.contains("BYE")){
                    System.out.println("Disconnect user");
                    shouldRun=false;
                    server.connections.remove(this);
                    System.out.println("The size of server connection is :"+server.connections.size());
                    
                    
                }
                
            }
            
            din.close();
            dout.close();
            socket.close();
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
}
