package com.vecoo.legendcontrol.discord;

import com.vecoo.legendcontrol.LegendControl;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DiscordWebhook {
    @NotNull
    private final String url;

    public void sendEmbed(@NotNull String title, @NotNull String description, @NotNull String thumbnailUrl,
                          int color, boolean pingRole) {
        var role = "";

        if (pingRole) {
            val roleId = LegendControl.getInstance().getDiscordConfig().getWebhookRole();
            role = roleId != 0 ? "<@&" + roleId + ">" : "";
        }

        val json = String.format("{\"content\": \"%s\", \"embeds\": [{\"title\": \"%s\", \"description\": \"%s\", \"thumbnail\": {\"url\": \"%s\"}, \"color\": %s}]}",
                escapeJson(role), escapeJson(title), escapeJson(description), escapeJson(thumbnailUrl), color);

        CompletableFuture.runAsync(() -> {
            try {
                sendRequest(json);
            } catch (IOException e) {
                LegendControl.getLogger().error("Error sending discord embed.", e);
            }
        });
    }

    @NotNull
    private String escapeJson(@Nullable String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void sendRequest(@NotNull String json) throws IOException {
        if (!this.url.isEmpty()) {
            val connection = (HttpURLConnection) URI.create(this.url).toURL().openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                val input = json.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            val responseCode = connection.getResponseCode();

            if (responseCode != 204) {
                LegendControl.getLogger().error("Discord webhook failed {}.", responseCode);
            }
        }
    }
}