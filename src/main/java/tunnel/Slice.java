package tunnel;

import org.yaml.snakeyaml.error.YAMLException;

public record Slice(int length, String composition) {

    public Slice {
        if(length <= 0) throw new IllegalArgumentException("length of slice must be >=1");
    }

    public static Slice of(String config) {
        try {
            String[] arr = config.split(" ");
            return new Slice(Integer.parseInt(arr[0]), arr[1]);
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new YAMLException("Malformed YAML file! Please ensure you followed the tunnel template carefully.");
        }
    }

}