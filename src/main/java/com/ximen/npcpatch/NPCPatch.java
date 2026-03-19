 package com.ximen.npcpatch;

 import cpw.mods.fml.common.Mod;
 import cpw.mods.fml.common.Mod.EventHandler;
 import cpw.mods.fml.common.Mod.Instance;
 import cpw.mods.fml.common.event.FMLInitializationEvent;
 import cpw.mods.fml.common.event.FMLPostInitializationEvent;
 import cpw.mods.fml.common.event.FMLPreInitializationEvent;
 import cpw.mods.fml.common.eventhandler.EventBus;
 import java.lang.reflect.Field;
 import java.util.concurrent.ConcurrentHashMap;
 import net.minecraftforge.common.MinecraftForge;


 @Mod(modid = "npcpatch", version = "0.2.1", name = "npcpatch", dependencies = "required-after:jinryuudragonblockc;required-after:jinryuujrmcore;required-after:npcdbc")
 public class NPCPatch
 {
   public static final String MODID = "npcpatch";
   public static final String VERSION = "0.2.1";

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
     System.out.println("[NPC DBC Dodge Patch] 初始化中... 版本: 0.2.0");
     System.out.println("[NPC DBC Dodge Patch] 调试模式: 关闭");
     System.out.println("[NPC DBC Dodge Patch] 闪避事件处理器已注册");
   } public static final String NAME = "NPC DBC Dodge Patch"; public static final boolean DEBUG = false; @Instance("npcpatch")
   public static NPCPatch instance;
   @EventHandler
   public void init(FMLInitializationEvent event) {
     System.out.println("[NPC DBC Dodge Patch] 初始化完成！");
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
     System.out.println("[NPC DBC Dodge Patch] PostInit：尝试通过代理强力隔离 NPC+ 的闪避监听器...");
     isolateOriginalNPCDodge();
   }

   private void isolateOriginalNPCDodge() {
     try {
       Field listenersField = EventBus.class.getDeclaredField("listeners");
       listenersField.setAccessible(true);
       ConcurrentHashMap listenersMap = (ConcurrentHashMap)listenersField.get(MinecraftForge.EVENT_BUS);
       Object targetInstance = null;
       for (Object key : listenersMap.keySet()) {
         if (!key.getClass().getName().equals("kamkeel.npcdbc.ServerEventHandler"))
           continue;  targetInstance = key;
       }

       if (targetInstance != null) {
         MinecraftForge.EVENT_BUS.unregister(targetInstance);
       } else {
         System.out.println("[NPC DBC Dodge Patch] 警告：未找到 kamkeel.npcdbc.ServerEventHandler 实例！");
       }

     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 }
