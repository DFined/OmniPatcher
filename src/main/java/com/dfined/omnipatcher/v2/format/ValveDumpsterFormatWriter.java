package com.dfined.omnipatcher.v2.format;

import com.dfined.omnipatcher.dalt.ALTNode;
import com.dfined.omnipatcher.v2.io.BufferedLineWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ValveDumpsterFormatWriter {

    public static void write(File file, ALTNode sourceNode) throws IOException {
        var writer = new BufferedLineWriter(file);

        writer.close();
    }
}
