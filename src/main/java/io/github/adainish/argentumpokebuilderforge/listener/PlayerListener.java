package io.github.adainish.argentumpokebuilderforge.listener;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import io.github.adainish.argentumpokebuilderforge.ArgentumPokeBuilderForge;
import io.github.adainish.argentumpokebuilderforge.obj.Player;
import io.github.adainish.argentumpokebuilderforge.storage.PlayerStorage;
import kotlin.Unit;

public class PlayerListener {
    public PlayerListener()
    {
        subscribeToPlayerLogin();
        subscribeToPlayerLogout();
    }

    public void subscribeToPlayerLogin()
    {
        PlatformEvents.SERVER_PLAYER_LOGIN.subscribe(Priority.NORMAL, event -> {


            Player player = ArgentumPokeBuilderForge.playerStorage.getPlayer(event.getPlayer().getUUID());
            if (player == null) {
                ArgentumPokeBuilderForge.playerStorage.makePlayer(event.getPlayer());
                player = ArgentumPokeBuilderForge.playerStorage.getPlayer(event.getPlayer().getUUID());
            }

            if (player != null) {
                player.updateCache();
            }

            return Unit.INSTANCE;
        });
    }
    //subscribe to logout
    public void subscribeToPlayerLogout()
    {
        PlatformEvents.SERVER_PLAYER_LOGOUT.subscribe(Priority.NORMAL, event -> {
            Player player = ArgentumPokeBuilderForge.playerStorage.getPlayer(event.getPlayer().getUUID());
            if (player != null) {
                player.save();
            }
            return Unit.INSTANCE;
        });

    }
}
