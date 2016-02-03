package pk.gov.pitb.speakmytext;

import pk.gov.pitb.speakmytext.helper.Constants;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class FragmentShowJobs extends Fragment {

	String audiourl;
	MediaPlayer mp = new MediaPlayer();
	private GridView gridView;
	private static GridViewAdapter customGridAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.activity_viewmyjob, container, false);
		gridView = (GridView) rootView.findViewById(R.id.gridView);
		customGridAdapter = new GridViewAdapter(getActivity(), R.layout.row_grid, Constants.all_jobs_srcs);
		gridView.setAdapter(customGridAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				String chk_status = Constants.info_jobs_status.get(position);
				if (Constants.jobsdone.contains(Integer.valueOf(chk_status))) {
					Intent i = new Intent();
					i.setClass(getActivity(), Read_Selected.class);
					i.putExtra("job", Constants.info_jobs_status.get(position));
					startActivityForResult(i, Constants.AC_READ_SELECTED);
				}

			}

		});

		return rootView;
	}
	
	public static void updateResources(){
		try{
			Log.e("size", Constants.all_jobs_srcs.size()+";;");
			customGridAdapter.notifyDataSetChanged();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == Constants.AC_READ_SELECTED) {
			try {
				ActivityHomePage.viewPager.setCurrentItem(Integer.parseInt(data.getStringExtra(Constants.EK_NAVIGATE_TO)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
