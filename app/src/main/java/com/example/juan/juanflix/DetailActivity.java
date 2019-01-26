package com.example.juan.juanflix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.juan.juanflix.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

// extend YouTubeBaseActivity to use youtube APi properly
public class DetailActivity extends YouTubeBaseActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyB6nYhilTj35zsiHI5jOgdvfUVOEHpY_34";
    private static final String TRAILERS_API = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    Movie movie;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);



        // get data from intent via Parceler
        movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getVoteAverage());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(TRAILERS_API, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // get array of trailers
                try {
                    JSONArray results = response.getJSONArray("results");
                    // check if there are any trailers
                    if (results.length() == 0) {

                        // TODO - handle no trailer case by showing something else

                        return;
                    }
                    // TODO - check movieTrailer.getString("site") to make sure site is always YouTube and handle otherwise
                    JSONObject movieTrailer = results.getJSONObject(0);
                    String youTubeKey = movieTrailer.getString("key");
                    initializeYouTube(youTubeKey);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });


    }

    private void initializeYouTube(final String youTubeKey) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("smile", "on init success");

                youTubePlayer.cueVideo(youTubeKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("smile", "on init fail");

            }
        });
    }
}
