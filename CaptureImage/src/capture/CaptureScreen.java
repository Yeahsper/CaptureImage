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
import java.util.ArrayList;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class CaptureScreen implements Runnable {
	BufferedImage image;
	ImageWriter imageWriter;
	static ArrayList<BufferedImage> arrayList = new ArrayList<BufferedImage>();

	Float quality = 0.05f;
	int nrOfImages = 150;
	int fps = 30;
	int delayGif = 32;
	boolean createGif = false;
	boolean createImage = false;
	static int counter = 0;

	public CaptureScreen(boolean createGif, boolean createImage) {
		this.createGif = createGif;
		this.createImage = createImage;
	}

	@Override
	public void run() {

		synchronized (this) {
			for(int i = 0; i <= nrOfImages; i++) {

				try {
					Thread.sleep(1000 / fps);
					Thread.yield();
					captureScreen();
					//				System.out.println("Memory used="+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1000)+"Kb");
					//				System.out.println(Thread.currentThread().getName());
					System.out.println(counter);
					Runtime.getRuntime().gc();
					if(counter >= nrOfImages) {
						break;
					}
				} catch (HeadlessException | AWTException | IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if(createImage) {
			try {
				writeToFile();
			} catch (HeadlessException | AWTException | IOException e) {
				e.printStackTrace();
			}
		}

		if(createGif) {
			try {
				createGif();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public synchronized void createGif() throws IOException {

		BufferedImage first = arrayList.get(0);
		ImageOutputStream output = null;
		int i = 0;

		File gifFile = new File("gifs/gif"+i+".gif");
		while(gifFile.exists()) {
			gifFile = new File("gifs/gif"+i+".gif");
			i++;
			if(!gifFile.exists())
				break;
		}
		output = new FileImageOutputStream(gifFile);

		//Change the delay between each picture
		GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), delayGif, true);
		writer.writeToSequence(first);

		for (BufferedImage bi : arrayList) {
			BufferedImage next = bi;
			writer.writeToSequence(next);
		}

		System.out.println(gifFile.getName() + " created.");
		writer.close();
		output.close();
	}

	public synchronized void captureScreen() throws HeadlessException, AWTException, IOException {
		image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		arrayList.add(image);
		Runtime.getRuntime().gc(); Runtime.getRuntime().gc(); Runtime.getRuntime().gc();
		image.flush();
		counter++;


	}

	public synchronized void writeToFile() throws HeadlessException, AWTException, IOException {
		int i = 0;
		for(BufferedImage image : arrayList) {
			File file = new File("images/screenshot"+i+".png");
			while(file.exists()) {
				file = new File("images/screenshot"+i+".png");
				i++;
				if(!file.exists())
					break;
			}
			OutputStream out = new FileOutputStream(file);
			imageWriter = ImageIO.getImageWritersByFormatName("png").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(out);
			imageWriter.setOutput(ios);
			ImageWriteParam param = imageWriter.getDefaultWriteParam();
			if (param.canWriteCompressed()){
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(quality);
			}

			imageWriter.write(null, new IIOImage(image, null, null), param);
			imageWriter.dispose();

			System.out.println("Printed file " + file);
		}
	}



}
