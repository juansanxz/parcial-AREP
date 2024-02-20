package edu.escuelaing.arem.parcial.app;

import java.net.*;
import java.io.*;

public class ServiceFacade {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
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
            System.out.println(uri.getPath() +  " -------");
            if (uri.getPath().startsWith("/calculadora")){
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n" +
                        "<!DOCTYPE html>\r\n" +
                        "<html>\r\n" +
                        "    <head>\r\n" +
                        "        <title>Calculadora</title>\r\n" +
                        "        <meta charset=\"UTF-8\">\r\n" +
                        "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" +
                        "    </head>\r\n" +
                        "    <body>\r\n" +
                        "        <h1>Indique la operacion</h1>\r\n" +
                        "        <form action=\"/computar\">\r\n" +
                        "            <label for=\"operacion\">Escriba la operacion que desea, por ejemplo:</label><br>\r\n" +
                        "            <input type=\"text\" id=\"operacion\" name=\"operacion\" value=\"cos(180)\"><br><br>\r\n" +
                        "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\r\n" +
                        "        </form> \n" +
                        "        <div id=\"getrespmsg\"></div>\r\n" +
                        "\r\n" +
                        "        <script>\n" +
                        "            function loadGetMsg() {\r\n" +
                        "                let opVar = document.getElementById(\"operacion\").value;\r\n" +
                        "                const xhttp = new XMLHttpRequest();\r\n" +
                        "                xhttp.onload = function() {\r\n" +
                        "                    document.getElementById(\"getrespmsg\").innerHTML =\r\n" +
                        "                    this.responseText;\r\n" +
                        "                }\r\n" +
                        "                xhttp.open(\"GET\", \"/computar?comando=\"+opVar);\r\n" +
                        "                xhttp.send();\r\n" +
                        "            }\r\n" +
                        "        </script>\r\n" +
                        "\r\n" +
                        "    </body>\r\n" +
                        "</html>";

            } else if (uri.getPath().startsWith("/computar")) {
                String query = uri.getQuery();
                outputLine =  "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n" + HttpConnectionWithCalc.ConnectToCalc(query);
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }
}