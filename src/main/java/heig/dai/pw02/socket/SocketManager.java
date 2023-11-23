package heig.dai.pw02.socket;

import heig.dai.pw02.model.Message;

import java.io.*;
import java.net.Socket;

public class SocketManager {
    private final Socket socket;
    private final BufferedReader input;
    private final BufferedWriter output;
    private final static String EOT = "\u0004";
    private final static String BREAK = "\n";

    public SocketManager(Socket socket) {
        this.socket = socket;
        try {
            this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.output = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isConnected() {
        return this.socket.isConnected();
    }

    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    public void closeSocket() throws IOException {
        this.socket.close();
        this.input.close();
        this.output.flush();
        this.output.close();
    }

    public void send(Message message) {
        try{
            this.output.write(message.toString() + BREAK);
            this.output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Message read(){
        try{
            String line = input.readLine();
            return Message.parse(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
