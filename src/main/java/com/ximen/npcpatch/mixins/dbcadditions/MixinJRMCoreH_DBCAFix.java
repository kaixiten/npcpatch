 package com.ximen.npcpatch.mixins.dbcadditions;

 import JinRyuu.JRMCore.JRMCoreH;
 import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
 import com.tobiasmjc.dbcadditions.DBCAConfig;
 import com.tobiasmjc.dbcadditions.data.races.DBCARace;
 import com.tobiasmjc.dbcadditions.data.races.DBCARaces;
 import com.tobiasmjc.dbcadditions.data.skills.DBCASkill;
 import com.tobiasmjc.dbcadditions.data.skills.DBCASkills;
 import com.tobiasmjc.dbcadditions.utils.DBCAUtils;
 import com.tobiasmjc.dbcadditions.utils.DataUtils;
 import net.minecraft.client.Minecraft;
 import net.minecraft.client.entity.EntityClientPlayerMP;
 import net.minecraft.entity.player.EntityPlayer;
 import net.minecraft.util.StatCollector;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 @Mixin(value = {JRMCoreH.class}, remap = false)
 public class MixinJRMCoreH_DBCAFix
 {
   @Inject(method = {"attributeStart"}, at = {@At("HEAD")}, cancellable = true)
   private static void onAttributeStart(int powerType, int attribute, int race, int classID, CallbackInfoReturnable<Integer> cir) {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     if (race > 5)
     {

       if (powerType == 1) {
         int[][][] attributes = JGConfigRaces.CONFIG_RACES_ATTRIBUTE_START;
         int safeAttribute = ((attributes[0][classID]).length > attribute) ? attribute : ((attributes[0][classID]).length - 1);
         cir.setReturnValue(Integer.valueOf(attributes[0][classID][safeAttribute]));
       } else {
         int[][][] attributes = JRMCoreH.attrStart;
         int safeAttribute = ((attributes[powerType]).length > attribute) ? attribute : ((attributes[powerType]).length - 1);
         cir.setReturnValue(Integer.valueOf(attributes[powerType][safeAttribute][0]));
       }
     }
   }

   @Inject(method = {"getStatIncreases"}, at = {@At("HEAD")}, cancellable = true)
   private static void onGetStatIncreases(int powerType, int race, int classID, CallbackInfoReturnable<float[]> cir) {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     if (powerType == 1 && race > 5) {
       cir.setReturnValue(JGConfigRaces.CONFIG_RACES_STATS_MULTI[0][classID]);
     }
   }

   @Inject(method = {"trl"}, at = {@At("HEAD")}, cancellable = true)
   private static void onTrl(String mod, String name, CallbackInfoReturnable<String> cir) {
     if (Minecraft.getMinecraft() != null && (Minecraft.getMinecraft()).thePlayer != null) {
       EntityClientPlayerMP player = (Minecraft.getMinecraft()).thePlayer;
       if (name != null) {
         for (DBCASkill customSkill : DBCASkills.getPlayerSkills(DataUtils.getDBCASkills((EntityPlayer)player))) {
           if (name.equalsIgnoreCase(customSkill.getName())) {
             cir.setReturnValue(StatCollector.translateToLocal(customSkill.getDisplayName()));
             return;
           }
           if (name.equalsIgnoreCase(customSkill.getName() + "Desc")) {
             cir.setReturnValue(StatCollector.translateToLocal(customSkill.getDescription()));
             return;
           }
         }
       }
       if (DBCAConfig.CustomRaces) {
         byte raceID = DataUtils.getDBCARace((EntityPlayer)player);
         if (raceID > 0) {
           DBCARace race = DBCARaces.getRace(raceID);
           if (race != null) {
             if (name != null && name.equalsIgnoreCase("Human")) {
               cir.setReturnValue(DBCAUtils.translate(race.getName()));
               return;
             }
             if (name != null && name.equalsIgnoreCase("HiddenPotential")) {
               cir.setReturnValue(DBCAUtils.translate(race.getName() + "-FormName"));
               return;
             }
           }
         }
       }
     }
   }
 }

