package com.sunhx.exam;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class App {
	final private String urlWallheave 
		= "https://wallhaven.cc/api/v1/search?"
				+ isNSFW()
				+ "&categories=101"
				+ "&sorting=random"
				+ "&resolutions=%dx%d"
				+ "&page=1";
	private final String savePath;
	private final String saveOldPath;
	private final String newPath;
	private NotifyDialog nDialog;
	private final String osName;
	
    public App() {
		String saveDir = String.format(System.getProperty("user.home") + "/%s", ".WallHeaven");
        savePath = String.format("%s/%s", saveDir, "last.jpg");
        newPath = String.format("%s.%s", savePath, "new");
		String saveOldDir = String.format("%s/%s", saveDir, "old");
        saveOldPath = String.format("%s/%s", saveOldDir, System.currentTimeMillis() + ".jpg");

        checkDirExists(saveDir);
        checkDirExists(saveOldDir);
		boolean hasUI = System.getProperty("sun.desktop") != null;
		osName = System.getProperty("os.name");

        if(hasUI) {
			nDialog = new NotifyDialog();
		}
    }

    private String isNSFW() {
        String ret = "purity=110";
		System.out.println(System.getProperty("user.dir"));
		File f = new File("NSFW");
		if(!f.exists()) {
		    return ret;
		}
		try(BufferedReader fr = new BufferedReader(new FileReader(f))) {
		    ret = "apikey=" + fr.readLine() + "&purity=001";
		} catch (IOException e) {
            e.printStackTrace();
        }
		return ret;		
	}

	public static void main(String[] args) throws IOException {

        App app = new App();
        if(app.nDialog != null)
        	app.nDialog.Show();

		if(app.getImageWallhaven()) {
			app.moveFile(app.savePath, app.saveOldPath);
			app.moveFile(app.newPath, app.savePath);
			app.setWallPaper(app.savePath);
		}

		if(app.nDialog != null)
			app.nDialog.Hide();
    }
    
    private void doWithApacheHttpClient(String u, String p) {
    	String fu = getFileUrl(u);
    	JSONObject j = new JSONObject(fu);
    	JSONArray a = j.getJSONArray("data");
    	fu = a.getJSONObject(0).getString("path");
    	try(CloseableHttpClient c = HttpClients.createDefault()) {
    		HttpGet g = new HttpGet(fu);
    		try(CloseableHttpResponse r = c.execute(g)) {
    			HttpEntity e = r.getEntity();
    			if(e != null) {
    				try(FileOutputStream o = new FileOutputStream(p)){
    					e.writeTo(o);
    				}
    			}  
    		}
    	} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
    private String getFileUrl(String u) {
    	String ret = null;
    	try(CloseableHttpClient c = HttpClients.createDefault()) {
    		HttpGet g = new HttpGet(u);
    		try(CloseableHttpResponse r = c.execute(g)) {
    			HttpEntity e = r.getEntity();
    			ret = EntityUtils.toString(e);
    		}
    	} catch (IOException e1) {
			e1.printStackTrace();
		}
    	return ret;
    } 
    
    private void setWallPaper(String imgFileName) throws IOException {
		if(osName.toLowerCase().contains("windows")) {
			User32.INSTANCE.SystemParametersInfoA(20, 0, imgFileName, 0);
		} else if (osName.toLowerCase().contains("linux")) {
			Runtime.getRuntime().exec("gsettings set org.gnome.desktop.background picture-uri " + imgFileName);
		}

	}
    
    private void moveFile(String of, String nf) {
        File f = new File(of);
        if (f.exists()) {
            if(!f.renameTo(new File(nf))) {
                System.out.println("error");
            }
        }
    }

    private void checkDirExists(String p) {
        File f = new File(p);

        if(!f.exists()) {
            if(!f.mkdirs()) {
                System.out.println("error");
            }
        }
    }
    
    private void doDownload(String u, String p) {
    	doWithApacheHttpClient(u, p);
    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    private boolean getImageWallhaven() {
		Dimension dim = getScreenSize(); 		
		return downloadImage(dim.width, dim.height);
	}

	public static Dimension getScreenSize() {
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = g.getScreenDevices();
        Dimension ret = new Dimension();
        if(devices.length > 0) {
        	ret.setSize(devices[0].getDisplayMode().getWidth(), devices[0].getDisplayMode().getHeight());
        }
        return ret;
	}

	private boolean downloadImage(int width, int height) {
		String url = String.format(urlWallheave, width, height);
		System.out.println(url);
        try {
			doDownload(url, newPath);
		} catch (Exception e) {			
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
