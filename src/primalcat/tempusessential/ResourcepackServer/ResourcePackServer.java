package primalcat.tempusessential.ResourcepackServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class ResourcePackServer {

    private final int port = 8162;
    private HttpServer server;
    private static File resourcePackFile;
    public static String resourcePackHash;

    public ResourcePackServer(File configFolder) {
        // Указываем путь к файлу ресурс-пака в папке конфигурации
        resourcePackFile = new File(configFolder, "resourcepack.zip");
        System.out.println("resourcePackFile " + resourcePackFile);
        try {
            resourcePackHash = calculateSHA1(resourcePackFile);
            System.out.println("Хеш ресурс-пака (SHA-1): " + resourcePackHash);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            resourcePackHash = ""; // Значение по умолчанию, если хеш не был создан
        }

    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("65.109.61.34", port), 0);

        server.createContext("/resourcepack", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if (!resourcePackFile.exists()) {
                    exchange.sendResponseHeaders(404, -1); // Файл не найден
                    return;
                }

                exchange.sendResponseHeaders(200, resourcePackFile.length());
                try (FileInputStream fis = new FileInputStream(resourcePackFile);
                     OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }
        });

        server.start();
        System.out.println("HTTP-сервер запущен на 65.109.61.34:" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public String calculateSHA1(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                sha1.update(buffer, 0, bytesRead);
            }
        }

        try (Formatter formatter = new Formatter()) {
            for (byte b : sha1.digest()) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }

    public static String getResourcePackHash() {
        return resourcePackHash;
    }
}
