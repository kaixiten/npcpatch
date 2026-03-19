 package com.ximen.npcpatch;

 import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
 import com.gtnewhorizon.gtnhmixins.LateMixin;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Set;

 @LateMixin
 public class NPCPatchLateMixins
   implements ILateMixinLoader {
   public String getMixinConfig() {
     return "mixins.npcpatch.late.json";
   }

   public List<String> getMixins(Set<String> loadedMods) {
     ArrayList<String> mixins = new ArrayList<>();
     mixins.add("MixinJRMCoreGuiScreen");
     if (loadedMods.contains("dbcadditions")) {
        mixins.add("MixinJRMCorePacHanS_DBCAdditionsFix");
        mixins.add("dbcadditions.MixinJRMCoreGuiScreen_DBCA");
        mixins.add("dbcadditions.MixinDataUtils_DBCA");
        mixins.add("dbcadditions.MixinJRMCoreH_DBCAFix");
        mixins.add("npcdbc.MixinDBCData");
        mixins.add("npcdbc.MixinPlayerDBCInfo");
     }
     return mixins;
   }
 }
