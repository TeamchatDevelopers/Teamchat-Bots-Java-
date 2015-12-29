package com.tc.sol.service.smml;

import org.json.JSONObject;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tc.sol.service.smml.util.Utility;
import com.tc.sol.service.smml.util.Utility.KEYWORDS;

public class GetSMId
{
	public static String getPollSMID(String question, String apikey) throws Exception
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, KEYWORDS.QUESTION + "=" + question);

		Request request = new Request.Builder().url(Utility.config.getProperty(KEYWORDS.POLL_SMID_URL)).put(body).addHeader(KEYWORDS.API_KEY, apikey)
				.addHeader("content-type", "application/x-www-form-urlencoded").build();

		Response response = client.newCall(request).execute();

		String jsonString = response.body().string();
		JSONObject poll = new JSONObject(jsonString);

		String id = poll.getString(KEYWORDS.ID);
		return id;
	}

	public static String getSurveySMID(String question, String options, String apikey) throws Exception
	{
		OkHttpClient client = new OkHttpClient();
		options = options.replaceAll(",", "&" + KEYWORDS.OPTIONS + "=");

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, KEYWORDS.QUESTION + "=" + question + "&" + KEYWORDS.OPTIONS + "=" + options);

		Request request = new Request.Builder().url(Utility.config.getProperty(KEYWORDS.SURVEY_SMID_URL)).put(body).addHeader(KEYWORDS.API_KEY, apikey)
				.addHeader("content-type", "application/x-www-form-urlencoded").build();

		Response response = client.newCall(request).execute();

		String jsonString = response.body().string();
		JSONObject survey = new JSONObject(jsonString);

		String id = survey.getString(KEYWORDS.ID);
		return id;
	}
}
