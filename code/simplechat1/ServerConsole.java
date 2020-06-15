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

        String str = message.trim();
        if (str.charAt(0) == '#') {
          processCMD(str);
        } else {
          server.sendToAllClients("SERVER MSG> " + message);
        }
      }
    } catch (Exception ex) {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  private void processCMD(String str) {

    if (str.equalsIgnoreCase("#quit")) {
      try {
        server.close();
      } catch (IOException e){
        // Nothing is done if there is an error.
      } finally {
        System.out.println("Terminating the server.");
        System.exit(0);
      }

    } else if (str.equalsIgnoreCase("#stop")) {
      server.stopListening();
      //server.serverStopped();
      System.out.println("The server has stopped listening for clients.");

    } else if (str.equalsIgnoreCase("#close")) {
      try {
        server.close();
      } catch (IOException e){
        // Nothing is done if there is an error.
      } finally {
        System.out.println("The server has closed.");
      }

    } else if (str.contains("setport")) {
      if (!server.isListening() && server.getNumberOfClients() == 0){
        String[] array = str.split(" ");
        if (array.length == 2) {
          try{
            server.setPort(Integer.parseInt(array[1]));
            System.out.println("The port has now been set to " + server.getPort() + ".");
          } catch (NumberFormatException e){
            System.out.println("Error: port value is not an integer.");
          }
        } else {
          System.out.println("Error: Command format is incorrect.");
        }
      } else {
        System.out.println("Error: The port cannot be set because the server is not closed.");
      }


    } else if (str.equalsIgnoreCase("#start")) {
      if (!server.isListening()){
        try {
          server.listen();
          System.out.println("The server is listening for clients.");
        } catch (IOException e){
          System.out.println("An IO Exception occured.");
        }

      } else {
        System.out.println("The server is already listening for clients.");
      }

    } else if (str.equalsIgnoreCase("#getport")) {
      System.out.println("The port is " + server.getPort());

    } else {
      System.out.println("The command was not recognized.");
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
