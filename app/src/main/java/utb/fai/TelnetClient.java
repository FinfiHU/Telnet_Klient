package utb.fai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class TelnetClient {

    private final String serverIp;
    private final int port;

    public TelnetClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void run() {
        try (Socket socket = new Socket(serverIp, port)) {
            Thread sender = new Thread(() -> sendInput(socket), "telnet-sender");
            Thread receiver = new Thread(() -> receiveOutput(socket), "telnet-receiver");
            sender.setDaemon(true);

            sender.start();
            receiver.start();
            receiver.join();
        } catch (IOException e) {
            System.err.println("Telnet connection failed: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendInput(Socket socket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            OutputStream output = socket.getOutputStream();
            String line;
            while ((line = input.readLine()) != null) {
                if ("/QUIT".equals(line)) {
                    break;
                }

                output.write(line.getBytes(Charset.defaultCharset()));
                output.write(System.lineSeparator().getBytes(Charset.defaultCharset()));
                output.flush();
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.err.println("Failed to send data: " + e.getMessage());
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close the connection: " + e.getMessage());
            }
        }
    }

    private void receiveOutput(Socket socket) {
        try {
            InputStream input = socket.getInputStream();
            byte[] buffer = new byte[4096];

            while (!socket.isClosed()) {
                int available = input.available();
                if (available > 0) {
                    int bytesRead = input.read(buffer, 0, Math.min(available, buffer.length));
                    if (bytesRead == -1) {
                        break;
                    }
                    System.out.write(buffer, 0, bytesRead);
                    System.out.flush();
                } else {
                    Thread.sleep(20);
                }
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.err.println("Failed to receive data: " + e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
