package net.haesleinhuepf.imglib2;

import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.algorithm.integral.IntegralImg;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.algorithm.morphology.*;
import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;

public class SimpleImglib2 implements SimpleImglib2Ops {

    private static int num_threads = determineNumThreads();

    private static int determineNumThreads() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static Img topHat(Img input, int... radius) {
        return TopHat.topHat(input, StructuringElements.rectangle(radius, false), num_threads);
    }

    public static Img bottomHat(Img input, int... radius) {
        return BlackTopHat.blackTopHat(input, StructuringElements.rectangle(radius, false), num_threads);
    }

    public static Img opening(Img input, int... radius) {
        return Opening.open(input, StructuringElements.rectangle(radius, false), num_threads);
    }

    public static Img closing(Img input, int... radius) {
        return Closing.close(input, StructuringElements.rectangle(radius, false), num_threads);
    }

    public static Img erode(Img input, int... radius) {
        return Erosion.erode(input, StructuringElements.rectangle(radius, false), num_threads);
    }

    public static Img dilate(Img input, int... radius) {
        return Dilation.dilate(input, StructuringElements.rectangle(radius, false), num_threads);
    }

    public static Img connectedComponentsLabelingBox(RandomAccessibleInterval input) {
        long[] dimensions = new long[input.numDimensions()];
        input.dimensions(dimensions);
        Img output = ArrayImgs.unsignedShorts(dimensions);
        ConnectedComponents.labelAllConnectedComponents(input, output, ConnectedComponents.StructuringElement.EIGHT_CONNECTED);
        return output;
    }

    public static Img gaussianBlur(Img input, double ... sigmas) {
        Img output = input.factory().create( input );
        Gauss3.gauss(sigmas, input, output);
        return output;
    }










    /**
     * Generic, type-agnostic method to create an identical copy of an Img
     *
     * Source: https://imagej.net/ImgLib2_Examples.html#Example_2a_-_Duplicating_an_Img_using_a_generic_method
     *
     * @param input - the Img to copy
     * @return - the copy of the Img
     */
    public Img copy( final Img input )
    {
        // create a new Image with the same properties
        // note that the input provides the size for the new image as it implements
        // the Interval interface
        Img output = input.factory().create( input );

        // create a cursor for both images
        Cursor cursorInput = input.cursor();
        Cursor cursorOutput = output.cursor();

        // iterate over the input
        while ( cursorInput.hasNext())
        {
            // move both cursors forward by one pixel
            cursorInput.fwd();
            cursorOutput.fwd();

            // set the value of this pixel of the output image to the same as the input,
            // every Type supports T.set( T type )
            ((RealType)cursorOutput.get()).set( (RealType)cursorInput.get() );
        }

        // return the copy
        return output;
    }
}
