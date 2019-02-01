package net.flazzard.api.bingimg;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.management.RuntimeErrorException;
import javax.swing.JFrame;

import sun.misc.BASE64Decoder;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;

public final class BingImageSearcher {
	private boolean searchQuerySet = false;
	private String searchQuery;
	
	private BingImage.Size imageSize		= BingImage.Size.ALL;
	private BingImage.Type imageType		= BingImage.Type.All;
	private BingImage.Color imageColor		= BingImage.Color.ALL;
	private BingImage.Date imageDate		= BingImage.Date.ALL;
	private BingImage.Layout imageLayout	= BingImage.Layout.ALL;
	private BingImage.People imagePeople	= BingImage.People.ALL;
	private BingImage.License imageLicense	= BingImage.License.ALL;
	
	private BingImage.SafeSearch safeSearch = BingImage.SafeSearch.ON;
	
	
	public BingImageSearcher() {}
	
	public BingImageSearcher(String searchFor) {
		setQuery(searchFor);
	}
	
	
	
	public BingImageSearcher setQuery(String query)
	{
		searchQuerySet = false;
		
		if(query == null) {throw new RuntimeException("Search query should not be null!");}
		else if(query.equals("")) {throw new RuntimeException("Search query should not be empty!");}
		
		this.searchQuery = query;
		searchQuerySet = true;
		return this;
	}
	
	public BingImageSearcher setImageSize(BingImage.Size size)
	{
		if(size == null) {throw new RuntimeException("Size should not be null!");}
		
		imageSize = size;
		return this;
	}
	
	public BingImageSearcher setImageType(BingImage.Type type)
	{
		if(type == null) {throw new RuntimeException("Type should not be null!");}
		
		imageType = type;
		return this;
	}
	
	public BingImageSearcher setImageColor(BingImage.Color color)
	{
		if(color == null) {throw new RuntimeException("Color should not be null!");}
		
		imageColor = color;
		return this;
	}
	
	public BingImageSearcher setImageLayout(BingImage.Layout layout)
	{
		if(layout == null) {throw new RuntimeException("Color should not be null!");}
		
		imageLayout = layout;
		return this;
	}
	
	public BingImageSearcher setPeople(BingImage.People people)
	{
		if(people == null) {throw new RuntimeException("Color should not be null!");}
		
		imagePeople = people;
		return this;
	}
	
	public BingImageSearcher setImageLicense(BingImage.License license)
	{
		if(license == null) {throw new RuntimeException("Color should not be null!");}
		
		imageLicense = license;
		return this;
	}
	
	public BingImageSearcher setSafeSearchType(BingImage.SafeSearch safesearch)
	{
		if(safeSearch == null) {throw new RuntimeException("Color should not be null!");}
		
		safeSearch = safesearch;
		return this;
	}
	
	
	
	private static final class Link {
		public final String link;
		public final int type;
		public Link(String l, int t) {
			link = l;
			type = t;
		}
		@Override
		public String toString() {
			return link;
		}
	}
	
	
	public synchronized BufferedImage[] fetchImages(int howMuch) throws RuntimeException
	{
		return fetchImages(howMuch, 0);
	}
	
	public synchronized BufferedImage[] fetchImages(int howMuch, int timeout) throws RuntimeException
	{
		BufferedImage[] downloadedImages = new BufferedImage[howMuch];
		
		List<Link> image_links = fetchImageLinks(howMuch, timeout);
		image_links.forEach(System.out::println);
		
		for(int i = 0; i < howMuch; i++)
		{
			Link l = image_links.get(i);
			try {
				BufferedImage img = null;
				if(l.type == 0)
				{
					img = ImageIO.read(new URL(l.link));
				}
				else if(l.type == 1)
				{
					byte[] image_data;

					//BASE64Decoder decoder = new BASE64Decoder();
					String data = l.link.split(",")[1];
					image_data = Base64.getDecoder().decode(data);//decoder.decodeBuffer(data);
					ByteArrayInputStream bis = new ByteArrayInputStream(image_data);
					img = ImageIO.read(new ByteArrayInputStream(image_data));
				}
				
				downloadedImages[i] = img;
			} catch (Exception e) {
				System.err.println("Exception on "+plural(i)+" image:");
				System.err.println("Image link: "+image_links.get(i).link);
				System.err.flush();
				e.printStackTrace();
			}
		}
		
		return downloadedImages;
	}
	
	public static void main(String[] args) {
		BufferedImage[] imgs = new BingImageSearcher("cats").fetchImages(64);
		
		JFrame f = new JFrame("") {
			public void paint(Graphics g) {
				super.paint(g);
				
				g.drawImage(imgs[0], 0, 0, 300,300, null);
				g.drawImage(imgs[1], 300, 0, 300,300, null);
				g.drawImage(imgs[2], 0, 300, 300,300, null);
				g.drawImage(imgs[3], 300, 300, 300,300, null);
			}
		};
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 800);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	public synchronized List<Link> fetchImageLinks(int howMuch) throws RuntimeException
	{
		return fetchImageLinks(howMuch, 0);
	}
	
	public synchronized List<Link> fetchImageLinks(int howMuch, int timeout) throws RuntimeException
	{
		if(!searchQuerySet) throw new RuntimeException("Search query not set");
		
		final HtmlUnitDriver driver = new HtmlUnitDriver(true) {{
			getWebClient().setCssErrorHandler(new SilentCssErrorHandler());
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.SEVERE);
		}};
		
		
		driver.get(generateQuery());
		
		driver.executeScript(safeSearch.command, "");
		
		WebElement list_container = null;
		if(timeout > 0)
		{
				List<WebElement> check = new WebDriverWait(driver, timeout).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("mmComponent_images_1")));
			
				if(check.size() < 1) {System.err.println("Failed to locate list_container! <- aborting");return null;}
				else list_container = check.get(0);
			
		} else {
			try {
				list_container = driver.findElementById("mmComponent_images_1");
			}catch(Exception e) {System.err.println("Failed to locate list_container! <- aborting");return null;}
		}
		
		List<WebElement> data_rows = list_container.findElements(By.className("dgControl_list"));
		
		List<WebElement> results = new ArrayList<>();
		for (WebElement e : data_rows) {
			List<WebElement> l = e.findElements(By.cssSelector("img[src]"));
			System.out.println(l.size());
			results.addAll(l);
			if(results.size() >= howMuch) break;
			if(l.size() == 0) {driver.executeScript("window.scrollTo(0, document.body.scrollHeight)", "");try {
				Thread.sleep(6500);
			} catch (InterruptedException e1) {
			}}
		}
		
		
		List<Link> image_links = new ArrayList<>(results.size());
		for (WebElement e : results) {
			String link = e.getAttribute("src");
			if(!link.startsWith("http") && !link.startsWith("https") && !link.startsWith("data"))
			{
				System.err.println("This link is a traitor >:( - "+link);
				continue;
			}
			image_links.add(new Link(e.getAttribute("src"), (link.startsWith("data") ? 1 : 0) ));
		}
		
		return image_links;
	}
	
	private String plural(int number)
	{
		switch (number) {
		case 1:
			return number+"st";
		case 2:
			return number+"nd";
		case 3:
			return number+"rd";
		default:
			return number+"th";
		}
	}
	
	private String generateQuery()
	{
		StringBuilder query = new StringBuilder();
		
		query.append("https://www.bing.com/images/search?");
		query.append("q="+searchQuery);
		query.append("&qft=");
		
		query.append(imageSize.getVal());
		query.append(imageColor.val);
		query.append(imageType.val);
		query.append(imageLayout.val);
		query.append(imagePeople.val);
		query.append(imageDate.getVal());
		query.append(imageLicense.val);
		
		System.out.println(query);
		return query.toString();
	}
}
