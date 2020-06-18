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
 * @author Ann Soong (For Assignment 1)
 * @version June 2020
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
      System.out.println("Error: Can't setup connection!" + " Terminating client.");
      System.exit(1);
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
   * The parameters may vary:
   *   The ID of the client is mandatory.
   *   If the user wishes to specify the host, the host name needs to
   *    be preceeded by "-h". Otherwise, the default is "localhost".
   *   If the user wishes to specify the port, the port needs to be
   *    be preceeded by "-p". Otherwise, the default is 5555.
   * The order of host and port do not matter.
   *
   * @param args[0] The ID of the client
   */
  public static void main(String[] args){
    String host = ""; // The host name
    int port = 0;  // The port number
    String id = ""; // The ID of the client.

    try {
      // Make sure the first parameter is the ID of the client.
      if (args[0].equalsIgnoreCase("-p") || args[0].equalsIgnoreCase("-h")){
        throw new Exception(); // If not, then it's an error.
      } else {
        id = args[0];
      }

      // The argument array can only be certain lengths. The format can be:
      // ClientConsole <loginID> -h <host> -p <port>
      // ClientConsole <loginID> -p <port> -h <host>
      // ClientConsole <loginID> -h <host>
      // ClientConsole <loginID> -p <port>
      // ClientConsole <loginID>
      // So the lengths can only be 1, 3, 5. Everything else is incorrect.
      if(args.length == 0 || args.length > 5 || (args.length % 2) == 0){
        throw new ArrayIndexOutOfBoundsException();
      }

      // This loop finds possible port and host given in the command line.
      for (int i = 1; i < args.length - 1; i++){
        if (args[i].equals("-p")){
          // -p must preceed the port number.
          port = Integer.parseInt(args[i+1]);
        } else if (args[i].equals("-h")){
          // -h must preceed the host name.
          host = args[i+1];
        }
      }

      // If there was no host given, then it is set to the default.
      if (host.equalsIgnoreCase("")){
        host = "localhost";
      }
      if (port < 1 || port > 99999){
        // According to the professor, a port is limited to 1 to 5 digits.
        // If the port is not a valid number, it is set to the default value.
        port = DEFAULT_PORT;
      }

      ClientConsole chat = new ClientConsole(id, host, port);
      chat.accept();  //Wait for console data

    } catch(ArrayIndexOutOfBoundsException e1) {
      System.out.println("ERROR - No login ID specified.  Connection aborted.");
      System.exit(1);
    } catch(NumberFormatException e2) {
      System.out.println("ERROR - Invalid port value.");
      System.exit(1);
    } catch(Exception e) {
      System.out.println("ERROR - Input format is incorrect.");
      System.exit(1);
    }
  }
}
//End of ConsoleChat class
