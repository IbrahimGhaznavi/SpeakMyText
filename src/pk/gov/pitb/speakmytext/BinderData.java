package pk.gov.pitb.speakmytext;

import pk.gov.pitb.speakmytext.helper.Constants;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

public class BinderData extends BaseAdapter {

	LayoutInflater inflater;
	 String[] DataCollection;
	ViewHolder holder;
	public BinderData() {
		// TODO Auto-generated constructor stub
	}
	
	public BinderData(Activity act, String[] langs_name) {
		
		this.DataCollection = langs_name;			
		inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	public int getCount() {
		// TODO Auto-generated method stub
//		return idlist.size();
		return DataCollection.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		 
		View vi=convertView;
	    if(convertView==null){
	     
	      vi = inflater.inflate(R.layout.lang_list_view_xml, null);
	      holder = new ViewHolder();
	      holder.c = (CheckBox)vi.findViewById(R.id.checkBox1); //  name
	      vi.setTag(holder);
	    }
	    else{
	    	
	    	holder = (ViewHolder)vi.getTag();
	    }

	      holder.c.setText(DataCollection[position]);
	      holder.c.setOnClickListener(new OnClickListener() {
			final int temp=position; 
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckBox c=(CheckBox) arg0;
				if(c.isChecked())
				{
					Constants.temp_lang.set(position,true);
				}else
				{
					Constants.temp_lang.set(position,false);
				}
			}
		});
	      return vi;
	}
	
	/*
	 * 
	 * */
	static class ViewHolder{
		CheckBox c;
	}
	
}