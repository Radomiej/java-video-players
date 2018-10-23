package de.disupport.video.players;

import de.disupport.video.NextFrameListener;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PureJavaVideoPlayer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private FrameGrab grab;

    private int frameRate;
    private volatile NextFrameListener nextFrameListener;
    private volatile Picture bufforedPicture;
    private volatile LinkedBlockingQueue<Picture> picturesQueue = new LinkedBlockingQueue();

    private ScheduledFuture<?> animatorFuture;
    private ScheduledFuture<?> bufforFuture;


    private boolean error;
    private AtomicBoolean endVideo = new AtomicBoolean();

    public PureJavaVideoPlayer(String videoPath, int frameRate) {
        this.frameRate = frameRate;
        try {
            openVideo(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
            error = true;
        } catch (JCodecException e) {
            e.printStackTrace();
            error = true;
        }

        if (error) return;

        executorService.submit(() -> cyclicFrameBuffer());
//        bufforFuture = scheduler.scheduleAtFixedRate(() -> {
//            bufferFrames();
//        }, 0, (long) (1000f / (frameRate * 2)), TimeUnit.MILLISECONDS);
//
        animatorFuture = scheduler.scheduleAtFixedRate(() -> {
            nextFrame();
        }, 0, (long) (1000f / frameRate), TimeUnit.MILLISECONDS);
    }

    private void cyclicFrameBuffer() {
        while(!endVideo.get()){
            bufferFrames();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void bufferFrames() {
        if (picturesQueue.size() > 8) return;
        for (int i = 0; i < 4; i++) {

            Picture picture = tryGrabNextFrame();
            if (picture == null) {
                bufforFuture.cancel(false);
                endVideo.set(true);
                return;
            }
            Picture copy = Picture.copyPicture(picture);
            System.out.println(picture.getWidth() + "x" + picture.getHeight() + " " + picture.getColor());
//        BufferedImage bufferedImage = AWTUtil.toBufferedImage(bufforedPicture);
            picturesQueue.add(copy);
        }

    }

    private void nextFrame() {
        if (nextFrameListener == null) return;

        if (endVideo.get() && picturesQueue.isEmpty()) {
            animatorFuture.cancel(false);
            //TODO end video
            return;
        }
        Picture picture = null;
        try {
            picture = picturesQueue.take();
            BufferedImage frame = AWTUtil.toBufferedImage(picture);
            nextFrameListener.nextFrame(frame);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    private Picture tryGrabNextFrame() {
        Picture picture = null;
        try {
            picture = grab.getNativeFrame();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return picture;
    }

    private void openVideo(String videoPath) throws IOException, JCodecException {
        File file = new File(videoPath);
        grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
    }


    public void addNextFrameListener(NextFrameListener nextFrameListener) {
        this.nextFrameListener = nextFrameListener;
    }
}
