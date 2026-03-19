 package com.ximen.npcpatch.mixins.dbcadditions;

 import JinRyuu.JRMCore.JRMCoreGuiButtonC1;
 import JinRyuu.JRMCore.JRMCoreGuiButtonsA3;
 import JinRyuu.JRMCore.JRMCoreGuiScreen;
 import JinRyuu.JRMCore.JRMCoreH;
 import JinRyuu.JRMCore.server.JGRaceHelper;
 import com.llamalad7.mixinextras.sugar.Local;
 import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
 import com.tobiasmjc.dbcadditions.DBCAConfig;
 import com.tobiasmjc.dbcadditions.data.races.DBCARace;
 import com.tobiasmjc.dbcadditions.data.races.DBCARaces;
 import com.tobiasmjc.dbcadditions.data.skills.DBCASkill;
 import com.tobiasmjc.dbcadditions.data.skills.DBCASkills;
 import com.tobiasmjc.dbcadditions.packets.DBUPacketRemoveSkill;
 import com.tobiasmjc.dbcadditions.packets.DBUPacketUpgradeSkill;
 import com.tobiasmjc.dbcadditions.packets.DBUPackets;
 import com.tobiasmjc.dbcadditions.utils.DataUtils;
 import com.ximen.npcpatch.utils.DBCAStateHelper;
 import cpw.mods.fml.common.network.simpleimpl.IMessage;
 import java.util.*;



 import java.util.stream.Collector;
 import java.util.stream.Collectors;
 import net.minecraft.client.Minecraft;
 import net.minecraft.client.entity.EntityClientPlayerMP;
 import net.minecraft.client.gui.FontRenderer;
 import net.minecraft.client.gui.GuiButton;
 import net.minecraft.client.gui.GuiScreen;
 import net.minecraft.entity.player.EntityPlayer;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.Unique;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.Redirect;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


 @Mixin({JRMCoreGuiScreen.class})
 public class MixinJRMCoreGuiScreen_DBCA
   extends GuiScreen
 {
   @Shadow
   public int guiID;
   @Shadow
   private boolean confirmationWindow;
   @Shadow
   private int IDtoProcessConfirmFor;

   @Inject(method = {"actionPerformed", "func_146284_a"}, at = {@At("TAIL")}, remap = false)
   public void onActionPerformed(GuiButton button, CallbackInfo ci) {
     if (button.id == 392) {
       setMenuDefColors();
     }
     if (button.id < 300 || button.id >= 390) {
       return;
     }
     String[] skills = getSkills();
     HashMap<Integer, DBCASkill> customSkillsIndex = new HashMap<>();
     for (DBCASkill dBCASkill : DBCASkills.getPlayerSkills(DataUtils.getDBCASkills((EntityPlayer)(Minecraft.getMinecraft()).thePlayer))) {
       String customSkillStr = dBCASkill.idToCode() + (dBCASkill.getLevel() - 1);
       int index = Arrays.<String>asList(skills).indexOf(customSkillStr);
       customSkillsIndex.put(Integer.valueOf(index), dBCASkill);
       if (button.id != 300 + index || index != this.IDtoProcessConfirmFor)
         continue;  DBUPackets.sendToServer((IMessage)new DBUPacketRemoveSkill(dBCASkill.getID()));
     }
     for (Map.Entry<Integer, DBCASkill> entry : customSkillsIndex.entrySet()) {
       if (button.id == 360 + ((Integer)entry.getKey()).intValue()) {
         this.confirmationWindow = true;
         this.IDtoProcessConfirmFor = ((Integer)entry.getKey()).intValue();
         continue;
       }
       if (button.id != 330 + ((Integer)entry.getKey()).intValue())
         continue;  DBUPackets.sendToServer((IMessage)new DBUPacketUpgradeSkill(entry.getValue().getID()));
     }
   }

   @Unique
   private static String[] getRaces() {
     return new String[] { "Human", "Saiyan", "Half-Saiyan", "Namekian", "Arcosian", "Majin", DBCARaces.BIO_ANDROID.getName() };
   }

   @Unique
   private static String[] getSkills() {
     String[] skills = JRMCoreH.PlyrSkills;
     ArrayList<String> customSkills = new ArrayList<>();
     customSkills.addAll(Arrays.asList(skills));
     EntityClientPlayerMP player = (Minecraft.getMinecraft()).thePlayer;
     for (DBCASkill customSkill : DBCASkills.getPlayerSkills(DataUtils.getDBCASkills(player))) {
       customSkills.add(customSkill.idToCode() + (customSkill.getLevel() - 1));
     }
     return customSkills.toArray(new String[0]);
   }

   @Inject(method = {"initGui", "func_73866_w_"}, at = {@At("TAIL")})
   private void initRace(CallbackInfo ci) {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     byte dbcaRace = DataUtils.getDBCARace((EntityPlayer)(Minecraft.getMinecraft()).thePlayer);
     if (JRMCoreH.Race == 0 && dbcaRace > 0) {
       JRMCoreGuiScreen.RaceSlcted = 5 + dbcaRace;
     }
   }

   @Redirect(method = {"drawScreen", "func_73863_a", "RaceSlctF", "RaceSlctB", "actionPerformed", "func_146284_a", "setchangerace"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;Races:[Ljava/lang/String;", remap = false), require = 0, remap = false)
   private static String[] addRaces() {
     if (!DBCAConfig.CustomRaces) {
       return JRMCoreH.Races;
     }
     return getRaces();
   }

   @Redirect(method = {"drawScreen", "func_73863_a", "RaceSlctF", "RaceSlctB", "setchangerace"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceAllow:[Ljava/lang/String;", remap = false), require = 0, remap = false)
   private static String[] allowRaces() {
     if (!DBCAConfig.CustomRaces) {
       return JRMCoreH.RaceAllow;
     }
     return new String[] { "All", "DBC", "DBC", "DBC", "DBC", "DBC", "DBC", "HHC", "HHC", "HHC", "HHC", "HHC", "HHC", "HHC", "HHC" };
   }

   @Redirect(method = {"drawScreen", "func_73863_a", "setchangerace"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceCanHaveHair:[Ljava/lang/String;", remap = false), require = 0, remap = false)
   private static String[] canHaveHair() {
     if (!DBCAConfig.CustomRaces) {
       return JRMCoreH.RaceCanHaveHair;
     }
     return new String[] { "H", "H", "H", "A", "R", "H", DBCARaces.BIO_ANDROID.getHairType(), "H", "H", "H", "H", "H", "H", "H", "H" };
   }

   @Redirect(method = {"drawScreen", "func_73863_a", "actionPerformed", "func_146284_a", "setchangerace"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;customSknLimits:[[I", remap = false), require = 0, remap = false)
   private static int[][] customSkinLimits() {
     if (!DBCAConfig.CustomRaces) {
       return JRMCoreH.customSknLimits;
     }
     return new int[][] { { 1, 1, 5, 5, 6, 2 }, { 1, 1, 5, 5, 6, 0 }, { 1, 2, 5, 5, 6, 2 }, { 3, 3, 5, 5, 3, 2 }, { 3, 4, 5, 6, 2, 2 }, { 1, 1, 5, 5, 6, 2 }, DBCARaces.BIO_ANDROID.getSkinLimits() };
   }

   @Redirect(method = {"drawScreen", "func_73863_a", "actionPerformed", "func_146284_a", "setchangerace"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;customSknLimitsBCP:[I", remap = false), require = 0, remap = false)
   private static int[] customSkinLimitsBCP() {
     if (!DBCAConfig.CustomRaces) {
       return JRMCoreH.customSknLimitsBCP;
     }
     int[] def = { 7, 7, 7, 3, 3, 7 };
     ArrayList<Integer> defList = new ArrayList<>((Collection) Arrays.stream(def).boxed().collect((Collector)Collectors.toList()));
     for (DBCARace race : DBCARaces.RACES) {
       defList.add(Integer.valueOf(race.getColorPresetLimit()));
     }
     return defList.stream().mapToInt(Integer::intValue).toArray();
   }

   @Redirect(method = {"setchangeeyecol"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;defeyecols:[[I", remap = false), require = 0, remap = false)
   private static int[][] eyeColors() {
     if (!DBCAConfig.CustomRaces) {
       return JRMCoreH.defeyecols;
     }
     return new int[][] { { 1, 1, 1, 1, 14617612, 14551628, 11606784 }, { 4896782, 1, 4896782, 4896782, 1, 8235495, 11606784 }, { 14617612, 1, 14617612, 14617612, 4896782, 16777215, 11606784 } };
   }

   @Redirect(method = {"setchangebodycol"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;defbodycols:[[[I", remap = false), require = 0, remap = false)
   private static int[][][] bodyColors() {
     if (!DBCAConfig.CustomRaces) {
       return JRMCoreH.defbodycols;
     }
     return new int[][][] { { { 16297621, 6498048 }, { 16297621, 6498048 }, { 16297621, 6498048 }, { 5095183, 13796998, 12854822 }, { 15460342, 16111595, 8533141, 16550015 }, { 16757199, 15766205 }, { 3140920, 16768592, 16750672 } }, { { 15979704, 6498048 }, { 15979704, 6498048 }, { 15979704, 6498048 }, { 4566029, 14191242, 14363435 }, { 15460342, 15188457, 287340, 16550015 }, { 16752073, 16028862 }, { 0 } }, { { 13014656, 6498048 }, { 13014656, 6498048 }, { 13014656, 6498048 }, { 4896782, 12875121, 12920870 }, { 15460342, 10442657, 3625381, 13125463 }, { 16483508, 15825582 }, { 0 } }, { { 12622942, 6498048 }, { 12622942, 6498048 }, { 12622942, 6498048 }, { 0 }, { 0 }, { 14383492, 13987449 }, { 0 } }, { { 10112303, 6498048 }, { 10112303, 6498048 }, { 10112303, 6498048 }, { 0 }, { 0 }, { 11433702, 10776284 }, { 0 } }, { { 7225375, 6498048 }, { 7225375, 6498048 }, { 7225375, 6498048 }, { 0 }, { 0 }, { 7907292, 7578067 }, { 0 } }, { { 3677711, 6498048 }, { 3677711, 6498048 }, { 3677711, 6498048 }, { 0 }, { 0 }, { 7916929, 7652472 }, { 0 } } };
   }

   @Redirect(method = {"drawScreen", "func_73863_a", "setchangerace"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceGenders:[I", remap = false), require = 0, remap = false)
   private static int[] raceGenders() {
     if (!DBCAConfig.CustomRaces) {
       return JRMCoreH.RaceGenders;
     }
     return new int[] { 2, 2, 2, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
   }

   @Redirect(method = {"drawScreen", "func_73863_a"}, at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;PlyrSkills:[Ljava/lang/String;", remap = false), require = 0)
   public String[] addCustomSkills() {
     if (!DBCAConfig.CustomForms) {
       return JRMCoreH.PlyrSkills;
     }
     return getSkills();
   }


   @Inject(method = {"drawScreen", "func_73863_a"}, at = {@At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;dat5711:Z", ordinal = 0, shift = At.Shift.BEFORE, remap = false)}, cancellable = true, require = 0)
   private void customRaceColorButton(int x, int y, float f, CallbackInfo ci, @Local(name = {"skillID"}) LocalIntRef skillID) {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     int skillLvl = Integer.parseInt(JRMCoreH.PlyrSkillX.substring(2));
     int guiLeft2 = (this.width - 256) / 2;
     int guiTop2 = (this.height - 159) / 2;
     FontRenderer var8 = (Minecraft.getMinecraft()).fontRenderer;
     String un = JRMCoreH.SklName(JRMCoreH.PlyrSkillX, JRMCoreH.vlblRSkls, JRMCoreH.vlblRSklsNms, JRMCoreH.Race);
     String name2 = JRMCoreH.trl("dbc", un);
     int mindUsed = JRMCoreH.skillSlot_MindUsed();
     int mindRequirement = JRMCoreH.skillMindRequirement_X(JRMCoreH.PlyrSkillX, JRMCoreH.Race, JRMCoreH.DBCRacialSkillMindCost);
     int mindRequirementResult = mindUsed + mindRequirement;
     boolean canAffordMind = JRMCoreH.canAffordSkill(JRMCoreH.statMindC(), mindRequirementResult);
     EntityClientPlayerMP player = (Minecraft.getMinecraft()).thePlayer;
     DBCARace race = DBCARaces.getRace(DataUtils.getDBCARace((EntityPlayer)player));
     boolean customRace = (race != null);
     byte maxLevel = customRace ? (byte)(race.getTPCosts()).length : JGRaceHelper.getMaxRacialSkillLevel(true, false, JRMCoreH.Race), by = maxLevel;
     if (skillLvl < maxLevel) {
       if (!customRace) {
         if (JRMCoreH.rSai(JRMCoreH.Race) && skillLvl >= 7) {
           this.buttonList.add(new JRMCoreGuiButtonsA3(390, guiLeft2 - 10, guiTop2 + 13 + skillID.get() * 10, 10, 2, canAffordMind));
           DBCAStateHelper.isRacialString = true;
           DBCAStateHelper.mindCost = mindRequirement;
           DBCAStateHelper.tpCost = JRMCoreH.skillTPCost_X(JRMCoreH.PlyrSkillX, JRMCoreH.Race, JRMCoreH.DBCRacialSkillTPCost);
         } else if (JRMCoreH.Race == 4 && skillLvl >= 6) {
           this.buttonList.add(new JRMCoreGuiButtonsA3(390, guiLeft2 - 10, guiTop2 + 13 + skillID.get() * 10, 10, 2, canAffordMind));
           DBCAStateHelper.isRacialString = true;
           DBCAStateHelper.mindCost = mindRequirement;
           DBCAStateHelper.tpCost = JRMCoreH.skillTPCost_X(JRMCoreH.PlyrSkillX, JRMCoreH.Race, JRMCoreH.DBCRacialSkillTPCost);
         } else if (JRMCoreH.Race != 4 && skillLvl >= 5 && !JRMCoreH.rSai(JRMCoreH.Race)) {
           this.buttonList.add(new JRMCoreGuiButtonsA3(390, guiLeft2 - 10, guiTop2 + 13 + skillID.get() * 10, 10, 2, canAffordMind));
           DBCAStateHelper.isRacialString = true;
           DBCAStateHelper.mindCost = mindRequirement;
           DBCAStateHelper.tpCost = JRMCoreH.skillTPCost_X(JRMCoreH.PlyrSkillX, JRMCoreH.Race, JRMCoreH.DBCRacialSkillTPCost);
         }
       } else {
         int[] tpCosts = race.getTPCosts();
         if (skillLvl >= 5) {
           this.buttonList.add(new JRMCoreGuiButtonsA3(390, guiLeft2 - 10, guiTop2 + 13 + skillID.get() * 10, 10, 2, canAffordMind));
           DBCAStateHelper.isRacialString = true;
           DBCAStateHelper.mindCost = race.getMindCosts()[skillLvl];
           DBCAStateHelper.tpCost = tpCosts[skillLvl];
         }
       }
     }
     if (race == null) {
       return;
     }
     String dnsau = JRMCoreH.data(16, "");
     if (race.getColorMinRacial() != -1 && skillLvl >= race.getColorMinRacial() && !dnsau.contains(";")) {
       this.buttonList.add(new JRMCoreGuiButtonsA3(392, guiLeft2 + 10 + var8.getStringWidth(name2 + ((skillLvl < 6) ? textLevel1(skillLvl) : "")), guiTop2 + 13 + skillID.get() * 10, 20, 1));
     }
   }

   @Inject(method = {"drawScreen", "func_73863_a"}, at = {@At("TAIL")}, cancellable = true)
   private void customRaceColorMenu(int x, int y, float f, CallbackInfo ci) {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     EntityClientPlayerMP player = (Minecraft.getMinecraft()).thePlayer;
     if (!DBCARaces.isCustomRace((EntityPlayer)player)) {
       return;
     }
     DBCARace race = DBCARaces.getRace(DataUtils.getDBCARace((EntityPlayer)player));
     if (race == null) {
       return;
     }
     if (this.guiID == 19) {
       this.buttonList.removeIf(b -> {
             if (b instanceof JRMCoreGuiButtonC1) {
               JRMCoreGuiButtonC1 b1 = (JRMCoreGuiButtonC1)b;
               if (b1.id >= 5016 && b1.id <= 5019 && race.getUltimateFormColors()[b1.id - 5016] == -1) {
                 b1.enabled = false;
                 return true;
               }
             }
             return false;
           });
     }
   }

   @Unique
   private static void setMenuDefColors() {
     if (!DBCAConfig.CustomRaces) {
       return;
     }
     EntityClientPlayerMP player = (Minecraft.getMinecraft()).thePlayer;
     if (!DBCARaces.isCustomRace((EntityPlayer)player)) {
       return;
     }
     DBCARace race = DBCARaces.getRace(DataUtils.getDBCARace((EntityPlayer)player));
     if (race == null) {
       return;
     }
     JRMCoreGuiScreen.BodyauColMainSlcted = (race.getUltimateFormColors()[0] != -1) ? race.getUltimateFormColors()[0] : 0;
     JRMCoreGuiScreen.BodyauColSub1Slcted = (race.getUltimateFormColors()[1] != -1) ? race.getUltimateFormColors()[1] : 0;
     JRMCoreGuiScreen.BodyauColSub2Slcted = (race.getUltimateFormColors()[2] != -1) ? race.getUltimateFormColors()[2] : 0;
     JRMCoreGuiScreen.BodyauColSub3Slcted = (race.getUltimateFormColors()[3] != -1) ? race.getUltimateFormColors()[3] : 0;
   }

   @Inject(method = {"csau_df"}, at = {@At("TAIL")}, cancellable = true, remap = false)
   private static void customRaceColorMenuDefColors(CallbackInfo ci) {
   }

   @Unique
   private String textLevel1(int lvl) {
     return "§8(lvl: " + lvl + ")";
   }
 }

