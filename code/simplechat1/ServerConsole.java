// This file was made for SEG 2105 - Assignment 1

import java.io.*;
import common.*;

/**
 * This class constructs the UI for EchoServer. It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here was cloned from ClientConsole
 * and EchoServer.
 *
 * @author Ann Soong (For Assignment 1)
 * @version June 2020
 */
public class ServerConsole implements ChatIF {

  //Variables *******************************************************

  /**
   * The default port to connect on.
   */
  final private static int DEFAULT_PORT = 5555;

  /**
   * The instance of the EchoServer.
   */
  private EchoServer server;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param port The port to connect on.
   */
  public ServerConsole(int port) {
    // The EchoServer is created.
    server = new EchoServer(port, this);
    try {
      server.listen(); // Start listening for connections
    } catch (Exception ex) {
      System.out.println("ERROR - Could not listen for clients!");
      System.exit(1);
    }
  }

  //Instance methods ************************************************

  /**
   * This method waits for input from the console. Once it is
   * received, it sends it to the server's message handler.
   * This code was taken from ClientConsole.
   */
  public void obtainInput() {
    try {
      BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true) {
        message = fromConsole.readLine();
        server.handleMessageFromServerUI(message);
      }
    } catch (Exception ex) {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface. It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display (String message) {
    System.out.println("SERVER MSG> " + message);
  }

  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of Server UI.
   * This code mimics the main function in EchoServer.
   *
   * @param args[0] The port number to listen on. Defaults to 5555
   *                if no argument is entered.
   */
  public static void main(String[] args) {
    int port = 0; // Port to listen on

    try {
      port = Integer.parseInt(args[0]); // Get port from command line
    } catch(Throwable t) {
      port = DEFAULT_PORT; // Set port to 5555
    }

    // The server UI is created.
    ServerConsole console = new ServerConsole(port);
    console.obtainInput();
  }
}
