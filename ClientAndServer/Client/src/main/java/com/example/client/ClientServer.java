package com.example.client;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;


public class ClientServer {
    private ConnectionThread connectionThread = new ConnectionThread();
    private Consumer<String> onReceiveCallback;

    public ClientServer(Consumer<String> onReceiveCallback){
        this.onReceiveCallback = onReceiveCallback;
        connectionThread.setDaemon(true);
    }

    public void startConnection() throws Exception{
        connectionThread.start();
    }

    public void closeConnection() throws IOException {
        connectionThread.socket.close();
    }

    public void send(String data) throws IOException{
        connectionThread.out.writeUTF(data);
    }



    private class ConnectionThread extends Thread{
        private Socket socket;
        DataOutputStream out;


        @Override
        public void run() {
            InetAddress ip = null;
            try {
                ip = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try(Socket socket = new Socket(ip,5056);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                this.socket = socket;
                this.out = out;

                socket.setTcpNoDelay(true);

                while (true){
                    String data =  in.readUTF();
                    onReceiveCallback.accept(data);
                }


            }catch (Exception e){
                try(ServerSocket ss = new ServerSocket(5056);
                    Socket socket = ss.accept();
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                    this.socket = socket;
                    this.out = out;

                    socket.setTcpNoDelay(true);

                    while (true){
                        String data =  in.readUTF();
                        onReceiveCallback.accept(data);
                    }

                }catch (Exception e1){
                    onReceiveCallback.accept("Connection Failed");
                }
            }
        }
    }


}





