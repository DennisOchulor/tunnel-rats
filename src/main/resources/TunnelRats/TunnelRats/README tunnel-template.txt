The tunnel template is as follows:
-------------------------------------------------------------------------------
width: <width>
height: <height>
middle: <name_of_middle_schem>
slices:
 - <length> <composition>
 - <length> <composition>
 ...
-------------------------------------------------------------------------------
Please ensure you follow all the indentation/spaces properly or things might break...
Save the tunnel file as a ".yml" file inside the "tunnels" folder. A YAML file is basically a glorified TXT file so any regular text editor will suffice.

<composition> follows WorldEdit's //set command's random pattern (https://worldedit.enginehub.org/en/latest/usage/general/patterns/#random-pattern) with one notable exception, that is block NBT data (the stuff within "{}") can be used as well. This is useful for randomly generating chests (or any container really) with pre-defined items inside of them.


You do _not_ need to restart the server when you edit a tunnel YAML file.
