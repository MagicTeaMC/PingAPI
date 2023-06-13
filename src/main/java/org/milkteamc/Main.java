package org.milkteamc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(3187);

        get("/", (request, response) -> {
            String target = request.queryParams("target");
            String pingResult = runPingCommand(target);

            JSONObject data = new JSONObject();
            data.put("target", target);
            data.put("data", pingResult);

            response.type("application/json");
            return data.toString();
        });

        awaitInitialization();
    }

    private static String runPingCommand(String target) {
        String pingCommand = getPingCommand();

        try {
            Process process = Runtime.getRuntime().exec(pingCommand + " " + target);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getPingCommand() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return "ping -n 4";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            return "ping -c 4";
        }

        throw new UnsupportedOperationException("Unsupported operating system");
    }
}