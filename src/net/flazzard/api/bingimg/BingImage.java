package net.flazzard.api.bingimg;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class BingImage {
	private BingImage() {}
	
	public static enum Size
	{
		ALL(""),
		SMALL("+filterui:imagesize-small"),
		MEDIUM("+filterui:imagesize-medium"),
		LARGE("+filterui:imagesize-large"),
		VERY_LARGE("+filterui:imagesize-wallpaper");
		
		public static final Size CUSTOM(int width, int height) {ALL.w = width; ALL.h = height; return ALL;}
		
		private String val;
		int w = -1;
		int h = -1;
		private Size(String s) {this.val = s;}
		
		public String getVal() {int _w = w; int _h = h; w=-1;h=-1; return (_w != -1 && _h != -1) ? "+filterui:imagesize-custom_"+_w+"_"+_h : val;}
	}
	
	public static enum Type
	{
		All(""),
		PHOTO("+filterui:photo-photo"),
		GRAPHICS("+filterui:photo-clipart"),
		TRANSPARENT("+filterui:photo-transparent"),
		ANIMATED_GIF("+filterui:photo-animatedgif"),
		CLIPART("+filterui:photo-clipart"),
		LINE_DRAWING("+filterui:photo-linedrawing");
		
		String val;
		private Type(String s) {this.val = s;}
	}
	
	public static enum Layout
	{
		ALL(""),
		SQUARE("+filterui:aspect-square"),
		WIDE("+filterui:aspect-wide"),
		TALL("+filterui:aspect-tall");
		
		String val;
		private Layout(String s) {this.val = s;}
	}
	
	public static enum People
	{
		ALL(""),
		JUST_FACES("+filterui:face-face"),
		HEAD_AND_SHOULDERS("+filterui:face-portrait");
		
		String val;
		private People(String s) {this.val = s;}
	}
	
	public static enum Date
	{
		ALL(""),
		PAST_DAY("+filterui:age-lt1440"),
		PAST_WEEK("+filterui:age-lt10080"),
		PAST_MONTH("+filterui:age-lt43800"),
		PAST_YEAR("+filterui:age-lt525600");
		
		public static Date CUSTOM(int minutes_ago) {ALL.time = minutes_ago; return ALL;}
		
		private String val;
		int time = -1;
		private Date(String s) {this.val = s;}
		
		String getVal() {int _time = time; time = -1; return (_time != -1) ? "+filterui:age-lt"+time : val;}
	}
	
	public static enum License
	{
		ALL(""),
		ALL_CREATIVE_COMMONS("+filterui:licenseType-Any"),
		PUBLIC_DOMAINNN("+filterui:license-L1"),
		SHARE_AND_USE("+filterui:license-L2_L3_L4_L5_L6_L7"),
		SHARE_AND_COMMERCIAL_USE("+filterui:license-L2_L3_L4"),
		MODIFY_SHARE_USE("+filterui:license-L2_L3_L5_L6"),
		MODIFY_SHARE_AND_COMMERCIAL_USE("+filterui:license-L2_L3");
		
		String val;
		private License(String s) {this.val = s;}
	}
	
	public static enum Color
	{
		ALL(""),
		RED("+filterui:color2-FGcls_RED"),
		ORANGE("+filterui:color2-FGcls_ORANGE"),
		YELLOW("+filterui:color2-FGcls_YELLOW"),
		GREEN("+filterui:color2-FGcls_GREEN"),
		TEAL("+filterui:color2-FGcls_TEAL"),
		BLUE("+filterui:color2-FGcls_BLUE"),
		PURPLE("+filterui:color2-FGcls_PURPLE"),
		PINK("+filterui:color2-FGcls_PINK"),
		BROWN("+filterui:color2-FGcls_BROWN"),
		BLACK("+filterui:color2-FGcls_BLACK"),
		GRAY("+filterui:color2-FGcls_GRAY"),
		WHITE("+filterui:color2-FGcls_WHITE"),
		
		BLACK_WHITE("+filterui:color2-bw"),
		COLORED("+filterui:color2-color");
		
		String val;
		private Color(String s) {this.val = s;}
		
		public static Color fromColor(java.awt.Color c1)
		{
			final java.awt.Color[] constantColors = new java.awt.Color[] {null, java.awt.Color.RED, java.awt.Color.ORANGE, java.awt.Color.YELLOW,
					java.awt.Color.GREEN, java.awt.Color.CYAN, java.awt.Color.BLUE, java.awt.Color.MAGENTA, java.awt.Color.PINK,
					new java.awt.Color(102, 51, 0), java.awt.Color.BLACK, java.awt.Color.GRAY, java.awt.Color.WHITE, null};
			
			ArrayList<Double> d = new ArrayList<>();
			
			for(java.awt.Color c2 : constantColors)
			{
				if(c2 == null) {d.add(Double.MAX_VALUE);continue;}
				
			    int red1 = c1.getRed();
			    int red2 = c2.getRed();
			    int rmean = (red1 + red2) >> 1;
			    int r = red1 - red2;
			    int g = c1.getGreen() - c2.getGreen();
			    int b = c1.getBlue() - c2.getBlue();
			    d.add(Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8)));
		    }
			int closestColorIndex = d.indexOf(d.stream().min(Comparator.comparing(Double::valueOf)).get());
			System.out.println(closestColorIndex);
			return Color.values()[closestColorIndex];
		}
	}
	
	public static enum SafeSearch
	{
		OFF("SafeSearch.submit(this, 'off')"),
		ON("SafeSearch.submit(this, 'demote')"),
		STRICT("SafeSearch.submit(this, 'strict')");
		
		String command;
		private SafeSearch(String s) {command = s;}
	}
}