import java.io.*;
import common.*;

public class ServerConsole implements ChatIF {

  EchoServer server;

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
}
