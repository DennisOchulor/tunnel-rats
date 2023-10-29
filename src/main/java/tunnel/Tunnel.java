package tunnel;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.ParserContext;
import main.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public record Tunnel(int width, int height, Middle middle, List<Slice> slices) {

    public Tunnel {
        if(width < 5) throw new IllegalArgumentException("width of tunnel must be >=5");
        if(height < 5) throw new IllegalArgumentException("height of tunnel must be >=5");
        if(slices.isEmpty()) throw new IllegalArgumentException("must have at least one slice!");
        if(width != middle.width()) throw new IllegalArgumentException("width of tunnel and middle must be the same!");
        if(height != middle.height()) throw new IllegalArgumentException("height of tunnel and middle must be the same!");
    }

    /**
     * @return the total length of slices in ONE half only.
     */
    public int totalSlicesLength() {
        int total = 0;
        for(Slice s : slices) total += s.length();
        return total;
    }

    public static Tunnel getTunnel(String name, Player player) {
        File file = FileManager.getTunnelFile(name);
        if(file == null) return null;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            int width = yaml.getInt("width");
            int height = yaml.getInt("height");
            String middle = yaml.getString("middle");
            List<String> slicesStr = (List<String>) yaml.getList("slices");
            List<Slice> slices = new ArrayList<>();
            ParserContext context = new ParserContext();
            context.setActor(BukkitAdapter.adapt(player));
            slicesStr.forEach(s -> slices.add(Slice.of(s, context)));
            return new Tunnel(width,height,Middle.getMiddle(middle),slices);
        }
        catch (NullPointerException | ClassCastException e) {
            throw new YAMLException("Malformed YAML file! Please ensure you followed the tunnel template carefully.");
        }
    }

}