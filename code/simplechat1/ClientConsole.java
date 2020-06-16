// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;
import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF
{
  //Class variables *************************************************

  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;

  //Instance variables **********************************************

  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;


  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String id, String host, int port) {
    try {
      client = new ChatClient(id, host, port, this);
    } catch(IOException exception) {
      //System.out.println("Error: Can't setup connection!" + " Terminating client.");
      //System.exit(1);
      System.out.println("Cannot open connection. Awaiting command.");
    }
  }


  //Instance methods ************************************************

  /**
   * This method waits for input from the console.  Once it is
   * received, it sends it to the client's message handler.
   */
  public void accept()
  {
    try
    {
      BufferedReader fromConsole =
        new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true)
      {
        message = fromConsole.readLine();
        client.handleMessageFromClientUI(message);
      }
    }
    catch (Exception ex)
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message)
  {
    System.out.println("> " + message);
  }


  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args){
    String host = "";
    int port = 0;  //The port number
    String id = "";

    try {
      if (args[0].equalsIgnoreCase("-p") || args[0].equalsIgnoreCase("-h")){
        throw new Exception();
      } else {
        id = args[0];
      }

      if(args.length == 0 || args.length > 5 || (args.length % 2) == 0){
        throw new ArrayIndexOutOfBoundsException();
      }

      for (int i = 1; i < args.length - 1; i++){
        if (args[i].equals("-p")){
          port = Integer.parseInt(args[i+1]);
        } else if (args[i].equals("-h")){
          host = args[i+1];
        }
      }

      // If there was no host given, then it is set to the default
      if (host.equalsIgnoreCase("")){
        host = "localhost";
      }
      if (port < 1  && port > 99999){
        // According to the professor, a port is limited to 1 to 5 digits.
        port = DEFAULT_PORT; // If the port is not a valid number, it is set to the default value.
      }

      ClientConsole chat = new ClientConsole(id, host, port);
      chat.accept();  //Wait for console data

    } catch(ArrayIndexOutOfBoundsException e1) {
      System.out.println("ERROR - No login ID specified.  Connection aborted.");
      System.exit(1);
    } catch(NumberFormatException e2) {
      System.out.println("Error: Input format was incorrect.");
      System.exit(1);
    } catch(Exception e) {
      System.out.println("ERROR - Input format is incorrect.");
      System.exit(1);
    }
  }
}
//End of ConsoleChat class
