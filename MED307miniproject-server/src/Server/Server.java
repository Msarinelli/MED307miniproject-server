package Server;

import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    ArrayList clientOutputStreams;
    ArrayList<String> onlineUsers = new ArrayList();
    
    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        PrintWriter client;
        
        public ClientHandler (Socket clientSocket, PrintWriter user) {
            client = user;
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            }
            catch(Exception ex) {
                System.out.println("Error beginning Streamreader");
            }
        }
        
        public void run(){
            String message;
            String[] data;
            String connect = "Connect";
            String disconnect = "Disconnect";
            String chat = "Chat";
            
            try {
                while ((message = reader.readLine()) != null) {
                    
                    System.out.println("Recieved: " + message);
                    data = message.split("å");
                    for (String token:data) {
                        
                        System.out.println(token);
                        
                    }
                    
                    if (data[2].equals(connect)) {
                        
                        tellEveryone((data[0] + "å" + data[1] + "å" + chat));
                        userAdd(data[0]);
                        
                    } else if (data[2].equals(disconnect)) {
                        
                        tellEveryone((data[0] + "åhas disconnected." + "å" + chat));
                        userRemove(data[0]);
                                
                    } else if (data[2].equals(chat)) {
                        
                        tellEveryone(message);
                        
                    } else {
                        System.out.println("No Conditions were met.");
                    }
                }
            }
            catch (Exception ex) {
                System.out.println("lost a connection");
                clientOutputStreams.remove(client);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Server().go();
    }
    
    public void go() {
        clientOutputStreams = new ArrayList();
        
        try {
            
            ServerSocket serverSock = new ServerSocket(8000);
            
            while (true) {
                
                Socket clientSock = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                clientOutputStreams.add(writer);
                
                Thread listener = new Thread(new ClientHandler (clientSock, writer));
                listener.start();
                System.out.println("got a connection");
            }
        }
        catch (Exception ex) {
            
            System.out.println("Error making a connection");
        }
    }
    
    public void userAdd (String data) {
        String message;
        String add = "å åConnect", done = "Serverå åDone";
        onlineUsers.add(data);
        String[] tempList = new String[(onlineUsers.size())];
        onlineUsers.toArray(tempList);
        
        for (String token:tempList) {
            
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }
    
    
    public void userRemove (String data) {
        String message;
        String add = "å åConnect", done = "Serverå åDone";
        onlineUsers.remove(data);
        String[] tempList = new String[(onlineUsers.size())];
        onlineUsers.toArray(tempList);
        
        for (String token:tempList) {
            
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }
    
    public void tellEveryone(String message) {
        
        Iterator it = clientOutputStreams.iterator();
        
        while(it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                System.out.println("Sending" + message);
                writer.flush();
            }
            catch(Exception ex) {
                System.out.println("Error telling everyone");
            }
        }
    }
    
}
