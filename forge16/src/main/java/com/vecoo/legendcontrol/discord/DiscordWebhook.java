package com.vecoo.legendcontrol.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vecoo.legendcontrol.LegendControl;
import lombok.RequiredArgsConstructor;
import lombok.val;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DiscordWebhook {
    @Nonnull
    private final String url;

    public void sendEmbed(@Nonnull String title, @Nonnull String description, @Nonnull String thumbnailUrl,
                          int color, boolean pingRole) {
        val json = new JsonObject();
        val embed = new JsonObject();
        val embedsArray = new JsonArray();

        if (pingRole) {
            val roleId = LegendControl.getInstance().getDiscordConfig().getWebhookRole();
            if (roleId != 0) {
                json.addProperty("content", "<@&" + roleId + ">");
            }
        }

        embed.addProperty("title", title);
        embed.addProperty("description", escapeMarkdown(description));
        embed.addProperty("color", color & 0xFFFFFF);

        if (!thumbnailUrl.isEmpty()) {
            val thumbnail = new JsonObject();

            thumbnail.addProperty("url", thumbnailUrl);
            embed.add("thumbnail", thumbnail);
        }

        embedsArray.add(embed);
        json.add("embeds", embedsArray);

        CompletableFuture.runAsync(() -> {
            try {
                sendRequest(json.toString());
            } catch (IOException e) {
                LegendControl.getLogger().error("Error sending discord embed.", e);
            }
        });
    }

    @Nonnull
    private String escapeMarkdown(@Nullable String input) {
        if (input == null) {
            return "";
        }

        return input.replace("_", "\\_")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace("|", "\\|");
    }

    private void sendRequest(@Nonnull String json) throws IOException {
        if (!this.url.isEmpty()) {
            val connection = (HttpURLConnection) URI.create(this.url).toURL().openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (val outputStream = connection.getOutputStream()) {
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