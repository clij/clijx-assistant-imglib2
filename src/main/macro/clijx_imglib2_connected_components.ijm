selectWindow("blobs.tif");

// init GPU
run("CLIJ2 Macro Extensions", "cl_device=[Intel(R) UHD Graphics 620]");
Ext.CLIJ2_clear();

// Push image to GPU
image1 = getTitle();
Ext.CLIJ2_push(image1);

// threshold otsu
Ext.CLIJ2_thresholdOtsu(image1, image2);

// imglib2 connected components labeling
Ext.CLIJx_imglib2ConnectedComponentsLabeling(image2, image3);

// show result
Ext.CLIJ_pull(image3);
run("glasbey on dark");
