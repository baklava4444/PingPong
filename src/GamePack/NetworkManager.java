package GamePack;

import java.io.*;
import java.net.*;

public class NetworkManager {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isHost;
    private boolean isConnected = false;

    // Interface to link network events back to your main Game Panel
    public interface NetworkListener {
        void onDataReceived(String data);
        void onConnected();
    }

    private NetworkListener listener;

    public NetworkManager(NetworkListener listener) {
        this.listener = listener;
    }

    // Called when a user clicks "HOST"
    public void startHost(int port) {
        this.isHost = true;
        // Run network setup in a background thread so the GUI doesn't freeze
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Server started. Waiting for client...");
                socket = serverSocket.accept(); // Stops here until client joins
                setupStreams();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Called when a user clicks "JOIN"
    public void startClient(String ip, int port) {
        this.isHost = false;
        new Thread(() -> {
            try {
                System.out.println("Connecting to host...");
                socket = new Socket(ip, port);
                setupStreams();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        isConnected = true;
        listener.onConnected();

        // Start a continuous loop thread to constantly listen for incoming data
        new Thread(() -> {
            try {
                String line;
                while (isConnected && (line = in.readLine()) != null) {
                    listener.onDataReceived(line);
                }
            } catch (IOException e) {
                System.out.println("Connection lost.");
            } finally {
                closeConnection();
            }
        }).start();
    }

    // Send a string of data to the other machine
    public void sendData(String data) {
        if (out != null && isConnected) {
            out.println(data);
        }
    }

    public boolean isHost() { return isHost; }
    public boolean isConnected() { return isConnected; }

    public void closeConnection() {
        try {
            isConnected = false;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
