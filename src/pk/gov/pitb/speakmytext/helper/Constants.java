package pk.gov.pitb.speakmytext.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pk.gov.pitb.speakmytext.FragmentRecordAudio;
import pk.gov.pitb.speakmytext.FragmentTakePicture;
import android.os.Environment;
import android.util.Log;

public class Constants {

	public static final int AC_READ_SELECTED = 10;
	public static final String EK_NAVIGATE_TO = "NavigateTo";

	public static FragmentTakePicture fragmentTakePicture;
	public static FragmentRecordAudio fragmentRecordAudio;

	public static String homeurl = "http://newt.itu.edu.pk";

	public static String url = homeurl + "/poorman-webapp/index.php/webapp/";
	// public static String homeurl="http://192.168.1.2";
	public static String app_token = "k234jj24bjkjhkjds9u3904jijeifj034fnkdng";
	public static String user_id = "";
	public static String auth_token = "";
	public static List<Boolean> temp_lang = new ArrayList<Boolean>();
	public static String imageurl;
	public static String job_id = "0";
	public static String image_path = "28";
	public static String SimSerialNumber = "28";
	public static String currentjob = "1";
	public static String translator_chunk_id = "1";
	public static String chunk_id = "-1";
	public static String imie = "";
	public static String mac = "";
	public static boolean lod = false;
	public static boolean lod2 = false;

	public static List<Integer> jobsdone = new ArrayList<Integer>(0);
	public static List<Integer> jobsnotdone = new ArrayList<Integer>(0);
	public static List<String> jobsid = new ArrayList<String>(0);
	public static ArrayList<String> all_jobs_srcs = new ArrayList<String>(0);
	public static ArrayList<String> info_jobs_status = new ArrayList<String>(0);
	public static boolean inProgress1 = false;
	public static boolean inProgress2 = false;

	public static boolean createDirIfNotExists(String path) {
		boolean ret = true;

		File file = new File(Environment.getExternalStorageDirectory(), path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e("TravellerLog :: ", "Problem creating pmr folder");
				ret = false;
			}
		}
		return ret;
	}

	public static boolean createFileIfNotExists(String filename) {
		boolean ret = true;
		String dir = Environment.getExternalStorageDirectory() + "/pmr";
		File file = new File(dir, filename);
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					Log.e("TravellerLog :: ", "Problem creating files");
					ret = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return ret;
	}

	public static boolean get_current_job() {

		int myNum = Integer.parseInt(currentjob);
		currentjob = String.valueOf(myNum);
		String dir = Environment.getExternalStorageDirectory() + "/pmr";
		File f = new File(dir, "currentversion.txt");
		if (f.exists()) {

			FileInputStream fIn = null;
			try {
				fIn = new FileInputStream(f);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			String aDataRow = "";
			String aBuffer = "";
			try {
				while ((aDataRow = myReader.readLine()) != null) {
					aBuffer += aDataRow;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			currentjob = aBuffer;
			try {
				myReader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		} else {

			try {
				if (!f.createNewFile()) {
					Log.e("TravellerLog :: ", "Problem creating files");

				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			try {
				FileOutputStream fOut = new FileOutputStream(f);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.write(currentjob);
				myOutWriter.close();
			} catch (IOException e) {
				Log.e("Exception", "File write failed: " + e.toString());
			}
		}
		return true;
	}

	public static boolean increase_current_job() {

		int myNum = Integer.parseInt(currentjob);
		myNum++;
		currentjob = String.valueOf(myNum);
		String dir = Environment.getExternalStorageDirectory() + "/pmr";
		File f = new File(dir, "currentversion.txt");
		if (f.exists()) {

			f.delete();
			try {
				if (!f.createNewFile()) {
					Log.e("TravellerLog :: ", "Problem creating files");

				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		try {
			FileOutputStream fOut = new FileOutputStream(f);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.write(currentjob);
			myOutWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
		return true;
	}

	public static boolean update_jobs() {
		String dir = Environment.getExternalStorageDirectory() + "/pmr";
		File f = new File(dir, "jobsnotdone.txt");

		List<String> jobs = new ArrayList<String>(0);

		try {

			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {
				jobs.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("hello");
		}

		jobsnotdone.clear();
		for (int i = 0; i < jobs.size(); i++) {
			jobsnotdone.add(Integer.valueOf(jobs.get(i)));
		}

		jobsnotdone.add(Integer.valueOf(currentjob));

		int[] arr1 = new int[jobsnotdone.size()];
		// option 1
		for (int i = 0; i < jobsnotdone.size(); i++) {
			arr1[i] = jobsnotdone.get(i);
		}

		Arrays.sort(arr1);
		System.out.println(Arrays.toString(arr1));

		jobsnotdone.clear();
		for (int i = (arr1.length) - 1; i >= 0; i--) {
			jobsnotdone.add(Integer.valueOf(arr1[i]));
		}
		f.delete();
		try {
			if (!f.createNewFile()) {
				Log.e("TravellerLog :: ", "Problem creating files");

			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			FileOutputStream fOut = new FileOutputStream(f);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

			for (int i = (arr1.length) - 1; i >= 0; i--) {
				myOutWriter.write(String.valueOf(arr1[i]));
				myOutWriter.write("\n");
			}

			myOutWriter.close();

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
		return true;
	}

	public static boolean update_jobs_id() {
		String dir = Environment.getExternalStorageDirectory() + "/pmr";
		File f = new File(dir, "jobsid.txt");

		List<String> jobs = new ArrayList<String>(0);

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {
				jobs.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("hello");

		}

		jobsid.clear();
		for (int i = 0; i < jobs.size(); i++) {
			jobsid.add(String.valueOf(jobs.get(i)));
		}

		jobsid.add(String.valueOf(job_id));
		f.delete();
		try {
			if (!f.createNewFile()) {
				Log.e("TravellerLog :: ", "Problem creating files");

			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			FileOutputStream fOut = new FileOutputStream(f);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

			for (int i = 0; i < jobsid.size(); i++) {
				myOutWriter.write(String.valueOf(jobsid.get(i)));
				myOutWriter.write("\n");
			}

			myOutWriter.close();

		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
		return true;
	}

	public static boolean update_jobs_resources() throws Exception {

		info_jobs_status.clear();
		all_jobs_srcs.clear();
		String dir = Environment.getExternalStorageDirectory() + "/pmr";
		File f = new File(dir, "jobsdone.txt");

		List<String> jobs = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {
				jobs.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("hello");

		}
		jobsdone.clear();
		int continued = jobs.size();
		for (int i = 0; i < jobs.size(); i++) {
			jobsdone.add(Integer.valueOf(jobs.get(i)));
		}

		f = new File(dir, "jobsnotdone.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {
				jobs.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("hello");
		}
		jobsnotdone.clear();
		for (int i = continued; i < jobs.size(); i++) {
			jobsnotdone.add(Integer.valueOf(jobs.get(i)));
		}

		for (int i = 0; i < jobs.size(); i++) {
			info_jobs_status.add(jobs.get(i));
			all_jobs_srcs.add(dir + "/" + jobs.get(i) + "job.jpg");
		}

		return true;

	}
}