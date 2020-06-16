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
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************

  /**
   * The interface type variable.  It allows the implementation of
   * the display method in the client.
   */
  ChatIF clientUI;

  int id;


  //Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */

  public ChatClient(String host, int port, ChatIF clientUI)
    throws IOException
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  public ChatClient(String id, String host, int port, ChatIF clientUI) throws IOException {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.id = id;
    openConnection();
    if(isConnected()){
      sendToServer("#login " + id);
    }
  }


  //Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg)
  {
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
        processCMD(str);
      } else {
        sendToServer(message);
      }
    }
    catch (IOException e)
    {
      clientUI.display
      ("Could not send message to server. Terminating client.");
      quit();
    }
  }

  private void processCMD(String str) throws IOException {

    if (str.equalsIgnoreCase("#quit")) {
      System.out.println("Terminating the program.");
      this.quit();

    } else if (str.equalsIgnoreCase("#logoff")) {
      System.out.println("Logging off the server.");
      this.closeConnection();

    } else if (str.contains("sethost")) {
      if (!this.isConnected()){
        String[] split = str.split(" ");
        if (split.length == 2) {
          this.setHost(split[1]);
          System.out.println("The host has now been set to " + this.getHost() + ".");
        } else {
          System.out.println("Error: Command format is incorrect.");
        }
      } else {
        System.out.println("Error: The host cannot be set because the client is still connected to the server.");
      }


    } else if (str.contains("setport")) {
      if (!this.isConnected()) {
        String[] split = str.split(" ");
        if (split.length == 2) {
          try{
            this.setPort(Integer.parseInt(split[1]));
            System.out.println("The port has now been set to " + this.getPort() + ".");
          }
          catch (NumberFormatException e) {
            System.out.println("Error: Port value is not an integer.");
          }
        } else {
          System.out.println("Error: Command format is incorrect.");
        }
      } else {
        System.out.println("Error: The host cannot be set because the client is still connected to the server.");
      }


    } else if (str.equalsIgnoreCase("#login")) {
      if (this.isConnected()) {
        System.out.println("The client is still connected to a server. Cannot connect to another server.");
      } else {
        System.out.println("Logging in to the server.");
        this.openConnection();
        if (this.isConnected()){
          System.out.println("The login was successful.");
          sendToServer("#login " + id);
        } else {
          System.out.println("The login was unsuccessful.");
        }
      }

    } else if (str.equalsIgnoreCase("#gethost")) {
      System.out.println("The host is " + this.getHost() + ".");

    } else if (str.equalsIgnoreCase("#getport")) {
      System.out.println("The port is " + this.getPort() + ".");

    } else {
      System.out.println("The command was not recognized.");
    }
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  //My programming ************************************************

  public void connectionClosed() {
    System.out.println("The connection to the server has closed.");
  }

  public void connectionException(Exception exception) {
    //System.out.println(exception.toString());
    /*if (exception instanceof java.net.SocketException) {
      System.out.println("The server has shut down.");
    }
    else {
      System.out.println("Error.");
    }*/
    System.out.println("The server has shut down.");
    this.quit();
  }
}
//End of ChatClient class
