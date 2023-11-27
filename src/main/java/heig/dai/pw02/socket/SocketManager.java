package heig.dai.pw02.socket;

import heig.dai.pw02.model.Message;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketManager implements Closeable {

    private final Socket socket;
    private final BufferedReader input;
    private final PrintWriter output;

    public SocketManager(Socket socket) {
        this.socket = socket;
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error while creating socket manager");
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    public void send(Message message) {
        output.println(message.toString());
        log.debug("Sent: {}", message);
    }

    public Message read() {
        try {
            String line = input.readLine();
            log.debug("Received: {}", line);
            return Message.parse(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
