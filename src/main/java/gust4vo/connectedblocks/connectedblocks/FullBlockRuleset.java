package gust4vo.connectedblocks.connectedblocks;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksUtil;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.CustomTemplateConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin.ConnectedBlockOutput;

import java.util.Optional;

public class FullBlockRuleset extends ConnectedBlockRuleSet {

    public static final BuilderCodec<FullBlockRuleset> CODEC = BuilderCodec.<FullBlockRuleset>builder(FullBlockRuleset.class, FullBlockRuleset::new)
            .append(new KeyedCodec<>("Block", ConnectedBlockOutput.CODEC), (ruleSet, output) -> ruleSet.block = output, ruleSet -> ruleSet.block)
            .addValidator(Validators.nonNull())
            .add()
            .append(new KeyedCodec<>("Roof", ConnectedBlockOutput.CODEC), (ruleSet, output) -> ruleSet.roof = output, ruleSet -> ruleSet.roof)
            .addValidator(Validators.nonNull())
            .add()
            .build();

    private ConnectedBlockOutput block, roof;

    @Override
    public boolean onlyUpdateOnPlacement() {
        return false;
    }

    @Override
    public Optional<ConnectedBlocksUtil.ConnectedBlockResult> getConnectedBlockType(World world, Vector3i coordinate, BlockType currentBlockType, int rotation, Vector3i placementNormal, boolean isPlacement) {

        var chunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(coordinate.x, coordinate.z));
        if (chunk == null)
            return Optional.empty();

        int aboveBlockId = chunk.getBlock(coordinate.x, coordinate.y + 1, coordinate.z);
        BlockType aboveBlockType = BlockType.getAssetMap().getAsset(aboveBlockId);

        if (aboveBlockType == null)
            return getResult(currentBlockType, State.BLOCK, rotation);
        var aboveBlockRuleSet = aboveBlockType.getConnectedBlockRuleSet();
        if (aboveBlockRuleSet == null)
            return getResult(currentBlockType, State.BLOCK, rotation);

        if (aboveBlockRuleSet instanceof CustomTemplateConnectedBlockRuleSet) {
            String aboveKey = aboveBlockType.getId();
            if (aboveKey.contains("_Roof")) {
                int aboveBlockRotation = chunk.getRotationIndex(coordinate.x, coordinate.y + 1, coordinate.z);
                return getResult(currentBlockType, State.ROOF, aboveBlockRotation);
            }
        }
        return getResult(currentBlockType, State.BLOCK, rotation);
    }

    private Optional<ConnectedBlocksUtil.ConnectedBlockResult> getResult(BlockType resultBaseType, State state, int rotation) {
        String key = resultBaseType.getId();
        boolean isRoof = key.endsWith("_Variant_Roof");

        switch (state) {
            case ROOF -> {
                if (!isRoof) key = key + "_Variant_Roof";
            }
            case BLOCK -> {
                if (isRoof) key = key.substring(0, key.length() - "_Variant_Roof".length());
            }
        }

        if (BlockType.getAssetMap().getAsset(key) == null) return Optional.empty();

        return Optional.of(new ConnectedBlocksUtil.ConnectedBlockResult(key, rotation));
    }

    public enum State {
        BLOCK,
        ROOF;
        public static final State[] VALUES = values();
    }

// Might be useful later:
//  public enum State {
//      BLOCK(0),
//      ROOF(1);
//
//      private final int StateCode;
//      State(int StateCode) {
//          this.StateCode = StateCode;
//      }
//      public int getStateCode() {
//          return this.StateCode;
//      }
//      public static final State[] VALUES = values();
//  }
}
