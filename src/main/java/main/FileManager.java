package main;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileManager {
    private FileManager() {}

    private static final Path mainFolder;
    private static final Path tunnelsFolder;
    private static final Path schematicsFolder;

    static {
        try {
            mainFolder = Path.of(new File(TunnelRats.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParent());
            tunnelsFolder = Path.of(mainFolder + "/plugins/TunnelRats/tunnels/");
            schematicsFolder = Path.of(mainFolder + "/plugins/WorldEdit/schematics/");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getTunnelFile(String name) {
        File file = new File(tunnelsFolder + "/" + name + ".yml");
        if(file.exists()) return file;
        else return null;
    }

    public static File getSchemaFile(String name) {
        File file = new File(schematicsFolder + "/" + name + ".schem");
        if(file.exists()) return file;
        else return null;
    }

    public static List<String> listTunnels() {
        try(var tunnels = Files.walk(tunnelsFolder)) {
            return tunnels.map(Path::getFileName).map(Path::toString).filter(s -> !s.equals("tunnels")).map(s -> s.substring(0,s.length()-4)).toList();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ensureRequiredFoldersExists() {
        File tunnels = tunnelsFolder.toFile();
        try {
            if(!tunnels.exists()) {
                tunnels.mkdirs();
                Files.copy(FileManager.class.getResourceAsStream("/TunnelRats/tunnels/default_two_teams.yml"), Path.of(tunnels.getPath() + "/default_two_teams.yml"));
                Files.copy(FileManager.class.getResourceAsStream("/TunnelRats/tunnels/default_four_teams.yml"), Path.of(tunnels.getPath() + "/default_four_teams.yml"));
                Files.copy(FileManager.class.getResourceAsStream("/TunnelRats/README middle.txt"), Path.of(mainFolder + "/plugins/TunnelRats/README middle.txt"));
                Files.copy(FileManager.class.getResourceAsStream("/TunnelRats/README tunnel-template.txt"), Path.of(mainFolder + "/plugins/TunnelRats/README tunnel-template.txt"));
            }

            if(!Files.exists(Path.of(schematicsFolder + "/default_two_teams.schem")) || !Files.exists(Path.of(schematicsFolder + "/default_four_teams.schem"))) {
                Files.copy(FileManager.class.getResourceAsStream("/schematics/default_two_teams.schem"), Path.of(mainFolder + "/plugins/WorldEdit/schematics/default_two_teams.schem"));
                Files.copy(FileManager.class.getResourceAsStream("/schematics/default_four_teams.schem"), Path.of(mainFolder + "/plugins/WorldEdit/schematics/default_four_teams.schem"));
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
