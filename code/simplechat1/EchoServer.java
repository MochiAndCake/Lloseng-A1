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
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

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
      processCMD(str);
    } else {
      this.sendToAllClients("SERVER MSG> " + message);
    }
  }

  private void processCMD(String str) {

    if (str.equalsIgnoreCase("#quit")) {
      try {
        this.close();
      } catch (IOException e){
        // Nothing is done if there is an error.
      } finally {
        System.out.println("Terminating the server.");
        System.exit(0);
      }

    } else if (str.equalsIgnoreCase("#stop")) {
      this.stopListening();
      //server.serverStopped();
      System.out.println("The server has stopped listening for clients.");

    } else if (str.equalsIgnoreCase("#close")) {
      try {
        this.close();
      } catch (IOException e){
        // Nothing is done if there is an error.
      } finally {
        System.out.println("The server has closed.");
      }

    } else if (str.contains("setport")) {
      if (!this.isListening() && this.getNumberOfClients() == 0){
        String[] array = str.split(" ");
        if (array.length == 2) {
          try{
            this.setPort(Integer.parseInt(array[1]));
            System.out.println("The port has now been set to " + this.getPort() + ".");
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
      if (!this.isListening()){
        try {
          this.listen();
          System.out.println("The server is listening for clients.");
        } catch (IOException e){
          System.out.println("An IO Exception occured.");
        }

      } else {
        System.out.println("The server is already listening for clients.");
      }

    } else if (str.equalsIgnoreCase("#getport")) {
      System.out.println("The port is " + this.getPort() + ".");

    } else {
      System.out.println("The command was not recognized.");
    }
  }

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
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
    System.out.println("Welcome!");
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println("Have a nice day!");
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
    System.out.println("Have a nice day!");
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
