package net.haesleinhuepf.clijx.imglib2;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.HasLicense;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.view.Views;
import org.scijava.plugin.Plugin;

/**
 * Demo plugin for integrating imglib2 based algorithms into CLIJ workflows.
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_imglib2ConnectedComponentsLabeling")
public class Imglib2ConnectedComponentsLabeling extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput
{

    public Imglib2ConnectedComponentsLabeling() {
        super();
    }

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = imglib2ConnectedComponentsLabeling(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]));
        return result;
    }

    public static boolean imglib2ConnectedComponentsLabeling(CLIJ2 clij2, ClearCLBuffer input1, ClearCLBuffer output) {
        // imglibs CC needs input and output of IntegerType, so we convert on the GPU
        ClearCLBuffer in = clij2.create(input1.getDimensions(), NativeTypeEnum.UnsignedByte);
        clij2.copy(input1, in);
        ClearCLBuffer out = clij2.create(input1.getDimensions(), NativeTypeEnum.UnsignedShort);

        // get images out of the GPU
        RandomAccessibleInterval in_rai = clij2.pullRAI(in);
        RandomAccessibleInterval out_rai = clij2.pullRAI(out);
        in.close();
        out.close();
        
        // actually apply the algorithm from imglib2
        ConnectedComponents.labelAllConnectedComponents(in_rai, out_rai, ConnectedComponents.StructuringElement.EIGHT_CONNECTED);

        // push result back to the GPU
        ClearCLBuffer result = clij2.push(out_rai);
        clij2.copy(result, output);
        result.close();

        return true;
    }

    @Override
    public String getDescription() {
        return "Apply imglib2 ConnectedComponents (8-connected) to an image to create a label map.\n\nNote: This operation runs on the CPU.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Labeling";
    }

    @Override
    public String getInputType() {
        return "Binary Image";
    }

    @Override
    public String getOutputType() {
        return "Label Image";
    }
    
    
    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return clij.create(input.getDimensions(), NativeTypeEnum.Float);
    }
}
