package heig.dai.pw02.socket;

import java.io.*;
import java.net.Socket;

public class SocketManager {
    private final String ipAddress;
    private final int port;
    private final Socket socket;
    private final BufferedReader input;
    private final BufferedWriter output;
    private final static String EOT = "\u0004";

    public SocketManager(String ipAddress, int port) throws IOException {
        this.port = port;
        this.ipAddress = ipAddress;
        this.socket = createSocket(ipAddress, port);
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.output = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

    }

    private Socket createSocket(String ipAddress, int port) throws IOException {
        return new Socket(ipAddress, port);
    }

    public boolean isConnected() {
        return this.socket.isConnected();
    }

    public void closeSocket() throws IOException {
        this.socket.close();
        this.input.close();
        this.output.flush();
        this.output.close();
    }

    public void send(String message) throws IOException {
        // Maybe make a MessageBuilder class to build messages ?
        this.output.write(message);
        this.output.flush();
    }

    public void read() throws IOException {
        // Maybe fixed size messages ?
        String line;
        while ((line = input.readLine()) != null && !line.equals(EOT)) {
            // Do something with the message
        }
    }

}
