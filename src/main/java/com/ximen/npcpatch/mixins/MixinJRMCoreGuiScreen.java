 package com.ximen.npcpatch.mixins;

 import JinRyuu.JRMCore.JRMCoreClient;
 import JinRyuu.JRMCore.JRMCoreGuiButtons00;
 import JinRyuu.JRMCore.JRMCoreGuiButtonsA2;
 import JinRyuu.JRMCore.JRMCoreGuiScreen;
 import JinRyuu.JRMCore.JRMCoreH;
 import com.tobiasmjc.dbcadditions.DBCAConfig;
 import com.tobiasmjc.dbcadditions.data.forms.DBCAForm;
 import com.tobiasmjc.dbcadditions.data.forms.DBCAFormMastery;
 import com.tobiasmjc.dbcadditions.data.forms.DBCAForms;
 import com.tobiasmjc.dbcadditions.data.races.DBCARace;
 import com.tobiasmjc.dbcadditions.data.races.DBCARaces;
 import com.tobiasmjc.dbcadditions.utils.DBCAUtils;
 import com.tobiasmjc.dbcadditions.utils.DataUtils;
 import cpw.mods.fml.common.FMLCommonHandler;
 import java.math.BigDecimal;
 import java.math.RoundingMode;
 import java.text.DecimalFormat;
 import java.util.List;
 import kamkeel.npcdbc.client.ColorMode;
 import kamkeel.npcdbc.client.gui.dbc.StatSheetGui;
 import kamkeel.npcdbc.config.ConfigDBCClient;
 import kamkeel.npcdbc.data.PlayerDBCInfo;
 import kamkeel.npcdbc.data.dbcdata.DBCData;
 import kamkeel.npcdbc.data.form.Form;
 import kamkeel.npcdbc.mixins.late.IDBCGuiScreen;
 import kamkeel.npcdbc.util.PlayerDataUtil;
 import kamkeel.npcdbc.util.Utility;
 import net.minecraft.client.Minecraft;
 import net.minecraft.client.entity.EntityClientPlayerMP;
 import net.minecraft.client.gui.FontRenderer;
 import net.minecraft.client.gui.GuiButton;
 import net.minecraft.client.gui.GuiScreen;
 import net.minecraft.client.gui.ScaledResolution;
 import net.minecraft.entity.player.EntityPlayer;
 import net.minecraft.util.StatCollector;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.Unique;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 @Mixin(value = {JRMCoreGuiScreen.class}, remap = false)
 public class MixinJRMCoreGuiScreen
   extends GuiScreen
   implements IDBCGuiScreen {
   private static final int GUI_CHANGE_BUTTON = 303030303;
   private static final int CLIENT_FIRST_PERSON_3D_OPACITY_ADD = 303030304;
   private static final int CLIENT_FIRST_PERSON_3D_OPACITY_REMOVE = 303030305;
   @Shadow
   protected static List<Object[]> detailList;
   @Shadow
   public static String wish;
   @Shadow
   public static String button1;
   @Shadow
   public int guiID;
   @Unique
   private int newGuiID;
   @Unique
   private boolean ignoreInit = false;
   @Shadow
   private static int cs_mode;
   @Shadow
   private static int cs_page;
   @Shadow
   private int wid;
   @Shadow
   private int hei;
   @Shadow
   public static JRMCoreGuiScreen instance;

   @Inject(method = {"updateScreen", "func_73876_c"}, at = {@At("HEAD")}, remap = false)
   private void onUpdateScreen(CallbackInfo ci) {
     if (this.guiID == 10 && (ConfigDBCClient.EnhancedGui || !ConfigDBCClient.EnableDebugStatSheetSwitching) && (DBCData.getClient()).Powertype == 1) {
       FMLCommonHandler.instance().showGuiScreen(new StatSheetGui());
     }
   }




   @Inject(method = {"drawDetails"}, at = {@At("HEAD")}, remap = false, cancellable = true, require = 0)
   private static void onDrawDetails(String s1, String s2, int xpos, int ypos, int x, int y, FontRenderer var8, CallbackInfo ci) {
     if (PlayerDataUtil.getClientDBCInfo() == null) {
       return;
     }
     boolean isDrawingAttributes = ((s1.contains("STR:") || s1.contains("DEX:") || s1.contains("WIL:")) && s1.contains("§"));
     boolean isDrawingStats = (s1.contains(JRMCoreH.trl("jrmc", "mleDB") + ":") || s1.contains(JRMCoreH.trl("jrmc", "DefDB") + ":") || s1.contains(JRMCoreH.trl("jrmc", "Passive") + ":") || (s1.contains(JRMCoreH.trl("jrmc", "EnPwDB") + ":") && s1.contains("§")));
     boolean shouldCancel = false;
     if (PlayerDataUtil.getClientDBCInfo().isInCustomForm())
     { DBCData dbcData = DBCData.getClient();
       Form form2 = dbcData.getForm();
       PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
       if (s1.contains(JRMCoreH.trl("jrmc", "TRState") + ":")) {
         String TRState2 = JRMCoreH.trl("jrmc", "TRState");
         if (form2 != null) {
           String name = form2.getMenuName();
           s1 = TRState2 + ": " + name;
           DecimalFormat formatter = new DecimalFormat("#.##");
           float curLevel = dbcData.addonFormLevel;
           boolean removeBase = s2.contains(JRMCoreH.trl("jrmc", "Base"));
           boolean isInKaioken = JRMCoreH.StusEfctsMe(5);
           int kaiokenID = JRMCoreH.getFormID("Kaioken", JRMCoreH.Race);
           double kaiokenLevel = JRMCoreH.getFormMasteryValue((EntityPlayer)JRMCoreClient.mc.thePlayer, kaiokenID);
           String kaiokenString = "\n" + JRMCoreH.cldgy + "§cKaioken §8Mastery Lvl: " + JRMCoreH.cldr + formatter.format(kaiokenLevel);
           s2 = Utility.removeBoldColorCode((formData.getCurrentForm()).menuName) + " §8Mastery Lvl: §4" + formatter.format(curLevel) + (removeBase ? (isInKaioken ? kaiokenString : "") : ("\n§8" + s2));
           shouldCancel = true;
         }
       } else if (isDrawingAttributes) {
         String currentColor = formData.getFormColorCode(form2);
         currentColor = Utility.removeBoldColorCode(currentColor);
         String[] data = getAdjustedAttributeData(s1, s2, currentColor);
         s1 = data[0];
         s2 = data[1];
         shouldCancel = true;
       } else if (s1.contains("CON:")) {
          float multi = (float)(DBCData.getClient()).stats.getCurrentMulti();
         if (s1.contains("x")) {
           s1 = s1.substring(0, s1.indexOf("x") - 1);
         }
         s1 = s1 + ((JRMCoreH.round(multi, 1) != 1.0D) ? (formData.getFormColorCode(form2) + " x" + JRMCoreH.round(multi, 1)) : "");
         shouldCancel = true;
       } else if (isDrawingStats) {
         s1 = replaceFormColor(s1, formData.getFormColorCode(form2));
         shouldCancel = true;
       }  }
     else if (DBCData.getClient().isForm(25) && isInBaseForm(DBCData.getClient()))
     { if (DBCData.getClient().containsSE(19)) {
         String legendColor = "§a";
         if (isDrawingAttributes) {
           String[] data = getAdjustedAttributeData(s1, s2, legendColor);
           s1 = data[0];
           s2 = data[1];
           shouldCancel = true;
         } else if (isDrawingStats) {
           s1 = replaceFormColor(s1, legendColor);
           shouldCancel = true;
         }
       }  }
     else { DBCAForm form; EntityClientPlayerMP player; byte formID; if ((DBCAConfig.CustomRaces || DBCAConfig.CustomForms) && (formID = DataUtils.getDBCAState((EntityPlayer)(player = (Minecraft.getMinecraft()).thePlayer))) > 0 && (form = DBCAForms.getForm(formID)) != null) {
         boolean isInKaioken = JRMCoreH.StusEfctsMe(5);
         String formName = (form.DisplayName == null) ? DBCAUtils.translate(form.getName()) : form.DisplayName;
         DBCAFormMastery mastery = form.getMastery((EntityPlayer)player);
         if (s1.contains(JRMCoreH.trl("jrmc", "TRState") + ":")) {
           String TRState2 = JRMCoreH.trl("jrmc", "TRState");
           String name = form.getColorCode() + formName;
           s1 = TRState2 + ": " + name;
           int kaiokenID = JRMCoreH.getFormID("Kaioken", JRMCoreH.Race);
           double kaiokenLevel = JRMCoreH.getFormMasteryValue((EntityPlayer)JRMCoreClient.mc.thePlayer, kaiokenID);
           double formMasteryLevel = BigDecimal.valueOf(mastery.level).setScale(2, RoundingMode.HALF_UP).doubleValue();
           String baseName = JRMCoreH.trl("jrmc", "Base");
           DBCARace race = DBCARaces.getRace(DataUtils.getDBCARace((EntityPlayer)player));
           if (race != null) {
             baseName = DBCAUtils.translate(race.getName() + "-BaseName");
           }
           try {
             s2 = formName + s2.split(baseName)[1].split("Lvl:")[0] + "Lvl: §4" + formMasteryLevel;
           }
           catch (Exception e) {
             s2 = formName + " Mastery Lvl: §4" + formMasteryLevel;
           }
           if (isInKaioken) {
             s2 = s2 + "\n§8Kaioken Mastery Lvl: §4" + BigDecimal.valueOf(kaiokenLevel).setScale(2, RoundingMode.HALF_UP).doubleValue();
           }
           shouldCancel = true;
         }
         if (isDrawingAttributes) {

           int id = -1;
           if (s1.contains("STR:")) {
           id = 0;
           } else if (s1.contains("DEX:")) {
             id = 1;
           } else if (s1.contains("WIL:")) {
             id = 2;
           }  String[] parts;
           if (s1.contains("§4") && (parts = s1.split(":")).length > 1 && parts[1].contains("§4")) {
             String attributeStr = parts[1].split("§4")[1];
             s1 = s1.split("§4")[0] + "§6" + attributeStr;
             if (id >= 0) {
               s2 = JRMCoreH.cldgy + JRMCoreH.trl("jrmc", "Modified") + ": §6" + attributeStr + "\n" + JRMCoreH.cldgy + JRMCoreH.trl("jrmc", "Original") + ": " + JRMCoreH.cldr + JRMCoreH.PlyrAttrbts()[id] + "\n" + JRMCoreH.cldgy + s2;
             }
           }
           shouldCancel = true;
         } else if (isDrawingStats && s1.contains("§4")) {
           s1 = s1.split("§4")[0] + "§6" + s1.split("§4")[1];
           shouldCancel = true;
         }
       }  }
      if (shouldCancel) {
       ci.cancel();
       int wpos = var8.getStringWidth(s1);
       var8.drawString(s1, xpos, ypos, 0);
       if (xpos < x && xpos + wpos > x && ypos - 3 < y && ypos + 10 > y) {
         int ll = 200;
         Object[] txt = { s2, "§8", Integer.valueOf(0), Boolean.valueOf(true), Integer.valueOf(x + 5), Integer.valueOf(y + 5), Integer.valueOf(ll) };
         detailList.add(txt);
       }
     }
   }

   private static boolean isInBaseForm(DBCData client) {
     if (client.Race == 4) {
       return (client.State == 4);
     }
     return (client.State == 0);
   }

   private static String[] getAdjustedAttributeData(String s1, String s2, String replacementColor) {
     s1 = replaceFormColor(s1, replacementColor);
     if (s2.contains(JRMCoreH.trl("jrmc", "Modified"))) {
       s2 = replaceFormColor(s2, replacementColor);
     } else {
       int attributeId = getAttributeIdByName(s1);
       int modified = (DBCData.getClient()).stats.getFullAttribute(attributeId);
       int original = JRMCoreH.PlyrAttrbts()[attributeId];
       String tooltipData = JRMCoreH.cldgy + JRMCoreH.trl("jrmc", "Modified") + ": " + replacementColor + modified + "\n" + JRMCoreH.cldgy + JRMCoreH.trl("jrmc", "Original") + ": " + JRMCoreH.cldr + original + "\n" + JRMCoreH.cldgy;
       s2 = tooltipData + s2;
     }
     return new String[] { s1, s2 };
   }

   private static int getAttributeIdByName(String s1) {
     if (s1.contains("STR:")) {
       return 0;
     }
     if (s1.contains("DEX:")) {
       return 1;
     }
     if (s1.contains("WIL:")) {
       return 2;
     }
     return 0;
   }

   private static String replaceFormColor(String s1, String replacementColor) {
     int secondIndex = 0;
     for (int i = 0; i < s1.length(); ) {
       if (s1.charAt(i) != '§' || s1.substring(i, i + 2).equals("§8")) { i++; continue; }
        secondIndex = i;
     }

     String originalColor = s1.substring(secondIndex, secondIndex + 2);
     s1 = s1.replace(originalColor, replacementColor);
     return s1;
   }

   @Inject(method = {"initGui", "func_73866_w_"}, at = {@At("RETURN")}, remap = false)
   private void onInitGui(CallbackInfo ci) {
     if (this.ignoreInit) {
       this.guiID = this.newGuiID;
     }
     if (ConfigDBCClient.EnhancedGui || !ConfigDBCClient.EnableDebugStatSheetSwitching) {
       if (ConfigDBCClient.DarkMode) {
         wish = "npcdbc:textures/gui/gui_dark.png";
         button1 = "npcdbc:textures/gui/button_dark.png";
       } else {
         wish = "npcdbc:textures/gui/gui_light.png";
         button1 = "npcdbc:textures/gui/button_light.png";
       }
     }
   }

   @Inject(method = {"drawScreen", "func_73863_a"}, at = {@At(value = "INVOKE", target = "Ljava/util/List;clear()V", shift = At.Shift.AFTER)}, remap = false)
   private void onDrawScreen(CallbackInfo ci) {
     if (this.guiID != 10) {
       return;
     }
     if (!ConfigDBCClient.EnableDebugStatSheetSwitching) {
       return;
     }
     String s = (!ConfigDBCClient.EnhancedGui ? "Old" : "§aModern") + " GUI";
     int i = this.fontRendererObj.getStringWidth(s) + 10;
     this.buttonList.add(new JRMCoreGuiButtons00(303030303, (this.width - i) / 2 + 154, (this.height - 159) / 2 + 65, i + 8, 20, s, 0));
   }


   @Inject(method = {"actionPerformed", "func_146284_a"}, at = {@At("HEAD")}, remap = false)
   public void onActionPerformed(GuiButton button, CallbackInfo ci) {
     if (button.id == 303030303) {
       ConfigDBCClient.EnhancedGui = true;
       ConfigDBCClient.EnhancedGuiProperty.set(true);
       ConfigDBCClient.config.save();
     }
     if (button.id == 303030304) {
       int value = Math.min(ConfigDBCClient.FirstPerson3DAuraOpacity + 10, 100);
       ConfigDBCClient.FirstPerson3DAuraOpacityProperty.set(value);
       ConfigDBCClient.config.save();
     }
     if (button.id == 303030305) {
       int value = Math.max(ConfigDBCClient.FirstPerson3DAuraOpacity - 10, 0);
       ConfigDBCClient.FirstPerson3DAuraOpacityProperty.set(value);
       ConfigDBCClient.config.save();
     }
   }

   @Inject(method = {"drawHUD_clntsett"}, at = {@At("RETURN")}, remap = false)
   public void addClientSettings(int posX, int posY, ScaledResolution var5, int var6, FontRenderer var8, CallbackInfo ci) {
     int xSize = 256;
     int ySize = 159;
     int guiLeft = (this.width - xSize) / 2;
     int guiTop = (this.height - ySize) / 2 + 7;
     if (cs_mode == 0 && cs_page == 3) {
       enhancedGUIdrawString(var8, StatCollector.translateToLocalFormatted("dbc.clientsettings.aura.firstPersonOpacity", new Object[] { Integer.valueOf(ConfigDBCClient.FirstPerson3DAuraOpacity) }), guiLeft + 5, guiTop + 45, 0);
       this.buttonList.add(new JRMCoreGuiButtonsA2(303030305, guiLeft - 10 - 13, guiTop - 2 + 45, "<"));
       this.buttonList.add(new JRMCoreGuiButtonsA2(303030304, guiLeft - 10, guiTop - 2 + 45, ">"));
     }
   }

     private static int enhancedGUIdrawString(FontRenderer instance, String text, int x, int y, int color) {
         return ConfigDBCClient.EnhancedGui ? instance.drawStringWithShadow(ColorMode.skimColors(text), x, y, ColorMode.textColor()) : instance.drawString(text, x, y, color);
     }

   @Unique
   public void setGuiIDPostInit(int id) {
     this.newGuiID = id;
     this.ignoreInit = true;
   }
 }
