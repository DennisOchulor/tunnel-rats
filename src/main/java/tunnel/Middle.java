package tunnel;

import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import main.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public record Middle(File file, int length, int width, int height) {

    public static Middle getMiddle(String name) {
        File file = FileManager.getSchemaFile(name);
        if(file == null) throw new IllegalArgumentException("Schema \"" + name + "\" does not exists!");

        try(FileInputStream in = new FileInputStream(file);
            ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(in))
        {
            BlockVector3 v = reader.read().getDimensions();
            return new Middle(file, v.getX(), v.getZ(), v.getY());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
