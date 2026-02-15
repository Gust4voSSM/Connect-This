package gust4vo.connectedblocks.connectedblocks;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksUtil;

import java.util.Optional;

public class RoofRuleSet extends ConnectedBlockRuleSet {

    public enum RoofKind {
        STRAIGHT,
        CORNER_VALLEY,
        CORNER_RIDGE;
    }
    private RoofKind Kind;
    public RoofKind getKind() {
        return this.Kind;
    }

    @Override
    public boolean onlyUpdateOnPlacement() {
        return false;
    }

    @Override
    public Optional<ConnectedBlocksUtil.ConnectedBlockResult> getConnectedBlockType(
            World world, Vector3i position, BlockType blockType, int rotation, Vector3i vector3i1, boolean b) {
        Rotation currentYaw = RotationTuple.get(rotation).yaw();
        Vector3i mutablePos = new Vector3i();
        Roof resultRoof = getRidgeCorner(world, position, mutablePos, blockType.getId(), currentYaw);
        if (resultRoof == null) resultRoof = getValleyCorner(world, position, mutablePos, blockType.getId(), currentYaw);
        if (resultRoof instanceof Roof(String id, Rotation offset)) {
            return Optional.of(new ConnectedBlocksUtil.ConnectedBlockResult(id, RotationTuple.of(currentYaw.add(offset), Rotation.None).index()));
        }
        String key = blockType.getId()
                .replace("_Corner", "")
                .replace("_Valley", "")
                .replace("_Ridge", "");
        return Optional.of(new ConnectedBlocksUtil.ConnectedBlockResult(key, rotation));
    }
    @SuppressWarnings("UnnecessaryLocalVariable")
    protected Roof getCorner(
            World world,
            Vector3i position,
            Vector3i mutablePos,
            String blockId,
            Rotation currentYaw,
            RoofKind cornerToTest
    ) {
        Vector3i direction = mutablePos;
        String suffix, otherSuffix;
        switch (cornerToTest) {
            case RoofKind.CORNER_RIDGE -> {
                direction.assign(Vector3i.NORTH);
                suffix = "_Ridge";
                otherSuffix ="_Valley" ;
            }
            case RoofKind.CORNER_VALLEY -> {
                direction.assign(Vector3i.SOUTH);
                suffix = "_Valley";
                otherSuffix = "_Ridge";
            }
            default -> {
                return null;
            }
        }
        String key = blockId
                .replace("_Corner", "")
                .replace("_Valley", "")
                .replace("_Ridge", "")
                + "_Corner" + suffix;

        currentYaw.rotateY(direction, direction);
        Vector3i otherBlock = (new Vector3i()).assign(position).add(direction);
        Roof otherRoof = getRoof(world, otherBlock);

        if (otherRoof instanceof Roof(String otherId, Rotation otherYaw)) {
            boolean toRight = otherYaw == currentYaw.add(Rotation.Ninety);
            boolean toLeft = otherYaw == currentYaw.subtract(Rotation.Ninety);
            boolean toBack = otherYaw == currentYaw.subtract(Rotation.OneEighty);
            boolean sameRotation = otherYaw == currentYaw;
            boolean cornerSameKind = otherId.contains(suffix);
            boolean cornerOtherKind = otherId.contains(otherSuffix);
            Rotation offset =
                otherId.contains("_Corner")?
                    cornerSameKind?
                        toRight?        Rotation.None:
                        toBack?         Rotation.TwoSeventy:
                        null:
                    cornerOtherKind?
                        sameRotation?   Rotation.None:
                        toLeft?         Rotation.TwoSeventy:
                        null:
                    null:
                toRight?            Rotation.None:
                toLeft?             Rotation.TwoSeventy:
                null;

            if (BlockType.getAssetMap().getAsset(key) == null) return null;
            if (offset == null) return null;
            return new Roof(key, offset);
        }
        return null;

    }

    protected Roof getRidgeCorner(World world, Vector3i position, Vector3i mutablePos, String blockId, Rotation currentYaw
    ) {
        return getCorner(world, position, mutablePos, blockId, currentYaw, RoofKind.CORNER_RIDGE);
    }
    protected Roof getValleyCorner(World world, Vector3i position, Vector3i mutablePos, String blockId, Rotation currentYaw
    ) {
        return getCorner(world, position, mutablePos, blockId, currentYaw, RoofKind.CORNER_VALLEY);
    }
    protected record Roof(String id, Rotation yaw) {}

    protected Roof getRoof(World world, Vector3i position) {
        WorldChunk chunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(position.x, position.z));
        if (chunk == null) return null;
        int blockIndex = chunk.getBlock(position);
        var blockType = BlockType.getAssetMap().getAsset(blockIndex);
        if (blockType == null) return null;
        if (!(blockType.getConnectedBlockRuleSet() instanceof RoofRuleSet)) return null;
        String blockId = blockType.getId();
        var yaw = RotationTuple.get(world.getBlockRotationIndex(position.x, position.y, position.z)).yaw();
        return new Roof(blockId, yaw);
    }

    protected String removeSuffix(String s, String suffix) {
        return s.substring(0, s.length() - suffix.length());
    }

    @Override
    public void updateCachedBlockTypes(BlockType blockType, BlockTypeAssetMap<String, BlockType> assetMap) {
    }

    public Simple straight;
    public SimpleCorner corner;

    public static class Configuration {
        public String standard;
        public String beam;

        public static final BuilderCodec<Configuration> CODEC =
                BuilderCodec.builder(Configuration.class, Configuration::new)
                        .append(new KeyedCodec<>("Standard", Codec.STRING),
                                (high, key) -> high.standard = key,
                                high -> high.standard)
                        .add()
                        .append(new KeyedCodec<>("Beam", Codec.STRING),
                                (high, key) -> high.beam = key,
                                high -> high.beam)
                        .add()
                        .build();
    }
    public static class SimpleCorner {
        public Simple ridge;
        public Simple valley;

        private static final BuilderCodec<SimpleCorner> CODEC =
                BuilderCodec.builder(SimpleCorner.class, SimpleCorner::new)
                        .append(new KeyedCodec<>("Ridge", Simple.CODEC),
                                (simpleCorner, value) -> simpleCorner.ridge = value,
                                simpleCorner -> simpleCorner.ridge)
                        .add()
                        .append(new KeyedCodec<>("Valley", Simple.CODEC),
                                (simpleCorner, value) -> simpleCorner.valley = value,
                                simpleCorner -> simpleCorner.valley)
                        .add()
                        .build();
    }
    public static class Simple {
        public Configuration high;
        public Configuration low;

        public static final BuilderCodec<Simple> CODEC =
                BuilderCodec.builder(Simple.class, Simple::new)
                        .append(new KeyedCodec<>("High", Configuration.CODEC),
                                (shallow, key) -> shallow.high = key,
                                shallow -> shallow.high)
                        .add()
                        .append(new KeyedCodec<>("Low", Configuration.CODEC),
                                (shallow, key) -> shallow.low = key,
                                shallow -> shallow.low)
                        .add()
                        .build();
    }
    public static final BuilderCodec<RoofRuleSet> CODEC =
            BuilderCodec.builder(RoofRuleSet.class, RoofRuleSet::new)
                    .append(new KeyedCodec<>("Stragiht", Simple.CODEC),
                            (roof, value) -> roof.straight = value,
                            roof -> roof.straight)
                    .add()
                    .append(new KeyedCodec<>("Corner", SimpleCorner.CODEC),
                            (roof, value) -> roof.corner = value,
                            roof -> roof.corner)
                    .add()
                    .build();
}
