package heig.dai.pw02.socket;

import heig.dai.pw02.ccp.Message;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SocketManager implements Closeable {

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

    /**
     * Check if the socket is closed.
     *
     * @return true if the socket is closed, false otherwise
     */
    public boolean isClosed() {
        return socket.isClosed();
    }

    /**
     * Send a message to the socket.
     *
     * @param message the message to send
     */
    public void send(Message message) {
        output.println(message.toString());
        log.debug("Sent: {}", message);
    }

    /**
     * Read a message from the socket.
     *
     * @return the message read from the socket, or null if the socket was disconnected.
     */
    public Message read() {
        try {
            String line = input.readLine();
            log.debug("Received: {}", line);
            if (line == null) {
                return null;
            }

            return Message.parse(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}