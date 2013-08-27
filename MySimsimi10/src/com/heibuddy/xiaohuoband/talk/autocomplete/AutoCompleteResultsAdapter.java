package com.heibuddy.xiaohuoband.talk.autocomplete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.heibuddy.xiaohuoban.http.SimpleClient;
import com.heibuddy.xiaohuoban.http.SimpleRequest;
import com.heibuddy.xiaohuoban.http.SimpleResponse;
import com.heibuddy.xiaohuoband.talk.autocomplete.SuggestObject;

import com.heibuddy.xiaohuoband.R;
import com.heibuddy.xiaohuoband.TalkActivity;
import com.heibuddy.xiaohuoband.Xiaohuoband;
import com.heibuddy.xiaohuoband.XiaohuobandSettings;

public class AutoCompleteResultsAdapter extends ArrayAdapter<SuggestObject> implements Filterable {
	protected final String TAG = "AutoCompleteResultsAdapter";
	public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
	
	private final LayoutInflater inflater;
	private final Context mContext;
	
	public List<SuggestObject> mResultList = Collections.synchronizedList(new ArrayList<SuggestObject>());
	
	public AutoCompleteResultsAdapter(Context context) {
		super(context, 0);
		mContext = context;
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mResultList.size();
	}
	
	@Override
	public SuggestObject getItem(int index) {
		SuggestObject suggestObject = getSuggestionObject(index);
		return suggestObject;
	}
	
	public SuggestObject getSuggestionObject(int index) {
		return mResultList.get(index);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = inflater.inflate(R.layout.autocomplete_list_layout, null);
			view.setTag(new Holder((TextView)view.findViewById(R.id.autoCompleteResultText), 
									  (ImageView)view.findViewById(R.id.autoCompletePlusButton)));
		}
		
		final SuggestObject suggestion = getSuggestionObject(position);
		
		final Holder holder = (Holder) view.getTag();
		
		if (suggestion != null) {
			holder.autoCompleteResult.setText(suggestion.getPhrase());
			/*
			final int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 
	                (float) 2.0, getContext().getResources().getDisplayMetrics());
			//TODO
			holder.autoCompleteResult.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.mainTextSize+pixelValue);
			*/
			
			holder.plusImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					String phrase = suggestion.getPhrase();
					if(phrase != null) {
						//BusProvider.getInstance().post(new SuggestionPasteEvent(suggestion.getPhrase()));
						Intent intent = new Intent(TalkActivity.INTENT_ACTION_PASTE_SUGGESTION);
						intent.putExtra("query", phrase);
						mContext.sendBroadcast(intent);
						if (DEBUG) Log.d(TAG, "query paste intent: " + phrase);
					}
				}
			});
		}
		
		return view;
	}
	
	class Holder {
		final TextView autoCompleteResult;
		final ImageView plusImage;
		
		public Holder(final TextView autoCompleteResult, final ImageView plusImage) {
			this.autoCompleteResult = autoCompleteResult;
			this.plusImage = plusImage;
		}
	}

	@Override
	public Filter getFilter() {
		Filter webFilter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				ArrayList<SuggestObject> newResults = new ArrayList<SuggestObject>();
				
				if (constraint != null) {
					//TODO: Check if this constraint is already in the cache
					JSONArray json = getJSONResultForConstraint(constraint);
					for (int i = 0; i < json.length(); i++) {
						try {
							JSONObject nextObj = json.getJSONObject(i);
							SuggestObject item = new SuggestObject(nextObj);
							if (item != null) {
								newResults.add(item);
							}
						} catch (JSONException e) {
							Log.e(TAG, "No JSON Object at index " + i);
							Log.e(TAG, "Exception: " + e.getMessage());
							e.printStackTrace();
						}
					}
					//TODO: Cache the results for later
				}
				
				results.values = newResults;
				results.count = newResults.size();
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				mResultList.clear();
				if (results != null && results.count > 0) {
					@SuppressWarnings("unchecked")
					ArrayList<SuggestObject> newResults = (ArrayList<SuggestObject>)results.values;
					mResultList.addAll(newResults);
					notifyDataSetChanged();
				} else {
					mResultList.clear();
					notifyDataSetInvalidated();
				}
			}
			
			private JSONArray doGetString(final String query, int limit, final String url) throws IOException{
				SimpleRequest.Builder signupBuilder = SimpleRequest.newBuilder();
		    	signupBuilder.url(url);
		    	signupBuilder.param("q", query);
		    	signupBuilder.param("n", String.valueOf(limit));
		    	SimpleRequest nr = signupBuilder.build();
		       
		    	SimpleClient client = new SimpleClient(Xiaohuoband.getAppContext());
		    	HttpResponse response = client.exec(nr);
		    	SimpleResponse res = new SimpleResponse(response);
		    	return res.getJSONArray();
			}
			
			private JSONArray getJSONResultForConstraint(CharSequence constraint) {
				JSONArray json = null;
				try {
					String query = constraint.toString();
					if (DEBUG) Log.d(TAG, "query is : " + query);
					json = doGetString(query, TalkActivity.SUGGESTION_LIMIT, TalkActivity.AUTO_COMPLETE_URL);
					if (DEBUG) Log.d(TAG, "return suggestion is: " + json.toString());
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
				
				return json;
		    }
		};
		
		return webFilter;
	}
}
