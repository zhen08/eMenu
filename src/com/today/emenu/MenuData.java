package com.today.emenu;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MenuData {

	public ArrayList<CatalogEntry> catalogEntryList;
	public String coverImage;
	public int globalTextColor;
	public String globalBackground;
	public int globalBackgroundColor1;
	public int globalBackgroundColor2;
	public boolean showCatalogName;
	public boolean showItemCode;
	public int numberOfPages;
	public boolean allowDirectLoad;

	public OnDownloadingListener onDownloading;

	private Context context;
	private Document doc;
	private Element docEle;
	private CatalogEntry catalogEntry;
	private boolean updateFile = false;
	private String baseURL = "";
	private ArrayList<String> filesChecked;
	private ArrayList<PageEntry> pageEntryList;
	private int progress;

	private boolean downloadFile(String fileName)
	{
		final String fileDownloading = baseURL+fileName;
		Debug.Log("Downloading: "+fileDownloading);
		onDownloading.onDownloading(fileDownloading, progress);
		try {
			final URL url = new URL(fileDownloading);
			final URLConnection connection = url.openConnection();
			final InputStream in = connection.getInputStream();
			final FileOutputStream f = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte[] buffer = new byte[262144];
			int len1 = 0;
			while ( (len1 = in.read(buffer)) != -1 ) {
				f.write(buffer,0,len1);
			}
			f.close();	
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	private void checkFile(String fileName){
		if (updateFile && !fileName.isEmpty()) {
			if (filesChecked == null){
				filesChecked = new ArrayList<String>();
			}
			if (!downloadFile(fileName)){
				if (!downloadFile(fileName)){
					  downloadFile(fileName);
				}
			}
			filesChecked.add(fileName);
			System.gc();
		}
		return;
	}

	public MenuData(Context context) {
		this.context = context;
		numberOfPages = 0;
		progress = 0;
	}

	public void loadData(String serverAddress){
		float price = 0;
		numberOfPages = 0;
		progress = 0;
		allowDirectLoad = false;
		if (!serverAddress.isEmpty()){
			updateFile = true;
			baseURL = serverAddress;
		}
		try {
			String tmpStr;

			if (pageEntryList == null){
				pageEntryList = new ArrayList<PageEntry>();
			}
			pageEntryList.clear();
			checkFile("eMenu.xml");
			FileInputStream fis = context.openFileInput("eMenu.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(fis);
			docEle = doc.getDocumentElement();

			coverImage = docEle.getAttribute("coverimg");
			checkFile(coverImage);

			tmpStr = docEle.getAttribute("textcolor");
			if (!tmpStr.isEmpty()){
				globalTextColor = Long.decode(tmpStr).intValue();
			} else {
				globalTextColor = 0xff000000;
			}

			tmpStr = docEle.getAttribute("backgroundcolor1");
			if (!tmpStr.isEmpty()){
				globalBackgroundColor1 = Long.decode(tmpStr).intValue();
			} else {
				globalBackgroundColor1 = 0x50ffffff;
			}

			tmpStr = docEle.getAttribute("backgroundcolor2");
			if (!tmpStr.isEmpty()){
				globalBackgroundColor2 = Long.decode(tmpStr).intValue();
			} else {
				globalBackgroundColor2 = 0x00ffffff;
			}

			globalBackground = docEle.getAttribute("background");
			checkFile(globalBackground);

			tmpStr = docEle.getAttribute("showcatalogname");
			showCatalogName = tmpStr.equals("true");

			tmpStr = docEle.getAttribute("showitemcode");
			if (Constants.FORCESHOWCODE) {
				showItemCode = true;
			} else {
				showItemCode = tmpStr.equals("true");
			}

			catalogEntryList = new ArrayList<CatalogEntry>();
			NodeList catnList = docEle.getElementsByTagName("catalog");
			int catnListLength = catnList.getLength();
			if(catnList != null && catnListLength > 0) {  
				for(int i=0; i < catnListLength; i++) {
					progress = (i+1) * 100 / catnListLength;
					Element catEntry = (Element)catnList.item(i);
					checkFile(catEntry.getAttribute("icon"));
					FileInputStream fisIcon = context.openFileInput(catEntry.getAttribute("icon"));
					Bitmap bitIcon = BitmapFactory.decodeStream(fisIcon);
					checkFile(catEntry.getAttribute("icond"));
					FileInputStream fisIcond = context.openFileInput(catEntry.getAttribute("icond"));
					Bitmap bitIcond = BitmapFactory.decodeStream(fisIcond);
					tmpStr = catEntry.getAttribute("iconheight");
					if (tmpStr.isEmpty()){
						tmpStr = "112";
					}
					catalogEntry = new CatalogEntry( Integer.valueOf(catEntry.getAttribute("id")),
							catEntry.getAttribute("name"),
							bitIcon,
							bitIcond, 
							Integer.decode(tmpStr),
							showCatalogName);
					NodeList pagenList = catEntry.getElementsByTagName("page");
					if (pagenList != null && pagenList.getLength() > 0) {
						for (int j=0;j<pagenList.getLength();j++){
							Element pgEntry = (Element)pagenList.item(j); 
							tmpStr = pgEntry.getAttribute("background");
							if (tmpStr.isEmpty()){
								tmpStr = globalBackground;
							} else {
								checkFile(tmpStr);
							}
							numberOfPages ++;
							PageEntry pageEntry = new PageEntry(catalogEntry,Integer.valueOf(pgEntry.getAttribute("id")),tmpStr);
							pageEntryList.add(pageEntry);
							NodeList itemnList = pgEntry.getElementsByTagName("item");
							if (itemnList != null && itemnList.getLength() > 0){
								for (int k=0;k<itemnList.getLength();k++){
									Element itEntry = (Element)itemnList.item(k);
									checkFile(itEntry.getAttribute("img"));
									String priceStr = itEntry.getAttribute("price");
									if (priceStr.isEmpty()) {
										price = 0;
									} else {
										try{
											price = Float.valueOf(priceStr);
										}
										catch (Exception e){
											Debug.Log("Incorrect Price Data:"+priceStr);
											e.printStackTrace();
											price = 0;
										}
									}
									ItemEntry itemEntry = new ItemEntry( Integer.valueOf(itEntry.getAttribute("id")),
											itEntry.getAttribute("type"),
											itEntry.getAttribute("name"),
											itEntry.getAttribute("code"),
											itEntry.getAttribute("description"),
											price,
											itEntry.getAttribute("unit"),
											itEntry.getAttribute("img"),
											showItemCode);
									pageEntry.itemList.add(itemEntry);
								}
							}
							catalogEntry.pageList.add(pageEntry);
						}
					}
					catalogEntryList.add(catalogEntry);
				}
			}
			if ((numberOfPages < 20)&&(Constants.BGDIRECTLOAD)) {
				allowDirectLoad = true;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public PageEntry getPageById(int id){
		return (pageEntryList.get(id));
	}
}
