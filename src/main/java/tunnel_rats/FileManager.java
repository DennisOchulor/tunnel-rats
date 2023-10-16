package tunnel_rats;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileManager {
    private FileManager() {}

    private static final String tunnelsFolder;
    private static final String schematicsFolder;

    static {
        try {
            String mainFolder = new File(TunnelRats.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParent();
            tunnelsFolder = mainFolder + "/plugins/TunnelRats/tunnels/";
            schematicsFolder = mainFolder + "/plugins/WorldEdit/schematics/";
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getTunnelFile(String name) {
        File file = new File(tunnelsFolder + name + ".yml");
        if(file.exists()) return file;
        else return null;
    }

    public static File getSchemaFile(String name) {
        File file = new File(schematicsFolder + name + ".schem");
        if(file.exists()) return file;
        else return null;
    }

    public static List<String> listTunnels() {
        try(var tunnels = Files.walk(Path.of(tunnelsFolder))) {
            return tunnels.map(Path::getFileName).map(Path::toString).filter(s -> !s.equals("tunnels")).map(s -> s.substring(0,s.length()-4)).toList();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
