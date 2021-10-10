package me.sxmurai.inferno.loader.mixins.world.blocks;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.modules.miscellaneous.Avoid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFire.class)
public class MixinBlockFire extends Block {
    public MixinBlockFire(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "getCollisionBoundingBox", at = @At("HEAD"), cancellable = true)
    public void getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> info) {
        if (Avoid.INSTANCE.isToggled() && Avoid.INSTANCE.fire.getValue()) {
            // so that you dont stand on top of fire
            if (pos.getY() < Inferno.mc.player.posY) {
                return;
            }

            info.setReturnValue(Block.FULL_BLOCK_AABB);
        }
    }
}
