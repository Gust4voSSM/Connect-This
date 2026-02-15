package gust4vo.connectedblocks.connectedblocks;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksUtil;

import java.util.Optional;

public class RoofSteepRuleSet extends RoofRuleSet{
    @Override
    public Optional<ConnectedBlocksUtil.ConnectedBlockResult> getConnectedBlockType(World world, Vector3i position, BlockType blockType, int rotation, Vector3i vector3i1, boolean b) {
        return super.getConnectedBlockType(world, position, blockType, rotation, vector3i1, b);
    }

    @Override
    protected Roof getCorner(World world, Vector3i position, Vector3i mutablePos, String blockId, Rotation currentYaw, RoofKind cornerToTest) {

        return super.getCorner(world, position, mutablePos, blockId, currentYaw, cornerToTest);
    }

    public Simple straight;
    public SteepCorner corner;

    public static class SteepCorner {
        public Combination ridge;
        public Combination valley;

        private static final BuilderCodec<SteepCorner> CODEC =
                BuilderCodec.builder(SteepCorner.class, SteepCorner::new)
                        .append(new KeyedCodec<>("Ridge", Combination.CODEC),
                                (corner, value) -> corner.ridge = value,
                                corner -> corner.ridge)
                        .add()
                        .append(new KeyedCodec<>("Valley", Combination.CODEC),
                                (corner, value) -> corner.valley = value,
                                corner -> corner.valley)
                        .add()
                        .build();

        public static class Combination {
            public Simple front;
            public Simple back;

            private static final BuilderCodec<SteepCorner.Combination> CODEC =
                    BuilderCodec.builder(SteepCorner.Combination.class, SteepCorner.Combination::new)
                            .append(new KeyedCodec<>("Front", Simple.CODEC),
                                    (steep, other) -> steep.front = other,
                                    steep -> steep.front)
                            .add()
                            .append(new KeyedCodec<>("Back", Simple.CODEC),
                                    (steep, other) -> steep.back = other,
                                    steep -> steep.back)
                            .add()
                            .build();
        }
    }

    public static class Simple {
        public Configuration front;
        public Configuration back;

        private static final BuilderCodec<Simple> CODEC =
                BuilderCodec.builder(Simple.class, Simple::new)
                        .append(new KeyedCodec<>("Front", Configuration.CODEC),
                                (steep, key) -> steep.front = key,
                                steep -> steep.front)
                        .add()
                        .append(new KeyedCodec<>("Back", Configuration.CODEC),
                                (steep, key) -> steep.back = key,
                                steep -> steep.back)
                        .add()
                        .build();
    }
    public static final BuilderCodec<RoofSteepRuleSet> CODEC =
            BuilderCodec.builder(RoofSteepRuleSet.class, RoofSteepRuleSet::new)
                    .append(new KeyedCodec<>("Straight", Simple.CODEC),
                            (ruleSet, value) -> ruleSet.straight = value,
                            ruleSet -> ruleSet.straight)
                    .add()
                    .append(new KeyedCodec<>("Corner", SteepCorner.CODEC),
                            (ruleSet, value) -> ruleSet.corner = value,
                            ruleSet -> ruleSet.corner)
                    .add()
                    .build();
}
