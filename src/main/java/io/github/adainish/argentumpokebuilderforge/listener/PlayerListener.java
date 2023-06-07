package io.github.adainish.argentumpokebuilderforge.listener;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
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
        CobblemonEvents.PLAYER_JOIN.subscribe(Priority.NORMAL, event -> {


            Player player = PlayerStorage.getPlayer(event.getUUID());
            if (player == null) {
                PlayerStorage.makePlayer(event);
                player = PlayerStorage.getPlayer(event.getUUID());
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
        CobblemonEvents.PLAYER_QUIT.subscribe(Priority.NORMAL, event -> {
            Player player = PlayerStorage.getPlayer(event.getUUID());
            if (player != null) {
                player.save();
            }
            return Unit.INSTANCE;
        });

    }
}
