package com.example.gtsafe.library;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.gtsafe.library.listeners.interfaces.OnGetJSONListener;


import android.os.AsyncTask;
import android.util.Log;

public class DBRequester 
{
	private HTTPRequestTask task;
	private OnGetJSONListener listener;
	private HttpParams param;
	
	private final String SERVERURL = "http://api-gtsafe.rhcloud.com/";
	
	public DBRequester()
	{
		param = new BasicHttpParams();
		param.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	}
	
	public void setOnJSONEventListener(OnGetJSONListener listener)
	{
		this.listener = listener;
	}
	
	public void getJSON(List<NameValuePair> params)
	{
		task = new HTTPRequestTask();
		task.setParams(params);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SERVERURL);
	}
	
	private class HTTPRequestTask extends AsyncTask<String, Void, JSONObject>
	{
		private List<NameValuePair> params;
		private InputStream is;
		private JSONObject jObj;
	    private String json = "";
	    private DefaultHttpClient httpClient;
		private HttpPost httpPost;
		
		
		public void setParams(List<NameValuePair> params)
		{
			this.params = params;
		}

		@Override
		protected JSONObject doInBackground(String... urls) 
		{
			try 
			{
				httpClient = new DefaultHttpClient(param);
				httpPost = new HttpPost(urls[0]);
	            httpPost.setEntity(new UrlEncodedFormEntity(params));
	 
	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            is = httpEntity.getContent();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			try 
			{
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    is, "iso-8859-1"), 8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "n");
	            }
	            is.close();
	            json = sb.toString();
	            //Log.e("JSON", json);
	        } 
			catch (Exception e) 
			{
	            Log.e("Buffer Error", "Error converting result " + e.toString());
	        }
			
			try 
			{
	            jObj = new JSONObject(json);            
	        } 
			catch (JSONException e) 
			{
	            Log.e("JSON Parser", "Error parsing data " + e.toString());
	        }
	 
	        // return JSON String
	        return jObj;
		}
		
		public void onPostExecute(JSONObject result)
		{
			try 
			{
				if(listener != null)
				{
					listener.OnGetJSON(result);
				}
			} 
			catch (JSONException e) {}
		}
	}
}
