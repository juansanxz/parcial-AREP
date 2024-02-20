package edu.escuelaing.arem.parcial.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ReflexCalculator {
    public static void main(String[] args) throws IOException, URISyntaxException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine = "", firstLine = "";

            boolean inFirstLine = true;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);

                if (inFirstLine){
                    firstLine = inputLine.split(" ")[1];
                    inFirstLine = false;
                }
                if (!in.ready()) {break; }
            }
            URI uri = new URI(firstLine);
            if (uri.getPath().startsWith("/compreflex")){
                String className = uri.getPath().substring(1).split("=")[1].replace("("," ").replace(")", " ").split(" ")[0];
                String parametersString = uri.getPath().substring(1).split("=")[1].replace("("," ").replace(")", " ").split(" ")[1];
                if (className.startsWith("qck")) {

                } else {
                    Class<?> c = Math.class;
                    Method operation = null;
                    Object result = null;
                    System. out.println(uri.getPath().substring(1) + " ---------");
                    System.out.println(className + " ---------");
                    System.out.println(parametersString + " ---------");
                    String[] parameters = parametersString.split(",");
                    if (parameters.length == 1) {
                        System.out.println("1");
                        operation = c.getDeclaredMethod(className, Double.TYPE);
                        result = operation.invoke(null, Double.parseDouble(parametersString));
                    } else if (parameters.length == 2) {
                        operation = c.getDeclaredMethod(className, Double.TYPE, Double.TYPE);
                        result = operation.invoke(null, Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1]));

                    }

                    outputLine = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: application/json\r\n"
                            + "\r\n" +
                            "{\"result\":\"" + result + "\"}";
                }

            }
            
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }




}
