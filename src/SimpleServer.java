import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleServer {
    private static final int LISTENING_PORT = 23634;
    private static final String ROOT_DIRECTORY = ".";

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(LISTENING_PORT);
        }
        catch (Exception e) {
            System.out.println("Failed to create listening socket.");
            return;
        }
        System.out.println("Listening on port " + LISTENING_PORT);
        try {
            while (true) {
                Socket connection = serverSocket.accept();
                System.out.println("\nConnection from "
                        + connection.getRemoteSocketAddress());
                ConnectionThread thread = new ConnectionThread(connection);
                thread.start();
            }
        }
        catch (Exception e) {
            System.out.println("Server socket shut down unexpectedly!");
            System.out.println("Error: " + e);
            System.out.println("Exiting.");
        }
    }

    /**
     *
     * @param connection Socket for handling client connections
     */
    private static void handleConnection(Socket connection) {
        PrintWriter out = null;
        OutputStream outputStream = null;

        try {
            Scanner in = new Scanner(connection.getInputStream());
            // character output stream for writing the headers
            outputStream = connection.getOutputStream();
            out = new PrintWriter(outputStream);
            String request, method, pathToFile, httpVersion;

            request = in.nextLine();

            Scanner scanner = new Scanner(request);
            method = scanner.next();
            pathToFile = scanner.next();
            httpVersion = scanner.next();

            File file = new File(ROOT_DIRECTORY + pathToFile);

            if (file.isDirectory()) {
                // read the index file if it is a directory
                file = new File(file, "index.html");
            }

            String mimeType = getMimeType(file.getName());

            if (! (httpVersion.equalsIgnoreCase("HTTP/1.1") || httpVersion.equalsIgnoreCase("HTTP/1.0"))) {
                // invalid request format
                sendErrorHeaders(out, "400 Bad Request");
                sendErrorResponse(400, outputStream);
                return;
            }

            if (! method.equalsIgnoreCase("GET")) {
                // we only support the GET method
                sendErrorHeaders(out, "501 Not Implemented");
                sendErrorResponse(501, outputStream);
                return;
            }

            if (! file.exists()) {
                // check if file does not exist
                sendErrorHeaders(out, "404 Not found");
                sendErrorResponse(404, outputStream);
                return;
            }

            if (! file.canRead()) {
                sendErrorHeaders(out, "403 Forbidden");
                sendErrorResponse(403, outputStream);
                return;
            }

            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Connection: close\r\n");
            out.print("Content-Type: " + mimeType + "\r\n");
            out.print("Content-length: " + file.length() + "\r\n");
            out.print("\r\n");
            out.flush();
            sendFile(file, connection.getOutputStream());
        }
        catch (Exception e) {
            System.out.println("Error while communicating with client: " + e);
            assert out != null;
            sendErrorHeaders(out, "500 Internal Server Error");
            sendErrorResponse(500, outputStream);
        }
        finally {  // make SURE connection is closed before returning!
            try {
                connection.close();
            }
            catch (Exception ignored) {
            }
            System.out.println("Connection closed.");
        }
    }

    private static void sendErrorHeaders(PrintWriter out, String s) {
        out.print("HTTP/1.1 ");
        out.print(s);
        out.print("\r\n");
        out.print("Connection: close\r\n");
        out.print("Content-Type: text/html\r\n");
        out.print("\r\n");
        out.flush();
    }

    private static String getMimeType(String fileName) {
        int pos = fileName.lastIndexOf('.');
        if (pos < 0)  // no file extension in name
            return "x-application/x-unknown";
        String ext = fileName.substring(pos+1).toLowerCase();
        switch (ext) {
            case "txt":
                return "text/plain";
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "text/javascript";
            case "java":
                return "text/x-java";
            case "jpeg":
            case "jpg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "ico":
                return "image/x-icon";
            case "class":
                return "application/java-vm";
            case "jar":
                return "application/java-archive";
            case "zip":
                return "application/zip";
            case "xml":
                return "application/xml";
            case "xhtml":
                return "application/xhtml+xml";
            default:
                return "x-application/x-unknown";
        }
        // Note:  x-application/x-unknown  is something made up;
        // it will probably make the browser offer to save the file.
    }

    private static void sendFile(File file, OutputStream socketOut) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        OutputStream out = new BufferedOutputStream(socketOut);
        while (true) {
            int x = in.read(); // read one byte from file
            if (x < 0)
                break; // end of file reached
            out.write(x);  // write the byte to the socket
        }
        out.flush();
    }

    private static void sendErrorResponse(int errorCode, OutputStream socketOut) {

        String notFoundTemplate = "" +
                "<html><head><title>Error</title></head><body>\n" +
                "<h2>Error: 404 Not Found</h2>\n" +
                "<p>The resource that you requested does not exist on this server.</p>\n" +
                "</body></html>";

        try {
            if (errorCode == 400) {
                socketOut.write("Bad Request!".getBytes());
            }
            if (errorCode == 403) {
                socketOut.write("Forbidden!".getBytes());
            }
            if (errorCode == 404) {
                socketOut.write(notFoundTemplate.getBytes());
            }
            if (errorCode == 500) {
                socketOut.write("Internal Server Error!".getBytes());
            }
            if (errorCode == 501) {
                socketOut.write("Not Implemented!".getBytes());
            }
        } catch (Exception ignored) {

        }
    }

    private static class ConnectionThread extends Thread {
        Socket connection;

        ConnectionThread(Socket connection) {
            this.connection = connection;
        }

        public void run() {
            handleConnection(connection);
        }
    }
}
