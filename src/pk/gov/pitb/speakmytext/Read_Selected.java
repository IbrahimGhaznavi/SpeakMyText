package pk.gov.pitb.speakmytext;

import java.io.File;
import java.io.IOException;

import pk.gov.pitb.speakmytext.helper.Constants;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Read_Selected extends ActionBarActivity {

	// Globals:
	private TouchImageView touchViewImage;
	private int ViewSize = 0;
	String job = "";
	boolean switcch = false;
	String page = "";
	String path = "";
	Bitmap bmp = null;
	String recording = ".mp3";
	String rec = "";
	int page_no = 0;
	int total = 0;
	boolean music = false;
	// player controls variable

	ImageView imgViewPrevious, imgViewPlayPause, imgViewNext;
	Button buttonQuit;
	TextView textState;
	final MediaPlayer mediaPlayer = new MediaPlayer();
	private int stateMediaPlayer;
	private final int stateMP_Error = 0;
	private final int stateMP_NotStarter = 1;
	private final int stateMP_Playing = 2;
	private final int stateMP_Pausing = 3;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_default, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case android.R.id.home:
				Intent intent = new Intent();
				intent.putExtra(Constants.EK_NAVIGATE_TO, "0");
				setResult(RESULT_OK, intent);
				finish();
				return true;
			case R.id.action_message:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	// OnCreate Method:
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_read);

		Intent intent = getIntent();

		this.job = intent.getStringExtra("job");
		total = 3;
		// Setup webview
		touchViewImage = (TouchImageView) findViewById(R.id.rwebView1);
		page = job;
		page_no = 1;
		rec = job;
		path = Environment.getExternalStorageDirectory() + "/" + "pmr" + "/" + page + "/" + page_no + ".jpg";
//		path = Environment.getExternalStorageDirectory() + "/" + "pmr" + "/" + page + "job.jpg";
		recording = Environment.getExternalStorageDirectory() + "/" + "pmr" + "/" + rec + "/" + page_no + ".mp3";

		Log.e("TEST", path);
		pdfLoadImages();// load images\
		getActionBar().setDisplayHomeAsUpEnabled(true);
		imgViewPrevious = (ImageView) findViewById(R.id.iv_back);
		imgViewPlayPause = (ImageView) findViewById(R.id.iv_play_pause);
		imgViewNext = (ImageView) findViewById(R.id.iv_next);

		Display display = getWindowManager().getDefaultDisplay();
		int mScreenWidth = display.getWidth();
		int mScreenHeight = display.getHeight();

		LinearLayout.LayoutParams lpImageView = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.90), (int) (mScreenWidth * 1.15));
		lpImageView.setMargins(0, 0, 0, (int) (mScreenHeight * 0.05));
		touchViewImage.setLayoutParams(lpImageView);

		LinearLayout.LayoutParams lpBtnSmallFirst = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.15), (int) (mScreenWidth * 0.15));
		lpBtnSmallFirst.setMargins(0, 0, (int) (mScreenWidth * 0.05), 0);
		LinearLayout.LayoutParams lpBtnSmall = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.15), (int) (mScreenWidth * 0.15));
		lpBtnSmall.setMargins((int) (mScreenWidth * 0.05), 0, (int) (mScreenWidth * 0.05), 0);
		LinearLayout.LayoutParams lpBtnSmallLast = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.15), (int) (mScreenWidth * 0.15));
		lpBtnSmallLast.setMargins((int) (mScreenWidth * 0.05), 0, 0, 0);

		imgViewPrevious.setPadding(0, 0, 0, 0);
		imgViewPlayPause.setPadding(0, 0, 0, 0);
		imgViewNext.setPadding(0, 0, 0, 0);

		imgViewPrevious.setLayoutParams(lpBtnSmallFirst);
		imgViewPlayPause.setLayoutParams(lpBtnSmall);
		imgViewNext.setLayoutParams(lpBtnSmallLast);

		imgViewPrevious.setOnClickListener(show_prev_page);
		imgViewPrevious.setEnabled(false);
		if (total == 1) {
			imgViewNext.setEnabled(false);
		}
		imgViewNext.setOnClickListener(show_page);
		imgViewPlayPause.setOnClickListener(buttonPlayPauseOnClickListener);
		touchViewImage.setOnTouchListener(new View.OnTouchListener() {

			public final static int FINGER_RELEASED = 0;
			public final static int FINGER_TOUCHED = 1;
			public final static int FINGER_DRAGGING = 2;
			public final static int FINGER_UNDEFINED = 3;

			private int fingerState = FINGER_RELEASED;

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				switch (motionEvent.getAction()) {

					case MotionEvent.ACTION_DOWN:
						if (fingerState == FINGER_RELEASED) {

							fingerState = FINGER_TOUCHED;
							if (switcch == false) {
								((LinearLayout) findViewById(R.id.ll_footer)).setVisibility(View.VISIBLE);
								switcch = true;
							} else {
								((LinearLayout) findViewById(R.id.ll_footer)).setVisibility(View.GONE);
								switcch = false;
							}
						} else
							fingerState = FINGER_UNDEFINED;

					break;

					case MotionEvent.ACTION_UP:
						if (fingerState != FINGER_DRAGGING) {
							fingerState = FINGER_RELEASED;

							// Your onClick codes

						} else if (fingerState == FINGER_DRAGGING)
							fingerState = FINGER_RELEASED;
						else
							fingerState = FINGER_UNDEFINED;
					break;

					case MotionEvent.ACTION_MOVE:
						if (fingerState == FINGER_DRAGGING) {
							fingerState = FINGER_DRAGGING;

						}

						else
							fingerState = FINGER_UNDEFINED;
					break;

					default:
						fingerState = FINGER_UNDEFINED;

				}

				return false;
			}
		});

	}

	// Load Images:
	private void pdfLoadImages() {
		try {
			// run async
			new AsyncTask<Void, Void, Void>() {

				// create and show a progress dialog
				ProgressDialog progressDialog = ProgressDialog.show(Read_Selected.this, "", "Opening...");

				@Override
				protected void onPostExecute(Void result) {
					touchViewImage.setImageBitmap(bmp);
					progressDialog.dismiss();
					initMediaPlayer();
				}

				@Override
				protected Void doInBackground(Void... params) {
					try {
						File imgFile = new File(path);
						if (imgFile.exists()) {
							bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			}.execute();
			System.gc();// run GC
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initMediaPlayer() {
		String PATH_TO_FILE = recording;

		try {
			mediaPlayer.setDataSource(PATH_TO_FILE);
			mediaPlayer.prepare();

			stateMediaPlayer = stateMP_NotStarter;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			stateMediaPlayer = stateMP_Error;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			stateMediaPlayer = stateMP_Error;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			stateMediaPlayer = stateMP_Error;
		}

		mediaPlayer.start();
		imgViewPlayPause.setBackgroundResource(R.drawable.paause);
		stateMediaPlayer = stateMP_Playing;
		music = true;
	}

	// page button controls

	Button.OnClickListener show_prev_page = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if (page_no == 2) {
				imgViewPrevious.setEnabled(false);
			}
			if (page_no == total) {
				imgViewNext.setEnabled(true);
			}
			page_no--;

			// load images\

			path = Environment.getExternalStorageDirectory() + "/" + "pmr" + "/" + page + "/" + page_no + ".jpg";
			recording = Environment.getExternalStorageDirectory() + "/" + "pmr" + "/" + rec + "/" + page_no + ".mp3";

			pdfLoadImages();
			mediaPlayer.reset();
			initMediaPlayer();

		}

	};

	Button.OnClickListener show_page = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if (page_no == 1) {
				imgViewPrevious.setEnabled(true);
			}
			page_no++;
			if (page_no == total) {
				imgViewNext.setEnabled(false);

			}

			path = Environment.getExternalStorageDirectory() + "/" + "pmr" + "/" + page + "/" + page_no + ".jpg";
			recording = Environment.getExternalStorageDirectory() + "/" + "pmr" + "/" + rec + "/" + page_no + ".mp3";
			pdfLoadImages();

			mediaPlayer.reset();
			initMediaPlayer();

		}
	};

	// player button controls

	Button.OnClickListener buttonPlayPauseOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mediaPlayer.isPlaying() == true || mediaPlayer.isLooping() == true) {
				onPause();
				imgViewPlayPause.setBackgroundResource(R.drawable.plaaay);
			} else {
				mediaPlayer.start();
				imgViewPlayPause.setBackgroundResource(R.drawable.paause);
			}

		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		mediaPlayer.pause();

	}
}
