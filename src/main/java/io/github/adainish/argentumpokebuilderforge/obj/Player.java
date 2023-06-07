package io.github.adainish.argentumpokebuilderforge.obj;

import io.github.adainish.argentumpokebuilderforge.ArgentumPokeBuilderForge;
import io.github.adainish.argentumpokebuilderforge.storage.PlayerStorage;
import io.github.adainish.argentumpokebuilderforge.util.Util;

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

    public void save() {
        //save to storage file
        PlayerStorage.savePlayer(this);
    }

    public void sendMessage(String message)
    {
        Util.send(Util.getPlayer(uuid), message);
    }


}
