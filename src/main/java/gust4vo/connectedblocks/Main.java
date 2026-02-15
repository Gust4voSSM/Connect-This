package gust4vo.connectedblocks;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksModule;
import gust4vo.connectedblocks.commands.ExampleCommand;
import gust4vo.connectedblocks.connectedblocks.FullBlockRuleset;
import gust4vo.connectedblocks.connectedblocks.RoofRuleSet;
import gust4vo.connectedblocks.connectedblocks.RoofSteepRuleSet;
// Removed event subscribers and preview interaction references
import gust4vo.connectedblocks.interactions.RotateInteraction;

import javax.annotation.Nonnull;

public class Main extends JavaPlugin {

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));
        var eventRegistry = this.getEventRegistry();

        // Event subscribers removed — keep startup minimal

        this.getLogger().atInfo().log("Logger Check");

        this.getCodecRegistry(Interaction.CODEC)
                .register("ConnectedBlocks:Rotate",
                        RotateInteraction.class,
                        RotateInteraction.CODEC);

        ConnectedBlocksModule.get()
                .getCodecRegistry(ConnectedBlockRuleSet.CODEC)
                .register("FullBlock",
                        FullBlockRuleset.class,
                        FullBlockRuleset.CODEC)
                .register("ConnectedBlocks:Roof",
                        RoofRuleSet.class,
                        RoofRuleSet.CODEC)
                .register("ConnectThis:RoofSteep",
                        RoofSteepRuleSet.class,
                        RoofSteepRuleSet.CODEC);
    }
}
