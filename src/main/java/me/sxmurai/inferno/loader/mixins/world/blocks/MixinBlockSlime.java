package me.sxmurai.inferno.loader.mixins.world.blocks;

import me.sxmurai.inferno.client.modules.movement.NoSlow;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockSlime.class)
public class MixinBlockSlime extends BlockBreakable {
    protected MixinBlockSlime(Material materialIn, boolean ignoreSimilarityIn, MapColor mapColorIn) {
        super(materialIn, ignoreSimilarityIn, mapColorIn);
    }

    @Redirect(method = "onEntityWalk", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean hookOnEntityWalk(Entity entity, World worldIn, BlockPos pos, Entity entityIn) {
        if (NoSlow.INSTANCE.isToggled() && NoSlow.INSTANCE.slime.getValue()) {
            return true;
        }

        return entityIn.isSneaking();
    }
}
