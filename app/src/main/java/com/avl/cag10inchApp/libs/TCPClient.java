package com.avl.cag10inchApp.libs;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPClient implements Runnable {

    private static final long RECONNECTION_DELAY = 5000; // 5 seconds
    private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds

    private String serverIp;
    private int serverPort;
    private final String tag;
    private OnMessageReceived messageListener;
    private OnConnectionStatusChanged connectionListener;
    private volatile boolean running = false;
    private PrintWriter bufferOut;
    private OutputStream outputStream;
    private BufferedReader bufferIn;
    private Socket socket;
    private Thread clientThread;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

    public interface OnMessageReceived {
        void onMessageReceived(String message);
    }

    public interface OnConnectionStatusChanged {
        void onConnected();
        void onDisconnected();
    }

    public TCPClient(String tag, OnMessageReceived listener, OnConnectionStatusChanged connectionListener) {
        this.tag = tag;
        this.messageListener = listener;
        this.connectionListener = connectionListener;
    }

    public void connect(String ip, int port) {
        if (ip == null || ip.isEmpty()) {
            Log.e(tag, "Connect failed: IP is null or empty");
            return;
        }

        // If already connecting/connected to the SAME ip and port, do nothing
        if (running && ip.equals(serverIp) && port == serverPort && (isConnected.get() || (clientThread != null && clientThread.isAlive()))) {
            Log.d(tag, "Already running or connecting to " + ip + ":" + port);
            return;
        }

        this.serverIp = ip;
        this.serverPort = port;

        stopClient(); // Ensure old thread is cleaned up
        startClient();
    }

    private synchronized void startClient() {
        if (clientThread == null || !clientThread.isAlive()) {
            running = true;
            clientThread = new Thread(this, "TCP-" + tag);
            clientThread.start();
        }
    }

//    public void sendMessage(final String message) {
//        new Thread(() -> {
//            if (bufferOut != null && isConnected.get()) {
//                Log.d(tag, "Sending String: " + message);
//                bufferOut.println(message);
//                bufferOut.flush();
//            } else {
//                Log.w(tag, "Cannot send message, not connected: " + message);
//            }
//        }).start();
//    }
    public void sendMessage(final String message) {

        Log.d(tag, "sendMessage() called: [" + message + "]");

        try {

            writeExecutor.execute(()->{
                Log.d(tag, "Executor running: [" + message + "]");

                PrintWriter writer = bufferOut;

                if (writer != null && isConnected.get()) {
                    Log.d(tag, "Sending String: " + message);
                    writer.println(message);
                    writer.flush();
                    Log.d(tag, "sendMessage() called: [" + message + "]");

                    if (writer.checkError()) {
                        Log.e(tag, "PrintWriter reported a write error");
                    } else {
                        Log.d(tag, "Write completed successfully");
                    }

                } else {
                    Log.w(tag, "Cannot send message, not connected: " + message);
                }
            });

        } catch (RejectedExecutionException e) {
            Log.w(tag, "TCPClient already clean up.");
        }

    }

    public void sendHex(final String hexString) {

        try {
            writeExecutor.execute(()->{

                OutputStream out = outputStream;

                if (out != null && isConnected.get()) {
                    try {
                        byte[] bytes = hexToBytes(hexString);
                        Log.d(tag, "Sending Hex: " + hexString);
                        out.write(bytes);
                        out.flush();
                    } catch (IOException e) {
                        Log.e(tag, "Error sending hex", e);
                    }
                } else {
                    Log.w(tag, "Cannot send hex, not connected: " + hexString);
                }
            });
        } catch (RejectedExecutionException e) {
            Log.w(tag, "TCPClient already clean up.");
        }

    }

//    public void sendHex(final String hexString) {
//        new Thread(() -> {
//            if (outputStream != null && isConnected.get()) {
//                try {
//                    byte[] bytes = hexToBytes(hexString);
//                    Log.d(tag, "Sending Hex: " + hexString);
//                    outputStream.write(bytes);
//                    outputStream.flush();
//                } catch (IOException e) {
//                    Log.e(tag, "Error sending hex", e);
//                }
//            } else {
//                Log.w(tag, "Cannot send hex, not connected: " + hexString);
//            }
//        }).start();
//    }

    public static byte[] hexToBytes(String s) {
        s = s.replaceAll("\\s+", ""); // Remove spaces
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public synchronized void stopClient() {
        running = false;
        setConnected(false);

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.e(tag, "Error closing socket", e);
            }
        }

        if (clientThread != null) {
            clientThread.interrupt();
            clientThread = null;
        }
    }

    private void setConnected(boolean connected) {
        if (isConnected.getAndSet(connected) != connected) {
            if (connectionListener != null) {
                if (connected) {
                    connectionListener.onConnected();
                } else {
                    connectionListener.onDisconnected();
                }
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Log.d(tag, "Attempting to connect to " + serverIp + ":" + serverPort);
                
                socket = new Socket();
                // Explicitly use InetSocketAddress for connection with timeout
                socket.connect(new InetSocketAddress(serverIp, serverPort), CONNECTION_TIMEOUT);

                setConnected(true);
                Log.i(tag, "Connected successfully to " + serverIp);

                outputStream = socket.getOutputStream();
                bufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);
                bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (running) {
                    String message = bufferIn.readLine();
                    if (message != null) {
                        if (messageListener != null) {
                            messageListener.onMessageReceived(message);
                        }
                    } else {
                        Log.d(tag, "Server closed connection (null read).");
                        break; 
                    }
                }
            } catch (Exception e) {
                if (running) {
                    Log.e(tag, "Connection error: " + e.toString());
                    setConnected(false);
                }
            } finally {
                closeResources();
            }

            if (running) {
                try {
                    Log.d(tag, "Reconnecting in " + (RECONNECTION_DELAY / 1000) + "s...");
                    Thread.sleep(RECONNECTION_DELAY);
                } catch (InterruptedException e) {
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
        }
        Log.d(tag, "TCP Client thread finished.");
    }

    private synchronized void closeResources() {
        setConnected(false);
        try {
            if (bufferIn != null) bufferIn.close();
            if (bufferOut != null) bufferOut.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            Log.e(tag, "Error closing resources", e);
        }
        socket = null;
        bufferIn = null;
        bufferOut = null;
        outputStream = null;
    }

    public void cleanup() {
        stopClient();
        writeExecutor.shutdownNow();
        messageListener = null;
        connectionListener = null;
    }
}