package com.screenshotComparison;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class CommonSeleniumMethods {

	// Class variables
	WebDriver driver;
	String imageFlag;

	public CommonSeleniumMethods() {
		driver = null;
		imageFlag = ConfigReader.getProperty("imageFlag");
	}

	// This method will initiate browser instance
	public boolean startBrowser(String browserName) {

		browserName = browserName.toLowerCase();

		try {
			switch (browserName) {
			case "chrome": {
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "\\src\\resources\\Executors\\chromedriver.exe");
				driver = new ChromeDriver();
				break;
			}
			case "firefox": {
				driver = new FirefoxDriver();
				break;
			}
			case "default": {
				System.out.println("No browser is mentioned in the config file, plecheck check....");
				return false;
			}
			}
			driver.manage().window().maximize();
			return true;
		} catch (Exception e) {
			System.out.println(e + " occurs while accessing url....");
			return false;
		}
	}

	// This method will open the URL provided
	public boolean openURL(String url) {
		try {
			driver.get(url);
			return true;
		} catch (Exception e) {
			System.out.println(e + " occurs while accessing url....");
			return false;
		}
	}

	// This method will take the screenshot of active page
	public boolean takeScreenShot() throws WebDriverException, IOException {

		File screenshotFile = null;
		String shotName = Thread.currentThread().getStackTrace()[2].getMethodName() + ".png";
		File imageFile = new File(
				System.getProperty("user.dir") + "\\src\\resources\\Images\\" + imageFlag + "\\" + shotName);

		// Deleting old file
		if (imageFile.exists())
			imageFile.delete();

		try {
			screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshotFile, imageFile);
			return true;
		} catch (WebDriverException e) {
			System.out.println(e + " while taking screenshot");
			return false;
		}
	}

	// This method is to compare the screenshot
	public boolean compareImages() throws Exception {

		// Checking if the run executed to take expected images
		// In such case skipping the comparison
		if (imageFlag.equalsIgnoreCase("expected"))
			return true;

		String shotName = Thread.currentThread().getStackTrace()[2].getMethodName() + ".png";
		String expectedImage = System.getProperty("user.dir") + "\\src\\resources\\Images\\Expected\\" + shotName;
		String actualImage = System.getProperty("user.dir") + "\\src\\resources\\Images\\Actual\\" + shotName;
		String diffImage = System.getProperty("user.dir") + "\\src\\resources\\Images\\Difference\\" + shotName;

		try {
			File expImgFile = new File(expectedImage);
			File actImgFile = new File(actualImage);
			File diffImgFile = new File(diffImage);

			int sample = 0;

			if (expImgFile.exists()) {

				BufferedImage expBuffImg = ImageIO.read(expImgFile);
				BufferedImage actBuffImg = ImageIO.read(actImgFile);

				if (expBuffImg.getHeight() == actBuffImg.getHeight()) {
					if (expBuffImg.getWidth() == actBuffImg.getWidth()) {
						int loopwidth = expBuffImg.getWidth();
						int loopheight = expBuffImg.getHeight();
						if (expBuffImg.getWidth() > actBuffImg.getWidth()) {
							loopwidth = actBuffImg.getWidth();
						}
						if (expBuffImg.getHeight() > actBuffImg.getHeight()) {
							loopheight = actBuffImg.getHeight();
						}

						System.out.println("Comparison Started....");

						// Copying actual image in the Difference folder to mark
						// the differences
						FileUtils.copyFile(actImgFile, diffImgFile);
						BufferedImage diffBuffImg = ImageIO.read(diffImgFile);

						for (int i = 0; i < loopwidth; i++) {
							for (int j = 0; j < loopheight; j++) {
								if (!(expBuffImg.getRGB(i, j) == actBuffImg.getRGB(i, j))) {
									diffBuffImg.setRGB(i, j, -147220175);
									diffBuffImg.flush();
									sample++;
								}
								if (j == loopheight - 1) {
									diffBuffImg.setRGB(i, j, -147220175);
								}
							}
						}
						if (sample != 0) {
							FileOutputStream strm = new FileOutputStream(diffImgFile);
							ImageIO.write(diffBuffImg, "png", strm);
							strm.close();
							System.out.println(
									"There are " + sample + 1 + " pixel difference in actual and expected images.");
						} else {
							diffImgFile.delete();
							return true;
						}
					} else
						System.out.println("Images width are not same...");
				} else
					System.out.println("Images height are not same...");
			} else
				System.out.println("Expected Image " + shotName + " is not present at location");
			return false;
		} catch (Exception e) {
			System.out.println(e + " occured while comparing the screenshot...");
			return false;
		}
	}

	// This method is to get dimension of an image
	public Dimension getImageDim(File expImgFile) throws IOException {
		ImageInputStream in;
		ImageReader reader = null;
		try {
			in = ImageIO.createImageInputStream(expImgFile);
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				reader = readers.next();
				reader.setInput(in);
			}
		} catch (Exception e) {
			System.out.println("Exception occurred: " + e);
		} finally {
			reader.dispose();
		}
		return new Dimension(reader.getWidth(0), reader.getHeight(0));
	}

	// This method will close the driver instance
	public boolean stopBrowser() {
		try {
			driver.quit();
			return true;
		} catch (Exception e) {
			System.out.println(e + " occurs while stoping browser....");
			return false;
		}
	}
}