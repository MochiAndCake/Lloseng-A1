import java.io.*;
import common.*;

public class ServerConsole implements ChatIF {

  final public static int DEFAULT_PORT = 5555;

  EchoServer server;

  public ServerConsole(int port) {
    server = new EchoServer(port);

    try {
      server.listen(); //Start listening for connections
    } catch (Exception ex) {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }

  public void obtainInput() {
    try {
      BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true) {
        message = fromConsole.readLine();
        server.sendToAllClients("SERVER MSG>" + message);
      }
    } catch (Exception ex) {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  public void display (String message) {
    System.out.println("SERVER MSG> " + message);
  }

  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555
   *          if no argument is entered.
   */
  public static void main(String[] args) {
    int port = 0; //Port to listen on

    try {
      port = Integer.parseInt(args[0]); //Get port from command line
    } catch(Throwable t) {
      port = DEFAULT_PORT; //Set port to 5555
    }

    ServerConsole console = new ServerConsole(port);
    console.obtainInput();
  }
}
