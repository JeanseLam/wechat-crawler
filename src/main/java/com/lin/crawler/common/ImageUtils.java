package com.lin.crawler.common;

import org.bytedeco.javacpp.opencv_core;

import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvGet2D;
import static org.bytedeco.javacpp.opencv_core.cvSet2D;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_LANCZOS4;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_NN;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;

/**
 * 图片工具类
 */
public class ImageUtils {

    public static void clearWaterMark(String watermarkedImgPath) {

        String thumnbailImgPath = watermarkedImgPath;
        String maskedImgPath = watermarkedImgPath;

        opencv_core.IplImage watermarkedImage = cvLoadImage(watermarkedImgPath);

        // Filenames
        String outdir = watermarkedImgPath.split("\\.")[0];
        String extension = watermarkedImgPath.split("\\.")[1];
        String outName = outdir + "_unwatermarked." + extension;

        // Our cleaned image
        opencv_core.IplImage cleanedImage = new opencv_core.IplImage();
        cleanedImage = opencv_core.IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
        cleanedImage = getCleanImage(watermarkedImgPath, thumnbailImgPath, maskedImgPath);
        cvSaveImage(outName, cleanedImage);
    }


    // Gets converted picture
    private static opencv_core.IplImage getCleanImage(String watermarkedImgPath, String thumnbailImgPath, String maskedImgPath) {
        opencv_core.IplImage watermarkedImage = cvLoadImage(watermarkedImgPath);
        opencv_core.IplImage thumnbailImage = cvLoadImage(thumnbailImgPath);
        opencv_core.IplImage maskedImage = cvLoadImage(maskedImgPath);

        // Resize thumbnail
        opencv_core.IplImage thumbnail_resized = new opencv_core.IplImage();
        thumbnail_resized = opencv_core.IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
        cvResize(thumnbailImage, thumbnail_resized, CV_INTER_LANCZOS4);

        // Resize mask
        opencv_core.IplImage masked_resized = new opencv_core.IplImage();
        masked_resized = opencv_core.IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
        cvResize(maskedImage, masked_resized, CV_INTER_NN);

        // Our cleaned image
        opencv_core.IplImage cleanedImage = new opencv_core.IplImage();
        cleanedImage = opencv_core.IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
        cvCopy(watermarkedImage, cleanedImage);

        // Copy thumbnail resized over to mask
        for (int x = 0; x < watermarkedImage.cvSize().width(); x++) {
            for (int y = 0; y < watermarkedImage.cvSize().height(); y++) {
                opencv_core.CvScalar g = cvGet2D(masked_resized, y, x);

                if (g.red() == 0 && g.green() == 0 && g.blue() == 0) {
                    opencv_core.CvScalar s = cvGet2D(thumbnail_resized, y, x);
                    cvSet2D(cleanedImage, y, x, s);
                }
            }
        }

        return cleanedImage;
    }
}
