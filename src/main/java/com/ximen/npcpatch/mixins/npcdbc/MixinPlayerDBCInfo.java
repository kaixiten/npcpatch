package com.ximen.npcpatch.mixins.npcdbc;

import com.tobiasmjc.dbcadditions.data.forms.DBCAForm;
import com.tobiasmjc.dbcadditions.data.forms.DBCAForms;
import com.tobiasmjc.dbcadditions.utils.DBCAUtils;
import com.tobiasmjc.dbcadditions.utils.DataUtils;
import com.ximen.npcpatch.utils.DBCAFormHelper;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerDBCInfo.class, remap = false)
public abstract class MixinPlayerDBCInfo {

    @Shadow public PlayerData parent;
    @Shadow public int currentForm;

    @Inject(method = "isInCustomForm", at = @At("HEAD"), cancellable = true)
    private void dbca_isInCustomForm(CallbackInfoReturnable<Boolean> cir) {
        if (this.currentForm > -1) return;
        if (this.parent != null && this.parent.player != null && DBCAFormHelper.isSpoofingGUI()) {
            int formID = DataUtils.getDBCAState(this.parent.player);
            if (formID > 0 && DBCAForms.getForm(formID) != null) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getCurrentForm", at = @At("HEAD"), cancellable = true)
    private void dbca_getCurrentForm(CallbackInfoReturnable<Form> cir) {
        if (this.currentForm > -1) return; // npcdbc native form priority
        if (this.parent != null && this.parent.player != null && DBCAFormHelper.isSpoofingGUI()) {
            int formID = DataUtils.getDBCAState(this.parent.player);
            if (formID > 0) {
                DBCAForm dbcaForm = DBCAForms.getForm(formID);
                if (dbcaForm != null) {
                    cir.setReturnValue(DBCAFormHelper.getFakeForm(formID, dbcaForm));
                }
            }
        }
    }
}
