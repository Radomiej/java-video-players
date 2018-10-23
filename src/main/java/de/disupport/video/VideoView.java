package de.disupport.video;

import de.disupport.video.players.PureJavaVideoPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoView {

    private JLabel videoLabel;
    private VideoPanel videoPanel;

    private JFrame frame;
    private PureJavaVideoPlayer videoPlayer;

    public VideoView() {
        videoLabel = new JLabel("Test");
        videoPanel = new VideoPanel();
        videoPanel.setSize(480, 320);
        //1. Create the frame.
        frame = new JFrame("VideoDemo");

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //3. Create components and put them in the frame.
        //...create emptyLabel...
        frame.getContentPane().add(videoPanel, BorderLayout.CENTER);

        //4. Size the frame.
        frame.pack();

        frame.setSize(480, 320);

        //5. Show it.
        frame.setVisible(true);

//        videoPlayer = new PureJavaVideoPlayer("C:\\Users\\RadoslawBojba\\Documents\\git\\kiosksoftware-g6\\working_dir\\Add_ons\\screensaver.mp4", 20);
        videoPlayer = new PureJavaVideoPlayer("C:\\Users\\RadoslawBojba\\Downloads\\PrintCube.mp4", 10);
        videoPlayer.addNextFrameListener((image) -> nextFrame(image));

//        videoPlayer.endVideoListener(() -> endVideo());
    }

    private void nextFrame(BufferedImage image) {
        videoPanel.setImage(image);
//        videoPanel.repaint();
//        videoLabel.setIcon(new ImageIcon(image));
//        videoLabel.repaint();
//        frame.repaint();
//        videoLabel.getContentPane().repaint();
    }
}
