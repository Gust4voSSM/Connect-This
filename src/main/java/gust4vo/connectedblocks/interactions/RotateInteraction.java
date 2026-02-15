package gust4vo.connectedblocks.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import javax.annotation.Nonnull;

public class RotateInteraction extends SimpleInstantInteraction {

    static HytaleLogger logger = HytaleLogger.forEnclosingClass();

    public static final BuilderCodec<RotateInteraction> CODEC =
            BuilderCodec.builder(
                    RotateInteraction.class,
                    RotateInteraction::new,
                    SimpleInstantInteraction.CODEC
            ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType type,
                            @Nonnull InteractionContext context,
                            @Nonnull CooldownHandler cooldownHandler) {;
        var container = context.getHeldItemContainer();
        if (container == null) {
            logger.atWarning().log("Item Container is null");
            return;
        }
        var stack = context.getHeldItem();
        if (stack == null) {
            logger.atWarning().log("Stack is null");
            return;
        }
        var quantity = stack.getQuantity();
        String itemId = stack.getItemId();
        //container.removeItemStack(stack);
        String[] suffixSequence = {"Shallow_Low", "Steep_Back", "Shallow_High", "Steep_Front"};
        for (int i = 0; i < 4; i++) {
            String s = suffixSequence[i];
            if (itemId.endsWith(s)) {
                for (int step = 1; step < 4; step++) {
                    String idCandidate = removeSuffix(itemId, s) + suffixSequence[(i+step)%4];
                    if (BlockType.getAssetMap().getAsset(idCandidate) != null) {
                        byte heldSlot = context.getHeldItemSlot();
                        var newStack = new ItemStack(idCandidate, quantity);
                        container.setItemStackForSlot(heldSlot, newStack);
                        return;
                    }
                }
                return;
            }
        }
    }
    static String removeSuffix(String string, String suffix) {
        return string.substring(0, string.length() - suffix.length());
    }
}
