package xianxian.mc.starocean.validateuuid;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import xianxian.mc.starocean.AbstractPlugin;

public class MojangAPI {
    private HttpClient API_CLIENT = new HttpClient();
    private Logger logger;

    public MojangAPI(AbstractPlugin plugin) {
        API_CLIENT.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        API_CLIENT.getHttpConnectionManager().getParams().setSoTimeout(10000);
        API_CLIENT.getParams().setConnectionManagerTimeout(10000);
        this.logger = Logger.getLogger(plugin.getName() + "-MojangAPI");
    }

    /**
     * 
     * @param username
     * @return UUID without "-"
     */
    public String getUUIDFromUsername(String username) {
        HttpMethod method = new GetMethod("https://api.mojang.com/users/profiles/minecraft/" + username);
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10000);
        String uuid = null;
        try {
            logger.info("Getting UUID of " + username + " from Mojang");
            int statusCode = API_CLIENT.executeMethod(method);

            if (statusCode == HttpStatus.SC_OK) {
                String response = method.getResponseBodyAsString();
                JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                uuid = object.get("id").getAsString();
                logger.info("Expected UUID of " + username + " is " + uuid);
            } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
                logger.severe("No UUID for " + username);
            } else {
                logger.severe("Server returned " + statusCode + ", unknow how to process it");
            }
            method.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uuid;
    }

}
