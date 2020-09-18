package dfined.patcher_parser.data.data_structure.io;

import java.io.IOException;

public interface LineWriter {
    public void writeLine(String line) throws IOException;

    void close() throws IOException;
}
