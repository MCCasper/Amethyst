package wtf.casper.amethyst.minecraft.mq.messages;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.core.mq.Message;

import java.util.UUID;

@Getter
public class ProxyMoveMessage extends Message {
    private final UUID uniqueId;
    private final String name;
    @Nullable
    private final String oldServer;
    @Nullable
    private final String newServer;

    public ProxyMoveMessage(UUID uniqueId, String name, @Nullable String oldServer, @Nullable String newServer) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.oldServer = oldServer;
        this.newServer = newServer;
    }
}
