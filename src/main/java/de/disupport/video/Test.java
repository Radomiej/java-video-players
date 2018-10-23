package de.disupport.video;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    private final String videoPath = "C:\\Users\\RadoslawBojba\\Downloads\\PrintCube.mp4";
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private FrameGrab[] grabs;
    private AtomicInteger frameCounter = new AtomicInteger();
    private volatile long startTime;
    private int grabsCount = 4;

    public static void main(String[] args) throws IOException, JCodecException {
        new Test();

    }

    public Test(){
        File videoFile = new File(videoPath);
        startTime = System.currentTimeMillis();


        grabs = new FrameGrab[grabsCount];
        for(int i = 0; i < grabsCount; i++) {
            try {
                grabs[i] = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));
                grabs[i].seekToFramePrecise(i);
                int finalI = i;
                executorService.submit(() -> loadFrames(grabs[finalI], finalI));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JCodecException e) {
                e.printStackTrace();
            }
        }
//        for(int i = 0; i < 100; i++) {
//            int finalI = i;
//            executorService.submit(() -> loadFrame(finalI));
//        }


    }

    private void loadFrames(FrameGrab grab, int startFrame) {
        Picture picture = null;
        while(true) {
            try {
                picture = grab.getNativeFrame();
                if(picture == null) break;

                long timeOfExecution = System.currentTimeMillis() - startTime;
                System.out.println(picture.getWidth() + "x" + picture.getHeight() + " " + picture.getColor() + " time: " + timeOfExecution + " frame: " + startFrame);
                startFrame += grabsCount;
                grab.seekToFramePrecise(startFrame);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JCodecException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFrame(int frameNumber) {
        int index = frameNumber % grabsCount;
        FrameGrab grab = grabs[index];
//        FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
        Picture picture = null;
        try {
            picture = grab.getNativeFrame();
            long timeOfExecution = System.currentTimeMillis() - startTime;
            System.out.println(picture.getWidth() + "x" + picture.getHeight() + " " + picture.getColor() + " time: " + timeOfExecution + " frame: " + frameNumber);
            grab.seekToFramePrecise(frameNumber + grabsCount);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JCodecException e) {
            e.printStackTrace();
        }
    }
}
