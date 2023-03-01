/*-------------------------*/
/* DO NOT DELETE THIS TEST */
/*-------------------------*/

package edu.wpi.FlashyFrogs;

import com.fazecast.jSerialComm.SerialPort;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class DefaultTest {

  @Test
  public void test() {
    SerialPort[] ports = SerialPort.getCommPorts();

    if (ports.length != 0) {

      Node node = new Node("02X1000Y1000", "hello", Node.Floor.L1, 1000, 1000);

      ports[0].setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
      ports[0].setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 100); // Blocking write

      //        for (Node node : currentPath) {
      if (ports[0].isOpen() || ports[0].openPort()) {
        //            System.out.println("Port opened successfully");
        String send = node.getId() + "\0";
        byte[] bytes = send.getBytes(StandardCharsets.US_ASCII);
        for (int i = 0; i < bytes.length; i++) {
          System.out.print(bytes[i] + " ");
        }
        System.out.println();
        ports[0].writeBytes(bytes, bytes.length);
        bytes = send.getBytes(StandardCharsets.US_ASCII);
        ports[0].writeBytes(bytes, bytes.length);
      } else {
        System.out.println("Failed to open port");
        System.out.println(ports[0].getLastErrorCode());
        //          }
      }

      if (ports[0].isOpen() || ports[0].openPort()) {
        String endMessage = "endMessage00\0";
        byte[] bytes = endMessage.getBytes(StandardCharsets.US_ASCII);
        ports[0].writeBytes(bytes, bytes.length);
      }

      boolean wait = true;
      int count = 0;
      while (wait) {
        byte[] bytes = new byte[12];
        int num = ports[0].readBytes(bytes, 12);
        if (num != 0) {
          count++;
          for (int i = 0; i < bytes.length; i++) {
            System.out.print(bytes[i] + " ");
          }
          System.out.println();
          if (count > 1) wait = false;
        }
      }

      ports[0].closePort();
      //        System.out.println("Port closed");
    }
  }
}
