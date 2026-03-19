 package com.ximen.npcpatch.mixins;

 import JinRyuu.JRMCore.JRMCoreH;
 import JinRyuu.JRMCore.JRMCorePacHanS;
 import com.llamalad7.mixinextras.sugar.Local;
 import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
 import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
 import com.llamalad7.mixinextras.sugar.ref.LocalRef;
 import com.tobiasmjc.dbcadditions.DBCAConfig;
 import com.tobiasmjc.dbcadditions.common.CommonProxy;
 import com.tobiasmjc.dbcadditions.data.DBCAPlayer;
 import com.tobiasmjc.dbcadditions.data.forms.DBCAForms;
 import com.tobiasmjc.dbcadditions.data.races.DBCARace;
 import com.tobiasmjc.dbcadditions.data.races.DBCARaces;
 import com.tobiasmjc.dbcadditions.utils.DBCAUtils;
 import com.tobiasmjc.dbcadditions.utils.DataUtils;
 import net.minecraft.entity.Entity;
 import net.minecraft.entity.player.EntityPlayer;
 import net.minecraft.nbt.NBTTagCompound;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 @Mixin(value = {JRMCorePacHanS.class}, remap = false)
 public class MixinJRMCorePacHanS_DBCAdditionsFix {
   @Inject(method = {"handleRls"}, at = {@At("HEAD")}, cancellable = true)
   private void fixDescendRelease0(byte b, EntityPlayer p, CallbackInfo ci) {
     if (!DBCAConfig.CustomForms) {
       return;
     }
     DBCAPlayer player = DBCAPlayer.get(p);
     if (b == 0 && player.DBAForm > 0) {
       ci.cancel();
     }
   }

   @Inject(method = {"handleTri"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_72956_a(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V", remap = false)}, cancellable = true)
   private void cellFlyingSound(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci) {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     if (b3 == 5 && DBCARaces.isCustomRace(p) && DBCARaces.isRace(p, DBCARaces.BIO_ANDROID) && DataUtils.getDBCAState(p) != DBCAForms.SemiPerfect.getID()) {
       p.worldObj.playSoundAtEntity(p, "dbcadditions:bioandroid.fly", 6.0F, p.worldObj.rand.nextFloat() * 0.1F + 0.9F);
     }
   }

   @Inject(method = {"handleStats3"}, at = {@At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;skillTPCost_X(Ljava/lang/String;I[[I)I")}, cancellable = true)
   private void handleRacialOverLimit(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci, @Local(name = {"tpCost"}) LocalIntRef tpCost, @Local(name = {"skillLvl"}) LocalIntRef skillLvl, @Local(name = {"SklSlt"}) LocalBooleanRef SklSlt, @Local(name = {"nbt"}) LocalRef<NBTTagCompound> nbtTag, @Local(name = {"sklnm"}) LocalRef<String> sklnm, @Local(name = {"skill"}) LocalRef<String> skillParam) {
     NBTTagCompound nbt = DataUtils.nbt(p, "pres");
     DBCARace race = DBCARaces.getRace(DataUtils.getDBCARace(p));
     int currentTP = nbt.getInteger("jrmcTpint");
     if (tpCost.get() != -1 && currentTP >= tpCost.get()) {
       byte r = nbt.getByte("jrmcRace");
       int newSkillLevel = skillLvl.get();
       if (race == null) {
         if (!DBCAUtils.modifyMindOrTPS(r, newSkillLevel)) {
           return;
         }
         tpCost.set(DBCAUtils.getTPCost(r, newSkillLevel));
       } else if (newSkillLevel <= (race.getTPCosts()).length) {
         tpCost.set(race.getTPCosts()[newSkillLevel - 1]);
       } else {
         ci.cancel();
         return;
       }
       if (currentTP < tpCost.get()) {
         return;
       }
       String nskl = sklnm.get().substring(0, 2) + ((newSkillLevel >= 100) ? 9 : newSkillLevel);
       if (skillParam.get().equals("jrmcSSlts")) {
         String sn2 = JRMCoreH.cleanUpCommas(nbt.getString("jrmcSSlts").replaceAll(sklnm.get(), nskl));
         nbt.setString(skillParam.get(), sn2);
       } else {
         nbt.setString(skillParam.get(), nskl);
         ci.cancel();
       }
       if (!sklnm.get().equalsIgnoreCase(nskl)) {
         nbt.setInteger("jrmcTpint", currentTP - tpCost.get());
       }
     }
   }

   @Inject(method = {"handleStats3"}, at = {@At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;skillTPCost_X(Ljava/lang/String;I[[I)I", shift = At.Shift.BEFORE)}, cancellable = true)
   private void setPlayerSkill(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci) {
     CommonProxy.CurrentPlayerSkill = p;
   }

   @Inject(method = {"handleStats3"}, at = {@At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;skillTPCost_X(Ljava/lang/String;I[[I)I", shift = At.Shift.AFTER)}, cancellable = true)
   private void setPlayerSkill2(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci) {
     CommonProxy.CurrentPlayerSkill = null;
   }

   @Inject(method = {"handleData"}, at = {@At("HEAD")}, cancellable = true)
   private void handleDnsauBio(int c, String d, EntityPlayer p, CallbackInfo ci) {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     if (c == 4 || c == 3) {
       DBCAPlayer player = DBCAPlayer.get(p);
       byte acc = JRMCoreH.getByte(p, "jrmcAccept");
       String dnsau = JRMCoreH.getString(p, "jrmcDNSAU");
       if (acc == 1 && player.DBARace != -1 && !dnsau.contains(";")) {
         JRMCoreH.setString(((c == 4) ? ";" : "") + d, p, "jrmcDNSAU");
       }
     }
   }

   @Inject(method = {"handleChar"}, at = {@At("HEAD")}, cancellable = true)
   private void handleRaceSelection(byte b, int b2, EntityPlayer p, CallbackInfo ci) {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     DBCAPlayer player = DBCAPlayer.get(p);
     NBTTagCompound nbt = JRMCoreH.nbt(p, "pres");
     byte acc = nbt.getByte(JRMCorePacHanS.Acc);
     if (acc == 0 && b == 0 && b2 > 5) {
       nbt.setByte(JRMCorePacHanS.R, (byte)0);
       player.DBARace = 1;
       player.saveNBTData();
       ci.cancel();
     } else if (acc == 0 && b == 0 && player.DBARace != -1) {
       player.DBARace = -1;
       player.saveNBTData();
     }
   }
 }

