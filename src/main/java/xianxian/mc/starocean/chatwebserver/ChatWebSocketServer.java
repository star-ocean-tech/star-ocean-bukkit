package xianxian.mc.starocean.chatwebserver;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class ChatWebSocketServer extends WebSocketServer {
    private final Logger logger = Logger.getLogger("ChatWebSocketServer");
    private final Gson gson = new Gson();
    private final JsonParser parser = new JsonParser();

    public ChatWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("{\"type\":\"success_connect\",\"msg\":\"\"}");
        logger.fine("Client " + conn.getResourceDescriptor() + " opened");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.fine("Client " + conn.getResourceDescriptor() + " closed with code " + code + " because of " + reason);
    }
    
    public void sendChatMessage(String from, String message) {
        JsonObject jo = new JsonObject();
        jo.add("type", new JsonPrimitive("chat"));
        jo.add("from", new JsonPrimitive(from));
        jo.add("msg", new JsonPrimitive(message));
        String json = gson.toJson(jo);
        broadcast(json);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonObject jo = parser.parse(message).getAsJsonObject();
            String type = jo.get("type").getAsString();
            switch (type) {
                case "keepalive":
                    conn.send(message);
                    break;
            }
        } catch (JsonParseException e) {
            //logger.severe("Client " + conn.getResourceDescriptor() + " has sent invalid message: " + message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        logger.info("Chat Server started at " + getAddress());
    }

}
