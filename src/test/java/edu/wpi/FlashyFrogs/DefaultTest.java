/*-------------------------*/
/* DO NOT DELETE THIS TEST */
/*-------------------------*/

package edu.wpi.FlashyFrogs;

import com.fazecast.jSerialComm.SerialPort;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DefaultTest {

  @SneakyThrows
  @Test
  public void test() {

    SerialPort[] ports = SerialPort.getCommPorts();
    if (ports.length != 0) {

      ports[0].setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
      ports[0].setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 30); // Blocking write

      if (ports[0].openPort(16)) {
        int numMessages = 3;
        byte[] bytes = {
          (byte) (numMessages / 1000 % 10),
          (byte) (numMessages / 100 % 10),
          (byte) (numMessages / 10 % 10),
          (byte) (numMessages % 10),
          10
        };
        System.out.println(bytes.toString());
        ports[0].writeBytes(bytes, bytes.length);

        numMessages = 1000;
        byte[] bytes2 = {
          (byte) (numMessages / 1000 % 10),
          (byte) (numMessages / 100 % 10),
          (byte) (numMessages / 10 % 10),
          (byte) (numMessages % 10),
          10
        };
        System.out.println(bytes2.toString());
        ports[0].writeBytes(bytes2, bytes2.length);

        numMessages = 1000;
        byte[] bytes3 = {
          (byte) (numMessages / 1000 % 10),
          (byte) (numMessages / 100 % 10),
          (byte) (numMessages / 10 % 10),
          (byte) (numMessages % 10),
          10
        };
        System.out.println(bytes3.toString());
        ports[0].writeBytes(bytes3, bytes3.length);

        numMessages = 1200;
        byte[] bytes4 = {
          (byte) (numMessages / 1000 % 10),
          (byte) (numMessages / 100 % 10),
          (byte) (numMessages / 10 % 10),
          (byte) (numMessages % 10),
          10
        };
        System.out.println(bytes4.toString());
        ports[0].writeBytes(bytes4, bytes2.length);

        numMessages = 1100;
        byte[] bytes5 = {
          (byte) (numMessages / 1000 % 10),
          (byte) (numMessages / 100 % 10),
          (byte) (numMessages / 10 % 10),
          (byte) (numMessages % 10),
          10
        };
        System.out.println(bytes5.toString());
        ports[0].writeBytes(bytes5, bytes3.length);

        numMessages = 1300;
        byte[] bytes6 = {
          (byte) (numMessages / 1000 % 10),
          (byte) (numMessages / 100 % 10),
          (byte) (numMessages / 10 % 10),
          (byte) (numMessages % 10),
          10
        };
        System.out.println(bytes6.toString());
        ports[0].writeBytes(bytes6, bytes2.length);

        numMessages = 1300;
        byte[] bytes7 = {
          (byte) (numMessages / 1000 % 10),
          (byte) (numMessages / 100 % 10),
          (byte) (numMessages / 10 % 10),
          (byte) (numMessages % 10),
          10
        };
        System.out.println(bytes7.toString());
        ports[0].writeBytes(bytes7, bytes3.length);
      }
    }

    //    boolean wait = true;
    //    while (wait) {
    //      byte[] bytes = new byte[4];
    //      if (ports[0].readBytes(bytes, 4, 0) > 0) {
    //        wait = false;
    //      }
    //      for (int i = 0; i < bytes.length; i++) {
    //        System.out.println(bytes[i]);
    //      }
    //    }

    ports[0].closePort();
  }
}
