package com.dfined.omnipatcher.v2.model;

import com.dfined.omnipatcher.v2.Globals;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * A class to load overrides for corrupted, incorrect or missing data from the vpk archive
 */
@Slf4j
@Getter
@Setter
public class DataOverrides {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    List<SlotOverride> slotOverrides;

    public static void apply() {
        try {
            var overridesUrl = DataOverrides.class.getClassLoader().getResource("overrides.json");
            var overridesText = FileUtils.readFileToString(new File(overridesUrl.getFile()), Charsets.UTF_8);
            var overrides = MAPPER.readValue(overridesText, DataOverrides.class);
            for (var slotOverride : overrides.slotOverrides){
                Globals.DEFAULT_WEARABLE_ITEMS.get(
                        new WearableItemSlot(
                                Globals.getHero(slotOverride.hero),
                                slotOverride.slot
                        )
                        //TODO support multi model heroes
                ).setModelPath(slotOverride.models.get(0));
            }
        } catch (Exception e) {
            log.error("Unable to load overrides. Exiting");
            System.exit(1);
        }
    }


}
