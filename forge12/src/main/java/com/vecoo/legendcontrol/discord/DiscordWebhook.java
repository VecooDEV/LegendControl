package com.vecoo.legendcontrol.discord;

import com.vecoo.legendcontrol.LegendControl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class DiscordWebhook {
    private final String url;

    public DiscordWebhook(@Nonnull String url) {
        this.url = url;
    }

    public void sendEmbed(@Nonnull String title, @Nonnull String description, @Nonnull String thumbnailUrl,
                          @Nonnull String color, boolean pingRole) throws IOException {
        String role = "";

        if (pingRole) {
            long roleId = LegendControl.getInstance().getDiscordConfig().getWebhookRole();
            role = roleId != 0 ? "<@&" + roleId + ">" : "";
        }

        String json = String.format("{\"content\": \"%s\", \"embeds\": [{\"title\": \"%s\", \"description\": \"%s\", \"thumbnail\": {\"url\": \"%s\"}, \"color\": %s}]}",
                escapeJson(role), escapeJson(title), escapeJson(description), escapeJson(thumbnailUrl), color);

        CompletableFuture.runAsync(() -> {
            try {
                sendRequest(json);
            } catch (IOException e) {
                LegendControl.getLogger().error("Error sending embed.", e);
            }
        });
    }

    @Nonnull
    private String escapeJson(@Nullable String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void sendRequest(@Nonnull String json) throws IOException {
        if (!this.url.isEmpty()) {
            HttpURLConnection connection = (HttpURLConnection) URI.create(this.url).toURL().openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode != 204) {
                LegendControl.getLogger().error("Discord webhook failed: " + responseCode);
            }
        }
    }
}