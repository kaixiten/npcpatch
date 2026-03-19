 package com.ximen.npcpatch.mixins.dbcadditions;

 import com.tobiasmjc.dbcadditions.utils.DataUtils;
 import kamkeel.npcdbc.data.dbcdata.DBCData;
 import net.minecraft.entity.player.EntityPlayer;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 @Mixin(value = {DataUtils.class}, remap = false, priority = 2000)
 public class MixinDataUtils_DBCA
 {
   @Inject(method = {"getDBCAState(Lnet/minecraft/entity/player/EntityPlayer;)B"}, at = {@At("HEAD")}, cancellable = true)
   private static void onGetDBCAState(EntityPlayer p, CallbackInfoReturnable<Byte> cir) {
     if (p != null) {
       DBCData dbcData = DBCData.get(p);
       if (dbcData != null && dbcData.addonFormID != -1)
       {
           cir.setReturnValue(Byte.valueOf((byte)0));
       }
     }
   }
 }
