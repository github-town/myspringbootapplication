package com.myspringboot.myspringbootfirstapplication.https_socket_certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Scanner;

public class FoxClient {
    public static void main(String[] args) throws Exception {
        String clientKeyStoreFile = "D:\\WorkSpace\\my-springboot-test\\my-springboot-test-controller\\src\\main\\resources\\certificate\\foxclient.keystore";
        String clientKeyStorePwd = "huawei";
        String foxclientKeyPwd = "huawei";
        String clientTrustKeyStoreFile = "D:\\WorkSpace\\my-springboot-test\\my-springboot-test-controller\\src\\main\\resources\\certificate\\foxclienttrust.keystore";
        String clientTrustKeyStorePwd = "huawei";

        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(new FileInputStream(clientKeyStoreFile), clientKeyStorePwd.toCharArray());

        KeyStore clientTrustKeyStore = KeyStore.getInstance("JKS");
        clientTrustKeyStore.load(new FileInputStream(clientTrustKeyStoreFile), clientTrustKeyStorePwd.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, foxclientKeyPwd.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(clientTrustKeyStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        while (true) {
            System.out.println("create new socket ? ");
            Scanner scanner1 = new Scanner(System.in);
            if(scanner1.nextLine().equals("no")){
                break;
            }
            Socket socket = socketFactory.createSocket("localhost", CatServer.SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true){
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine();
                send(message, out);
                if ("exit".equals(message)){
                    break;
                }
            }
            receive(in);
            System.out.println("client socket close ...");
            socket.close();
        }
    }

    public static void send(String s, PrintWriter out) throws IOException {
        System.out.println("send time : "+System.currentTimeMillis());
        System.out.println("Sending: " + s);
        out.println(s);
    }

    public static void receive(BufferedReader in) throws IOException {
        String s;
        while ((s = in.readLine()) != null) {
            System.out.println("Reveived time : " + System.currentTimeMillis());
            System.out.println("Reveived: " + s);
            if (s.equals("Echo: exit")){
                break;
            }
        }
    }
}
