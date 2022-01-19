import java.io.*;
import java.net.*;
public class GreetServer {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    public static String greeting;

    public static   String start(int port) throws IOException {
        System.out.println("server started");
            serverSocket = new ServerSocket(port);
             clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
              in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    return reader(in);}
    public static String reader(BufferedReader in) throws IOException {
             greeting = in.readLine();
             //gibt input vom client
    return greeting;}


    public static void stop() {
        try {


            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    public static void  restart(int port) throws IOException{
        System.out.println("restart");
        stop();
        start(port);
    }
}

