package pk.gov.pitb.speakmytext;

import java.util.ArrayList;

import pk.gov.pitb.speakmytext.helper.Constants;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class GridViewAdapter extends ArrayAdapter<String> {

	private int layoutResourceId;
	private int mScreenWidth, mScreenHeight;

	public GridViewAdapter(Activity activity, int layoutResourceId, ArrayList<String> data) {
		super(activity, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		Display display = activity.getWindowManager().getDefaultDisplay();
		mScreenWidth = display.getWidth();
		mScreenHeight = display.getHeight();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.imageViewImage = (ImageView) row.findViewById(R.id.iv_image);
			holder.imageViewIsRead = (ImageView) row.findViewById(R.id.iv_read);

			RelativeLayout.LayoutParams lpImage = new RelativeLayout.LayoutParams((int) (mScreenWidth * 0.48), (int) (mScreenWidth * 0.48));
			lpImage.setMargins((int) (mScreenWidth * 0.02), (int) (mScreenWidth * 0.02), (int) (mScreenWidth * 0.02), (int) (mScreenWidth * 0.02));
			holder.imageViewImage.setLayoutParams(lpImage);

			RelativeLayout.LayoutParams lpImageDone = new RelativeLayout.LayoutParams((int) (mScreenWidth * 0.15), (int) (mScreenWidth * 0.15));
			lpImageDone.setMargins((int) (mScreenWidth * 0.01), (int) (mScreenWidth * 0.01), (int) (mScreenWidth * 0.01), (int) (mScreenWidth * 0.01));
			lpImageDone.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lpImageDone.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.imageViewIsRead.setLayoutParams(lpImageDone);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		String chk_status = Constants.info_jobs_status.get(position);
		if (Constants.jobsdone.contains(Integer.valueOf(chk_status))) {
			holder.imageViewIsRead.setVisibility(View.VISIBLE);
		} else {
			holder.imageViewIsRead.setVisibility(View.GONE);
		}
		String item = getItem(position);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Log.e("path", item+";;");
		Bitmap bmp = BitmapFactory.decodeFile(item, options);
		holder.imageViewImage.setImageBitmap(bmp);
		// scaleImage(bmp, holder.imageViewImage, 350);
		return row;
	}

	private void scaleImage(Bitmap bitmap, ImageView view, int boundBoxInDp) {

		// Get current dimensions
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) boundBoxInDp) / width;
		float yScale = ((float) boundBoxInDp) / height;
		float scale = (xScale <= yScale) ? xScale : yScale;

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the ImageView
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

		BitmapDrawable result = new BitmapDrawable(scaledBitmap);
		width = scaledBitmap.getWidth();
		height = scaledBitmap.getHeight();

		// Apply the scaled bitmap
		view.setImageDrawable(result);

		// Now change ImageView's dimensions to match the scaled image
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

	private int dpToPx(int dp) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	private class ViewHolder {

		ImageView imageViewImage;
		ImageView imageViewIsRead;
	}
}