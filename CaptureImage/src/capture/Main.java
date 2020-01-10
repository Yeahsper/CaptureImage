package capture;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Main {

	public static void main(String[] args) throws IOException, HeadlessException, AWTException, InterruptedException {

		Thread thread1 = new Thread(new CaptureScreen(true, false));
		Thread thread2 = new Thread(new CaptureScreen(false, false));
		Thread thread3 = new Thread(new CaptureScreen(false, false));
		Thread thread4 = new Thread(new CaptureScreen(false, false));
		thread1.start();
		thread1.join();
		thread2.start();
		thread2.join();
		thread3.start();
		thread3.join();
		thread4.start();
		thread4.join();

	}

}
