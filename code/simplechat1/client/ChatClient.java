// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Ann Soong (For Assignment 1)
 * @version June 2020
 */
public class ChatClient extends AbstractClient {
  //Variables *******************************************************

  /**
   * The interface type variable.  It allows the implementation of
   * the display method in the client.
   */
  ChatIF clientUI;

  /**
   * Private integer to hold the client ID.
   */
  private String id;


  //Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param id The ID of the client.
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  public ChatClient(String id, String host, int port, ChatIF clientUI) throws IOException {
    super(host, port); // Call the superclass constructor
    this.clientUI = clientUI;
    this.id = id;
    try {
      openConnection();
    } catch (IOException e) {
      System.out.println("Cannot open connection. Awaiting command.");
    }

  }


  //Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI.
   */
  public void handleMessageFromClientUI(String message) {
    try {
      String str = message.trim();
      if (str.charAt(0) == '#'){
        // If the message starts with "#", then it's a command.
        // Process the command using the helper function.
        processCMD(str);
      } else { // Otherwise, it is a message to send to server.
        sendToServer(message);
      }
    }
    catch (IOException e) {
      clientUI.display("Could not send message to server. Terminating client.");
      quit();
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
      this.quit();

    } else if (command.equalsIgnoreCase("#logoff")) {
      try { // Attempt to close client's connection.
        this.closeConnection();
      } catch (IOException e1){
        System.out.println("ERROR - Client close was unsuccessful.");
      }

    } else if (command.contains("sethost")) {
      // The client can only set its port if it is logged off.
      if (!this.isConnected()){
        String[] array = command.split(" ");
        // There should only be 2 parts to the command: "#sethost" and <host>
        if (array.length == 2) {
          this.setHost(array[1]);
          System.out.println("Host set to: " + this.getHost() + ".");
        } else { // If the command exceeded 2 parts, then it is incorrect.
          System.out.println("ERROR - Command format is incorrect.");
        }
      } else {
        System.out.println("ERROR - The client is still connected to server.");
      }

    } else if (command.contains("setport")) {
      // The client can only set its port if it is logged off.
      if (!this.isConnected()) {
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
              System.out.println("Port set to: " + this.getPort() + ".");
            }
          }
          catch (NumberFormatException e) {
              // If the port can't be parsed into an integer, then it is an error.
            System.out.println("ERROR - Port value is not an integer.");
          }
        } else { // If the command exceeded 2 parts, then it is incorrect.
          System.out.println("ERROR - Command format is incorrect.");
        }
      } else {
        System.out.println("ERROR - The client is still connected to server.");
      }


    } else if (command.equalsIgnoreCase("#login")) {
      if (this.isConnected()) {
        System.out.println("ERROR - The client is still connected to a server.");
      } else {
        try {
          this.openConnection();
        } catch (IOException e2) {
          System.out.println("ERROR - Client was unable to login.");
        }
      }

    } else if (command.equalsIgnoreCase("#gethost")) {
      System.out.println("The host is " + this.getHost() + ".");

    } else if (command.equalsIgnoreCase("#getport")) {
      System.out.println("The port is " + this.getPort() + ".");

    } else {
      System.out.println("The command was not recognized.");
    }
  }

  /**
   * This method terminates the client.
   */
  public void quit() {
    try {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  /**
	 * This method is called after the connection has been closed.
	 */
  public void connectionClosed() {
    System.out.println("Connection closed.");
  }

  /**
	 * This method is called after a connection has been established.
	 */
	protected void connectionEstablished() {
    try {
      // If the client connects to the server, then send login ID.
      sendToServer("#login " + this.id);
      // Notifies the user that ChatClient is now logged on.
      System.out.println(this.id + " has logged on.");
    } catch (IOException e) {
      System.out.println("Cannot open connection. Awaiting command.");
    }
	}

  /**
	 * This method is called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server.
	 *
	 * @param exception the exception raised.
	 */
  public void connectionException(Exception exception) {
    System.out.println("Abnormal termination of connection.");
    //this.quit();
  }
}
//End of ChatClient class
