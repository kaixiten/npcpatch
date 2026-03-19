package com.ximen.npcpatch.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.form.Form;
import com.tobiasmjc.dbcadditions.data.forms.DBCAForm;
import com.tobiasmjc.dbcadditions.utils.DBCAUtils;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public class DBCAFormHelper {

    private static final Map<Integer, Form> formCache = new HashMap<>();


    public static Form getFakeForm(int formID, DBCAForm dbcaForm) {
        if (dbcaForm == null) {
            return null;
        }

        if (formCache.containsKey(formID)) {
            return formCache.get(formID);
        }

        Form fakeForm = new Form(formID, dbcaForm.getName());

        if (dbcaForm.DisplayName != null && !dbcaForm.DisplayName.isEmpty()) {
            fakeForm.menuName = dbcaForm.DisplayName;
        } else {
            fakeForm.menuName = DBCAUtils.translate(dbcaForm.getName());
        }

        formCache.put(formID, fakeForm);

        return fakeForm;
    }

    public static Form getFakeBaseForm() {
        if (formCache.containsKey(0)) {
            return formCache.get(0);
        }

        Form fakeForm = new Form(0, "Bio-Android");
        fakeForm.menuName = DBCAUtils.translate("Bio-Android-BaseName");

        formCache.put(0, fakeForm);
        return fakeForm;
    }
    @SideOnly(Side.CLIENT)
    private static boolean isSpoofingGUIClient() {
        net.minecraft.client.gui.GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        return screen != null && screen.getClass().getName().endsWith("StatSheetGui");
    }

    public static boolean isSpoofingGUI() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            return isSpoofingGUIClient();
        }
        return false;
    }
}
