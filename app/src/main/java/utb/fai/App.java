package utb.fai;

public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar telnet-client.jar <IP address> <port>");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Port must be an integer between 1 and 65535.");
            return;
        }

        if (port < 1 || port > 65535) {
            System.err.println("Port must be an integer between 1 and 65535.");
            return;
        }

        TelnetClient telnetClient = new TelnetClient(args[0], port);
        telnetClient.run();
    }
}
