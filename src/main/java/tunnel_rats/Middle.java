package tunnel_rats;

import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.math.BlockVector3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public record Middle(String name, int width, int length, int height) {

    public static Middle getMiddle(String name) {
        File file = FileManager.getSchemaFile(name);
        if(file == null) throw new IllegalArgumentException("Schema \"" + name + "\" does not exists!");

        try {
            BlockVector3 v = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(file)).read().getDimensions();
            return new Middle(name, v.getX(), v.getZ(), v.getY());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
