package com.dfined.omnipatcher.v2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SlotOverride {
    String hero;
    String slot;
    List<String> models;
}
