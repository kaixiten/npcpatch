package com.ximen.npcpatch.mixins.npcdbc;


import com.tobiasmjc.dbcadditions.data.forms.DBCAForm;
import com.tobiasmjc.dbcadditions.data.forms.DBCAFormMastery;
import com.tobiasmjc.dbcadditions.data.forms.DBCAForms;
import com.tobiasmjc.dbcadditions.utils.DataUtils;
import com.ximen.npcpatch.utils.DBCAFormHelper;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DBCData.class, remap = false)
public abstract class MixinDBCData {

    @Shadow public EntityPlayer player;
    @Shadow public float addonFormLevel;
    @Shadow public int addonFormID;

    @Inject(method = "getForm", at = @At("HEAD"), cancellable = true)
    private void dbca_getForm(CallbackInfoReturnable<Form> cir) {
        if (this.addonFormID > -1) return; // npcdbc native form priority
        if (this.player != null && DBCAFormHelper.isSpoofingGUI()) {
            int formID = DataUtils.getDBCAState(this.player);
            if (formID > 0) {
                // Read from DBCAFormMastery
                DBCAForm dbcaForm = DBCAForms.getForm(formID);
                if (dbcaForm != null) {
                    DBCAFormMastery mastery = dbcaForm.getMastery(this.player);
                    if (mastery != null) {
                        this.addonFormLevel = (float) mastery.level;
                    }
                    cir.setReturnValue(DBCAFormHelper.getFakeForm(formID, dbcaForm));
                }
            }
        }
    }
}
