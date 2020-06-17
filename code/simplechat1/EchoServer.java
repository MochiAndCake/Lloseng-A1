// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @author Ann Soong (For Assignment 1)
 * @version June 2020
 */
public class EchoServer extends AbstractServer
{
  //Variables *******************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  /**
   * The server UI.
   * It could have been declared as a ChatIF, but then EchoServer needs
   * to implement the common package.
   */
  private ServerConsole serverUI;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port)
  {
    super(port);
  }

  public EchoServer(int port, ServerConsole serverUI){
    super(port);
    this.serverUI = serverUI;
  }


  //Instance methods ************************************************

  public void handleMessageFromServerUI(String message) {
    String str = message.trim();
    if (str.charAt(0) == '#') {
      // If the message starts with "#", then it's a command.
      // Process the command using the helper function.
      processCMD(str);
    } else { // Otherwise, it is a message to send echo to clients.
      this.sendToAllClients("SERVER MESSAGE> " + message);
      serverUI.display(message);
    }
  }

  /**
   * A private helper method used to process command strings recieved
   * from the Server UI.
   *
   * @param command The string command recieved
   */
  private void processCMD(String command) {

    if (command.equalsIgnoreCase("#quit")) {
      try { // Attempt to close the server and its connections.
        this.stopListening();
        this.close();
        System.out.println("Terminating the program.");
        System.exit(0); // The server terminates gracefully.
      } catch (IOException e){
        System.out.println("ERROR - Server quit was unsuccessful.");
      }

    } else if (command.equalsIgnoreCase("#stop")) {
      // The server stops listening for clients.
      this.stopListening();

    } else if (command.equalsIgnoreCase("#close")) {
      try { // Attempt to close the server and its connections.
        this.close();
        System.out.println("The server has closed.");
      } catch (IOException e){
        System.out.println("ERROR - Server close was unsuccessful.");
      }

    } else if (command.contains("setport")) {
      // The server can only set its port if it is closed.
      // That means the server is not listening for clients and has
      // disconnected from all clients.
      if (!this.isListening() && this.getNumberOfClients() == 0){
        String[] array = command.split(" ");
        // There should only be 2 parts to the command: "#setport" and <port>
        if (array.length == 2) {
          try{
            // We attempt to parse the port into an integer.
            int newport = Integer.parseInt(array[1]);

            // The port should only be 1 to 5 digits long.
            if(newport < 1 || newport > 99999){
              System.out.println("ERROR - Port number is out of bounds.");

            } else { // Otherwise, the port is succesfully set.
              this.setPort(newport);
              System.out.println("The port has now been set to " + this.getPort() + ".");
            }
          } catch (NumberFormatException e){
            // If the port can't be parsed into an integer, then it is an error.
            System.out.println("ERROR - Given port value is not an integer.");
          }
        } else { // If the command exceeded 2 parts, then it is incorrect.
          System.out.println("ERROR - Command format is incorrect.");
        }
      } else { // Command cannot be used if server is not closed.
        System.out.println("ERROR - The server is not closed.");
      }

    } else if (command.equalsIgnoreCase("#start")) {
      // Server can only be started if it's been stopped.
      if (!this.isListening()){
        try {
          this.listen();
          System.out.println("The server is listening for new clients.");
        } catch (IOException e){
          System.out.println("ERROR - Server was unable to start.");
        }
      } else {
        System.out.println("ERROR - Server is already listening for clients.");
      }

    } else if (command.equalsIgnoreCase("#getport")) {
      System.out.println("The port is " + this.getPort() + ".");

    }
  }

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    // When the client first connects, they will have no ID.
    if (client.getInfo("ID") == null){
      // Then their first message to server should be their ID.
      if ( ((String)msg).contains("#login") ){
        String[] split = ((String)msg).trim().split(" ");
        client.setInfo("ID", split[1]);

        // We notify the Server user that the client has successfully logged in.
        System.out.println(client.getInfo("ID") + " has logged on.");

      } else {
        try {
          // The server will send an error message to the client.
          // The server will then terminate the connection to the client.
          client.sendToClient("ERROR - Missing client ID.");
          client.close();
        } catch (IOException e1){
          System.out.println("ERROR - Could not send message to client.");
        }
      }

    } else {
      // If this is not the client's first time connecting, they cannot login again.
      if ( ((String)msg).contains("#login") ){
        try {
          client.sendToClient("ERROR - Login can can only be used when the client first connects.");
        } catch (IOException e1){
          System.out.println("ERROR - Could not send message to client.");
        }

      } else {
        // Otherwise, the message is relayed to all with the client ID.
        // Server prints the message: "Message received: <user input> from <loginID>"
        System.out.println("Message received: " + msg + " from " + client.getInfo("ID"));
        this.sendToAllClients(client.getInfo("ID") + "> " + msg);
      }
    }
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
    // A nice message is printed when a client connects to server.
    System.out.println("A new client has connected. Welcome!");
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    // A nice message is printed when a client disconnects from server.
    System.out.println("A client has disconnected. Have a nice day!");
  }

  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
    System.out.println("There was a connection exception.");
  }

  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555
   *          if no argument is entered.
   */
  public static void main(String[] args)
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }

    EchoServer sv = new EchoServer(port);

    try
    {
      sv.listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
