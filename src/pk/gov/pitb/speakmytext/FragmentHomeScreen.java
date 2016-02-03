package pk.gov.pitb.speakmytext;

import java.io.File;
import java.io.FileOutputStream;

import pk.gov.pitb.speakmytext.helper.Constants;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FragmentHomeScreen extends Fragment {

	private ImageButton imgBtnTakePicture;
	private ImageButton imgBtnRecordAudio;
	private ImageButton imgBtnMessages;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
		try {
			Display display = getActivity().getWindowManager().getDefaultDisplay();
			int mScreenWidth = display.getWidth();
			int mScreenHeight = display.getHeight();

			imgBtnRecordAudio = (ImageButton) rootView.findViewById(R.id.ib_record_audio);
			imgBtnTakePicture = (ImageButton) rootView.findViewById(R.id.ib_take_picture);
			imgBtnMessages = (ImageButton) rootView.findViewById(R.id.ib_messages);

			LinearLayout.LayoutParams lpImgBtn = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.90), (int) (mScreenWidth * 0.45));
			lpImgBtn.setMargins(0, 10, 0, 0);
			imgBtnRecordAudio.setLayoutParams(lpImgBtn);
			imgBtnTakePicture.setLayoutParams(lpImgBtn);
			imgBtnMessages.setLayoutParams(lpImgBtn);

			View.OnClickListener onClickTakePicture = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (FragmentTakePicture.ivTakePicture.getVisibility() == View.VISIBLE) {
						Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(camera, FragmentTakePicture.CAMERA_REQUEST);
					} else {
						ActivityHomePage.viewPager.setCurrentItem(1);
					}
				}
			};
			View.OnClickListener onClickRecordAudio = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ActivityHomePage.viewPager.setCurrentItem(2);
				}
			};
			View.OnClickListener onClickMessages = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						Constants.update_jobs_resources();
					} catch (Exception e) {
						e.printStackTrace();
					}
					FragmentShowJobs.updateResources();
					ActivityHomePage.viewPager.setCurrentItem(3);
				}
			};
			imgBtnTakePicture.setOnClickListener(onClickTakePicture);
			imgBtnRecordAudio.setOnClickListener(onClickRecordAudio);
			imgBtnMessages.setOnClickListener(onClickMessages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FragmentTakePicture.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
			try {
				FragmentTakePicture.photo = (Bitmap) data.getExtras().get("data");
				if (FragmentTakePicture.photo != null) {
					Constants.get_current_job();
					Constants.createDirIfNotExists("/pmr/" + Constants.currentjob);
					File sdCardDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/pmr/" + Constants.currentjob);
					String fileName = sdCardDirectory + "job.jpg";
					FileOutputStream fos = new FileOutputStream(new File(fileName));
					FragmentTakePicture.photo.compress(CompressFormat.JPEG, 100, fos);
					FragmentTakePicture.imageView.setImageBitmap(FragmentTakePicture.photo);
					FragmentTakePicture.ivTakePicture.setVisibility(View.GONE);
					FragmentTakePicture.ivAcceptImage.setVisibility(View.VISIBLE);
					FragmentTakePicture.ivCancelImage.setVisibility(View.VISIBLE);
					ActivityHomePage.viewPager.setCurrentItem(1);
				} else {
					Toast.makeText(getActivity(), "Error Loading Image. Please Retry", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(), "Error Loading Image. Please Retry", Toast.LENGTH_LONG).show();
			}
		}
	}
}