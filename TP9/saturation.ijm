titre=getTitle();
print (titre);
run("Color Space Converter", "from=RGB to=HSB white=D65");
run("Split Channels");
command = titre+" (HSB) (green)";
selectWindow(command);
//selectWindow("it3_72pp_saturation_faible.bmp (HSB) (green)");
run("Multiply...", "value=1.0");
command = "c1=["+titre+" (HSB) (red)] c2=["+titre+" (HSB) (green)] c3=["+titre+" (HSB) (blue)] ignore"
//run("Merge Channels...", "c1=[it3_72pp_saturation_faible.bmp (HSB) (red)] c2=[it3_72pp_saturation_faible.bmp (HSB) (green)] c3=[it3_72pp_saturation_faible.bmp (HSB) (blue)] ignore");
run("Merge Channels...", command);
run("Color Space Converter", "from=HSB to=RGB white=D65");
