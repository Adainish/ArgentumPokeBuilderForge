package io.github.adainish.argentumpokebuilderforge.obj;

import com.google.gson.Gson;
import io.github.adainish.argentumpokebuilderforge.ArgentumPokeBuilderForge;
import io.github.adainish.argentumpokebuilderforge.storage.PlayerStorage;
import io.github.adainish.argentumpokebuilderforge.util.Adapters;
import io.github.adainish.argentumpokebuilderforge.util.Util;
import org.bson.Document;

import java.util.UUID;

public class Player
{
    public UUID uuid;
    public int tokenCount;

    public Player(UUID uuid)
    {
        this.uuid = uuid;
        this.tokenCount = 0;
    }


    public void updateCache() {
        ArgentumPokeBuilderForge.dataWrapper.playerCache.put(uuid, this);
    }

    // Convert Player to Document
    public Document toDocument() {
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        String json = gson.toJson(this);
        return Document.parse(json);
    }

    public void saveNoCache()
    {
        ArgentumPokeBuilderForge.playerStorage.savePlayerNoCache(this);
    }

    public void save()
    {
        //save to storage file
        ArgentumPokeBuilderForge.playerStorage.savePlayer(this);
    }

    public void sendMessage(String message)
    {
        Util.send(Util.getPlayer(uuid), message);
    }


}
