 package com.ximen.npcpatch.mixins;

 import java.util.List;
 import java.util.Set;
 import org.spongepowered.asm.lib.tree.ClassNode;
 import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
 import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

 public class NPCPatchMixinPlugin
   implements IMixinConfigPlugin {
   public void onLoad(String mixinPackage) {
     System.out.println("[NPCPatch] Loading NPCPatchMixinPlugin...");
   }

   public String getRefMapperConfig() {
     return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
     return true;
   }


   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

   public List<String> getMixins() {
     return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
 }
