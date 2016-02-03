package pk.gov.pitb.speakmytext;

import pk.gov.pitb.speakmytext.helper.Constants;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
			case 0:
				return new FragmentHomeScreen();
			case 1:
				Constants.fragmentTakePicture = new FragmentTakePicture();
				return Constants.fragmentTakePicture;
			case 2:
				Constants.fragmentRecordAudio = new FragmentRecordAudio();
				return Constants.fragmentRecordAudio;
			case 3:
				return new FragmentShowJobs();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 4;
	}
}