package com.androidbeasts.bakingapp;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidbeasts.bakingapp.model.Steps;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
* Fragment to show recipe steps
*/
public class RecipeStepFragment extends Fragment implements ExoPlayer.EventListener {

    final String TAG = "RecipeStepFragment";

    // Final Strings to store state information about the list of images and list index
    public static final String STEPS_LIST = "steps";
    public static final String LIST_INDEX = "list_index";

    // Variables to store a list of steps objects and the index of the step object that this fragment displays
    private List<Steps> mSteps;
    private int mListIndex;

    @BindView(R.id.exoplayer_view)
    SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.ingredients_tv)
    TextView mIngredientsTv;
    @BindView(R.id.next_step)
    Button mNextStepButton;
    @BindView(R.id.previous_step)
    Button mPrevStepButton;
    @BindView(R.id.video_progressBar)
    ProgressBar progressBar;

    private SimpleExoPlayer player;

    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    Uri mp4VideoUri;

    public RecipeStepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG,"ON Config Changed called");
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putParcelableArrayList(STEPS_LIST, (ArrayList<Steps>) mSteps);
        currentState.putInt(LIST_INDEX, mListIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        ButterKnife.bind(this, rootView);

        // Load the saved state (the list of step objects and list index) if there is one
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(STEPS_LIST);
            mListIndex = savedInstanceState.getInt(LIST_INDEX);
        }

       /* int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        } else {
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }*/

        if (mSteps != null) {
            setStepVideoNText();
            // Set a click listener on the image view
            mNextStepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Increment position as long as the index remains <= the size of the steps object list
                    if (mListIndex < mSteps.size() - 1) {
                        mListIndex++;
                    } else {
                        // The end of list has been reached, so return to beginning index
                        mListIndex = 0;
                    }
                    Log.d(TAG, "Video Url " + "ListIndex " + mListIndex + ",......." + mSteps.get(mListIndex).getVideoURL());
                    // Set the image resource to the new list item
                    setStepVideoNText();
                }
            });

            mPrevStepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Decrement position as long as the index remains > 0
                    if (mListIndex >= 0) {
                        mListIndex--;
                    } else {
                        // The start of list has been reached, so return to end index
                        mListIndex = mSteps.size() - 1;
                    }
                    // Set the image resource to the new list item
                    setStepVideoNText();
                }
            });

        } else {
            Log.v(TAG, "This fragment has a null list of image id's");
        }

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        window = new Timeline.Window();

        return rootView;
    }

    // Setter methods for keeping track of the steps this fragment can display and which step
    // in the list is currently being displayed

    public void setSteps(List<Steps> steps) {
        mSteps = steps;
    }

    public void setListIndex(int index) {
        mListIndex = index;
    }

    //Set video uri and description of the step being displayed
    private void setStepVideoNText() {
        changeVideo();
        String videoURL = mSteps.get(mListIndex).getVideoURL();
        String imageURL = mSteps.get(mListIndex).getThumbnailURL();
        String descText = mSteps.get(mListIndex).getDescription();
        //Log.d(TAG, "Video Url " + videoURL);

        if (TextUtils.isEmpty(videoURL) && TextUtils.isEmpty(imageURL)) {
            simpleExoPlayerView.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(videoURL) && TextUtils.isEmpty(imageURL)) {
            startVideoPlayback(videoURL);
            if (player != null) {
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                initializePlayer();
            }
        } else if (!TextUtils.isEmpty(imageURL)) {
            startVideoPlayback(imageURL);
            if (player != null) {
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                initializePlayer();
            }
        } else {

        }
        if (descText != null) {
            mIngredientsTv.setText(descText);

            final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(1000);
            //fadeIn.setStartOffset(3000);

            mIngredientsTv.startAnimation(fadeIn);
        }

    }

    /*Set videoUrl in uri string
    * Parameters: String
    * */
    private void startVideoPlayback(String videoUrl) {
        try {
            //Log.d(TAG, "Video Url " + videoUrl);
            mp4VideoUri = Uri.parse(videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Stop previous video and seek to 0 level
    private void changeVideo() {
        if (player != null) {
            player.stop();
            player.seekTo(0L);
        }
    }

    //Initialize video player
    private void initializePlayer() {

        simpleExoPlayerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        simpleExoPlayerView.setPlayer(player);

        player.setPlayWhenReady(shouldAutoPlay);
/*        MediaSource mediaSource = new HlsMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
                mediaDataSourceFactory, mainHandler, null);*/

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(mp4VideoUri,
                mediaDataSourceFactory, extractorsFactory, null, null);

        player.addListener(this);
        player.prepare(mediaSource);
    }

    /*Release exo-player*/
    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /*On Player state changed show progressbar when loading or buffering*/
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_BUFFERING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        Log.d("RecipeStep", playbackState + "");
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
}
