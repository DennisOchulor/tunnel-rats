package tunnel;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.factory.parser.pattern.RandomPatternParser;
import com.sk89q.worldedit.extension.factory.parser.pattern.SingleBlockPatternParser;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Slice(int length, String composition, List<NbtReplace> nbtReplaces) {
    public record NbtReplace(String block, String filter) {}

    private static final Pattern pattern = Pattern.compile("(?<percentage>\\d+%)(?<mainPattern>.+([a-z]|]))(?<nbt>\\{.+\\})");
    private static final RandomPatternParser randomPatternParser = new RandomPatternParser(WorldEdit.getInstance());
    private static final SingleBlockPatternParser singlePatternParser = new SingleBlockPatternParser(WorldEdit.getInstance());
    private static int placeholderLightLevel = 0;

    public Slice {
        if(length <= 0) throw new IllegalArgumentException("length of slice must be >=1");
    }

    // ParserContext needed to validate slice composition
    public static Slice of(String config, ParserContext context) {
        try {
            String[] arr = config.split(" ");
            String composition = arr[1];
            if(!composition.contains("{")) {
                randomPatternParser.parseFromInput(composition, context); //validation
                return new Slice(Integer.parseInt(arr[0]), composition, null);
            }

            String[] componentsWithDelims = composition.splitWithDelimiters(",\\d+%", 0); //only match commas between different patterns, not within the same pattern
            List<String> components = new ArrayList<>();
            components.add(componentsWithDelims[0]);  // eg 85%cobblestone
            for(int i=1;i<componentsWithDelims.length;i+=2) { //current state example: ",10%" , "oak_log" , ",5%" , "oak_planks"
                components.add(componentsWithDelims[i].replaceFirst(",","") + componentsWithDelims[i+1]);
            }

            StringBuilder sb = new StringBuilder();
            List<NbtReplace> nbtReplaces = new ArrayList<>();
            for(String c : components) {
                if(c.contains("{")) {
                    Matcher m = pattern.matcher(c);
                    if(!m.matches()) throw new IllegalArgumentException("Invalid slice composition for \n" + c + "\nAre your {} properly formatted?"); // MUST explicitly call this or else MatchResult will never match anything...
                    MatchResult result = m.toMatchResult();
                    singlePatternParser.parseFromInput(result.group("mainPattern"), context); //validation
                    sb.append(result.group("percentage")).append("light[level=").append(placeholderLightLevel).append("]").append(",");
                    nbtReplaces.add(new NbtReplace(result.group("mainPattern") + result.group("nbt"), "light[level=" + placeholderLightLevel + "]"));
                    placeholderLightLevel++;
                }
                else sb.append(c).append(",");
            }
            placeholderLightLevel = 0; //reset it for the next Slice.
            String warpedComposition = sb.substring(0,sb.length()-1);
            randomPatternParser.parseFromInput(warpedComposition, context); //validation
            return new Slice(Integer.parseInt(arr[0]), warpedComposition, nbtReplaces);
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IllegalArgumentException("Malformed YAML file! Please ensure you followed the tunnel template carefully.");
        }
        catch (InputParseException e) { //if validation fails, it goes here
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
    }

}