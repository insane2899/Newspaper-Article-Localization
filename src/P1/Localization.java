package P1;

import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Arrays;
import java.awt.Color;

public class Localization {
	
	public static BufferedImage deepCopy(BufferedImage bi) {
	    ColorModel cm = bi.getColorModel();
	    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	    WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
	    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }
	
	static BufferedImage greyConvert(BufferedImage original) { 
		int alpha, red, green, blue;
        int newPixel;

        BufferedImage lum = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {

                alpha = new Color(original.getRGB(i, j)).getAlpha();
                red = new Color(original.getRGB(i, j)).getRed();
                green = new Color(original.getRGB(i, j)).getGreen();
                blue = new Color(original.getRGB(i, j)).getBlue();

                red = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
//grey level luminosity 0.21*red + 0.71*green + 0.07*blue
                newPixel = colorToRGB(alpha, red, red, red);
                lum.setRGB(i, j, newPixel);

            }
        }
        return lum;
	}
	
	
	public static int[] imageHistogram(BufferedImage input) {

        int[] histogram = new int[256];

        for(int i=0; i<histogram.length; i++) histogram[i] = 0;

        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
                int red = new Color(input.getRGB (i, j)).getRed();
                histogram[red]++;
            }
        }

        return histogram;
    }
	
	public static int otsuTreshold(BufferedImage original) {
        int[] histogram = imageHistogram(original);
        //Here original image given must be greyscale image or else only red threshold will be found
        long total = original.getHeight() * original.getWidth();
        double sum = 0;
        for(int i=0; i<256; i++) sum += i * histogram[i];
        double sumB = 0; //sum of pixel intensity till that point
        long wB = 0;     //no of pixels in back
        long wF = 0;     //no of pixels in front
        double varMax = 0;
        int threshold = 0;
        for(int i=0 ; i<256 ; i++) {
            wB += histogram[i];           //add the no of pixels on that particular point
            if(wB == 0) continue;         //if zero then add till it has a value
            wF = total - wB;              //pixels in front is pixels left to be parsed 
            if(wF == 0) break;
            sumB += (double) (i * histogram[i]);
            double mB = sumB / wB;             //mean till that point
            double mF = (sum - sumB) / wF;     //mean after that point
            double varBetween = (double) wB * (double) wF * (mB - mF) * (mB - mF);
            if(varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }
        return threshold;
    }
	
	public static BufferedImage otsuLocalConvert(BufferedImage original,int size) {
		if(size>original.getHeight()||size>original.getWidth()) {
			int t = otsuTreshold(original);
			return black_white(original,t);
		}
		int i,j;
		BufferedImage result = new BufferedImage(original.getWidth(),original.getHeight(),original.getType());
		for(i=0;i<original.getWidth()-size;i+=size) {
			for(j=0;j<original.getHeight()-size;j+=size) {
				BufferedImage img2 = new BufferedImage(size,size,original.getType());
				for(int k = i;k<i+size;k++) {
					for(int l = j;l<j+size;l++) {
						img2.setRGB(k-i, l-j, original.getRGB(k, l) );
					}
				}
				int t = otsuTreshold(img2);
				System.out.println(t);
				for(int k = i;k<i+size;k++) {
					for(int l=j;l<j+size;l++) {
						if((original.getRGB(k, l)&(0xff))>t) {
							int a = 255;
							int p=(a<<24)|(a<<16)|(a<<8)|(a);
							result.setRGB(k, l, p);
						}
						else {
							int a = 50;
							int p = (a<<24)|(a<<16)|(a<<8)|(a);
							result.setRGB(k, l, p);
						}
					}
				}
			}
			j-=size;
			BufferedImage img2 = new BufferedImage(size,original.getHeight()-j,original.getType());
			for(int k = i;k<i+size;k++) {
				for(int l = j;l<original.getHeight();l++) {
					img2.setRGB(k-i, l-j, original.getRGB(k, l));
				}
			}
			int t = otsuTreshold(img2);
			System.out.println(t);
			for(int k = i;k<i+size;k++) {
				for(int l=j;l<original.getHeight();l++) {
					if((original.getRGB(k, l)&(0xff))>t) {
						int a = 255;
						int p=(a<<24)|(a<<16)|(a<<8)|(a);
						result.setRGB(k, l, p);
					}
					else {
						int a = 50;
						int p = (a<<24)|(a<<16)|(a<<8)|(a);
						result.setRGB(k, l, p);
					}
				}
			}
		}
		i-=size;
		for(j=0;j<original.getHeight()-size;j+=size) {
			BufferedImage img2 = new BufferedImage(original.getWidth()-i,size,original.getType());
			for(int k = i;k<original.getWidth();k++) {
				for(int l = j;l<size;l++) {
					img2.setRGB(k-i, l-j, original.getRGB(k, l));
				}
			}
			int t = otsuTreshold(img2);
			System.out.println(t);
			for(int k = i;k<original.getWidth();k++) {
				for(int l=j;l<size;l++) {
					if((original.getRGB(k, l)&(0xff))>t) {
						int a = 255;
						int p=(a<<24)|(a<<16)|(a<<8)|(a);
						result.setRGB(k, l, p);
					}
					else {
						int a = 50;
						int p = (a<<24)|(a<<16)|(a<<8)|(a);
						result.setRGB(k, l, p);
					}
				}
			}
		}
		j-=size;
		BufferedImage img2 = new BufferedImage(original.getWidth()-i,original.getHeight()-j,original.getType());
		for(int k=i;k<original.getWidth();k++) {
			for(int l = j;l<original.getHeight();l++) {
				img2.setRGB(k-i, l-j, original.getRGB(k, l));
			}
		}
		int t = otsuTreshold(img2);
		System.out.println(t);
		for(int k = i;k<original.getWidth();k++) {
			for(int l=j;l<original.getHeight();l++) {
				if((original.getRGB(k, l)&(0xff))>t) {
					int a = 255;
					int p=(a<<24)|(a<<16)|(a<<8)|(a);
					result.setRGB(k, l, p);
				}
				else {
					int a = 50;
					int p = (a<<24)|(a<<16)|(a<<8)|(a);
					result.setRGB(k, l, p);
				}
			}
		}
		return result;
	}
	
	
	static BufferedImage black_white(BufferedImage img,int mid) {
		int width = img.getWidth();
		int height = img.getHeight();
		for(int x=0;x<height;x++) {
			for(int y=0;y<width;y++) {
				int p = img.getRGB(y, x);
				int a = p&0xff;
				if(a<mid) {
					a=50;
					p=(a<<24)|(a<<16)|(a<<8)|(a);
					img.setRGB(y, x, p);
				}
				else if(a>=mid) {
					a=255;
					p=(a<<24)|(a<<16)|(a<<8)|a;
					img.setRGB(y, x, p);
				}
			}
		}
		return img;
	}
	
	static BufferedImage RLSA_H(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		int a=10,value=5;
		for(int y=0;y<height;y++) {
			for(int x=a;x<width-a;x++) {
				int count=0;
				int t = img.getRGB(x, y);
				if((t&0xff)==50) {
					continue;
				}
				for(int i=x-a;i<=x+a;i++) {
					if(((img.getRGB(i, y))&(0xff))==50){
						count++;
					}
				}
				if(count>=value) {
					int p = (51<<24)|(51<<16)|(51<<8)|51;
					img.setRGB(x,y,p);
				}
			}
		}
		return img;
	}
	
	static BufferedImage RLSA_V(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		int a=10,value=2;
		for(int x=0;x<width;x++) {
			for(int y=a;y<height-2*a;y++) {
				int p = img.getRGB(x,y)&0xff;
				if(p==50) {
					continue;
				}
				int count=0;
				for(int i=y-a;i<=y+a;i++) {
					if(((img.getRGB(x, i))&(0xff))==50) {
						count++;
					}
				}
				if(count>=value) {
					int t = (51<<24)|(51<<16)|(51<<8)|51;
					img.setRGB(x, y, t);
				}
			}
		}
		return img;
	}
	
	static int[] Horizontal_Segregation(BufferedImage img) {
		int height=img.getHeight();
		int width = img.getWidth();
		int p=0;
		int[] array = new int[2];
		array[0]=0;
		for(int i=0;i<height;i++) {
			int j=0,count=0;
			for(j=0;j<width;j++) {
				int q = img.getRGB(j, i)&(0xff);
				if(q==50||q==51) {
					break;
				}
			}
			if(j==width) {
				boolean flag=false;
				for(int k=i;k<height&&!flag;k++) {
					int l;
					for(l=0;l<width;l++) {
						int r = img.getRGB(l,k)&(0xff);
						if(r==50||r==51) {
							break;
						}
					}
					if(l==width) {
						count++;
					}
					else {
						flag=true;
					}
				}
				if(p==0) {
					if(Math.abs(i+(count/2)-0)<10) {
						continue;
					}
					p++;
					array[p]=i+(count/2);
				}
				else {
					if(Math.abs(i+(count/2)-array[p])<20) {
						continue;
					}
					p++;
					array=Arrays.copyOf(array,p+1);
					array[p]=i+(count/2);
				}
				i+=count;
			}
		}
		p++;
		array=Arrays.copyOf(array, p+1);
		array[p]=img.getHeight();
		return array;
	}
	
	static int[] Vertical_Segregation(BufferedImage img) {
		int height=img.getHeight();
		int width = img.getWidth();
		int p=0;
		int[] array = new int[2];
		array[0]=0;
		for(int i=0;i<width;i++) {
			int j=0,count=0;
			for(j=0;j<height;j++) {
				int q = img.getRGB(i, j)&(0xff);
				if(q==50||q==51) {
					break;
				}
			}
			if(j==height) {
				boolean flag=false;
				for(int k=i;k<width&&!flag;k++) {
					int l;
					for(l=0;l<height;l++) {
						int r = img.getRGB(k,l)&(0xff);
						if(r==50||r==51) {
							break;
						}
					}
					if(l==height) {
						count++;
					}
					else {
						flag=true;
					}
				}
				if(p==0) {
					if(Math.abs(i+(count/2)-0)<10) {
						continue;
					}
					p++;
					array[p]=i+(count/2);
				}
				else {
					if(Math.abs(i+(count/2)-array[p])<20) {
						continue;
					}
					p++;
					array=Arrays.copyOf(array,p+1);
					array[p]=i+(count/2);
				}
				i+=count;
			}
		}
		p++;
		array=Arrays.copyOf(array, p+1);
		array[p]=img.getWidth();
		return array;
	}
	
	static void makeImages_Horizontal(int[] array,BufferedImage img) {
		int width = img.getWidth();
		for(int i=0;i<array.length-1;i++) {
			int x = array[i],y=array[i+1];
			BufferedImage img2 = new BufferedImage(width,y-x,img.getType());
			for(int j=0;j<y-x;j++) {
				for(int k=0;k<width;k++) {
					img2.setRGB(k,j,img.getRGB(k, j+x));
				}
			}
			BufferedImage img3=deepCopy(img2);
			img2=black_white(img2,otsuTreshold(img));
			int[] array2 = Vertical_Segregation(img2);
			//System.out.println(array2.length);
			makeImages_Vertical(array2,img3,i+1);
		}				
	}
	
	static void makeImages_Vertical(int[] array,BufferedImage img,int num) {
		int height = img.getHeight();
		for(int i=0;i<array.length-1;i++) {
			String s = "Result.jpg";
			int x = array[i],y=array[i+1];
			BufferedImage img2 = new BufferedImage(y-x,height,img.getType());
			for(int j=0;j<height;j++) {
				for(int k=0;k<y-x;k++) {
					img2.setRGB(k, j, img.getRGB(k+x, j));
				}
			}
			if(img2.getHeight()<100||img2.getWidth()<100) {
				continue;
			}
			s=num+"-"+Integer.toString(i+1)+s;
			try {
				File output = new File(s);
				ImageIO.write(img2,"jpg",output);
			}catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public static void main(String[] args)throws IOException{
		BufferedImage img=null;
		File f = null;
		try {
			f=new File("Datasets/newspaper2.jpg");
			img=ImageIO.read(f);
		}catch(Exception e) {
			System.out.println(e);
		}
		img = greyConvert(img);
		try {
			f=new File("Grey.jpg");
			ImageIO.write(img,"jpg",f);
		}catch(Exception e) {
			System.out.println(e);
		}
		//img = black_white(img,otsuTreshold(img));
		img = otsuLocalConvert(img,100);
		try {
			f=new File("BW.jpg");
			ImageIO.write(img,"jpg",f);
		}catch(Exception e) {
			System.out.println(e);
		}
		int[] array = Horizontal_Segregation(img);
		try {
			f=new File("Datasets/newspaper2.jpg");
			img=ImageIO.read(f);
		}catch(Exception e) {
			System.out.println(e);
		}
		makeImages_Horizontal(array,img);
		System.out.println("DONE!");
	}
}
