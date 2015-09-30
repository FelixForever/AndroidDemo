package com.isoftstone.tianlai.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.isoftstone.tianlai.MainActivity;

public class LyrcUtil {
	@SuppressWarnings("unused")
	private static final String TAG = "LRCUtils";

	MainActivity mediaPlay;

	private static Vector<Timelrc> lrclist;
	private boolean IsLyricExist = false;
	private int lastLine = 0;

	public Vector<Timelrc> getLrcList(){
		
		return lrclist;
	}
	
	public void setNullLrcList(){
		
		lrclist = null;
	}
	
	public LyrcUtil(MainActivity mediaPlayActivity) {
		mediaPlay = mediaPlayActivity;
	}

	public void RefreshLRC(int current) {
		if (IsLyricExist) {
			for (int i = 1; i < lrclist.size(); i++) {
				if (current < lrclist.get(i).getTimePoint())
					if (i == 1 || current >= lrclist.get(i - 1).getTimePoint()) {
						
						mediaPlay.setLRCText(lrclist.get(i - 1).getLrcString(),
								lastLine != (i - 1));
						lastLine = i - 1;
						// playlrcText.setText(lrclist.get(i-1).getLrcString());
					}
			}
		}
	}

	public void ReadLRC(File f) {
		try {
			if (f==null || !f.exists()) {
				IsLyricExist = false;
				lrclist = null;
				mediaPlay.setLRCText("歌词不存在",true);
			} else {
				lrclist = new Vector<Timelrc>();
				IsLyricExist = true;
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, GetCharset(f)));
				String strTemp = "";
				while ((strTemp = br.readLine()) != null) {
					// Log.d(TAG,"strTemp = "+strTemp);
					strTemp = AnalyzeLRC(strTemp); 
				}
				br.close();
				is.close();
				Collections.sort(lrclist, new Sort());

				for(int i=0;i<lrclist.size();i++){
					
					Timelrc one = lrclist.get(i);
					if(i+1<lrclist.size()){
						Timelrc two = lrclist.get(i+1);
						one.setSleepTime(two.getTimePoint()-one.getTimePoint());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private String AnalyzeLRC(String LRCText) {
		try {
			int pos1 = LRCText.indexOf("["); 
			int pos2 = LRCText.indexOf("]"); 
			
			if (pos1 == 0 && pos2 != -1) {
				Long time[] = new Long[GetPossiblyTagCount(LRCText)];
				time[0] = TimeToLong(LRCText.substring(pos1 + 1, pos2)); 
				if (time[0] == -1)
					return ""; // LRCText
				String strLineRemaining = LRCText;
				int i = 1;
				while (pos1 == 0 && pos2 != -1) {

					strLineRemaining = strLineRemaining.substring(pos2 + 1); 
					pos1 = strLineRemaining.indexOf("[");
					pos2 = strLineRemaining.indexOf("]");
					if (pos2 != -1) {
						time[i] = TimeToLong(strLineRemaining.substring(
								pos1 + 1, pos2));
						if (time[i] == -1) 
							return ""; // LRCText
						i++;
					}
				}

				Timelrc tl = new Timelrc();
				for (int j = 0; j < time.length; j++) {
					if (time[j] != null) {
						tl.setTimePoint(time[j].intValue());
						tl.setLrcString(strLineRemaining);
						lrclist.add(tl);
						tl = new Timelrc();
						
					}
				}
				return strLineRemaining;
			} else
				return "";
		} catch (Exception e) {
			return "";
		}
	}

	private int GetPossiblyTagCount(String Line) {
		String strCount1[] = Line.split("\\[");
		String strCount2[] = Line.split("\\]");
		if (strCount1.length == 0 && strCount2.length == 0)
			return 1;
		else if (strCount1.length > strCount2.length)
			return strCount1.length;
		else
			return strCount2.length;
	}

	public long TimeToLong(String Time) {
		try {
			String[] s1 = Time.split(":");
			int min = Integer.parseInt(s1[0]);
			String[] s2 = s1[1].split("\\.");
			int sec = Integer.parseInt(s2[0]);
			int mill = 0;
			if (s2.length > 1)
				mill = Integer.parseInt(s2[1]);
			return min * 60 * 1000 + sec * 1000 + mill * 10;
		} catch (Exception e) {
			return -1;
		}
	}

	
	
	@SuppressWarnings("resource")
	public String GetCharset(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try {
			boolean checked = false;
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked) {
				@SuppressWarnings("unused")
				int loc = 0;
				while ((read = bis.read()) != -1) {
					loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF)
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) 
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return charset;
	}

	private class Sort implements Comparator<Timelrc> {
		public Sort() {
		}

		public int compare(Timelrc tl1, Timelrc tl2) {
			return sortUp(tl1, tl2);
		}

		private int sortUp(Timelrc tl1, Timelrc tl2) {
			if (tl1.getTimePoint() < tl2.getTimePoint())
				return -1;
			else if (tl1.getTimePoint() > tl2.getTimePoint())
				return 1;
			else
				return 0;
		}
	}

	public static class Timelrc {
		private String lrcString;
		private int sleepTime;
		private int timePoint;

		Timelrc() {
			lrcString = null;
			sleepTime = 0;
			timePoint = 0;
		}

		public void setLrcString(String lrc) {
			lrcString = lrc;
		}

		public void setSleepTime(int time) {
			sleepTime = time;
		}

		public void setTimePoint(int tPoint) {
			timePoint = tPoint;
		}

		public String getLrcString() {
			return lrcString;
		}

		public int getSleepTime() {
			return sleepTime;
		}

		public int getTimePoint() {
			return timePoint;
		}
	}
} 

