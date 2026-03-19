 package com.ximen.npcpatch;

 import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
 import com.gtnewhorizon.gtnhmixins.LateMixin;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Set;

 @LateMixin
 public class NPCPatchDBCAdditionsMixins
   implements ILateMixinLoader {
   public String getMixinConfig() {
     return "mixins.npcpatch.dbcadditions.json";
   }

   public List<String> getMixins(Set<String> loadedMods) {
     ArrayList<String> mixins = new ArrayList<>();
     if (loadedMods.contains("dbcadditions") && loadedMods.contains("npcdbc")) {
       mixins.add("late.MixinJGRaceHelper");
       mixins.add("late.MixinJRMCoreMm");
       mixins.add("late.MixinJRMCoreH");
       mixins.add("late.packet.MixinDBCPacketHandler");
       mixins.add("late.MixinDBCKiTech");
       mixins.add("late.MixinJRMCoreH2");
       mixins.add("late.jbra.MixinModelBipedDBC");
       mixins.add("late.jbra.MixinModelBipedBody");
       mixins.add("late.MixinJRMCoreCliTicH");
       mixins.add("late.gui.MixinJRMCoreGui");
       mixins.add("late.gui.MixinDBCWishGui");
       mixins.add("late.MixinJRMCoreHDBC");
       mixins.add("late.jbra.MixinRenderPlayerJBRA");
     }
     return mixins;
   }
 }
