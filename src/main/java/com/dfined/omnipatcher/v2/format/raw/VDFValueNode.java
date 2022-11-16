package com.dfined.omnipatcher.v2.format.raw;

import lombok.Value;

@Value
public class VDFValueNode implements VDFNode {
    String name;
    String value;
}
